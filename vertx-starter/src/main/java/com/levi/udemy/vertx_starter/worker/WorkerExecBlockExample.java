package com.levi.udemy.vertx_starter.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WorkerExecBlockExample extends AbstractVerticle {
  static final Logger logger = LoggerFactory.getLogger(WorkerExecBlockExample.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExecBlockExample());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    startPromise.complete();
    vertx.executeBlocking(event ->{
      blockingCode();
      // 异步任务执行完毕，通知Vert.x框架，触发回调函数，也就是下面挂的回调函数
      event.complete();
    }, result -> {
      if (result.succeeded()){
        logger.info("blocking code complete " + Thread.currentThread().getName());
      }else {
        logger.error("blocking code failed", result.cause());
      }
    });
  }

  private void blockingCode() {
    logger.info("blocking code " + Thread.currentThread().getName());
    try {
      TimeUnit.SECONDS.sleep(5);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
