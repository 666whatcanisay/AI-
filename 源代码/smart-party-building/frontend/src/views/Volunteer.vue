<template>
  <div class="volunteer-page">
    <div class="page-header">
      <h2>志愿服务管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        发布志愿活动
      </el-button>
    </div>
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><Promotion /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalCount }}</div>
          <div class="stat-label">活动总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ activeCount }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalHours }}</div>
          <div class="stat-label">累计服务时长(h)</div>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.title"
          placeholder="请输入活动名称"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="活动状态">
          <el-option label="全部" value="" />
          <el-option label="报名中" value="报名中" />
          <el-option label="进行中" value="进行中" />
          <el-option label="已结束" value="已结束" />
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
        <el-table-column prop="title" label="活动名称" />
        <el-table-column prop="location" label="服务地点" />
        <el-table-column prop="serviceDate" label="服务日期" />
        <el-table-column prop="serviceHours" label="服务时长(h)" />
        <el-table-column prop="currentParticipants" label="报名人数">
          <template #default="scope">
            {{ scope.row.currentParticipants }} / {{ scope.row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="400">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看报名</el-button>
            <el-button size="small" type="success" @click="handleFinish(scope.row)" v-if="scope.row.status !== '已结束'">结束活动</el-button>
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
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
    <el-dialog :title="form.id ? '编辑志愿活动' : '发布志愿活动'" v-model="dialogVisible" width="600px">
      <el-form :model="form" ref="formRef" label-width="110px" :rules="rules">
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="服务地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入服务地点" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="服务日期" prop="serviceDate">
            <el-date-picker
              v-model="form.serviceDate"
              type="date"
              placeholder="选择服务日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="服务时长(h)" prop="serviceHours">
            <el-input v-model="form.serviceHours" type="number" placeholder="请输入服务时长" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="最大人数" prop="maxParticipants">
            <el-input v-model="form.maxParticipants" type="number" placeholder="请输入最大人数" />
          </el-form-item>
          <el-form-item label="活动状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option label="报名中" value="报名中" />
              <el-option label="进行中" value="进行中" />
              <el-option label="已结束" value="已结束" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="活动内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入活动内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog title="志愿活动详情" v-model="detailVisible" width="650px">
      <div v-if="currentVolunteer" class="volunteer-detail">
        <div class="detail-row">
          <span class="detail-label">活动名称：</span>
          <span class="detail-value">{{ currentVolunteer.title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">服务地点：</span>
          <span class="detail-value">{{ currentVolunteer.location }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">服务日期：</span>
          <span class="detail-value">{{ currentVolunteer.serviceDate }}</span>
          <span class="detail-label ml-4">服务时长：</span>
          <span class="detail-value">{{ currentVolunteer.serviceHours }} 小时</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">参与人数：</span>
          <span class="detail-value">{{ currentVolunteer.currentParticipants }} / {{ currentVolunteer.maxParticipants }}</span>
          <span class="detail-label ml-4">状态：</span>
          <el-tag :type="getStatusType(currentVolunteer.status)">{{ currentVolunteer.status }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">活动内容：</span>
        </div>
        <div class="detail-content">{{ currentVolunteer.content }}</div>
        <div class="detail-row mt-2">
          <span class="detail-label">报名列表：</span>
        </div>
        <el-table :data="signupList" border class="signup-table">
          <el-table-column prop="memberName" label="姓名" />
          <el-table-column prop="signupTime" label="报名时间" />
          <el-table-column prop="serviceHoursActual" label="实际时长(h)" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已完成' ? 'success' : 'warning'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search, Promotion, CircleCheck, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

const icons = { Plus, Search, Promotion, CircleCheck, Clock }

const searchForm = reactive({
  title: '',
  status: ''
})

const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const totalCount = ref(0)
const activeCount = ref(0)
const totalHours = ref(0)

const dialogVisible = ref(false)
const detailVisible = ref(false)
interface VolunteerItem {
  id: number
  title: string
  content: string
  location: string
  serviceDate: string
  serviceHours: string | number
  currentParticipants: number
  maxParticipants: number
  status: string
}

const currentVolunteer = ref<VolunteerItem | null>(null)
const signupList = ref<any[]>([])

const formRef = ref<InstanceType<typeof import('element-plus').ElForm>>()
const form = reactive({
  id: '',
  title: '',
  content: '',
  location: '',
  serviceDate: '',
  serviceHours: '',
  maxParticipants: 30,
  currentParticipants: 0,
  status: '报名中'
})

const rules = {
  title: [
    { required: true, message: '请输入活动名称', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入服务地点', trigger: 'blur' }
  ],
  serviceDate: [
    { required: true, message: '请选择服务日期', trigger: 'change' }
  ],
  serviceHours: [
    { required: true, message: '请输入服务时长', trigger: 'blur' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最大人数', trigger: 'blur' }
  ]
}

const getStatusType = (status: string) => {
  switch (status) {
    case '报名中': return 'warning'
    case '进行中': return 'success'
    case '已结束': return 'info'
    default: return 'info'
  }
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/volunteer', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        title: searchForm.title,
        status: searchForm.status
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
      await calculateStats()
    }
  } catch (error) {
    console.error('获取志愿活动列表失败:', error)
    ElMessage.error('获取志愿活动列表失败')
  }
}

const calculateStats = async () => {
  totalCount.value = total.value
  let active = 0
  let hours = 0
  
  // 统计当前页的进行中活动
  tableData.value.forEach(item => {
    if (item.status === '报名中' || item.status === '进行中') active++
  })
  
  // 获取所有已结束的活动来计算总时长
  try {
    const allResponse = await axios.get('/api/volunteer', {
      params: {
        pageNum: 1,
        pageSize: 9999,
        status: '已结束'
      }
    })
    if (allResponse.data.code === 200) {
      const allFinished = allResponse.data.data.records
      allFinished.forEach((item: any) => {
        const hour = parseFloat(item.serviceHours || '0')
        if (!isNaN(hour)) {
          hours += hour
        }
      })
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
  
  activeCount.value = active
  totalHours.value = hours
}

const handleAdd = () => {
  form.id = ''
  form.title = ''
  form.content = ''
  form.location = ''
  form.serviceDate = ''
  form.serviceHours = ''
  form.maxParticipants = 30
  form.currentParticipants = 0
  form.status = '报名中'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.id = row.id
  form.title = row.title
  form.content = row.content
  form.location = row.location
  form.serviceDate = row.serviceDate
  form.serviceHours = row.serviceHours
  form.maxParticipants = row.maxParticipants
  form.currentParticipants = row.currentParticipants
  form.status = row.status
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  currentVolunteer.value = row
  try {
    const response = await axios.get(`/api/volunteer/${row.id}/signups`)
    if (response.data.code === 200) {
      signupList.value = response.data.data
    }
  } catch (error) {
    console.error('获取报名列表失败:', error)
    signupList.value = []
  }
  detailVisible.value = true
}

const handleFinish = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      '确定要结束这个志愿活动吗？结束后将无法恢复，且会统计所有报名人员的服务时长。',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const response = await axios.put(`/api/volunteer/${row.id}/finish`)
    if (response.data.code === 200) {
      ElMessage.success('活动已结束')
      fetchData()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('结束活动失败:', error)
      ElMessage.error(error.response?.data?.message || '结束活动失败')
    }
  }
}

const handleSignup = async (row: any) => {
  try {
    const response = await axios.post(`/api/volunteer/${row.id}/signup`, {
      memberId: 1,
      memberName: '当前用户'
    })
    if (response.data.code === 200) {
      ElMessage.success('报名成功')
      fetchData()
    }
  } catch (error: any) {
    console.error('报名失败:', error)
    ElMessage.error(error.response?.data?.message || '报名失败')
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个志愿活动吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const response = await axios.delete(`/api/volunteer/${id}`)
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
  searchForm.title = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const submitData = { ...form } as any
    if (typeof submitData.serviceDate === 'object' && submitData.serviceDate !== null) {
      submitData.serviceDate = submitData.serviceDate.toISOString().split('T')[0]
    }
    if (form.id) {
      await axios.put(`/api/volunteer/${form.id}`, submitData)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/volunteer', submitData)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
      ElMessage.error(error.message || '保存失败')
    }
  } finally {
    loading.value = false
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
})
</script>

<style scoped>
.volunteer-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.blue {
  background: #eff6ff;
  color: #3b82f6;
}

.stat-icon.green {
  background: #f0fdf4;
  color: #22c55e;
}

.stat-icon.orange {
  background: #fff7ed;
  color: #f97316;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
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

.volunteer-detail {
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
  padding: 10px;
  border-radius: 8px;
  color: #1f2937;
  margin-bottom: 10px;
}


</style>
