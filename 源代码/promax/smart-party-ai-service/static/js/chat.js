/**
 * 智慧党建AI知识助手 - 聊天交互脚本
 */

const questionInput = document.getElementById('questionInput');
const sendBtn = document.getElementById('sendBtn');
const messagesContainer = document.getElementById('messages');
const emptyState = document.getElementById('emptyState');

let isLoading = false;

/**
 * 发送示例问题
 */
function sendExample(text) {
    questionInput.value = text;
    sendMessage();
}

/**
 * 发送消息
 */
async function sendMessage() {
    const message = questionInput.value.trim();
    if (!message || isLoading) return;

    isLoading = true;
    questionInput.value = '';
    sendBtn.disabled = true;

    // 隐藏空状态
    if (emptyState) {
        emptyState.style.display = 'none';
    }

    // 添加用户消息
    appendUserMessage(message);

    // 添加AI加载状态
    const loadingId = appendLoadingMessage();

    // 滚动到底部
    scrollToBottom();

    try {
        const response = await fetch('/api/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ message: message })
        });

        const data = await response.json();

        // 移除加载状态
        removeMessage(loadingId);

        // 添加AI回复
        appendAiMessage(data.answer, data.sources);

    } catch (error) {
        // 移除加载状态
        removeMessage(loadingId);

        // 添加错误消息
        appendErrorMessage('服务异常，请稍后重试。');
    } finally {
        isLoading = false;
        sendBtn.disabled = false;
        questionInput.focus();
        scrollToBottom();
    }
}

/**
 * 添加用户消息
 */
function appendUserMessage(text) {
    const wrapper = document.createElement('div');
    wrapper.style.cssText = 'display:flex; justify-content:flex-end; margin-bottom:16px;';

    const msgDiv = document.createElement('div');
    msgDiv.style.cssText = 'display:flex; flex-direction:row-reverse; align-items:flex-start; gap:10px; max-width:80%;';

    const bubble = document.createElement('div');
    bubble.style.cssText = 'background:#C62828; color:#fff; padding:12px 16px; border-radius:16px 16px 4px 16px; font-size:15px; line-height:1.6; word-break:break-word;';
    bubble.textContent = text;

    const avatar = document.createElement('div');
    avatar.style.cssText = 'width:36px; height:36px; border-radius:50%; background:#C62828; color:#fff; display:flex; align-items:center; justify-content:center; font-size:14px; font-weight:500; flex-shrink:0;';
    avatar.textContent = '我';

    msgDiv.appendChild(bubble);
    msgDiv.appendChild(avatar);
    wrapper.appendChild(msgDiv);
    messagesContainer.appendChild(wrapper);
}

/**
 * 添加AI消息
 */
function appendAiMessage(text, sources) {
    const wrapper = document.createElement('div');
    wrapper.style.cssText = 'display:flex; justify-content:flex-start; margin-bottom:16px;';

    const msgDiv = document.createElement('div');
    msgDiv.style.cssText = 'display:flex; flex-direction:row; align-items:flex-start; gap:10px; max-width:80%;';

    const avatar = document.createElement('div');
    avatar.style.cssText = 'width:36px; height:36px; border-radius:50%; background:#FDE8E8; display:flex; align-items:center; justify-content:center; flex-shrink:0; overflow:hidden;';
    avatar.innerHTML = '<img src="/static/images/ai-avatar.svg" alt="AI助手" width="36" height="36">';

    const bubble = document.createElement('div');
    bubble.style.cssText = 'background:#fff; color:#1A1A1A; padding:14px 18px; border-radius:4px 16px 16px 16px; font-size:15px; line-height:1.7; word-break:break-word; box-shadow:0 1px 3px rgba(0,0,0,0.06); border:1px solid #E8E8E8;';

    let contentHtml = escapeHtml(text).replace(/\n/g, '<br>');

    if (sources && sources.length > 0) {
        const tags = sources.map(s =>
            `<span style="display:inline-block; background:#F3F4F6; color:#666; font-size:12px; padding:4px 10px; border-radius:6px; border:1px solid #E8E8E8; margin:3px 6px 3px 0;">${escapeHtml(s.file)}（第${s.page}页）</span>`
        ).join('');
        contentHtml += `
            <div style="margin-top:10px; padding-top:10px; border-top:1px dashed #E8E8E8;">
                <div style="font-size:12px; color:#999; margin-bottom:6px;">参考来源</div>
                <div style="display:flex; flex-wrap:wrap; gap:6px;">${tags}</div>
            </div>
        `;
    }

    bubble.innerHTML = contentHtml;

    msgDiv.appendChild(avatar);
    msgDiv.appendChild(bubble);
    wrapper.appendChild(msgDiv);
    messagesContainer.appendChild(wrapper);
}

/**
 * 添加加载状态消息
 */
function appendLoadingMessage() {
    const id = 'loading-' + Date.now();
    const wrapper = document.createElement('div');
    wrapper.id = id;
    wrapper.style.cssText = 'display:flex; justify-content:flex-start; margin-bottom:16px;';

    const msgDiv = document.createElement('div');
    msgDiv.style.cssText = 'display:flex; flex-direction:row; align-items:flex-start; gap:10px;';

    const avatar = document.createElement('div');
    avatar.style.cssText = 'width:36px; height:36px; border-radius:50%; background:#FDE8E8; display:flex; align-items:center; justify-content:center; flex-shrink:0; overflow:hidden;';
    avatar.innerHTML = '<img src="/static/images/ai-avatar.svg" alt="AI助手" width="36" height="36">';

    const bubble = document.createElement('div');
    bubble.style.cssText = 'background:#fff; padding:14px 18px; border-radius:4px 16px 16px 16px; box-shadow:0 1px 3px rgba(0,0,0,0.06); border:1px solid #E8E8E8;';
    bubble.innerHTML = `
        <div style="display:flex; gap:4px; align-items:center; padding:6px 0;">
            <span style="width:8px; height:8px; background:#999; border-radius:50%; animation:bounce 1.4s infinite ease-in-out both;"></span>
            <span style="width:8px; height:8px; background:#999; border-radius:50%; animation:bounce 1.4s infinite ease-in-out both; animation-delay:-0.16s;"></span>
            <span style="width:8px; height:8px; background:#999; border-radius:50%; animation:bounce 1.4s infinite ease-in-out both; animation-delay:-0.32s;"></span>
        </div>
    `;

    msgDiv.appendChild(avatar);
    msgDiv.appendChild(bubble);
    wrapper.appendChild(msgDiv);
    messagesContainer.appendChild(wrapper);

    return id;
}

/**
 * 添加错误消息
 */
function appendErrorMessage(text) {
    const wrapper = document.createElement('div');
    wrapper.style.cssText = 'display:flex; justify-content:flex-start; margin-bottom:16px;';

    const msgDiv = document.createElement('div');
    msgDiv.style.cssText = 'display:flex; flex-direction:row; align-items:flex-start; gap:10px;';

    const avatar = document.createElement('div');
    avatar.style.cssText = 'width:36px; height:36px; border-radius:50%; background:#FDE8E8; display:flex; align-items:center; justify-content:center; flex-shrink:0; overflow:hidden;';
    avatar.innerHTML = '<img src="/static/images/ai-avatar.svg" alt="AI助手" width="36" height="36">';

    const bubble = document.createElement('div');
    bubble.style.cssText = 'background:#fff; padding:14px 18px; border-radius:4px 16px 16px 16px; box-shadow:0 1px 3px rgba(0,0,0,0.06); border:1px solid #E8E8E8;';
    bubble.innerHTML = `<div style="color:#C62828; font-size:14px;">${escapeHtml(text)}</div>`;

    msgDiv.appendChild(avatar);
    msgDiv.appendChild(bubble);
    wrapper.appendChild(msgDiv);
    messagesContainer.appendChild(wrapper);
}

/**
 * 移除消息
 */
function removeMessage(id) {
    const el = document.getElementById(id);
    if (el) el.remove();
}

/**
 * 滚动到底部
 */
function scrollToBottom() {
    const main = document.querySelector('.main');
    if (main) {
        main.scrollTo({
            top: main.scrollHeight,
            behavior: 'smooth'
        });
    }
}

/**
 * 转义HTML
 */
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

/* ============================================
   Event Listeners
   ============================================ */

// 回车发送
questionInput.addEventListener('keydown', function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

// 自动聚焦
window.addEventListener('load', function() {
    questionInput.focus();
});