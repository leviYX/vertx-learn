package com.levi.ws;

import com.levi.ws.handler.WebSocketHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle());
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.createHttpServer().webSocketHandler(new WebSocketHandler(vertx))
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        LOG.info("HTTP server started on port 8080");
                        startPromise.complete();
                    }else {
                        LOG.error("Failed to start HTTP server", result.cause());
                        startPromise.fail(result.cause());
                    }
                });
    }
}
