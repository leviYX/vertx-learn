package com.levi.ws;

import com.github.javafaker.Faker;
import com.levi.ws.factory.InstanceFactory;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceBroadcast {

    private static final Logger LOG = LoggerFactory.getLogger(PriceBroadcast.class);

    private static final Map<String, ServerWebSocket> connectionClients = new ConcurrentHashMap<>();

    public PriceBroadcast(Vertx vertx) {
        periodicUpdate(vertx);
    }

    private void periodicUpdate(Vertx vertx) {
        Faker faker = InstanceFactory.faker();
        String updatePrice = new JsonObject()
                .put("email", faker.internet().emailAddress())
                .put("price", faker.random().nextDouble())
                .toString();
        vertx.setPeriodic(Duration.ofSeconds(1).toMillis(), timerId -> {
            connectionClients.values().forEach(ws -> {
                LOG.info("send message to client:{}", ws.textHandlerID());
                ws.writeTextMessage(updatePrice);
            });
        });
    }

    public void register(ServerWebSocket webSocket) {
        LOG.info("register client:{}", webSocket.textHandlerID());
        connectionClients.put(webSocket.textHandlerID(), webSocket);
    }

    public void unregister(ServerWebSocket webSocket) {
        LOG.info("unRegister client:{}", webSocket.textHandlerID());
        connectionClients.remove(webSocket.textHandlerID());
    }

}
