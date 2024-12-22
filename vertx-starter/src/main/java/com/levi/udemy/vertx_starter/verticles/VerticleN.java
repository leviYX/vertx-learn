package com.levi.udemy.vertx_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class VerticleN extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    System.out.println("启动 VerticleN 在线程" + Thread.currentThread().getName() + "并且他的配置信息为:" + config().toString());
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    System.out.println("停止 VerticleN");
    stopPromise.complete();
  }
}
