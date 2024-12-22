package com.levi.udemy.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class VerticleAA extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    System.out.println("启动 verticleAA");
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    System.out.println("停止 verticleAA");
    stopPromise.complete();
  }
}
