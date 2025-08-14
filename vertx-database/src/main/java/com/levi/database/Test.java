package com.levi.database;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;

public class Test {

    public static void main(String[] args) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("127.0.0.1")
                .setDatabase("springai")
                .setUser("root")
                .setPassword("root");

        // 连接池选项
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        // 创建客户端池
        SqlClient client = MySQLPool.client(connectOptions, poolOptions);

        // 一个简单的查询
        client
                .query("SELECT * FROM users WHERE id='julien'")
                .execute(ar -> {
                    if (ar.succeeded()) {
                        RowSet<Row> result = ar.result();
                        System.out.println("Got " + result.size() + " rows ");
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }

                    // 现在关闭客户端池
                    client.close();
                });
    }
}
