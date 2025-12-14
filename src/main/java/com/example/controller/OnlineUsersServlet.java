package com.example.controller;

import com.example.entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/onlineUsers")
public class OnlineUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();

        // 获取 Listener 初始化好的 Map
        @SuppressWarnings("unchecked")
        Map<String, User> onlineUsers = (Map<String, User>) context.getAttribute("onlineUsers");

        response.setContentType("application/json");
        // EncodingFilter 已经设置了编码，这里其实可以省略，但加上也无妨
        response.setCharacterEncoding("UTF-8");

        // 手动拼接 JSON (也可以使用 Jackson/Gson 库)
        if (onlineUsers != null) {
            StringBuilder json = new StringBuilder("{\"count\":").append(onlineUsers.size()).append(",\"users\":[");
            int i = 0;
            for (String username : onlineUsers.keySet()) {
                if (i++ > 0) json.append(",");
                json.append("\"").append(username).append("\"");
            }
            json.append("]}");
            response.getWriter().write(json.toString());
        } else {
            response.getWriter().write("{\"count\":0,\"users\":[]}");
        }
    }
}