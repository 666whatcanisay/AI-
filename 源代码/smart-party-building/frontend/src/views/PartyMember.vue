<template>
  <div class="member-page">
    <div class="page-header">
      <h2>党员管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加党员
        </el-button>
        <el-button @click="handleExport">
          <el-icon><Download /></el-icon>
          导出Excel
        </el-button>
      </div>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入姓名"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.branchName" placeholder="请选择党支部" class="search-input" clearable>
          <el-option v-for="branch in branchList" :key="branch.id" :label="branch.name" :value="branch.name" />
        </el-select>
        <el-select v-model="searchForm.partyStatus" placeholder="党员状态">
          <el-option label="全部" value="" />
          <el-option label="正式党员" value="FORMAL" />
          <el-option label="预备党员" value="PROBATIONARY" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-table :data="tableData" border>
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" width="90" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.gender === '男' ? 'primary' : 'success'">
              {{ scope.row.gender }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="className" label="班级" />
        <el-table-column prop="branchName" label="党支部" />
        <el-table-column prop="partyStatus" label="党员状态">
          <template #default="scope">
            <el-tag :type="scope.row.partyStatus === 'FORMAL' ? 'success' : 'warning'">
              {{ scope.row.partyStatus === 'FORMAL' ? '正式党员' : '预备党员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="joinPartyTime" label="入党时间" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </div>
    <el-dialog :title="form.id ? '编辑党员' : '添加党员'" v-model="dialogVisible" width="700px">
      <el-form :model="form" ref="formRef" label-width="120px" :rules="rules">
        <div class="form-row">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="登录密码" prop="password">
            <el-input v-model="form.password" type="password" :placeholder="form.id ? '不填则保持原密码' : '默认123456'" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker
              v-model="form.birthDate"
              type="date"
              placeholder="选择出生日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="学号" prop="studentNo">
            <el-input v-model="form.studentNo" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="班级" prop="className">
            <el-input v-model="form.className" placeholder="请输入班级" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="form.email" placeholder="请输入邮箱" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="籍贯" prop="nativePlace">
            <el-input v-model="form.nativePlace" placeholder="请输入籍贯" />
          </el-form-item>
          <el-form-item label="学历" prop="education">
            <el-select v-model="form.education" placeholder="请选择学历">
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
            <el-select v-model="form.politicalStatus" placeholder="请选择政治面貌">
              <el-option label="中共党员" value="中共党员" />
            </el-select>
          </el-form-item>
          <el-form-item label="党员状态" prop="partyStatus">
            <el-select v-model="form.partyStatus" placeholder="请选择党员状态">
              <el-option label="正式党员" value="FORMAL" />
              <el-option label="预备党员" value="PROBATIONARY" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="党支部" prop="branchName">
            <el-select v-model="form.branchName" placeholder="请选择党支部">
              <el-option v-for="branch in branchList" :key="branch.id" :label="branch.name" :value="branch.name" />
            </el-select>
          </el-form-item>
          <el-form-item label="入党时间" prop="joinPartyTime">
            <el-date-picker
              v-model="form.joinPartyTime"
              type="date"
              placeholder="选择入党时间"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="入党介绍人" prop="introducer">
            <el-input v-model="form.introducer" placeholder="请输入入党介绍人" />
          </el-form-item>
        </div>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog title="党员详情" v-model="detailVisible" width="600px">
      <div v-if="currentMember" class="member-detail">
        <div class="detail-row">
          <span class="detail-label">姓名：</span>
          <span class="detail-value">{{ currentMember.name }}</span>
          <span class="detail-label ml-4">性别：</span>
          <span class="detail-value">{{ currentMember.gender }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">出生日期：</span>
          <span class="detail-value">{{ currentMember.birthDate }}</span>
          <span class="detail-label ml-4">身份证号：</span>
          <span class="detail-value">{{ currentMember.idCard }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">学号：</span>
          <span class="detail-value">{{ currentMember.studentNo }}</span>
          <span class="detail-label ml-4">班级：</span>
          <span class="detail-value">{{ currentMember.className }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话：</span>
          <span class="detail-value">{{ currentMember.phone }}</span>
          <span class="detail-label ml-4">邮箱：</span>
          <span class="detail-value">{{ currentMember.email }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">籍贯：</span>
          <span class="detail-value">{{ currentMember.nativePlace }}</span>
          <span class="detail-label ml-4">学历：</span>
          <span class="detail-value">{{ currentMember.education }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">政治面貌：</span>
          <span class="detail-value">{{ currentMember.politicalStatus }}</span>
          <span class="detail-label ml-4">党员状态：</span>
          <el-tag :type="currentMember.partyStatus === 'FORMAL' ? 'success' : 'warning'">
            {{ currentMember.partyStatus === 'FORMAL' ? '正式党员' : '预备党员' }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">党支部：</span>
          <span class="detail-value">{{ currentMember.branchName }}</span>
          <span class="detail-label ml-4">入党时间：</span>
          <span class="detail-value">{{ currentMember.joinPartyTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">入党介绍人：</span>
          <span class="detail-value">{{ currentMember.introducer || '-' }}</span>
        </div>
        <div v-if="currentMember.remark" class="detail-row">
          <span class="detail-label">备注：</span>
        </div>
        <div v-if="currentMember.remark" class="detail-content">{{ currentMember.remark }}</div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Download, Search, OfficeBuilding } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

interface Member {
  id: number
  name: string
  gender: string
  birthDate: string
  idCard: string
  studentNo: string
  className: string
  phone: string
  email: string
  nativePlace: string
  education: string
  politicalStatus: string
  partyStatus: string
  branchName: string
  joinPartyTime: string
  introducer?: string
  remark?: string
}

const searchForm = reactive({
  name: '',
  branchName: '',
  partyStatus: ''
})

const branchList = ref<any[]>([])
const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentMember = ref<Member | null>(null)

const formRef = ref<InstanceType<typeof import('element-plus').ElForm>>()
const form = reactive({
  id: '',
  name: '',
  password: '',
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
  remark: ''
})

const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在2到50个字符之间', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  branchName: [
    { required: true, message: '请选择党支部', trigger: 'change' }
  ],
  partyStatus: [
    { required: true, message: '请选择党员状态', trigger: 'change' }
  ],
  joinPartyTime: [
    { required: true, message: '请选择入党时间', trigger: 'change' }
  ]
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/member', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        name: searchForm.name,
        branchName: searchForm.branchName,
        partyStatus: searchForm.partyStatus
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取党员列表失败:', error)
    ElMessage.error('获取党员列表失败')
  }
}

const fetchBranchList = async () => {
  try {
    const response = await axios.get('/api/branch/all')
    if (response.data.code === 200) {
      branchList.value = response.data.data
    }
  } catch (error) {
    console.error('获取党支部列表失败:', error)
  }
}

const handleAdd = () => {
  form.id = ''
  form.name = ''
  form.gender = ''
  form.birthDate = ''
  form.idCard = ''
  form.studentNo = ''
  form.className = ''
  form.phone = ''
  form.email = ''
  form.nativePlace = ''
  form.education = ''
  form.politicalStatus = ''
  form.partyStatus = 'FORMAL'
  form.branchName = ''
  form.joinPartyTime = ''
  form.introducer = ''
  form.remark = ''
  dialogVisible.value = true
}

const handleView = (row: any) => {
  currentMember.value = row
  detailVisible.value = true
}

const handleEdit = (row: any) => {
  form.id = row.id
  form.name = row.name
  form.gender = row.gender
  form.birthDate = row.birthDate
  form.idCard = row.idCard
  form.studentNo = row.studentNo
  form.className = row.className
  form.phone = row.phone
  form.email = row.email
  form.nativePlace = row.nativePlace
  form.education = row.education
  form.politicalStatus = row.politicalStatus
  form.partyStatus = row.partyStatus
  form.branchName = row.branchName
  form.joinPartyTime = row.joinPartyTime
  form.introducer = row.introducer
  form.remark = row.remark
  dialogVisible.value = true
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这条记录吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const response = await axios.delete(`/api/member/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.branchName = ''
  searchForm.partyStatus = ''
  currentPage.value = 1
  fetchData()
}

const formatDateStr = (date: any) => {
  if (!date) return null
  if (typeof date === 'string') return date || null
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

const formatDateTimeStr = (date: any) => {
  if (!date) return null
  if (typeof date === 'string') return date
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    
    loading.value = true
    const submitData: any = {
      ...form,
      birthDate: formatDateStr(form.birthDate),
      joinPartyTime: formatDateTimeStr(form.joinPartyTime)
    }
    if (!form.id) {
      delete submitData.id
    }
    if (form.id) {
      await axios.put(`/api/member/${form.id}`, submitData)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/member', submitData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  } finally {
    loading.value = false
  }
}

const handleExport = async () => {
  try {
    const response = await axios.get('/api/member/export')
    if (response.data.code === 200) {
      const data = response.data.data
      let csv = '\uFEFFID,姓名,性别,出生日期,身份证号,学号,班级,联系电话,邮箱,籍贯,学历,政治面貌,党员状态,党支部,入党时间,入党介绍人\n'
      data.forEach((item: any) => {
        csv += `${item.id || ''},${item.name || ''},${item.gender || ''},${item.birthDate || ''},${item.idCard || ''},${item.studentNo || ''},${item.className || ''},${item.phone || ''},${item.email || ''},${item.nativePlace || ''},${item.education || ''},${item.politicalStatus || ''},${item.partyStatus === 'FORMAL' ? '正式党员' : '预备党员'},${item.branchName || ''},${item.joinPartyTime || ''},${item.introducer || ''}\n`
      })
      const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = '党员信息.csv'
      link.click()
      URL.revokeObjectURL(url)
      ElMessage.success('导出成功')
    }
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchBranchList()
})
</script>

<style scoped>
.member-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  width: 200px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
}

.member-detail {
  padding: 10px;
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.detail-row.mt-2 {
  margin-top: 15px;
}

.detail-label {
  font-weight: 500;
  color: #6b7280;
  min-width: 100px;
}

.detail-label.ml-4 {
  margin-left: 20px;
}

.detail-value {
  color: #1f2937;
}

.detail-content {
  background: #f9fafb;
  padding: 15px;
  border-radius: 8px;
  color: #1f2937;
}
</style>