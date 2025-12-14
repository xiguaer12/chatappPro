package com.example.controller;

import com.example.entity.Message;
import com.example.entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import java.util.List;
import java.util.Map;

@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    // === 1. 监听登录 (当 username 被放入 Session 时触发) ===
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        // 只关心 "username" 属性的添加
        if ("username".equals(event.getName())) {
            String username = (String) event.getValue();
            HttpSession session = event.getSession();
            ServletContext context = session.getServletContext();

            // 发送一条系统消息
            sendSystemMessage(context, username + " 加入了聊天室");
            System.out.println("系统消息: " + username + " 上线");
        }
    }

    // === 2. 监听退出 (当 Session 超时或失效时触发) ===
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        String username = (String) session.getAttribute("username");

        if (username != null) {
            ServletContext context = session.getServletContext();

            // A. 从在线列表移除用户
            @SuppressWarnings("unchecked")
            Map<String, User> onlineUsers = (Map<String, User>) context.getAttribute("onlineUsers");
            if (onlineUsers != null) {
                onlineUsers.remove(username);
            }

            // B. 发送一条系统消息
            sendSystemMessage(context, username + " 离开了聊天室");
            System.out.println("系统消息: " + username + " 下线");
        }
    }

    // === 辅助方法：构建并保存系统消息 ===
    private void sendSystemMessage(ServletContext context, String content) {
        // 获取消息列表
        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) context.getAttribute("chatMessages");

        if (messages != null) {
            // 创建系统消息：
            // sender = "系统"
            // receiver = "all" (群发)
            // type = "system" (特殊类型)
            Message sysMsg = new Message("系统", "all", content, "system");

            messages.add(sysMsg);

            // 保持消息数量限制
            if (messages.size() > 500) {
                messages.remove(0);
            }
        }
    }
}