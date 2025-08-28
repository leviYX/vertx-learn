package com.levi.domin;


import io.vertx.core.net.NetSocket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private NetSocket socket;
}