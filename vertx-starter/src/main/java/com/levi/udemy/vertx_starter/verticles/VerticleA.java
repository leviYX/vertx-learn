package com.levi.udemy.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class VerticleA extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    System.out.println("启动 verticleA");
    vertx.deployVerticle(new VerticleAA());
    vertx.deployVerticle(new VerticleAB());
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    System.out.println("停止 verticleA");
    stopPromise.complete();
  }
}
