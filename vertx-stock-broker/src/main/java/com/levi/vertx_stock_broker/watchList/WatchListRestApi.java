package com.levi.vertx_stock_broker.watchList;

import com.levi.vertx_stock_broker.assets.AssetsRestApi;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WatchListRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(WatchListRestApi.class);

  static final Map<UUID,WatchList> watchListPerAsset = new HashMap<UUID,WatchList>();

  static final String path = "/account/watchList/:accountId";

  public static void attach(Router parent){
    // 获取列表
    parent.get(path).handler(context -> {
      var accountIdParam = getAccountId(context);
      var watchList = Optional.ofNullable(watchListPerAsset.get(UUID.fromString(accountIdParam)));
      if(watchList.isEmpty()){
        context.response()
          .setStatusCode(HttpResponseStatus.NOT_FOUND.code())
          .end(new JsonObject().put("the accountId {} not found watchList ", accountIdParam).put("path",context.normalizedPath()).toBuffer());
        return;
      }
      context.response()
        .setStatusCode(HttpResponseStatus.OK.code())
        .putHeader("content-type", "application/json")
        .putHeader("my", "my")
        .end(watchList.get().toJsonObject().toBuffer());
    });

    // 创建列表
    parent.put(path).handler(context -> {
      var accountIdParam = getAccountId(context);
      watchListPerAsset.put(UUID.fromString(accountIdParam), new WatchList(AssetsRestApi.assets));
      context.response().setStatusCode(HttpResponseStatus.OK.code()).end();
    }).failureHandler(error -> {
      LOG.error("{}", error);
    });

    // 删除列表
    parent.delete(path).handler(context -> {
      var accountIdParam = getAccountId(context);
      WatchList remove = watchListPerAsset.remove(UUID.fromString(accountIdParam));
      context.response().setStatusCode(HttpResponseStatus.OK.code()).end(remove.toJsonObject().toBuffer());
    });
  }

  private static String getAccountId(RoutingContext context) {
    var accountIdParam = context.pathParam("accountId");
    LOG.info("{} for accountId: {}", context.normalizedPath(),accountIdParam);
    return accountIdParam;
  }
}
