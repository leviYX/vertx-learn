package com.levi.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 一个简单的 Verticle 示例
 * Verticle是vertx的一个组件，用于实现应用程序的逻辑。
 * 每个Verticle都是一个独立的线程，用于处理事件。
 * Verticle可以在vertx实例中部署和运行，也可以在集群中部署和运行。eventloop 线程模型,  对应到akaa，nodejs,netty,这些都是单线程
 * 异步循环模型，实现单线程下的高并发
 */
public class HelloVerticle2 extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(HelloVerticle2.class);

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new HelloVerticle2());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.setPeriodic(2000,id ->{
            LOG.info("当前有{}个用户访问",howMany());
        });
        vertx.createHttpServer().requestHandler(req -> {
            LOG.info("当前是{}号用户",COUNTER.incrementAndGet());
        }).listen(8080)
                .onSuccess(bind -> {
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    private int howMany() {
        return COUNTER.get();
    }
}
