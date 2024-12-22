package com.levi.udemy.vertx_starter.eventbus.customCodec;

import com.levi.udemy.vertx_starter.eventbus.Point2PointExamle;
import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingAndPongExample {

  final static Logger logger = LoggerFactory.getLogger(PingAndPongExample.class);

  private static final String ADDRESS = PingAndPongExample.class.getSimpleName();

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new PingVerticle(),errOnHandler());
    vertx.deployVerticle(new PongVerticle(),errOnHandler());
  }

  private static Handler<AsyncResult<String>> errOnHandler(){
    return res -> {
      if (res.failed()) {
        logger.error("PingAndPongExample error: " + res.cause());
      }
    };
  }

  static class PingVerticle extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(PingVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      var eventBus = vertx.eventBus();
      // 必须在启动verticle之前注册codec，否则会报错
      eventBus.registerDefaultCodec(Ping.class,new LocalMessageCodec<>(Ping.class));
      startPromise.complete();
      logger.info("sending ping message...");
      eventBus.request(ADDRESS,new Ping("ping msg",1,true),reply -> {
        if(reply.succeeded()) {
          logger.info("PingVerticle received reply: " + reply.result().body());
        }else {
          logger.error("PingVerticle error: " + reply.cause());
        }
      });
    }
  }

  static class PongVerticle extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(PongVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      var eventBus = vertx.eventBus();
      eventBus.registerDefaultCodec(Pong.class,new LocalMessageCodec<>(Pong.class));
      startPromise.complete();
      eventBus.consumer(ADDRESS, message -> {
        logger.info("PongVerticle received message: " + message.body());
        message.reply(new Pong(1));
      }).exceptionHandler(err -> {
        logger.error("PongVerticle exception", err);
      });
    }
  }
}
