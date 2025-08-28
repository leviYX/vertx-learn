package com.levi;

import com.levi.service.AuthService;
import com.levi.verticle.ChatServerVerticle;
import io.vertx.core.Vertx;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        AuthService authService = new AuthService(vertx);
        vertx.deployVerticle(new ChatServerVerticle(authService));
    }
}