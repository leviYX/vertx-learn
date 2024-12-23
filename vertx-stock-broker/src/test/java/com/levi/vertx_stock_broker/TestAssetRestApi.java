package com.levi.vertx_stock_broker;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestAssetRestApi {

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new MainVerticle()).onComplete(testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    var webClient = WebClient.create(vertx,new WebClientOptions().setDefaultPort(8080));
    webClient.get("/assets").send()
      .onComplete(testContext.succeeding(response ->{
        JsonArray objects = response.bodyAsJsonArray();
        // 做一些检查
        assert objects != null;
      }));
    testContext.completeNow();
  }
}
