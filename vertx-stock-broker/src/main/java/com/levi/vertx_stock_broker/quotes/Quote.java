package com.levi.vertx_stock_broker.quotes;

import com.levi.vertx_stock_broker.assets.Asset;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;

public record Quote(Asset asset, BigDecimal bid,
                    BigDecimal ask, BigDecimal lastPrice,
                    BigDecimal volume) {

  public JsonObject toJsonObject() {
    return new JsonObject()
      .put("asset", asset).put("bid", bid).put("ask", ask)
      .put("lastPrice", lastPrice).put("volume", volume);
  }
}
