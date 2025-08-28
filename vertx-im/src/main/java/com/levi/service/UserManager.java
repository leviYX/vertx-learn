package com.levi.service;

import com.levi.domin.User;
import io.vertx.core.net.NetSocket;
import java.util.concurrent.ConcurrentHashMap;

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

    public ConcurrentHashMap<String, User> getOnlineUsersSet() {
        return onlineUsers;
    }
}