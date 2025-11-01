package com.levi.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class BlockingVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new BlockingVerticle());
    }

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(5000,id -> {
            while (true){}
        });
    }
}
