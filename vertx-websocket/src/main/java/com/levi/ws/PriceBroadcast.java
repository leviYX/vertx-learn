package com.levi.ws;

import com.levi.ws.group.GroupDomin;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PriceBroadcast {

    private static final Logger LOG = LoggerFactory.getLogger(PriceBroadcast.class);

    private static final Map<String, ServerWebSocket> connectionClients = new ConcurrentHashMap<>();

    private GroupDomin groupDomin;

    public PriceBroadcast(GroupDomin groupDomin) {
        this.groupDomin = groupDomin;
    }

    public void groupChat(String textHandlerID) {
        connectionClients.values().forEach(ws -> {
             if (!ws.textHandlerID().equals(textHandlerID)) {
                 ws.writeTextMessage(textHandlerID);
            }
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
