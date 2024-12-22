package com.levi.udemy.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发布订阅消息模式
 */
public class PubAndSubExamle {


  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new Subscriber1());
    vertx.deployVerticle(new Subscriber2());
    vertx.deployVerticle(new Publish());

  }

  public static class Publish extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(Publish.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(1000, timerId -> {
        vertx.eventBus().publish(Publish.class.getSimpleName(),"hello this message for everyone");
      });
    }
  }

  public static class Subscriber1 extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(Subscriber1.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().consumer(Publish.class.getSimpleName(), message -> {
        logger.info("Subscriber1 received message {}",message.body());
      });
    }
  }

  public static class Subscriber2 extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(Subscriber2.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().consumer(Publish.class.getSimpleName(), message -> {
        logger.info("Subscriber2 received message {}",message.body());
      });
    }
  }

}
