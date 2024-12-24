package com.levi.vertx_stock_broker.config;

import com.levi.vertx_stock_broker.constant.ConfigConstant;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.Objects;

@Builder
@Value
@ToString
public class BrokerConfig {
  int serverPort;
  String version;

  public static BrokerConfig from(final JsonObject config){
    Integer serverPort = config.getInteger(ConfigConstant.SERVER_PORT);
    if (Objects.isNull(serverPort)){
      throw new RuntimeException("env Server port is not configured");
    }
    String version = config.getString(ConfigConstant.VERSION);
    if (Objects.isNull(version)){
      throw new RuntimeException("yaml Version is not configured");
    }
    return BrokerConfig.builder()
      .serverPort(serverPort)
      .version(version)
      .build();
  }
}
