package com.levi.udemy.vertx_starter.eventLoops;


import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class EventLoopExample extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(EventLoopExample.class);


  public static void main(String[] args) {
    var vertx = Vertx.vertx(new VertxOptions()
      .setMaxEventLoopExecuteTime(2)
      .setMaxEventLoopExecuteTimeUnit(TimeUnit.SECONDS)
      .setBlockedThreadCheckInterval(1)
      .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS)
      .setEventLoopPoolSize(2));

    vertx.deployVerticle(EventLoopExample.class.getName(),new DeploymentOptions().setInstances(4));
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.info("Starting EventLoopExample");
    startPromise.complete();
    // 永远不要阻塞事件循环线程
    //TimeUnit.SECONDS.sleep(5);
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }
}
