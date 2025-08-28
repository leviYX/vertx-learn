package com.levi.verticle;

import com.levi.domin.Message;
import com.levi.service.AuthService;
import com.levi.service.UserManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

public class ChatServerVerticle extends AbstractVerticle {

    private static final String CHAT_ACTION = "chat";
    private static final String LOGIN_ACTION = "login";
    // 50MB
    private static final int NET_SERVER_MAX_BUFFER_SIZE = 50 * 1024 * 1024;

    private final AuthService authService;
    private final UserManager userManager = new UserManager();

    public ChatServerVerticle(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void start() {
        NetServer server = vertx.createNetServer(new NetServerOptions().setReceiveBufferSize(NET_SERVER_MAX_BUFFER_SIZE));
        server.connectHandler(socket -> {
            // 用一个局部变量保存登录后的用户名
            final String[] holder = new String[1];
            // 处理认证
            socket.handler(buf -> {
                JsonObject msg = new JsonObject(buf.toString());
                String action = msg.getString("action");
                if (LOGIN_ACTION.equals(action)) {
                    handleLogin(socket, msg);
                } else if (CHAT_ACTION.equals(action)) {
                    // 其余任何消息都必须带 token，先鉴权
                    String token = msg.getString("token");
                    authService.authenticate(token)
                            .onSuccess(username -> {
                                // 记录登录身份
                                holder[0] = username;
                                // 发起聊天
                                handleChat(socket, username, msg);
                            })
                            .onFailure(err -> {
                                socket.write(new JsonObject()
                                        .put("status","error")
                                        .put("code",401)
                                        .put("message","Unauthorized").encode());
                                socket.close();
                            });
                }
            }).closeHandler(closeEvent -> {
                // 断开连接时清理用户,直接根据 socket 反查用户名并移除
                if(holder[0] != null){
                    userManager.removeUser(holder[0]);
                }
            });
        }).listen(1234, res -> {
            if (res.succeeded()) {
                System.out.println("Chat server started on port 1234");
            } else {
                System.err.println("Failed to start chat server: " + res.cause());
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
                    socket.write(new JsonObject()
                            .put("status","success")
                            .put("token",token).encode());
                })
                .onFailure(err -> {
                    socket.write(new JsonObject()
                            .put("status","error")
                            .put("message",err.getMessage()).encode());
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