package com.levi.udemy.vertx_starter.verticles;

import com.levi.udemy.vertx_starter.factory.FakeInstanceFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.UUID;

public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());

    vertx.deployVerticle(VerticleN.class.getName(),
      // 设置部署信息，比如实例数、配置信息等
      new DeploymentOptions()
        .setInstances(Runtime.getRuntime().availableProcessors())
        .setConfig(
          new JsonObject()
            .put("id", UUID.randomUUID().toString())
            .put("name", "main")
            .put("email", FakeInstanceFactory.getFaker().internet().emailAddress())
            .put("description", VerticleN.class.getSimpleName())
        ));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    logger.info("启动main verticle...");
    // 获取部署的时候setConfig的配置信息
    var config = config();
    System.out.println(config.toString());
    vertx.deployVerticle(new VerticleA(),deploy -> {
      if(deploy.succeeded()) {
        // 部署成功后2秒后卸载VerticleA
        vertx.setTimer(2000,id-> vertx.undeploy(deploy.result()));
      }
    });
    startPromise.complete();
  }

  @Override
  public void stop(Promise<Void> stopPromise) {
    logger.info("停止main verticle...");
    stopPromise.complete();
  }
}
