package com.levi.service;

import com.levi.domin.User;
import io.vertx.core.net.NetSocket;
import java.util.concurrent.ConcurrentHashMap;


import java.util.Set;

public class UserManager {
    // username -> User
    private final ConcurrentHashMap<String, User> onlineUsers = new ConcurrentHashMap<>();

    public void addUser(String username, NetSocket socket) {
        onlineUsers.put(username, new User(username, socket));
    }

    public void removeUser(String username) {
        onlineUsers.remove(username);
    }

    public NetSocket getUserSocket(String username) {
        User user = onlineUsers.get(username);
        return user == null ? null : user.getSocket();
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    // 调试用：列出在线用户
    public Set<String> getOnlineUsers() {
        return onlineUsers.keySet();
    }

    public ConcurrentHashMap<String, User> getOnlineUsersSet() {
        return onlineUsers;
    }
}