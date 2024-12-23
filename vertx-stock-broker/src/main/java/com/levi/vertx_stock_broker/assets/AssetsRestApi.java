package com.levi.vertx_stock_broker.assets;

import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class AssetsRestApi {

  private static final Logger LOG = LoggerFactory.getLogger(AssetsRestApi.class);

  public static List<Asset> assets = Arrays.asList(new Asset("Apple"), new Asset("Google"), new Asset("Facebook"));

  public static void attach(Router parent){
    parent.get("/assets").handler(new GetAssetsHandler());
  }
}
