package com.levi.udemy.vertx_starter.worker;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WorkerExecWorkerExample extends AbstractVerticle {
  static final Logger logger = LoggerFactory.getLogger(WorkerExecWorkerExample.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExecWorkerExample(),
      new DeploymentOptions().setWorker(true).setWorkerPoolName("my-worker-pool-"));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      blockingCode();
      logger.info("blocking code finished");
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
