import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import axios from '@/utils/axios'

const STORAGE_KEY = 'user_state'
localStorage.removeItem(STORAGE_KEY)

const loadState = (): Record<string, any> | null => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (saved) return JSON.parse(saved)
  } catch {}
  return null
}

const savedState = loadState()

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(savedState?.token ?? '')
  const username = ref<string>(savedState?.username ?? '')
  const role = ref<string>(savedState?.role ?? '')
  const memberId = ref<number | null>(savedState?.memberId ?? null)
  const realName = ref<string>(savedState?.realName ?? '')

  // 自动同步到 localStorage
  const saveState = () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      token: token.value,
      username: username.value,
      role: role.value,
      memberId: memberId.value,
      realName: realName.value
    }))
  }

  // 是否跳过持久化（logout时使用）
  let skipSave = false

  // 监听所有状态变化，自动持久化
  watch([token, username, role, memberId, realName], () => {
    if (!skipSave) saveState()
  }, { deep: true })

  const login = async (loginUsername: string, loginPassword: string, loginType: 'admin' | 'member' = 'admin') => {
    const loginUrl = loginType === 'admin' ? '/api/user/login' : '/api/user/portal/login'
    const response = await axios.post(loginUrl, { username: loginUsername, password: loginPassword })
    if (response.data.code === 200) {
      token.value = response.data.data.token
      username.value = response.data.data.user.username
      role.value = response.data.data.user.role
      memberId.value = response.data.data.user.memberId ?? null
      realName.value = response.data.data.user.realName ?? ''
    }
    return response.data
  }

  const logout = () => {
    skipSave = true
    token.value = ''
    username.value = ''
    role.value = ''
    memberId.value = null
    realName.value = ''
    localStorage.removeItem(STORAGE_KEY)
    skipSave = false
  }

  return {
    token,
    username,
    role,
    memberId,
    realName,
    login,
    logout
  }
})
