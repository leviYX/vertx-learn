package com.levi.vertx_stock_broker.assets;

import com.github.javafaker.Faker;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  private static final Faker faker = new Faker();

  public static void attach(Router parent){
    parent.get("/test").handler(context -> {
      var response = new JsonArray();
      response.add(new Asset(faker.funnyName().name()));
      response.add(new Asset(faker.funnyName().name()));
      response.add(new Asset(faker.funnyName().name()));
      response.add(new Asset(faker.funnyName().name()));
      LOG.info("Attached assets API response: {}", response.encode());
      context.response().end(response.toBuffer());
    });
  }
}
