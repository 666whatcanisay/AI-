<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-wrapper">
      <div class="login-left">
        <div class="logo-section">
          <div class="logo">
            <svg viewBox="0 0 100 100" class="logo-icon">
              <path fill="#DC2626" d="M50 10 L90 50 L50 90 L10 50 Z" />
              <path fill="#fff" d="M50 25 L70 50 L50 65 L30 50 Z" />
            </svg>
          </div>
          <h1 class="title">码润初心 智护党建</h1>
          <p class="subtitle">智慧党建综合管理平台</p>
        </div>
      </div>
      <div class="login-right">
        <div class="login-box">
          <h2 class="login-title">用户登录</h2>
          <div class="role-select">
            <div
              class="role-option"
              :class="{ active: loginForm.loginType === 'admin' }"
              @click="loginForm.loginType = 'admin'"
            >
              <el-icon :size="24"><Setting /></el-icon>
              <span>管理员</span>
            </div>
            <div
              class="role-option"
              :class="{ active: loginForm.loginType === 'member' }"
              @click="loginForm.loginType = 'member'"
            >
              <el-icon :size="24"><User /></el-icon>
              <span>党员/申请人</span>
            </div>
          </div>
          <el-form :model="loginForm" ref="loginFormRef" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                :placeholder="loginForm.loginType === 'admin' ? '请输入用户名' : '请输入姓名'"
                size="large"
                class="login-input"
              >
                <template #prefix>
                  <el-icon><component :is="icons.User" /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                class="login-input"
                @keyup.enter="handleLogin"
                show-password
              >
                <template #prefix>
                  <el-icon><component :is="icons.Lock" /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item class="remember-item">
              <div class="remember-wrapper">
                <el-checkbox v-model="loginForm.remember" class="remember-checkbox">记住我</el-checkbox>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                class="login-btn"
                @click="handleLogin"
                :loading="loading"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="transfer-link">
            <el-button link @click="transferInVisible = true" class="transfer-btn">
              <el-icon><ArrowRight /></el-icon>
              组织关系转入申请
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <el-dialog title="组织关系转入申请" v-model="transferInVisible" width="700px" :close-on-click-modal="false">
      <el-form :model="transferInForm" ref="transferInFormRef" label-width="100px" :rules="transferInRules">
        <div class="form-row">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="transferInForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-select v-model="transferInForm.gender" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="出生日期">
            <el-date-picker
              v-model="transferInForm.birthDate"
              type="date"
              placeholder="选择出生日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="transferInForm.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="学号">
            <el-input v-model="transferInForm.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="班级">
            <el-input v-model="transferInForm.className" placeholder="请输入班级" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="transferInForm.phone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="transferInForm.email" placeholder="请输入邮箱" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="籍贯">
            <el-input v-model="transferInForm.nativePlace" placeholder="请输入籍贯" />
          </el-form-item>
          <el-form-item label="学历">
            <el-select v-model="transferInForm.education" placeholder="请选择学历" style="width: 100%">
              <el-option label="小学" value="小学" />
              <el-option label="初中" value="初中" />
              <el-option label="高中" value="高中" />
              <el-option label="专科" value="专科" />
              <el-option label="本科" value="本科" />
              <el-option label="硕士" value="硕士" />
              <el-option label="博士" value="博士" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="政治面貌">
            <el-select v-model="transferInForm.politicalStatus" placeholder="请选择政治面貌" style="width: 100%">
              <el-option label="中共党员" value="中共党员" />
            </el-select>
          </el-form-item>
          <el-form-item label="党员状态">
            <el-select v-model="transferInForm.partyStatus" placeholder="请选择党员状态" style="width: 100%">
              <el-option label="正式党员" value="FORMAL" />
              <el-option label="预备党员" value="PROBATIONARY" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="转入党支部" prop="branchName">
            <el-select v-model="transferInForm.branchName" placeholder="请选择转入党支部" style="width: 100%">
              <el-option v-for="branch in branchList" :key="branch.id" :label="branch.name" :value="branch.name" />
            </el-select>
          </el-form-item>
          <el-form-item label="入党时间" prop="joinPartyTime">
            <el-date-picker
              v-model="transferInForm.joinPartyTime"
              type="date"
              placeholder="选择入党时间"
              style="width: 100%"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="入党介绍人">
            <el-input v-model="transferInForm.introducer" placeholder="请输入入党介绍人" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="transferInForm.remark" placeholder="请输入备注" />
          </el-form-item>
        </div>
        <el-form-item label="转入原因" prop="reason">
          <el-input
            type="textarea"
            v-model="transferInForm.reason"
            :rows="3"
            placeholder="请输入组织关系转入原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferInVisible = false">取消</el-button>
        <el-button type="primary" @click="handleTransferInSubmit" :loading="transferInLoading">提交申请</el-button>
      </template>
    </el-dialog>

    <el-dialog title="申请成功" v-model="successVisible" width="400px">
      <p>您的组织关系转入申请已提交，请等待管理员审核。</p>
      <template #footer>
        <el-button type="primary" @click="successVisible = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Setting, ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'

const icons = { User, Lock }

import type { FormInstance } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref<FormInstance | null>(null)
const transferInFormRef = ref<FormInstance | null>(null)
const loading = ref(false)
const transferInLoading = ref(false)
const transferInVisible = ref(false)
const successVisible = ref(false)
const branchList = ref<any[]>([])

const loginForm = reactive({
  username: '',
  password: '',
  remember: false,
  loginType: 'member' as 'admin' | 'member'
})

const transferInForm = reactive({
  name: '',
  gender: '',
  birthDate: '',
  idCard: '',
  studentNo: '',
  className: '',
  phone: '',
  email: '',
  nativePlace: '',
  education: '',
  politicalStatus: '中共党员',
  partyStatus: 'FORMAL',
  branchName: '',
  joinPartyTime: '',
  introducer: '',
  remark: '',
  reason: ''
})

const transferInRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }, { min: 2, max: 50, message: '姓名长度在2到50个字符之间', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }, { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }],
  branchName: [{ required: true, message: '请选择转入党支部', trigger: 'change' }],
  joinPartyTime: [{ required: true, message: '请选择入党时间', trigger: 'change' }],
  reason: [{ required: true, message: '请输入转入原因', trigger: 'blur' }, { min: 5, message: '转入原因至少5个字符', trigger: 'blur' }]
}

const fetchBranches = async () => {
  try {
    const response = await axios.get('/api/branch/all')
    if (response.data.code === 200) {
      branchList.value = response.data.data
    }
  } catch (error) {
    console.error('获取党支部列表失败:', error)
  }
}

const handleTransferInSubmit = async () => {
  if (!transferInFormRef.value) return
  try {
    await transferInFormRef.value.validate()
    transferInLoading.value = true
    await axios.post('/api/transfer/in/apply', transferInForm)
    transferInVisible.value = false
    successVisible.value = true
    transferInFormRef.value.resetFields()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '提交失败')
    }
  } finally {
    transferInLoading.value = false
  }
}

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    alert('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const loginUrl = loginForm.loginType === 'admin' ? '/api/user/login' : '/api/user/portal/login'
    const response = await axios.post(loginUrl, {
      username: loginForm.username,
      password: loginForm.password
    })
    const result = response.data
    if (result && result.code === 200) {
      userStore.token = result.data.token
      userStore.username = result.data.user.username
      userStore.role = result.data.user.role
      userStore.memberId = result.data.user.memberId || null
      userStore.realName = result.data.user.realName || ''
      if (result.data.user.role.toUpperCase() === 'ADMIN') {
        router.push('/dashboard')
      } else {
        router.push('/portal/home')
      }
    } else {
      alert(result ? result.message : '登录失败')
    }
  } catch (error: any) {
    console.error('登录错误:', error)
    alert('登录失败: ' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBranches()
})
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #DC2626 0%, #EA580C 50%, #F97316 100%);
}

.login-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100' height='100' viewBox='0 0 100 100'%3E%3Cpath fill='%23ffffff' fill-opacity='0.05' d='M50 0L100 50L50 100L0 50Z'%3E%3C/path%3E%3C/svg%3E");
}

.login-wrapper {
  position: relative;
  z-index: 10;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.login-left {
  width: 500px;
  padding: 40px;
  color: #fff;
}

.logo-section {
  text-align: center;
}

.logo {
  margin-bottom: 20px;
}

.logo-icon {
  width: 80px;
  height: 80px;
}

.title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 18px;
  opacity: 0.9;
}

.login-right {
  width: 400px;
}

.login-box {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
  color: #1f2937;
}

.login-form {
  margin-bottom: 20px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-input :deep(.el-input__wrapper) {
  padding: 8px 15px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e5e7eb inset;
  transition: all 0.3s;
}

.login-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #DC2626 inset;
}

.login-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #DC2626 inset;
}

.login-input :deep(.el-input__prefix) {
  color: #9ca3af;
}

.remember-checkbox {
  color: #6b7280;
  font-size: 14px;
}

.remember-item {
  margin-bottom: 24px !important;
}

.remember-wrapper {
  display: flex;
  justify-content: flex-end;
  width: 100%;
}

.remember-checkbox :deep(.el-checkbox__inner) {
  width: 18px;
  height: 18px;
  border-radius: 4px;
}

.remember-checkbox :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #DC2626;
  border-color: #DC2626;
}

.remember-checkbox :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #DC2626;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  background: linear-gradient(135deg, #DC2626 0%, #EA580C 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #B91C1C 0%, #C2410C 100%);
}

.login-footer {
  text-align: center;
  font-size: 12px;
  color: #9ca3af;
}

.role-select {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.role-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 15px;
  color: #6b7280;
}

.role-option:hover {
  border-color: #DC2626;
  color: #DC2626;
}

.role-option.active {
  border-color: #DC2626;
  background: #FEF2F2;
  color: #DC2626;
  font-weight: 600;
}

.transfer-link {
  margin-top: 16px;
  text-align: center;
}

.transfer-btn {
  color: #DC2626;
  font-size: 14px;
  padding: 0;
}

.transfer-btn:hover {
  color: #B91C1C;
}
</style>