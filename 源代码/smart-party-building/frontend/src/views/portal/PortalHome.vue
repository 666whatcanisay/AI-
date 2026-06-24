<template>
  <div class="portal-home">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h2>欢迎回来，{{ memberInfo.name || userStore.username }}同志</h2>
        <p>坚定理想信念，践行初心使命</p>
      </div>
      <div class="welcome-decoration">
        <svg viewBox="0 0 200 200" class="decoration-icon">
          <path fill="rgba(255,255,255,0.15)" d="M100 20 L180 100 L100 180 L20 100 Z" />
          <path fill="rgba(255,255,255,0.1)" d="M100 50 L150 100 L100 150 L50 100 Z" />
        </svg>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card fee-card">
          <div class="stat-icon">
            <el-icon :size="32"><Wallet /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.totalFee || 0 }}<span class="stat-unit">元</span></div>
            <div class="stat-label">本年党费</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card activity-card">
          <div class="stat-icon">
            <el-icon :size="32"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.activityCount || 0 }}<span class="stat-unit">次</span></div>
            <div class="stat-label">参与活动</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card report-card">
          <div class="stat-icon">
            <el-icon :size="32"><Edit /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.reportCount || 0 }}<span class="stat-unit">篇</span></div>
            <div class="stat-label">思想汇报</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card volunteer-card">
          <div class="stat-icon">
            <el-icon :size="32"><Promotion /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ dashboard.volunteerHours || 0 }}<span class="stat-unit">小时</span></div>
            <div class="stat-label">志愿服务</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 考核信息 & 通知公告 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="8">
        <el-card class="evaluation-card">
          <template #header>
            <div class="card-header">
              <span>{{ currentYear }}年{{ currentMonth }}月考核</span>
            </div>
          </template>
          <div class="evaluation-content" v-if="evaluationData.grade">
            <div 
              class="evaluation-grade" 
              :class="'grade-' + evaluationData.grade"
              style="width: 150px; height: 150px; border-radius: 50%; display: flex; align-items: center; justify-content: center; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);"
            >
              <span class="grade-text" style="font-size: 36px; font-weight: bold; color: #fff;">{{ evaluationData.grade }}</span>
            </div>
            <div class="evaluation-info" v-if="evaluationData.comment">
              <p class="evaluation-comment">{{ evaluationData.comment }}</p>
              <p class="evaluation-time">{{ evaluationData.evaluateTime }}</p>
            </div>
          </div>
          <div class="evaluation-empty" v-else>
            <el-icon :size="32"><Warning /></el-icon>
            <p v-if="memberInfo.partyStatus === 'FORMAL'">本月暂无考核记录</p>
            <p v-else>暂无考核记录</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card class="notification-card">
          <template #header>
            <div class="card-header">
              <el-icon><Bell /></el-icon>
              <span>通知公告</span>
            </div>
          </template>
          <div class="notification-list" v-if="notifications.length">
            <div class="notification-scroll" :class="{ 'has-scroll': notifications.length > 3 }">
              <div class="notification-item" v-for="item in notifications" :key="item.id" @click="openNotificationDetail(item)">
                <div class="notification-dot"></div>
                <div class="notification-content">
                  <h4>{{ item.title }}</h4>
                  <p class="notification-desc">{{ item.content?.substring(0, 80) }}{{ item.content?.length > 80 ? '...' : '' }}</p>
                  <p class="notification-time">{{ item.publishTime }}</p>
                </div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无通知公告" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 通知详情弹窗 -->
    <Teleport to="body">
      <div v-if="showNotificationModal" class="notification-modal-overlay" @click.self="closeNotificationModal">
        <div class="notification-modal">
          <div class="modal-header">
            <h3>{{ selectedNotification?.title }}</h3>
            <button class="close-btn" @click="closeNotificationModal">
              <el-icon><Close /></el-icon>
            </button>
          </div>
          <div class="modal-body">
            <p class="modal-time">{{ selectedNotification?.publishTime }}</p>
            <div class="modal-content">{{ selectedNotification?.content }}</div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'
import { Wallet, Calendar, Edit, Promotion, Warning, Bell, Close } from '@element-plus/icons-vue'

const userStore = useUserStore()
const memberInfo = ref<any>({})
const dashboard = ref<any>({})
const evaluationData = ref<any>({})
const notifications = ref<any[]>([])
const currentYear = new Date().getFullYear()
const currentMonth = new Date().getMonth() + 1

const showNotificationModal = ref(false)
const selectedNotification = ref<any>(null)

const openNotificationDetail = (item: any) => {
  selectedNotification.value = item
  showNotificationModal.value = true
}

const closeNotificationModal = () => {
  showNotificationModal.value = false
  selectedNotification.value = null
}

const fetchData = async () => {
  const memberId = userStore.memberId
  const role = userStore.role

  if (role === 'applicant') {
    // 申请人：用姓名查申请信息
    try {
      const appRes = await axios.get('/api/portal/application', {
        params: { name: userStore.username }
      })
      if (appRes.data.code === 200 && appRes.data.data) {
        const app = appRes.data.data
        memberInfo.value = {
          id: app.id,
          name: app.name,
          partyStatus: app.status,
          branchName: app.department || '-',
          phone: app.phone || '-'
        }
      }
      // 申请人首页统计（简化）
      dashboard.value = {
        totalFee: 0,
        activityCount: 0,
        reportCount: 0,
        volunteerHours: 0,
        recentActivities: []
      }
    } catch (e) {
      console.error(e)
    }
    return
  }

  // 党员：用memberId获取数据
  if (!memberId) return

  try {
    const [infoResult, dashResult, evalResult] = await Promise.allSettled([
      axios.get(`/api/portal/info/${memberId}`),
      axios.get(`/api/portal/dashboard/${memberId}`),
      axios.get(`/api/evaluation/member/${memberId}`)
    ])
    if (infoResult.status === 'fulfilled' && infoResult.value.data.code === 200) {
      memberInfo.value = infoResult.value.data.data || {}
    }
    if (dashResult.status === 'fulfilled' && dashResult.value.data.code === 200) {
      dashboard.value = dashResult.value.data.data || {}
    }
    if (evalResult.status === 'fulfilled' && evalResult.value.data.code === 200) {
      evaluationData.value = evalResult.value.data.data || {}
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchNotifications = async () => {
  try {
    const res = await axios.get('/api/notification/list')
    if (res.data.code === 200) {
      notifications.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchData()
  fetchNotifications()
})
</script>

<style scoped>
.portal-home {
  padding: 0;
}

.welcome-banner {
  background: linear-gradient(135deg, #DC2626 0%, #B91C1C 50%, #991B1B 100%);
  border-radius: 16px;
  padding: 32px 40px;
  margin-bottom: 24px;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.welcome-content h2 {
  font-size: 26px;
  margin-bottom: 8px;
  font-weight: 700;
}

.welcome-content p {
  font-size: 15px;
  opacity: 0.85;
}

.welcome-decoration {
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
}

.decoration-icon {
  width: 120px;
  height: 120px;
}

.stat-cards {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  padding: 24px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255,255,255,0.2);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  margin-left: 2px;
  opacity: 0.85;
}

.stat-label {
  font-size: 14px;
  opacity: 0.85;
  margin-top: 4px;
}

.fee-card {
  background: linear-gradient(135deg, #F59E0B, #D97706);
}

.activity-card {
  background: linear-gradient(135deg, #10B981, #059669);
}

.report-card {
  background: linear-gradient(135deg, #6366F1, #4F46E5);
}

.volunteer-card {
  background: linear-gradient(135deg, #EC4899, #DB2777);
}

.content-row {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.evaluation-card {
  height: 100%;
}

.evaluation-content {
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  gap: 20px !important;
  padding: 30px 20px !important;
}

.evaluation-card .evaluation-content .evaluation-grade {
  width: 150px !important;
  height: 150px !important;
  border-radius: 50% !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25) !important;
  min-width: 150px !important;
  min-height: 150px !important;
  max-width: 150px !important;
  max-height: 150px !important;
}

.evaluation-card .evaluation-content .grade-优秀 {
  background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%) !important;
}

.evaluation-card .evaluation-content .grade-良好 {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%) !important;
}

.evaluation-card .evaluation-content .grade-合格 {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%) !important;
}

.evaluation-card .evaluation-content .grade-不合格 {
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%) !important;
}

.evaluation-card .evaluation-content .grade-text {
  font-size: 36px !important;
  font-weight: bold !important;
  color: #fff !important;
}

.evaluation-info {
  text-align: center;
}

.evaluation-comment {
  font-size: 14px;
  color: #374151;
  margin: 0 0 8px;
}

.evaluation-time {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}

.evaluation-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #9ca3af;
  padding: 20px 0;
}

.evaluation-empty p {
  margin: 0;
  font-size: 14px;
}

.notification-card {
  height: 100%;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-scroll {
  max-height: none;
  overflow-y: visible;
}

.notification-scroll.has-scroll {
  max-height: 280px;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 8px;
}

.notification-scroll.has-scroll::-webkit-scrollbar {
  width: 6px;
}

.notification-scroll.has-scroll::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.notification-scroll.has-scroll::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.notification-scroll.has-scroll::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 10px;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f3f4f6;
}

.notification-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #DC2626;
  margin-top: 6px;
  flex-shrink: 0;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-content h4 {
  font-size: 15px;
  margin-bottom: 4px;
  color: #1f2937;
}

.notification-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 4px;
  line-height: 1.5;
}

.notification-time {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}

.notification-item {
  cursor: pointer;
}

.notification-item:hover {
  background: #e5e7eb;
  transform: translateX(4px);
}

.notification-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.notification-modal {
  width: 90%;
  max-width: 600px;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.close-btn {
  border: none;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.close-btn:hover {
  background: #e5e7eb;
}

.modal-body {
  padding: 24px;
}

.modal-time {
  font-size: 13px;
  color: #9ca3af;
  margin: 0 0 16px;
}

.modal-content {
  font-size: 15px;
  color: #374151;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
