package com.levi.vertx_stock_broker.config;

import com.levi.vertx_stock_broker.constant.ConfigConstant;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigLoader {

  final static Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

  static final List<String> EXPOSED_ENVIRONMENTS_VARIABLES = List.of(ConfigConstant.SERVER_PORT);

  public static Future<BrokerConfig> load(Vertx vertx){
    // 初始化一个暴露的环境变量数组，这里用的是环境变量的key。用的json数组
    final var exposeKeys = new JsonArray();
    EXPOSED_ENVIRONMENTS_VARIABLES.forEach(exposeKeys::add);
    logger.info("Loading config from {}", exposeKeys.encode());
    // 创建一个环境变量的存储配置
    var envStore = new ConfigStoreOptions()
      // 设置存储类型为环境变量，这个存储配置就是读取环境变量的配置
      .setType("env")
      // 配置暴露出来的环境变量，这里put的其他地方就可以获取到，没有put的就获取不到
      .setConfig(new JsonObject().put("keys",exposeKeys));
    // 创建一个系统属性存储配置
    var sysStore = new ConfigStoreOptions()
      .setType("sys")
      .setConfig(new JsonObject().put("cache", Boolean.FALSE));

    var yamlStore = new ConfigStoreOptions()
      .setType("file")
      .setFormat("yaml")
      .setConfig(new JsonObject().put("path", "application.yml"));
    // 创建一个配置检索器
    var retriever = ConfigRetriever.create(vertx,
      // 这里可以添加多个evenStore，这里只添加了一个环境变量的存储配置,注意有覆盖顺序
      new ConfigRetrieverOptions()
        .addStore(yamlStore)
        .addStore(envStore)
        // 覆盖默认的存储配置，这里用的是系统属性存储配置
        .addStore(sysStore));

    // retriever.getConfig()是future，返回的就是上面配置仓库读取到的配置，然后做map转换变为BrokerConfig
    return retriever.getConfig().map(BrokerConfig::from);
  }
}
