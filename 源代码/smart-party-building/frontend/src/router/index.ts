import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/admin',
    name: 'Layout',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/dashboard',
    meta: { role: 'admin' },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('@/views/User.vue')
      },
      {
        path: '/member',
        name: 'Member',
        component: () => import('@/views/PartyMember.vue')
      },
      {
        path: '/fee',
        name: 'Fee',
        component: () => import('@/views/PartyFee.vue')
      },
      {
        path: '/activity',
        name: 'Activity',
        component: () => import('@/views/Activity.vue')
      },
      {
        path: '/application',
        name: 'Application',
        component: () => import('@/views/Application.vue')
      },
      {
        path: '/report',
        name: 'Report',
        component: () => import('@/views/Report.vue')
      },
      {
        path: '/volunteer',
        name: 'Volunteer',
        component: () => import('@/views/Volunteer.vue')
      },
      {
        path: '/branch',
        name: 'Branch',
        component: () => import('@/views/Branch.vue')
      },
      {
        path: '/transfer',
        name: 'Transfer',
        component: () => import('@/views/Transfer.vue')
      },
      {
        path: '/evaluation',
        name: 'Evaluation',
        component: () => import('@/views/Evaluation.vue')
      },
      {
        path: '/notification',
        name: 'Notification',
        component: () => import('@/views/Notification.vue')
      },
      {
        path: '/ai',
        name: 'AiChat',
        component: () => import('@/views/AiChat.vue')
      }
    ]
  },
  {
    path: '/portal',
    name: 'MemberLayout',
    component: () => import('@/layout/MemberLayout.vue'),
    redirect: '/portal/home',
    meta: { role: 'member' },
    children: [
      {
        path: '/portal/home',
        name: 'PortalHome',
        component: () => import('@/views/portal/PortalHome.vue')
      },
      {
        path: '/portal/profile',
        name: 'PortalProfile',
        component: () => import('@/views/portal/PortalProfile.vue')
      },
      {
        path: '/portal/fee',
        name: 'PortalFee',
        component: () => import('@/views/portal/PortalFee.vue')
      },
      {
        path: '/portal/activity',
        name: 'PortalActivity',
        component: () => import('@/views/portal/PortalActivity.vue')
      },
      {
        path: '/portal/report',
        name: 'PortalReport',
        component: () => import('@/views/portal/PortalReport.vue')
      },
      {
        path: '/portal/volunteer',
        name: 'PortalVolunteer',
        component: () => import('@/views/portal/PortalVolunteer.vue')
      },
      {
        path: '/portal/development',
        name: 'PortalDevelopment',
        component: () => import('@/views/portal/PortalDevelopment.vue')
      },
      {
        path: '/portal/transfer',
        name: 'PortalTransfer',
        component: () => import('@/views/portal/PortalTransfer.vue')
      },
      {
        path: '/portal/ai',
        name: 'PortalAiChat',
        component: () => import('@/views/portal/PortalAiChat.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.public) {
    next()
    return
  }
  
  if (to.path === '/login') {
    if (userStore.token) {
      // 已登录，根据角色跳转（忽略大小写）
      if (userStore.role.toUpperCase() === 'ADMIN') {
        next('/dashboard')
      } else {
        next('/portal/home')
      }
    } else {
      next()
    }
    return
  }
  
  if (!userStore.token) {
    next('/login')
    return
  }
  
  // 角色权限校验：管理员不能访问党员端，非管理员不能访问管理端
  const isAdmin = userStore.role.toUpperCase() === 'ADMIN'
  if (to.path.startsWith('/portal') && isAdmin) {
    next('/dashboard')
    return
  }
  if (!to.path.startsWith('/portal') && !isAdmin) {
    next('/portal/home')
    return
  }
  
  next()
})

export default router
