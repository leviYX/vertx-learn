package com.levi.vertx_stock_broker;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(err -> LOG.error("Vertx exception", err));
    vertx.deployVerticle(new MainVerticle())
      .onFailure(err -> LOG.error("Vertx deployment failed", err))
      .onSuccess(id -> LOG.info("Vertx deployment successful: {}", id));
  }

  @Override
  public void start(Promise<Void> startPromise){
    vertx.deployVerticle(RestApiVerticle.class.getName(),new DeploymentOptions().setInstances(getProcessorNumber()))
      .onFailure(startPromise::fail)
      .onSuccess(res -> {
        LOG.info("deployment {} succeeded with id {}", RestApiVerticle.class.getSimpleName(),res);
        startPromise.complete();
      });
  }

  private static int getProcessorNumber() {
    return Math.max(1,Runtime.getRuntime().availableProcessors());
  }
}


