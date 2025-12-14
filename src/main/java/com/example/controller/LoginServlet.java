package com.example.controller;

import com.example.entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 直接跳转到登录页面
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        ServletContext context = getServletContext();

        // 1. 验证用户名和密码
        // 注意：这里从 Context 获取 Listener 初始化好的用户数据库
        if (isValidUser(context, username, password)) {

            // 2. 获取在线用户列表 (Listener 保证了它一定存在，不需要判空创建)
            @SuppressWarnings("unchecked")
            Map<String, User> onlineUsers = (Map<String, User>) context.getAttribute("onlineUsers");

            // 3. 检查是否重复登录
            if (onlineUsers.containsKey(username)) {
                request.setAttribute("error", "该用户已在线，请勿重复登录！");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }

            // 4. 登录成功处理
            User user = new User(username, password);

            // 加入在线列表
            onlineUsers.put(username, user);

            // 创建 Session
            HttpSession session = request.getSession();
            session.setAttribute("currentUser", user);
            session.setAttribute("username", username);

            // 设置 Session 超时时间 (30分钟)
            session.setMaxInactiveInterval(30 * 60);

            // 重定向到聊天室
            response.sendRedirect("chatroom.jsp");
        } else {
            request.setAttribute("error", "用户名或密码错误！");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private boolean isValidUser(ServletContext context, String username, String password) {
        if (username == null || password == null || username.trim().isEmpty()) {
            return false;
        }

        // 从 Context 获取用户数据库
        @SuppressWarnings("unchecked")
        Map<String, String> userDatabase = (Map<String, String>) context.getAttribute("userDatabase");

        if (userDatabase == null) return false; // 防御性编程

        String storedPassword = userDatabase.get(username.trim());
        return storedPassword != null && storedPassword.equals(password);
    }
}