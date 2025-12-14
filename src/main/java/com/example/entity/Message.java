package com.example.entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Message {
    private String id;
    private String username; // 发送者
    private String receiver; // 接收者 (新增字段: null代表所有人)
    private String content;
    private Date timestamp;
    private String type;

    public Message(String username, String receiver, String content, String type) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.receiver = receiver; // 设置接收者
        this.content = content;
        this.timestamp = new Date();
        this.type = type != null ? type : "text";
    }

    // getter/setter for receiver
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    // ... 其他 getter/setter 保持不变 ...

    public String getId() { return id; }
    public String getUsername() { return username; }
    public Date getTimestamp() { return timestamp; }
    public String getType() { return type; }
    public String getContent() { return content; }

    public String toJson() {
        // 在JSON中包含 receiver 字段
        return String.format("{\"id\":\"%s\",\"username\":\"%s\",\"receiver\":\"%s\",\"content\":\"%s\",\"timestamp\":\"%s\",\"type\":\"%s\"}",
                id, username, (receiver == null ? "all" : receiver),
                content.replace("\"", "\\\""), // 简单的JSON转义
                new SimpleDateFormat("HH:mm:ss").format(timestamp), type);
    }
}