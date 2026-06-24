<template>
  <div class="portal-profile">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-tag v-if="isApplicant" type="warning" size="small">入党申请人</el-tag>
          <el-tag v-else type="danger" size="small">党员</el-tag>
        </div>
      </template>
      <div class="profile-content" v-if="profileData.id">
        <div class="avatar-section">
          <div class="avatar">
            <el-icon :size="60"><UserFilled /></el-icon>
          </div>
          <div class="basic-info">
            <h2>{{ profileData.name }}</h2>
            <div class="tags">
              <el-tag :type="getStatusType(profileData.partyStatus || profileData.status)" size="default">
                {{ getStatusLabel(profileData.partyStatus || profileData.status) }}
              </el-tag>
              <el-tag type="info" size="default" v-if="profileData.branchName || profileData.department">
                {{ profileData.branchName || profileData.department }}
              </el-tag>
            </div>
          </div>
        </div>

        <el-divider />

        <!-- 申请人信息表单 -->
        <el-form v-if="isApplicant" :model="profileData" :rules="applicantRules" ref="applicantFormRef" label-width="120px" class="profile-form">
          <div class="form-row">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="profileData.name" disabled />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-select v-model="profileData.gender" placeholder="请选择性别">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="profileData.birthDate" type="date" placeholder="选择出生日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="profileData.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="profileData.studentNo" placeholder="请输入学号" />
            </el-form-item>
            <el-form-item label="班级" prop="className">
              <el-input v-model="profileData.className" placeholder="请输入班级" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="profileData.phone" placeholder="请输入联系电话" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="籍贯" prop="nativePlace">
              <el-input v-model="profileData.nativePlace" placeholder="请输入籍贯" />
            </el-form-item>
            <el-form-item label="学历" prop="education">
              <el-select v-model="profileData.education" placeholder="请选择学历" style="width: 100%">
                <el-option label="大专" value="大专" />
                <el-option label="本科" value="本科" />
                <el-option label="硕士" value="硕士" />
                <el-option label="博士" value="博士" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="政治面貌" prop="politicalStatus">
              <el-select v-model="profileData.politicalStatus" placeholder="请选择政治面貌" style="width: 100%">
                <el-option label="共青团员" value="共青团员" />
              </el-select>
            </el-form-item>
            <el-form-item label="所在党支部" prop="department">
              <el-select v-model="profileData.department" placeholder="请选择党支部" style="width: 100%">
                <el-option v-for="branch in branchOptions" :key="branch.value" :label="branch.label" :value="branch.value" />
              </el-select>
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="入党介绍人">
              <el-input v-model="profileData.introducer" placeholder="请输入入党介绍人" />
            </el-form-item>
          </div>
        </el-form>

        <!-- 党员信息表单 -->
        <el-form v-else :model="profileData" label-width="100px" class="profile-form">
          <div class="form-row">
            <el-form-item label="姓名">
              <el-input v-model="profileData.name" disabled />
            </el-form-item>
            <el-form-item label="性别">
              <el-input v-model="profileData.gender" disabled />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="出生日期">
              <el-input v-model="profileData.birthDate" disabled />
            </el-form-item>
            <el-form-item label="身份证号">
              <el-input v-model="profileData.idCard" disabled />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="学号">
              <el-input v-model="profileData.studentNo" disabled />
            </el-form-item>
            <el-form-item label="班级">
              <el-input v-model="profileData.className" disabled />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="籍贯">
              <el-input v-model="profileData.nativePlace" disabled />
            </el-form-item>
            <el-form-item label="学历">
              <el-input v-model="profileData.education" disabled />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="党支部">
              <el-input v-model="profileData.branchName" disabled />
            </el-form-item>
            <el-form-item label="党员状态">
              <el-input :value="profileData.partyStatus === 'FORMAL' ? '正式党员' : '预备党员'" disabled />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="入党时间">
              <el-input v-model="profileData.joinPartyTime" disabled />
            </el-form-item>
            <el-form-item label="入党介绍人">
              <el-input v-model="profileData.introducer" placeholder="请输入入党介绍人" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="联系电话">
              <el-input v-model="profileData.phone" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileData.email" />
            </el-form-item>
          </div>
          <div class="form-row">
            <el-form-item label="备注">
              <el-input v-model="profileData.remark" />
            </el-form-item>
          </div>
        </el-form>

        <div class="form-actions">
          <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
          <el-button @click="showPasswordDialog">修改密码</el-button>
        </div>
      </div>
      <el-empty v-else description="暂无个人信息，请联系管理员" />
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" v-model="passwordDialogVisible" width="450px" destroy-on-close>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const profileData = ref<any>({})
const saving = ref(false)
const applicantFormRef = ref<FormInstance | null>(null)
const branchOptions = ref<any[]>([]) // 党支部选项列表

// 修改密码相关
const passwordDialogVisible = ref(false)
const changingPassword = ref(false)
const passwordFormRef = ref<FormInstance | null>(null)
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const isApplicant = computed(() => userStore.role === 'applicant')

const applicantRules = {
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthDate: [{ required: true, message: '请选择出生年月', trigger: 'change' }],
  department: [{ required: true, message: '请输入所在部门', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    FORMAL: '正式党员', PROBATIONARY: '预备党员', APPLICANT: '入党申请人',
    ACTIVIST: '入党积极分子', APPLYING: '申请中', DEVELOP_TARGET: '发展对象'
  }
  return map[status] || status || '-'
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    FORMAL: 'danger', PROBATIONARY: 'warning', APPLICANT: 'info',
    ACTIVIST: '', APPLYING: 'warning', DEVELOP_TARGET: 'primary'
  }
  return map[status] || 'info'
}

const fetchBranches = async () => {
  // 获取所有党支部
  try {
    const res = await axios.get('/api/branch/all')
    if (res.data.code === 200 && res.data.data) {
      branchOptions.value = res.data.data.map((item: any) => ({
        label: item.name,
        value: item.name
      }))
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchData = async () => {
  // 先获取党支部列表
  await fetchBranches()
  
  if (isApplicant.value) {
    // 申请人：从application表获取信息
    try {
      const res = await axios.get('/api/portal/application', {
        params: { name: userStore.username }
      })
      if (res.data.code === 200 && res.data.data) {
        profileData.value = res.data.data
      }
    } catch (e) {
      console.error(e)
    }
  } else {
    // 党员：从party_member表获取信息
    const memberId = userStore.memberId
    if (!memberId) return
    try {
      const res = await axios.get(`/api/portal/info/${memberId}`)
      if (res.data.code === 200) {
        profileData.value = res.data.data || {}
      }
    } catch (e) {
      console.error(e)
    }
  }
}

const handleSave = async () => {
  // 申请人需要先校验表单
  if (isApplicant.value && applicantFormRef.value) {
    try {
      await applicantFormRef.value.validate()
    } catch {
      return
    }
  }

  saving.value = true
  try {
    if (isApplicant.value) {
      // 申请人：更新application表
      // 格式化日期为YYYY-MM-DD
      let formattedBirthDate = profileData.value.birthDate
      if (formattedBirthDate && typeof formattedBirthDate === 'string' && formattedBirthDate.includes('T')) {
        formattedBirthDate = formattedBirthDate.substring(0, 10)
      } else if (formattedBirthDate instanceof Date) {
        const y = formattedBirthDate.getFullYear()
        const m = String(formattedBirthDate.getMonth() + 1).padStart(2, '0')
        const d = String(formattedBirthDate.getDate()).padStart(2, '0')
        formattedBirthDate = `${y}-${m}-${d}`
      }
      const submitData = {
        gender: profileData.value.gender,
        birthDate: formattedBirthDate,
        idCard: profileData.value.idCard,
        studentNo: profileData.value.studentNo,
        className: profileData.value.className,
        department: profileData.value.department,
        phone: profileData.value.phone,
        email: profileData.value.email,
        nativePlace: profileData.value.nativePlace,
        education: profileData.value.education,
        politicalStatus: profileData.value.politicalStatus,
        introducer: profileData.value.introducer
      }
      const res = await axios.put(`/api/portal/application/${profileData.value.id}`, submitData)
      if (res.data.code === 200) {
        ElMessage.success('保存成功')
      } else {
        ElMessage.error(res.data.message || '保存失败')
      }
    } else {
      // 党员：更新party_member表
      const memberId = userStore.memberId
      const submitData = {
        phone: profileData.value.phone,
        email: profileData.value.email,
        remark: profileData.value.remark,
        introducer: profileData.value.introducer
      }
      const res = await axios.put(`/api/portal/info/${memberId}`, submitData)
      if (res.data.code === 200) {
        ElMessage.success('保存成功')
      } else {
        ElMessage.error(res.data.message || '保存失败')
      }
    }
  } catch (e: any) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const showPasswordDialog = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }

  changingPassword.value = true
  try {
    const res = await axios.post('/api/portal/change-password', {
      username: userStore.username,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    if (res.data.code === 200) {
      ElMessage.success('密码修改成功')
      passwordDialogVisible.value = false
    } else {
      ElMessage.error(res.data.message || '密码修改失败')
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '密码修改失败')
  } finally {
    changingPassword.value = false
  }
}

onMounted(fetchData)
</script>

<style scoped>
.portal-profile {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 8px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #DC2626, #EA580C);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.basic-info h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.tags {
  display: flex;
  gap: 8px;
}

.profile-form {
  margin-top: 8px;
  width: 100%;
}

.form-row {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.form-row .el-form-item {
  flex: 1;
  min-width: 300px;
}

.form-actions {
  text-align: center;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
}
</style>
