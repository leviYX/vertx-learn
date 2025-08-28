package com.levi.verticle;

import com.levi.domin.Message;
import com.levi.service.AuthService;
import com.levi.service.UserManager;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

public class ChatServerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ChatServerVerticle.class);
    private static final String CHAT_ACTION = "chat";
    private static final String LOGIN_ACTION = "login";
    private static final int NET_SERVER_MAX_BUFFER_SIZE = 50 * 1024 * 1024; // 50MB
    private final static int PORT = 1234;

    private final AuthService authService;
    private final UserManager userManager = new UserManager();

    public ChatServerVerticle(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void start() {
        NetServer server = vertx.createNetServer(new NetServerOptions().setReceiveBufferSize(NET_SERVER_MAX_BUFFER_SIZE));
        server.connectHandler(socket -> {
            // 登录后的用户名
            final var holder = new String[1];
            socket.handler(buf -> {
                var msg = new JsonObject(buf.toString());
                // 登录 or 聊天
                var action = msg.getString("action");
                if (LOGIN_ACTION.equals(action)) {
                    handleLogin(socket, msg);
                } else if (CHAT_ACTION.equals(action)) {
                    // 登录之外任何消息都必须带 token，先鉴权
                    var token = msg.getString("token");
                    authService.authenticate(token)
                            .onSuccess(username -> {
                                holder[0] = username;
                                handleChat(socket, username, msg);
                            })
                            .onFailure(err -> {
                                socket.write(
                                        new JsonObject()
                                                .put("status","error")
                                                .put("code", HttpResponseStatus.UNAUTHORIZED.code())
                                                .put("message",HttpResponseStatus.UNAUTHORIZED.reasonPhrase()).encode()
                                );
                                socket.close();
                            });
                }
            }).closeHandler(closeEvent -> {
                // 断开连接时清理用户
                if(holder[0] != null) userManager.removeUser(holder[0]);
            });
        }).listen(PORT, res -> {
            if (res.succeeded()) {
                LOG.info("服务端成功启动，并且监听在端口:" + PORT);
            } else {
                LOG.error("服务端启动失败，错误信息:{}", res.cause());
            }
        });
    }

    /**
     * 处理登录
     *
     * @param socket 客户端 socket
     * @param msg 登录请求
     */
    private void handleLogin(NetSocket socket, JsonObject msg){
        var username = msg.getString("username");
        var password = msg.getString("password");
        authService.login(username, password)
                .onSuccess(token -> {
                    userManager.addUser(username, socket);
                    socket.write(new JsonObject().put("status","success").put("token",token).encode());
                })
                .onFailure(err -> {
                    socket.write(new JsonObject().put("status","error").put("message",err.getMessage()).encode());
                    socket.close();
                });
    }

    /**
     * 处理聊天消息
     *
     * @param socket 客户端 socket
     * @param username 登录用户名
     * @param chatRequest 聊天请求
     */
    private void handleChat(NetSocket socket, String username, JsonObject chatRequest) {
        var to = chatRequest.getString("to");
        var type = chatRequest.getString("type");
        var content = chatRequest.getString("content");
        var fileName = chatRequest.getString("fileName");
        var message = new Message(Message.Type.valueOf(type), username, to, content, fileName);

        if (userManager.isUserOnline(to)) {
            NetSocket targetSocket = userManager.getUserSocket(to);
            targetSocket.write(message.toJson().encode());
        } else {
            socket.write(new JsonObject().put("status", "error").put("message", "User offline").encode());
        }
    }
}