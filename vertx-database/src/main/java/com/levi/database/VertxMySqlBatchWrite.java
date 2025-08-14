package com.levi.database;

import io.vertx.core.*;
import io.vertx.core.Future;
import io.vertx.mysqlclient.*;
import io.vertx.sqlclient.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class VertxMySqlBatchWrite extends AbstractVerticle {

    private static final int TOTAL       = 50_000;
    private static final int BATCH       = 100;
    private static final int CONCURRENCY = 500;

    private MySQLPool pool;

    @Override
    public void start(Promise<Void> startPromise) {

        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setHost("127.0.0.1")
                .setPort(3306)
                .setDatabase("springai")
                .setUser("root")
                .setPassword("root")
                .setCachePreparedStatements(true);

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(Math.min(CONCURRENCY, 32));

        pool = MySQLPool.pool(vertx, connectOptions, poolOptions);

        pool.query(
                        "CREATE TABLE IF NOT EXISTS t_demo (" +
                                "  id INT PRIMARY KEY," +
                                "  payload VARCHAR(200)" +
                                ")")
                .execute()
                .onFailure(Throwable::printStackTrace)
                .onSuccess(r -> doInsert().onComplete(startPromise));
    }

    private Future<Void> doInsert() {
        Promise<Void> done = Promise.promise();

        List<Record> all = IntStream.rangeClosed(1, TOTAL)
                .mapToObj(i -> new Record(i, "payload-" + i))
                .collect(Collectors.toList());

        List<List<Record>> partitions = split(all, BATCH);

        Semaphore semaphore = new Semaphore(CONCURRENCY);
        AtomicInteger pending = new AtomicInteger(partitions.size());

        for (List<Record> batch : partitions) {
            semaphore.acquireUninterruptibly();
            writeBatch(batch)
                    .onComplete(ar -> {
                        semaphore.release();
                        if (pending.decrementAndGet() == 0) {
                            pool.close();
                            done.complete();
                        }
                    });
        }

        return done.future();
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
        final int id;
        final String payload;
        Record(int id, String payload) { this.id = id; this.payload = payload; }
    }

    public static void main(String[] args) {
        long start1 = System.currentTimeMillis();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxMySqlBatchWrite())
                .onSuccess(id -> System.out.println("一共耗时{}" + (System.currentTimeMillis() - start1)))
                .onFailure(Throwable::printStackTrace);
    }
}