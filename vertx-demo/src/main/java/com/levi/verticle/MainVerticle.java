package com.levi.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * 1、部署启动
 * 2、配置传递
 * 3、全局注入
 * 4、分布式扩展
 * 5、异步的调用，eventloop
 * 6、事件总线
 */
public class MainVerticle extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        DeploymentOptions options = new DeploymentOptions();

        JsonObject config = new JsonObject();
        config.put("es-client", new JsonObject().put("host", "localhost").put("port", 9200));
        options.setConfig(config);

        vertx.deployVerticle(new MainVerticle(),options);
    }

    @Override
    public void start() throws Exception {
        DeploymentOptions options = new DeploymentOptions();
        JsonObject config = new JsonObject();
        config.put("es-client", new JsonObject().put("host", "localhost").put("port", 9200));
        options.setConfig(config);
        vertx.deployVerticle(HelloVerticle.class.getName(),options);
        vertx.deployVerticle(HelloVerticle2.class.getName());
    }
}
