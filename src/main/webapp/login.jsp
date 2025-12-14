<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>聊天室登录</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .login-container {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            width: 300px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        button {
            width: 100%;
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
        .error {
            color: red;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;
        }
        .user-list {
            margin-top: 20px;
            font-size: 12px;
            color: #666;
            background-color: #e9ecef;
            padding: 10px;
            border-radius: 4px;
        }
        .user-list ul {
            padding-left: 20px;
            margin: 5px 0 0 0;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2 style="text-align: center;">聊天室登录</h2>

    <%-- 显示错误信息 (如果 LoginServlet 设置了 error 属性) --%>
    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
    <div class="error"><%= error %></div>
    <%
        }
    %>

    <form action="login" method="post">
        <div class="form-group">
            <label for="username">用户名:</label>
            <input type="text" id="username" name="username" required autocomplete="off">
        </div>

        <div class="form-group">
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <button type="submit">登录</button>
    </form>

    <div class="user-list">
        <strong>可用测试账号:</strong>
        <ul>
            <li>admin / 123456</li>
            <li>user1 / password</li>
            <li>user2 / password</li>
            <li>user3 / password</li>
        </ul>
    </div>
</div>
</body>
</html>