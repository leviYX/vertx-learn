package com.levi.vertx_stock_broker.quotes;

import com.levi.vertx_stock_broker.assets.Asset;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class QuotesRestApi {
  private static final Logger LOG = LoggerFactory.getLogger(QuotesRestApi.class);

  public static void attach(Router parent){
    parent.get("/quotes/:asset").handler(context -> {
      final String assetParam = context.pathParam("asset");
      LOG.info("Asset param: {}", assetParam);
      Quote quote = new Quote(new Asset(assetParam), randomValue(), randomValue(), randomValue(), randomValue());
      var response = quote.toJsonObject();
      LOG.info("Path {} response with {}", context.normalizedPath(), response.encode());
      context.response().end(response.toBuffer());
    });
  }

  private static BigDecimal randomValue() {
    return BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1,100));
  }
}
