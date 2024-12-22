package com.levi.udemy.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Point2PointExamle {


  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new Sender());
    vertx.deployVerticle(new Receiver());
  }

  static class Sender extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(Sender.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(1000,id ->{
        vertx.eventBus().send(Sender.class.getSimpleName(),"hello this is a request info from request verticle");
      });
    }
  }

  static class Receiver extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(Receiver.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().consumer(Sender.class.getSimpleName(), message->{
        logger.info("received message from sender {}", message.body());
      });
    }
  }

}
