package com.levi;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class ChatClient {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClient client = vertx.createNetClient();

        try {
            client.connect(1234, "localhost", res -> {
                if (res.succeeded()) {
                    NetSocket socket = res.result();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                    // 登录
                    System.out.print("Username: ");
                    String username = null;
                    try {
                        username = reader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.print("Password: ");
                    String password = null;
                    try {
                        password = reader.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    JsonObject loginRequest = new JsonObject()
                            .put("action", "login")
                            .put("username", username)
                            .put("password", password);

                    socket.write(loginRequest.encode());

                    // 处理服务器响应
                    socket.handler(buffer -> {
                        JsonObject response = new JsonObject(buffer.toString());
                        if ("success".equals(response.getString("status"))) {
                            String token = response.getString("token");
                            System.out.println("Login successful! Token: " + token);

                            // 启动消息监听线程
                            new Thread(() -> {
                                try {
                                    while (true) {
                                        String input = reader.readLine();
                                        if (input.startsWith("/exit")) {
                                            socket.close();
                                            vertx.close();
                                            System.exit(0);
                                        } else if (input.startsWith("/send ")) {
                                            String[] parts = input.split(" ", 3);
                                            String to = parts[1];
                                            String content = parts[2];

                                            JsonObject message = new JsonObject()
                                                    .put("action", "chat")
                                                    .put("token", token)
                                                    .put("to", to)
                                                    .put("type", "TEXT")
                                                    .put("content", content);

                                            socket.write(message.encode());
                                        } else if (input.startsWith("/sendfile ")) {
                                            String[] parts = input.split(" ", 3);
                                            String to = parts[1];
                                            String filePath = parts[2];
                                            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                                            String encoded = Base64.getEncoder().encodeToString(fileBytes);

                                            JsonObject message = new JsonObject()
                                                    .put("action", "chat")
                                                    .put("token", token)
                                                    .put("to", to)
                                                    .put("type", "FILE")
                                                    .put("content", encoded)
                                                    .put("fileName", Paths.get(filePath).getFileName().toString());

                                            socket.write(message.encode());
                                        } else if (input.startsWith("/sendimage ")) {
                                            String[] parts = input.split(" ", 3);
                                            String to = parts[1];
                                            String filePath = parts[2];
                                            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
                                            String encoded = Base64.getEncoder().encodeToString(fileBytes);

                                            JsonObject message = new JsonObject()
                                                    .put("action", "chat")
                                                    .put("token", token)
                                                    .put("to", to)
                                                    .put("type", "IMAGE")
                                                    .put("content", encoded)
                                                    .put("fileName", Paths.get(filePath).getFileName().toString());

                                            socket.write(message.encode());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();

                            // 处理接收消息
                            socket.handler(buffer1 -> {
                                JsonObject msg = new JsonObject(buffer1.toString());
                                System.out.println("[" + msg.getString("from") + "] " + msg.getString("content"));
                            });

                        } else {
                            System.out.println("Login failed: " + response.getString("message"));
                            socket.close();
                        }
                    });
                } else {
                    System.err.println("Failed to connect to server: " + res.cause());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}