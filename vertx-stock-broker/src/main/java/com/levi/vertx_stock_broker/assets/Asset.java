package com.levi.vertx_stock_broker.assets;

import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private String name;

    public JsonObject toJson() {
      return JsonObject.mapFrom(this);
    }
}
