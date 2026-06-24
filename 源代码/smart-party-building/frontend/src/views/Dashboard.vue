<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" @click="$router.push('/member')">
        <div class="stat-icon user-icon">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.memberTotal || 0 }}</p>
          <p class="stat-label">党员总数</p>
          <p class="stat-sub">正式 {{ stats.formalCount || 0 }} / 预备 {{ stats.probationaryCount || 0 }}</p>
        </div>
      </div>
      <div class="stat-card" @click="$router.push('/application')">
        <div class="stat-icon active-icon">
          <el-icon :size="28"><RefreshRight /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.traineeCount || 0 }}</p>
          <p class="stat-label">培养对象</p>
          <p class="stat-sub">待审批 {{ stats.applyingCount || 0 }} 人</p>
        </div>
      </div>
      <div class="stat-card" @click="$router.push('/activity')">
        <div class="stat-icon activity-icon">
          <el-icon :size="28"><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.activityTotal || 0 }}</p>
          <p class="stat-label">组织生活</p>
          <p class="stat-sub">进行中 {{ stats.activeActivityCount || 0 }} 项</p>
        </div>
      </div>
      <div class="stat-card" @click="$router.push('/volunteer')">
        <div class="stat-icon volunteer-icon">
          <el-icon :size="28"><Promotion /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value">{{ stats.volunteerTotal || 0 }}</p>
          <p class="stat-label">志愿服务</p>
          <p class="stat-sub">进行中 {{ stats.activeVolunteerCount || 0 }} 项</p>
        </div>
      </div>
    </div>
    <!-- 第二行统计 -->
    <div class="stats-grid stats-grid-3">
      <div class="stat-card mini-card fee-card" @click="$router.push('/fee')">
        <div class="stat-icon fee-icon">
          <el-icon :size="24"><Wallet /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value-sm">&yen;{{ stats.yearTotalFee || '0.00' }}</p>
          <p class="stat-label">本年党费</p>
        </div>
      </div>
      <div class="stat-card mini-card report-card" @click="$router.push('/report')">
        <div class="stat-icon report-icon">
          <el-icon :size="24"><Edit /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value-sm">{{ stats.pendingReportCount || 0 }}</p>
          <p class="stat-label">待审汇报</p>
        </div>
      </div>
      <div class="stat-card mini-card branch-card">
        <div class="stat-icon branch-icon">
          <el-icon :size="24"><OfficeBuilding /></el-icon>
        </div>
        <div class="stat-info">
          <p class="stat-value-sm">{{ stats.branchCount || 0 }}</p>
          <p class="stat-label">党支部数</p>
        </div>
      </div>
    </div>
    <!-- 支部党员分布 + 快捷入口 -->
    <div class="content-row">
      <div class="card">
        <h3 class="card-title"><el-icon><PieChart /></el-icon> 支部党员分布</h3>
        <div class="branch-chart">
          <div class="branch-bar-item" v-for="branch in branchData" :key="branch.branch_name">
            <div class="branch-name">{{ branch.branch_name || '未分配' }}</div>
            <div class="branch-bar-bg">
              <div class="branch-bar-fill" :style="{ width: getBarWidth(branch.count) + '%' }"></div>
            </div>
            <div class="branch-count">{{ branch.count }} 人</div>
          </div>
          <el-empty v-if="branchData.length === 0" description="暂无数据" :image-size="60" />
        </div>
      </div>
      <div class="card">
        <h3 class="card-title"><el-icon><Grid /></el-icon> 快捷入口</h3>
        <div class="shortcut-grid">
          <div class="shortcut-item" @click="$router.push('/member')">
            <div class="shortcut-icon sc-user"><el-icon :size="24"><UserFilled /></el-icon></div>
            <span>党员管理</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/fee')">
            <div class="shortcut-icon sc-fee"><el-icon :size="24"><Wallet /></el-icon></div>
            <span>党费管理</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/activity')">
            <div class="shortcut-icon sc-activity"><el-icon :size="24"><Calendar /></el-icon></div>
            <span>组织生活</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/volunteer')">
            <div class="shortcut-icon sc-volunteer"><el-icon :size="24"><Promotion /></el-icon></div>
            <span>志愿服务</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/application')">
            <div class="shortcut-icon sc-apply"><el-icon :size="24"><Document /></el-icon></div>
            <span>入党申请</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/report')">
            <div class="shortcut-icon sc-report"><el-icon :size="24"><Edit /></el-icon></div>
            <span>思想汇报</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/evaluation')">
            <div class="shortcut-icon sc-evaluation"><el-icon :size="24"><DataAnalysis /></el-icon></div>
            <span>评议考核</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/branch')">
            <div class="shortcut-icon sc-branch"><el-icon :size="24"><OfficeBuilding /></el-icon></div>
            <span>党支部管理</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/transfer')">
            <div class="shortcut-icon sc-transfer"><el-icon :size="24"><RefreshRight /></el-icon></div>
            <span>组织关系转移</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/notification')">
            <div class="shortcut-icon sc-notification"><el-icon :size="24"><Bell /></el-icon></div>
            <span>通知公告</span>
          </div>
          <div class="shortcut-item" @click="$router.push('/user')">
            <div class="shortcut-icon sc-setting"><el-icon :size="24"><UserFilled /></el-icon></div>
            <span>管理员用户</span>
          </div>
        </div>
      </div>
    </div>
    <!-- 最近活动 + 最近申请 -->
    <div class="content-row">
      <div class="card">
        <h3 class="card-title"><el-icon><Calendar /></el-icon> 最近组织生活
          <el-button type="primary" link class="card-more" @click="$router.push('/activity')">更多</el-button>
        </h3>
        <div class="recent-list">
          <div class="recent-item" v-for="item in recentActivities" :key="item.id">
            <div class="recent-left">
              <el-tag :type="item.status === '进行中' ? 'success' : 'info'" size="small">{{ item.status }}</el-tag>
              <span class="recent-title">{{ item.title }}</span>
            </div>
            <span class="recent-time">{{ formatDate(item.createTime) }}</span>
          </div>
          <el-empty v-if="recentActivities.length === 0" description="暂无活动" :image-size="60" />
        </div>
      </div>
      <div class="card">
        <h3 class="card-title"><el-icon><Document /></el-icon> 最近入党申请
          <el-button type="primary" link class="card-more" @click="$router.push('/application')">更多</el-button>
        </h3>
        <div class="recent-list">
          <div class="recent-item" v-for="item in recentApplications" :key="item.id">
            <div class="recent-left">
              <el-tag :type="getAppStatusType(item.status)" size="small">{{ getAppStatusLabel(item.status) }}</el-tag>
              <span class="recent-title">{{ item.name }} - {{ item.department }}</span>
            </div>
            <span class="recent-time">{{ formatDate(item.createTime) }}</span>
          </div>
          <el-empty v-if="recentApplications.length === 0" description="暂无申请" :image-size="60" />
        </div>
      </div>
    </div>
    <!-- 最近志愿活动 + 通知公告 -->
    <div class="content-row">
      <div class="card">
        <h3 class="card-title"><el-icon><Promotion /></el-icon> 最近志愿服务
          <el-button type="primary" link class="card-more" @click="$router.push('/volunteer')">更多</el-button>
        </h3>
        <div class="recent-list">
          <div class="recent-item" v-for="item in recentVolunteers" :key="item.id">
            <div class="recent-left">
              <el-tag :type="item.status === '报名中' ? 'warning' : item.status === '进行中' ? 'success' : 'info'" size="small">{{ item.status }}</el-tag>
              <span class="recent-title">{{ item.title }}</span>
            </div>
            <span class="recent-time">{{ item.serviceDate }}</span>
          </div>
          <el-empty v-if="recentVolunteers.length === 0" description="暂无志愿活动" :image-size="60" />
        </div>
      </div>
      <div class="card">
        <h3 class="card-title"><el-icon><Bell /></el-icon> 通知公告</h3>
        <div class="notification-list">
          <div class="notification-item" v-for="(item, index) in notifications" :key="index">
            <span class="notification-dot"></span>
            <span class="notification-title">{{ item.title }}</span>
            <span class="notification-time">{{ item.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  User, UserFilled, Calendar, Wallet, Promotion, Document, Edit,
  PieChart, Grid, Bell, OfficeBuilding, RefreshRight, DataAnalysis
} from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const stats = ref<any>({})
const branchData = ref<any[]>([])
const recentActivities = ref<any[]>([])
const recentApplications = ref<any[]>([])
const recentVolunteers = ref<any[]>([])

const notifications = ref<any[]>([])

const statusMap: Record<string, { label: string; type: string }> = {
  APPLYING: { label: '申请中', type: 'warning' },
  ACTIVIST: { label: '积极分子', type: 'info' },
  DEVELOP_TARGET: { label: '发展对象', type: 'primary' },
  PROBATIONARY: { label: '预备党员', type: 'success' },
  FORMAL: { label: '正式党员', type: 'success' }
}

const getAppStatusLabel = (status: string) => statusMap[status]?.label || status
const getAppStatusType = (status: string) => statusMap[status]?.type || 'info'

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getBarWidth = (count: number) => {
  const max = 50
  return Math.min((count / max) * 100, 100)
}

const fetchStats = async () => {
  try {
    const response = await axios.get('/api/dashboard/stats')
    if (response.data.code === 200) {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchBranchData = async () => {
  try {
    const response = await axios.get('/api/dashboard/member-branch')
    if (response.data.code === 200) {
      branchData.value = response.data.data
    }
  } catch (error) {
    console.error('获取支部数据失败:', error)
  }
}

const fetchRecentActivities = async () => {
  try {
    const response = await axios.get('/api/dashboard/recent-activities')
    if (response.data.code === 200) {
      recentActivities.value = response.data.data
    }
  } catch (error) {
    console.error('获取最近活动失败:', error)
  }
}

const fetchRecentApplications = async () => {
  try {
    const response = await axios.get('/api/dashboard/recent-applications')
    if (response.data.code === 200) {
      recentApplications.value = response.data.data
    }
  } catch (error) {
    console.error('获取最近申请失败:', error)
  }
}

const fetchRecentVolunteers = async () => {
  try {
    const response = await axios.get('/api/dashboard/recent-volunteers')
    if (response.data.code === 200) {
      recentVolunteers.value = response.data.data
    }
  } catch (error) {
    console.error('获取最近志愿活动失败:', error)
  }
}

const fetchNotifications = async () => {
  try {
    const response = await axios.get('/api/notification/list')
    if (response.data.code === 200 && Array.isArray(response.data.data)) {
      notifications.value = response.data.data.slice(0, 5).map((item: any) => ({
        title: item.title,
        time: formatDate(item.createTime)
      }))
    }
  } catch (error) {
    console.error('获取通知公告失败:', error)
  }
}

onMounted(() => {
  fetchStats()
  fetchBranchData()
  fetchRecentActivities()
  fetchRecentApplications()
  fetchRecentVolunteers()
  fetchNotifications()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stats-grid-3 {
  grid-template-columns: repeat(3, 1fr);
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.mini-card {
  padding: 15px 20px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.user-icon { background: linear-gradient(135deg, #DC2626 0%, #EF4444 100%); color: #fff; }
.active-icon { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); color: #fff; }
.activity-icon { background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%); color: #fff; }
.volunteer-icon { background: linear-gradient(135deg, #EC4899 0%, #BE185D 100%); color: #fff; }
.fee-icon { background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%); color: #fff; width: 42px; height: 42px; }
.report-icon { background: linear-gradient(135deg, #8B5CF6 0%, #7C3AED 100%); color: #fff; width: 42px; height: 42px; }
.branch-icon { background: linear-gradient(135deg, #06B6D4 0%, #0891B2 100%); color: #fff; width: 42px; height: 42px; }

.stat-info { flex: 1; }

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.stat-value-sm {
  font-size: 22px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin: 4px 0 0;
}

.stat-sub {
  font-size: 12px;
  color: #9ca3af;
  margin: 2px 0 0;
}

.content-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
  gap: 6px;
}

.card-more {
  margin-left: auto;
  font-size: 13px;
}

/* 支部分布 */
.branch-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.branch-bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.branch-name {
  width: 140px;
  font-size: 13px;
  color: #374151;
  text-align: right;
  flex-shrink: 0;
  white-space: nowrap;
}

.branch-bar-bg {
  flex: 1;
  max-width: 65%;
  height: 20px;
  background: #f3f4f6;
  border-radius: 10px;
  overflow: hidden;
}

.branch-bar-fill {
  height: 100%;
  background: linear-gradient(135deg, #DC2626 0%, #EF4444 100%);
  border-radius: 10px;
  transition: width 0.6s ease;
  min-width: 20px;
}

.branch-count {
  width: 50px;
  font-size: 13px;
  color: #6b7280;
  flex-shrink: 0;
}

/* 快捷入口 */
.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px 10px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
}

.shortcut-item:hover {
  background: #f9fafb;
}

.shortcut-item span {
  font-size: 13px;
  color: #374151;
}

.shortcut-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.sc-user { background: linear-gradient(135deg, #DC2626, #EF4444); }
.sc-fee { background: linear-gradient(135deg, #F59E0B, #D97706); }
.sc-activity { background: linear-gradient(135deg, #3B82F6, #2563EB); }
.sc-volunteer { background: linear-gradient(135deg, #EC4899, #BE185D); }
.sc-apply { background: linear-gradient(135deg, #22C55E, #16A34A); }
.sc-report { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
.sc-approval { background: linear-gradient(135deg, #06B6D4, #0891B2); }
.sc-evaluation { background: linear-gradient(135deg, #8B5CF6, #7C3AED); }
.sc-branch { background: linear-gradient(135deg, #10B981, #059669); }
.sc-transfer { background: linear-gradient(135deg, #F97316, #EA580C); }
.sc-notification { background: linear-gradient(135deg, #6366F1, #4F46E5); }
.sc-setting { background: linear-gradient(135deg, #6B7280, #4B5563); }

/* 最近列表 */
.recent-list {
  display: flex;
  flex-direction: column;
}

.recent-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.recent-item:hover {
  background: #f9fafb;
}

.recent-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.recent-title {
  font-size: 14px;
  color: #374151;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.recent-time {
  font-size: 12px;
  color: #9ca3af;
  flex-shrink: 0;
  margin-left: 10px;
}

/* 通知公告 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notification-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f9fafb;
}

.notification-dot {
  width: 8px;
  height: 8px;
  background: #DC2626;
  border-radius: 50%;
  flex-shrink: 0;
}

.notification-title {
  flex: 1;
  font-size: 14px;
  color: #374151;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 12px;
  color: #9ca3af;
}
</style>
