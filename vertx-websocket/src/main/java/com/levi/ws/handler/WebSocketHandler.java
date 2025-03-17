package com.levi.ws.handler;

import com.levi.ws.PriceBroadcast;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebSocketHandler implements Handler<ServerWebSocket> {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);

    public static final String WEBSOCKET_PATH = "/ws/echo";

    private static final String CLOSE_MSG = "please close";

    private PriceBroadcast priceBroadcast;

    public WebSocketHandler(Vertx vertx) {
        this.priceBroadcast = new PriceBroadcast(vertx);
    }

    @Override
    public void handle(ServerWebSocket ws) {
        String wsPath = ws.path();
        String textHandlerID = ws.textHandlerID();
        if (!WEBSOCKET_PATH.equalsIgnoreCase(wsPath)) {
            LOG.info("WebSocket path is {},and only accept path is {}", wsPath,WEBSOCKET_PATH);
            ws.writeFinalTextFrame("wrong path and only accept path is "+WEBSOCKET_PATH+" ,please check your path");
            ws.close();
            return;
        }
        LOG.info("openning WebSocket {} is connected,path is {}", textHandlerID, wsPath);
        ws.accept();
        ws.frameHandler(webSocketFrameHandler(ws));
        ws.endHandler( onClose -> {
            LOG.info("WebSocket {} is  closed", textHandlerID);
            priceBroadcast.unregister(ws);
        });
        ws.exceptionHandler(err -> LOG.error( "websocket is fail" ,err));
        ws.writeTextMessage( "connected");
        priceBroadcast.register(ws);
    }

    private static Handler<WebSocketFrame> webSocketFrameHandler(ServerWebSocket ws) {
        String textHandlerID = ws.textHandlerID();
        return buffer -> {
            String msg = buffer.textData();
            if (CLOSE_MSG.equalsIgnoreCase(msg)) {
                LOG.info("WebSocket  {} closed", textHandlerID);
                ws.writeFinalTextFrame("WebSocket  " + textHandlerID + " closed");
                ws.close();
            } else {
                LOG.info("WebSocket {} received message: {}", textHandlerID, msg);
                ws.writeTextMessage("WebSocket " + textHandlerID + " received message: " + msg);
            }
        };
    }
}
