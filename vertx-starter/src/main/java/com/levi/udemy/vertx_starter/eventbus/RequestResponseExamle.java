package com.levi.udemy.vertx_starter.eventbus;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseExamle{

  public static final String ADDRESS = "my.request.response";

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestVerticle());
    vertx.deployVerticle(new ResponseVerticle());
  }

  static class RequestVerticle extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(RequestVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().request(ADDRESS,"hello this is a request info from request verticle",reply->{
        if(reply.succeeded()){
          logger.info("reply received {}", reply.result().body());
        }else {
          logger.error("reply failed {}", reply.cause());
        }
      });
    }
  }

  static class ResponseVerticle extends AbstractVerticle {

    final static Logger logger = LoggerFactory.getLogger(ResponseVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().consumer(ADDRESS, message->{
        logger.info("received message from request {}", message.body());
        message.reply("hello this is a response from response verticle");
      });
    }
  }

}
