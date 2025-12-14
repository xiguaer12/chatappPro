package com.example.controller;

import com.example.entity.Message;
import com.example.entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@WebListener
public class ChatContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        // 1. 初始化在线用户Map (线程安全)
        context.setAttribute("onlineUsers", new ConcurrentHashMap<String, User>());

        // 2. 初始化消息列表 (线程安全)
        context.setAttribute("chatMessages", new CopyOnWriteArrayList<Message>());

        // 3. 初始化用户数据库 (模拟)
        Map<String, String> userDatabase = new HashMap<>();
        userDatabase.put("admin", "123456");
        userDatabase.put("user1", "password");
        userDatabase.put("user2", "password");
        userDatabase.put("user3", "password");
        context.setAttribute("userDatabase", userDatabase);

        System.out.println("聊天室系统初始化完成...");
    }
}