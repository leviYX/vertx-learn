package com.levi.database;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalBatchWrite {

    private static final int TOTAL   = 50_000;
    private static final int BATCH   = 1000;
    private static final int THREADS = 50;
    private static final int THREAD_POOL_QUEUE = 10_000;

    private static final String JDBC_URL =
            "jdbc:mysql://127.0.0.1:3306/springai?useSSL=false&allowPublicKeyRetrieval=true&rewriteBatchedStatements=true";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(
                    THREADS,
                    THREADS,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(THREAD_POOL_QUEUE),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );

    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();
        List<Record> all = IntStream.rangeClosed(1, TOTAL)
                .mapToObj(i -> new Record(i, "payload-" + i))
                .collect(Collectors.toList());
        List<List<Record>> partitions = split(all, BATCH);
        List<CompletableFuture<Void>> futures = partitions.stream()
                .map(batch -> CompletableFuture.runAsync(() -> {
                    insertBatch(batch);
                } , THREAD_POOL))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long t1 = System.currentTimeMillis();
        System.out.printf("全部写入完成，耗时 %d ms%n", t1 - t0);
        THREAD_POOL.shutdown();
    }

    private static void insertBatch(List<Record> batch) {
        String sql = "INSERT INTO t_demo (id, payload) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASS);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (Record r : batch) {
                ps.setInt(1, r.id);
                ps.setString(2, r.payload);
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> List<List<T>> split(List<T> src, int size) {
        List<List<T>> list = new ArrayList<>();
        for (int i = 0; i < src.size(); i += size) {
            list.add(src.subList(i, Math.min(src.size(), i + size)));
        }
        return list;
    }

    static class Record {
        final int id;
        final String payload;
        Record(int id, String payload) {
            this.id = id;
            this.payload = payload;
        }
    }
}