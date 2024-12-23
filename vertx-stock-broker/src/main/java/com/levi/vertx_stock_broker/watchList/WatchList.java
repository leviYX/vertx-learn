package com.levi.vertx_stock_broker.watchList;

import com.levi.vertx_stock_broker.assets.Asset;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.List;

@Value
public class WatchList{

  List<Asset> asset;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
