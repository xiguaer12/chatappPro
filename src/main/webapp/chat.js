let currentUser = document.getElementById('hiddenUsername').value;
let lastMessageId = null;

// 核心变量：当前正在跟谁聊天（'all' 代表群聊）
let currentChatPartner = "all";

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}

// === 核心功能：切换聊天窗口 ===
function switchChat(targetUser) {
    if (targetUser === currentChatPartner) return; // 点击当前窗口不做反应

    // 1. 更新当前聊天对象
    currentChatPartner = targetUser;

    // 2. 清空现有消息界面
    document.getElementById('chatArea').innerHTML = '';
    lastMessageId = null; // 重置消息ID，以便拉取新窗口的历史记录

    // 3. 更新UI显示
    updateActiveUI();

    // 4. 立即拉取新窗口的历史消息
    loadMessages();

    // 5. 更新输入框提示
    const input = document.getElementById('messageInput');
    if (targetUser === 'all') {
        document.getElementById('targetLabel').innerText = '当前: 群聊';
        input.placeholder = '大家好...';
    } else {
        document.getElementById('targetLabel').innerText = '正在与 ' + targetUser + ' 私聊';
        input.placeholder = '私信给 ' + targetUser + '...';
    }
    input.focus();
}

function updateActiveUI() {
    // 处理群聊按钮高亮
    const groupEntry = document.getElementById('groupChatEntry');
    if (currentChatPartner === 'all') {
        groupEntry.classList.add('active');
    } else {
        groupEntry.classList.remove('active');
    }

    // 处理用户列表高亮
    const items = document.querySelectorAll('#onlineUsersList li');
    items.forEach(li => {
        if (li.dataset.user === currentChatPartner) {
            li.classList.add('active');
        } else {
            li.classList.remove('active');
        }
    });
}

// === 统一的消息加载函数 ===
function loadMessages() {
    // 构建URL，必须带上 chatPartner 参数
    let url = 'message?chatPartner=' + currentChatPartner;
    if (lastMessageId) {
        url += '&since=' + lastMessageId;
    }

    fetch(url)
        .then(response => response.json())
        .then(messages => {
            if (messages && messages.length > 0) {
                displayMessages(messages);
                lastMessageId = messages[messages.length - 1].id;
                scrollToBottom();
            }
        })
        .catch(err => console.error(err));
}

function displayMessages(messages) {
    const chatArea = document.getElementById('chatArea');
    messages.forEach(msg => {
        const div = document.createElement('div');

        // === 判断消息类型 ===
        if (msg.type === 'system') {
            // 1. 系统消息处理
            div.className = 'message system';
            // 系统消息只显示内容，不显示发送者和时间
            div.innerHTML = '<div class="message-content">' + escapeHtml(msg.content) + '</div>';

        } else if (msg.username === currentUser) {
            // 2. 我发的消息
            div.className = 'message self';
            div.innerHTML = '<div class="message-header">' + msg.timestamp + '</div>' +
                '<div class="message-content">' + escapeHtml(msg.content) + '</div>';
        } else {
            // 3. 别人发的消息
            div.className = 'message other';
            div.innerHTML = '<div class="message-header">' + msg.username + ' ' + msg.timestamp + '</div>' +
                '<div class="message-content">' + escapeHtml(msg.content) + '</div>';
        }
        chatArea.appendChild(div);
    });
}

function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    if (!content) return;

    const params = new URLSearchParams();
    params.append('content', content);
    // 发送给谁（'all' 或 具体用户名）
    params.append('receiver', currentChatPartner);

    fetch('message', {
        method: 'POST',
        body: params
    }).then(resp => resp.json())
        .then(res => {
            if(res.status === 'success') {
                input.value = '';
                // 发送成功后，立即刷新一下（虽然轮询也会刷，但这样更即时）
                loadMessages();
            }
        });
}

function updateOnlineUsers() {
    fetch('onlineUsers')
        .then(r => r.json())
        .then(data => {
            document.getElementById('onlineCount').textContent = data.count;
            const list = document.getElementById('onlineUsersList');
            list.innerHTML = ''; // 这里只是重绘用户列表，不影响聊天区域

            data.users.forEach(u => {
                if (u === currentUser) return; // 列表中不显示自己

                const li = document.createElement('li');
                li.dataset.user = u;
                li.onclick = () => switchChat(u); // 点击切换到与该用户的私聊

                li.innerHTML = '<span class="user-avatar">' + u.charAt(0).toUpperCase() + '</span>' + u;

                // 保持高亮状态
                if (u === currentChatPartner) li.classList.add('active');

                list.appendChild(li);
            });
        });
}

function scrollToBottom() {
    const chatArea = document.getElementById('chatArea');
    chatArea.scrollTop = chatArea.scrollHeight;
}

// 初始化
window.onload = function() {
    loadMessages();      // 加载默认的群聊消息
    updateOnlineUsers(); // 加载在线用户

    // 轮询
    setInterval(loadMessages, 1500);     // 每1.5秒拉取一次当前窗口的消息
    setInterval(updateOnlineUsers, 5000); // 每5秒更新一次在线列表
};

// 回车发送
document.getElementById('messageInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') sendMessage();
});