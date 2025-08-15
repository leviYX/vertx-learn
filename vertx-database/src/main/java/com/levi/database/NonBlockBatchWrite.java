package com.levi.database;

import io.vertx.core.*;
import io.vertx.mysqlclient.*;
import io.vertx.sqlclient.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NonBlockBatchWrite extends AbstractVerticle {

    private static final int TOTAL   = 50_000;
    private static final int BATCH   = 1000;

    private MySQLPool pool;

    @Override
    public void start(Promise<Void> startPromise) {
        pool = MySQLPool.pool(vertx, new MySQLConnectOptions()
                        .setHost("127.0.0.1")
                        .setPort(3306)
                        .setDatabase("springai")
                        .setUser("root")
                        .setPassword("root")
                        .addProperty("rewriteBatchedStatements", "true")
                        .setCachePreparedStatements(true),
                new PoolOptions().setMaxSize(64));

        pool.query("CREATE TABLE IF NOT EXISTS t_demo (id INT PRIMARY KEY, payload VARCHAR(200))")
                .execute()
                .onFailure(Throwable::printStackTrace)
                .compose(r -> doInsert())
                .onComplete(startPromise);
    }

    private Future<Void> doInsert() {
        List<Record> records = IntStream.rangeClosed(1, TOTAL)
                .mapToObj(i -> new Record(i, "payload-" + i))
                .collect(Collectors.toList());

        List<List<Record>> batches = split(records, BATCH);

        List<Future> futures = batches.stream()
                .map(this::writeBatch)
                .collect(Collectors.toList());

        return CompositeFuture.all(new ArrayList<>(futures)).mapEmpty();
    }

    private Future<Void> writeBatch(List<Record> batch) {
        List<Tuple> params = batch.stream()
                .map(r -> Tuple.of(r.id, r.payload))
                .collect(Collectors.toList());

        return pool.preparedQuery("INSERT INTO t_demo (id, payload) VALUES (?, ?)")
                .executeBatch(params)
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
        final int id; final String payload;
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