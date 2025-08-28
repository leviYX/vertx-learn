package com.levi.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private final JWTAuth jwt;
    // 仅做用户名+密码校验（内存 Map）
    private final Map<String,String> userStore = new ConcurrentHashMap<>();

    public AuthService(Vertx vertx) {
        jwt = JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setBuffer("keyboard cat")));   // 对称密钥
        userStore.put("alice","123456");
        userStore.put("bob","123456");
    }

    /** 登录：校验账号密码 -> 返回 JWT */
    public Future<String> login(String username,String password){
        if(!userStore.containsKey(username) || !userStore.get(username).equals(password)){
            return Future.failedFuture("Invalid credentials");
        }
        String token = jwt.generateToken(
                new JsonObject().put("sub", username),
                new JWTOptions().setExpiresInMinutes(60));
        return Future.succeededFuture(token);
    }

    /** 校验 token（返回用户名） */
    public Future<String> authenticate(String token){
        return jwt.authenticate(new JsonObject().put("token", token))
                .map(user -> user.principal().getString("sub"));
    }
}