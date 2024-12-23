package com.levi.vertx_stock_broker.assets;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class GetAssetsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(GetAssetsHandler.class);

  public static List<Asset> assets = Arrays.asList(new Asset("Apple"), new Asset("Google"), new Asset("Facebook"));

  @Override
  public void handle(RoutingContext context) {
    var response = new JsonArray();
    assets.forEach(response::add);
    LOG.info("Attached assets API response: {}", response.encode());

    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("myheader", "myheadervalue")
      .end(response.toBuffer());
  }

}
