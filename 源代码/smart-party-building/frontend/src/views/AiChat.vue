<template>
  <div class="ai-chat-page">
    <div class="page-header">
      <h2>AI党建问答助手</h2>
      <div class="header-info" v-if="aiStatus">
        <el-tag :type="aiStatus.ready ? 'success' : 'warning'">
          {{ aiStatus.ready ? 'AI服务已就绪' : 'AI服务初始化中...' }}
        </el-tag>
      </div>
    </div>

    <div class="chat-container">
      <div class="chat-messages" ref="chatContainerRef">
        <div v-if="messages.length === 0" class="empty-state">
          <el-icon :size="60"><ChatDotRound /></el-icon>
          <p>您好！我是智慧党建AI助手</p>
          <p class="hint">您可以向我咨询党建相关的问题，例如：</p>
          <ul class="suggestions">
            <li @click="sendSuggestion('党员有哪些义务？')">党员有哪些义务？</li>
            <li @click="sendSuggestion('党章的内容有哪些？')">党章的内容有哪些？</li>
            <li @click="sendSuggestion('如何申请入党？')">如何申请入党？</li>
            <li @click="sendSuggestion('党费如何缴纳？')">党费如何缴纳？</li>
          </ul>
        </div>

        <div v-for="(msg, index) in messages" :key="index" :class="['message', msg.role]">
          <div class="message-avatar">
            <el-icon v-if="msg.role === 'user'" :size="24"><UserFilled /></el-icon>
            <el-icon v-else :size="24"><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-text">{{ msg.content }}</div>
            <div class="message-sources" v-if="msg.sources && msg.sources.length > 0">
              <div class="sources-title">参考来源：</div>
              <div v-for="(source, sIdx) in msg.sources" :key="sIdx" class="source-item">
                <span class="source-file">{{ source.file }}</span>
                <span class="source-page">第{{ source.page }}页</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="loading" class="message assistant">
          <div class="message-avatar">
            <el-icon :size="24"><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-text typing">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您的问题，按Enter发送，Shift+Enter换行"
          :disabled="loading || !aiStatus?.ready"
          @keydown.enter.exact.prevent="handleSend"
        />
        <el-button type="primary" @click="handleSend" :loading="loading" :disabled="!inputMessage.trim()">
          <el-icon><Promotion /></el-icon>
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ChatDotRound, UserFilled, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

interface Message {
  role: 'user' | 'assistant'
  content: string
  sources?: any[]
}

interface AiStatus {
  ready: boolean
  message: string
  aiUrl: string
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const loading = ref(false)
const aiStatus = ref<AiStatus | null>(null)
const chatContainerRef = ref<HTMLElement | null>(null)

const fetchAiStatus = async () => {
  try {
    const res = await axios.get('/api/ai/status')
    if (res.data.code === 200) {
      aiStatus.value = res.data.data
    }
  } catch (e) {
    aiStatus.value = { ready: false, message: 'AI服务连接失败', aiUrl: '' }
  }
}

const handleSend = async () => {
  const text = inputMessage.value.trim()
  if (!text) return
  if (!aiStatus.value?.ready) {
    ElMessage.warning('AI服务正在初始化，请稍后')
    return
  }

  messages.value.push({ role: 'user', content: text })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await axios.post('/api/ai/chat', { message: text })
    if (res.data.code === 200) {
      const data = res.data.data
      messages.value.push({
        role: 'assistant',
        content: data.answer || '抱歉，暂时无法回答您的问题',
        sources: data.sources || []
      })
    } else {
      messages.value.push({
        role: 'assistant',
        content: res.data.message || '抱歉，服务出现异常'
      })
    }
  } catch (e: any) {
    messages.value.push({
      role: 'assistant',
      content: e.response?.data?.message || e.message || '网络错误，请稍后重试'
    })
  } finally {
    loading.value = false
    await nextTick()
    scrollToBottom()
  }
}

const sendSuggestion = (text: string) => {
  inputMessage.value = text
  handleSend()
}

const scrollToBottom = () => {
  if (chatContainerRef.value) {
    chatContainerRef.value.scrollTop = chatContainerRef.value.scrollHeight
  }
}

onMounted(() => {
  fetchAiStatus()
})
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 140px);
  max-height: 800px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
  text-align: center;
}

.empty-state p {
  margin: 10px 0 5px;
  font-size: 16px;
}

.empty-state .hint {
  font-size: 14px;
  color: #c0c4cc;
}

.suggestions {
  list-style: none;
  padding: 0;
  margin-top: 15px;
}

.suggestions li {
  background: #fff;
  padding: 10px 20px;
  border-radius: 20px;
  margin: 8px 0;
  cursor: pointer;
  color: #409eff;
  transition: all 0.3s;
  border: 1px solid #409eff;
}

.suggestions li:hover {
  background: #409eff;
  color: #fff;
}

.message {
  display: flex;
  margin-bottom: 20px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message.user .message-content {
  background: #409eff;
  color: #fff;
  margin-right: 10px;
}

.message.assistant .message-content {
  background: #fff;
  margin-left: 10px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #409eff;
  color: #fff;
}

.message.assistant .message-avatar {
  background: #67c23a;
  color: #fff;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message-text {
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.message-sources {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
  font-size: 12px;
}

.message.user .message-sources {
  border-top-color: rgba(255, 255, 255, 0.3);
}

.sources-title {
  color: #909399;
  margin-bottom: 5px;
}

.message.user .sources-title {
  color: rgba(255, 255, 255, 0.8);
}

.source-item {
  display: flex;
  gap: 10px;
  margin: 3px 0;
}

.source-file {
  color: #409eff;
}

.message.user .source-file {
  color: rgba(255, 255, 255, 0.9);
}

.source-page {
  color: #c0c4cc;
}

.message.user .source-page {
  color: rgba(255, 255, 255, 0.7);
}

.typing {
  display: flex;
  align-items: center;
  gap: 4px;
}

.typing .dot {
  width: 8px;
  height: 8px;
  background: #909399;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-4px);
    opacity: 1;
  }
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #eee;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-button {
  height: auto;
  padding: 8px 20px;
}
</style>