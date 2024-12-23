package com.levi.vertx_stock_broker;

import com.github.javafaker.Faker;
import com.levi.vertx_stock_broker.assets.AssetsRestApi;
import com.levi.vertx_stock_broker.quotes.QuotesRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.exceptionHandler(err -> LOG.error("Vertx exception", err));
    vertx.deployVerticle(new MainVerticle())
      .onComplete(res ->{
        if (res.failed()) {
          LOG.error("deployment failed", res.cause());
        }
      });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var restApi = Router.router(vertx);
    restApi.route().failureHandler(failureHandler());
    // 绑定两个路由API信息
    AssetsRestApi.attach(restApi);
    QuotesRestApi.attach(restApi);
    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(err -> LOG.error("Vertx exception", err))
      .listen(8888).onComplete(http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private static Handler<RoutingContext> failureHandler() {
    return context -> {
      if (context.response().ended()) {
        // Already handled 正常结束了，不需要再次处理
        return;
      } else {
        context.response()
          .setStatusCode(500)
          .end(
            new JsonObject()
              .put("code", 500)
              .put("message", context.failure())
              .toBuffer()
          );
      }
    };
  }
}
