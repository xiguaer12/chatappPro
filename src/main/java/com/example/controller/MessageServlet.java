package com.example.controller;

import com.example.entity.Message;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/message")
public class MessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");

        String content = request.getParameter("content");
        String type = request.getParameter("type");
        // 获取接收者，如果为空或 "all" 则视为群发
        String receiver = request.getParameter("receiver");
        if ("all".equals(receiver) || "".equals(receiver)) {
            receiver = null;
        }

        if (content != null && !content.trim().isEmpty()) {
            Message message = new Message(username, receiver, content.trim(), type);

            ServletContext context = getServletContext();
            @SuppressWarnings("unchecked")
            List<Message> messages = (List<Message>) context.getAttribute("chatMessages");

            messages.add(message);
            // 限制消息数量
            if (messages.size() > 500) messages.remove(0);

            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"success\"}");
        } else {
            response.setStatus(400);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String currentUser = (String) session.getAttribute("username");

        ServletContext context = getServletContext();
        @SuppressWarnings("unchecked")
        List<Message> allMessages = (List<Message>) context.getAttribute("chatMessages");
        if (allMessages == null) allMessages = new ArrayList<>();

        String sinceId = request.getParameter("since");
        // 获取当前聊天对象（"all" 代表群聊，其他代表私聊对象）
        String chatPartner = request.getParameter("chatPartner");
        if (chatPartner == null) chatPartner = "all";

        List<Message> result = new ArrayList<>();
        boolean startCollecting = (sinceId == null || "".equals(sinceId));

        for (Message m : allMessages) {
            // --- 核心筛选逻辑开始 ---
            boolean isMatch = false;

            if ("all".equals(chatPartner)) {
                // 1. 群聊模式：只显示接收者是 null 或 "all" 的消息
                // 绝对不显示私聊消息
                if (m.getReceiver() == null || "all".equals(m.getReceiver())) {
                    isMatch = true;
                }
            } else {
                // 2. 私聊模式：只显示 (我发给他的) 或 (他发给我的)
                // 绝对不显示群聊消息，也不显示和其他人的私聊
                boolean fromMeToHim = m.getUsername().equals(currentUser) && chatPartner.equals(m.getReceiver());
                boolean fromHimToMe = m.getUsername().equals(chatPartner) && currentUser.equals(m.getReceiver());

                if (fromMeToHim || fromHimToMe) {
                    isMatch = true;
                }
            }
            // --- 核心筛选逻辑结束 ---

            if (!isMatch) continue;

            if (startCollecting) {
                result.add(m);
            } else if (m.getId().equals(sinceId)) {
                startCollecting = true;
            }
        }

        // 如果是首次加载(没有sinceId)，只返回最后50条
        if ((sinceId == null || "".equals(sinceId)) && result.size() > 50) {
            result = result.subList(result.size() - 50, result.size());
        }

        response.setContentType("application/json");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < result.size(); i++) {
            if (i > 0) json.append(",");
            json.append(result.get(i).toJson());
        }
        json.append("]");
        response.getWriter().write(json.toString());
    }
}