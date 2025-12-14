package com.example.controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// 拦截所有页面，但在内部排除登录页
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();

        // 放行静态资源(css/js)、登录页面、登录接口
        if (uri.endsWith("login.jsp") || uri.endsWith("login") || uri.contains("/static/")) {
            chain.doFilter(request, response);
            return;
        }

        // 检查 Session
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (isLoggedIn) {
            chain.doFilter(request, response);
        } else {
            // 如果是AJAX请求，返回401状态码
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                // 普通页面请求重定向到登录页
                response.sendRedirect("login.jsp");
            }
        }
    }
}