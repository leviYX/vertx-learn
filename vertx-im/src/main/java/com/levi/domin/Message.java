package com.levi.domin;

import io.vertx.core.json.JsonObject;
import lombok.Data;

@Data
public class Message {

    public enum Type { TEXT, IMAGE, FILE }

    private Type type;
    private String from;
    private String to;
    private String content; // 文本内容，或图片/文件的 base64 编码
    private String fileName; // 文件名（仅文件消息）
    private long timestamp;

    public Message(Type type, String from, String to, String content, String fileName) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
        this.fileName = fileName;
        this.timestamp = System.currentTimeMillis();
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("type", type.name())
                .put("from", from)
                .put("to", to)
                .put("content", content)
                .put("fileName", fileName)
                .put("timestamp", timestamp);
    }

    public static Message fromJson(JsonObject json) {
        Type type = Type.valueOf(json.getString("type"));
        String from = json.getString("from");
        String to = json.getString("to");
        String content = json.getString("content");
        String fileName = json.getString("fileName");
        return new Message(type, from, to, content, fileName);
    }
}
