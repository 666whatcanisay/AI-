<template>
  <div class="layout-container">
    <aside class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
      <div class="sidebar-header">
        <div class="logo" v-if="!isCollapsed">
          <svg viewBox="0 0 100 100" class="logo-icon">
            <path fill="#DC2626" d="M50 10 L90 50 L50 90 L10 50 Z" />
            <path fill="#fff" d="M50 25 L70 50 L50 65 L30 50 Z" />
          </svg>
          <span class="logo-text">党员之家</span>
        </div>
        <div class="logo-mini" v-else>
          <svg viewBox="0 0 100 100" class="logo-icon">
            <path fill="#DC2626" d="M50 10 L90 50 L50 90 L10 50 Z" />
            <path fill="#fff" d="M50 25 L70 50 L50 65 L30 50 Z" />
          </svg>
        </div>
      </div>
      <nav class="sidebar-menu">
        <el-menu
          :default-active="activeMenu"
          mode="vertical"
          class="el-menu-vertical"
          :collapse="isCollapsed"
          router
        >
          <el-menu-item index="/portal/home">
            <el-icon><HomeFilled /></el-icon>
            <span v-if="!isCollapsed">我的首页</span>
          </el-menu-item>
          <el-menu-item index="/portal/profile">
            <el-icon><UserFilled /></el-icon>
            <span v-if="!isCollapsed">个人信息</span>
          </el-menu-item>
          <el-menu-item index="/portal/fee">
            <el-icon><Wallet /></el-icon>
            <span v-if="!isCollapsed">我的党费</span>
          </el-menu-item>
          <el-menu-item index="/portal/activity">
            <el-icon><Calendar /></el-icon>
            <span v-if="!isCollapsed">组织活动</span>
          </el-menu-item>
          <el-menu-item index="/portal/report">
            <el-icon><Edit /></el-icon>
            <span v-if="!isCollapsed">思想汇报</span>
          </el-menu-item>
          <el-menu-item index="/portal/volunteer">
            <el-icon><Promotion /></el-icon>
            <span v-if="!isCollapsed">志愿服务</span>
          </el-menu-item>
          <el-menu-item index="/portal/development">
            <el-icon><Flag /></el-icon>
            <span v-if="!isCollapsed">发展流程</span>
          </el-menu-item>
          <el-menu-item index="/portal/transfer">
            <el-icon><RefreshRight /></el-icon>
            <span v-if="!isCollapsed">组织关系转移</span>
          </el-menu-item>
          <el-menu-item index="/portal/ai">
            <el-icon><ChatDotRound /></el-icon>
            <span v-if="!isCollapsed">AI助手</span>
          </el-menu-item>
        </el-menu>
      </nav>
    </aside>
    <main class="main-content">
      <header class="top-header">
        <div class="header-left">
          <button class="collapse-btn" @click="toggleCollapse">
            <el-icon><ArrowRight v-if="isCollapsed" /><ArrowLeft v-else /></el-icon>
          </button>
          <span class="header-title">{{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ userStore.realName || userStore.username }}</span>
              <el-tag size="small" :type="userStore.role === 'applicant' ? 'warning' : 'danger'" style="margin-left:6px">{{ userStore.role === 'applicant' ? '申请人' : '党员' }}</el-tag>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  ArrowLeft,
  ArrowRight,
  HomeFilled,
  User,
  UserFilled,
  Wallet,
  Calendar,
  ArrowDown,
  Edit,
  Promotion,
  Flag,
  RefreshRight,
  ChatDotRound
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const isCollapsed = ref(false)

const activeMenu = computed(() => route.path)

const currentTitle = computed(() => {
  const titles: Record<string, string> = {
    '/portal/home': '我的首页',
    '/portal/profile': '个人信息',
    '/portal/fee': '我的党费',
    '/portal/activity': '组织活动',
    '/portal/report': '思想汇报',
    '/portal/volunteer': '志愿服务',
    '/portal/development': '发展流程',
    '/portal/transfer': '组织关系转移',
    '/portal/ai': 'AI助手'
  }
  return titles[route.path] || '党员之家'
})

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
  background: linear-gradient(135deg, #fef2f2 0%, #fff7ed 100%);
}

.sidebar {
  width: 200px;
  height: 100vh;
  background: linear-gradient(180deg, #7f1d1d 0%, #991b1b 50%, #b91c1c 100%);
  color: #fff;
  transition: width 0.3s ease;
  flex-shrink: 0;
  overflow-y: auto;
  overflow-x: hidden;
  position: relative;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='60' height='60' viewBox='0 0 60 60'%3E%3Cpath fill='%23ffffff' fill-opacity='0.03' d='M30 0L60 30L30 60L0 30Z'%3E%3C/path%3E%3C/svg%3E");
  pointer-events: none;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.5);
}

.sidebar-collapsed {
  width: 60px;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  position: relative;
  z-index: 1;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-mini {
  display: flex;
  justify-content: center;
}

.logo-icon {
  width: 40px;
  height: 40px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.logo-text {
  font-size: 18px;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.sidebar-menu {
  padding: 10px;
  position: relative;
  z-index: 1;
}

.el-menu-vertical {
  border-right: none;
  background: transparent;
}

.el-menu-item {
  color: rgba(255, 255, 255, 0.85) !important;
  margin-bottom: 4px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-menu-item:hover {
  background: rgba(255, 255, 255, 0.15) !important;
  color: #fff !important;
}

.el-menu-item.is-active {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%) !important;
  color: #7f1d1d !important;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(251, 191, 36, 0.4);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

.top-header {
  height: 60px;
  background: linear-gradient(135deg, #ffffff 0%, #fef2f2 100%);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 12px rgba(127, 29, 29, 0.08);
  border-bottom: 2px solid #fecaca;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  color: #7f1d1d;
  border-radius: 6px;
  transition: all 0.2s;
}

.collapse-btn:hover {
  background: #fee2e2;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #7f1d1d;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 24px;
  transition: all 0.2s;
  color: #7f1d1d;
  font-weight: 500;
}

.user-info:hover {
  background: #fee2e2;
}

.content-wrapper {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
