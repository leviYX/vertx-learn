package com.levi.database;

import io.vertx.core.*;
import io.vertx.mysqlclient.*;
import io.vertx.sqlclient.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NonBlockBatchWrite extends AbstractVerticle {

    private static final int TOTAL = 50_000;
    private static final int BATCH = 7_000;
    private static long Start;

    private MySQLPool pool;

    @Override
    public void start(Promise<Void> startPromise) {
        Start = System.currentTimeMillis();
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost("127.0.0.1")
                .setPort(3306)
                .setDatabase("springai")
                .setUser("root")
                .setPassword("root")
                .addProperty("useSSL", "false")
                .addProperty("allowPublicKeyRetrieval", "true")
                .addProperty("rewriteBatchedStatements", "true")
                .addProperty("useServerPrepStmts", "false");

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(50)
                .setMaxWaitQueueSize(10_000);
        pool = MySQLPool.pool(vertx, connectOptions, poolOptions);

        long start = System.currentTimeMillis();
        doInsert().onSuccess(insertResult -> {
            long end = System.currentTimeMillis();
            System.out.println("全部写入完成, 耗时 " + (end - start) + " ms");
            startPromise.complete();
        });
    }

    private Future<Void> doInsert() {
        List<Record> records = IntStream.rangeClosed(1, TOTAL)
                .mapToObj(i -> new Record(i, "payload-" + i))
                .collect(Collectors.toList());

        List<List<Record>> batches = split(records, BATCH);

        WorkerExecutor worker = vertx.createSharedWorkerExecutor("batch-pool", 50);

        List<Future> futures = batches.stream()
                .map(batch -> worker.executeBlocking(promise -> writeBatch(batch), false))
                .collect(Collectors.toList());

        return CompositeFuture.all(new ArrayList<>(futures)).mapEmpty();
    }

    private Future<Void> writeBatch(List<Record> batch) {
        StringBuilder sqlContext = new StringBuilder("INSERT INTO t_demo (id, payload) VALUES ");
        sqlContext.append(batch.stream()
                .map(r -> "(?, ?)")
                .collect(Collectors.joining(",")));

        Tuple params = Tuple.tuple();
        batch.forEach(r -> params.addInteger(r.id).addString(r.payload));
        return pool.preparedQuery(sqlContext.toString())
                .execute(params)
                .onSuccess(result -> {
                    System.out.println("写入成功，耗时" + (System.currentTimeMillis() - Start) + "ms");
                })
                .mapEmpty();
    }

    private static <T> List<List<T>> split(List<T> src, int size) {
        List<List<T>> list = new ArrayList<>();
        for (int i = 0; i < src.size(); i += size) {
            list.add(src.subList(i, Math.min(src.size(), i + size)));
        }
        return list;
    }

    private static class Record {
        final int id;
        final String payload;
        Record(int id, String payload) { this.id = id; this.payload = payload; }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new NonBlockBatchWrite())
                .onSuccess(id -> System.out.println("verticle部署完毕，一共耗时: " + (System.currentTimeMillis() - start)))
                .onFailure(Throwable::printStackTrace);
    }

}