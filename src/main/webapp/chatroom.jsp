<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>在线聊天室</title>
    <link rel="stylesheet" type="text/css" href="css/style.css">
</head>
<body>
<input type="hidden" id="hiddenUsername" value="<%= session.getAttribute("username") %>">

<div class="header">
    <h2>WebChat</h2>
    <div>
        <%= session.getAttribute("username") %>
        <a href="logout" style="color: rgba(255,255,255,0.8); margin-left: 15px; text-decoration: none; font-size: 14px;">退出</a>
    </div>
</div>

<div class="container">
    <div class="sidebar">
        <!-- 新增：固定的群聊入口 -->
        <ul class="online-users" style="flex: 0 0 auto; border-bottom: 2px solid #dee2e6;">
            <li id="groupChatEntry" onclick="switchChat('all')" class="active">
                <span class="user-avatar" style="background-color: #007bff;">群</span>
                <span style="font-weight: bold;">群聊大厅</span>
            </li>
        </ul>

        <div class="user-count">在线用户 (<span id="onlineCount">0</span>)</div>
        <ul class="online-users" id="onlineUsersList">
            <!-- 用户列表动态生成 -->
        </ul>
    </div>

    <div class="main">
        <div id="chatTargetInfo">
            <span id="targetLabel">当前: 群聊</span>
        </div>

        <div class="chat-area" id="chatArea"></div>

        <div class="input-area">
            <input type="text" id="messageInput" placeholder="输入消息..." autocomplete="off">
            <button id="sendButton" onclick="sendMessage()">发送</button>
        </div>
    </div>
</div>
<script src="chat.js"></script>
</body>
</html>