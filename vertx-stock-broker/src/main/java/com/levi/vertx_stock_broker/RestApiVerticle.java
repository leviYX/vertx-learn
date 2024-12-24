package com.levi.vertx_stock_broker;

import com.levi.vertx_stock_broker.assets.AssetsRestApi;
import com.levi.vertx_stock_broker.config.BrokerConfig;
import com.levi.vertx_stock_broker.config.ConfigLoader;
import com.levi.vertx_stock_broker.quotes.QuotesRestApi;
import com.levi.vertx_stock_broker.watchList.WatchListRestApi;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // 加载配置
    ConfigLoader.load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess(config -> {
        startHttpServer(startPromise,config);
      });
  }

  private void startHttpServer(Promise<Void> startPromise, BrokerConfig config) {
    LOG.info("this app yaml version is {}",config.getVersion());
    var restApi = Router.router(vertx);
    restApi.route().handler(BodyHandler.create()).failureHandler(failureHandler());
    // 绑定多个路由API信息
    AssetsRestApi.attach(restApi);
    QuotesRestApi.attach(restApi);
    WatchListRestApi.attach(restApi);
    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(err -> LOG.error("Vertx exception", err))
      .listen(config.getServerPort()).onComplete(http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port {}", config.getServerPort());
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
