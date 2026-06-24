import axios from 'axios'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const instance = axios.create({
  baseURL: '',
  timeout: 10000
})

instance.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // token过期或无效，清除登录状态并跳转登录页
      const userStore = useUserStore()
      userStore.logout()
      // 避免重复跳转
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

export default instance
