package com.levi.udemy.vertx_starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * Verticle可以层级部署，但是总是父Verticle先启动，子Verticle后启动。
 * 当父级的verticle部署后才允许子Verticle部署。
 * 当父级的verticle解除部署后，子Verticle会自动解除部署。
 */
public class MainVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888).onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  // 处理一些资源的关闭等工作
  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    System.out.println("Stopping Vert.x");
    super.stop(stopPromise);
  }
}
