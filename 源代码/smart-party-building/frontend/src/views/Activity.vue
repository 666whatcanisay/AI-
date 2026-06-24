<template>
  <div class="activity-page">
    <div class="page-header">
      <h2>组织生活管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><component :is="icons.Plus" /></el-icon>
        发布活动
      </el-button>
    </div>
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><component :is="icons.Calendar" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalCount }}</div>
          <div class="stat-label">活动总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><component :is="icons.CircleCheck" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ activeCount }}</div>
          <div class="stat-label">进行中</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><component :is="icons.User" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalParticipants }}</div>
          <div class="stat-label">累计参与人数</div>
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
            <el-icon><component :is="icons.Search" /></el-icon>
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
        <el-table-column prop="location" label="活动地点" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column prop="currentParticipants" label="报名人数" width="110">
          <template #default="scope">
            {{ scope.row.currentParticipants }} / {{ scope.row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" @click="handleView(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
            </div>
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
    <el-dialog :title="form.id ? '编辑活动' : '发布活动'" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" ref="formRef" label-width="100px" :rules="rules">
        <el-form-item label="活动名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动名称" />
        </el-form-item>
        <el-form-item label="活动地点" prop="location">
          <el-input v-model="form.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="最大人数" prop="maxParticipants">
          <el-input v-model="form.maxParticipants" type="number" placeholder="请输入最大人数" />
        </el-form-item>
        <el-form-item label="活动状态" v-if="form.id">
          <el-select v-model="form.status" placeholder="请选择活动状态">
            <el-option label="报名中" value="报名中" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </el-form-item>
        <el-form-item label="活动内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="3" placeholder="请输入活动内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog title="活动详情" v-model="detailVisible" width="600px">
      <div v-if="currentActivity" class="activity-detail">
        <div class="detail-row">
          <span class="detail-label">活动名称：</span>
          <span class="detail-value">{{ (currentActivity as any).title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">活动地点：</span>
          <span class="detail-value">{{ (currentActivity as any).location }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">开始时间：</span>
          <span class="detail-value">{{ (currentActivity as any).startTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">结束时间：</span>
          <span class="detail-value">{{ (currentActivity as any).endTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">参与人数：</span>
          <span class="detail-value">{{ (currentActivity as any).currentParticipants }} / {{ (currentActivity as any).maxParticipants }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">活动状态：</span>
          <el-tag :type="(currentActivity as any).status === '进行中' ? 'success' : 'info'">
            {{ (currentActivity as any).status }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">活动内容：</span>
        </div>
        <div class="detail-content">{{ (currentActivity as any).content }}</div>
        <div class="detail-row mt-2">
          <span class="detail-label">报名列表：</span>
        </div>
        <el-table :data="signupList" border class="signup-table">
          <el-table-column prop="memberName" label="姓名" />
          <el-table-column prop="signupTime" label="报名时间" />
          <el-table-column prop="status" label="状态">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已签到' ? 'success' : 'warning'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" @click="handleCheckin((currentActivity as any).id, scope.row.memberId)" v-if="scope.row.status === '已报名'">签到</el-button>
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
import { Plus, Search, Calendar, CircleCheck, User } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import axios from '@/utils/axios'

const icons = { Plus, Search, Calendar, CircleCheck, User }

const getStatusType = (status: string) => {
  switch (status) {
    case '报名中':
      return 'primary'
    case '进行中':
      return 'success'
    case '已结束':
      return 'info'
    default:
      return 'info'
  }
}

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
const totalParticipants = ref(0)

const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentActivity = ref(null)
const signupList = ref<any[]>([])

const formRef = ref<InstanceType<typeof ElForm>>()
const form = reactive({
  id: '',
  title: '',
  content: '',
  location: '',
  startTime: '',
  endTime: '',
  maxParticipants: 50,
  currentParticipants: 0,
  status: '报名中'
})

const rules = {
  title: [
    { required: true, message: '请输入活动名称', trigger: 'blur' },
    { min: 2, max: 100, message: '活动名称长度在2到100个字符之间', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  maxParticipants: [
    { required: true, message: '请输入最大人数', trigger: 'blur' },
    { type: 'number', min: 1, message: '最大人数必须大于0', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/activity', {
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
      calculateStats()
    }
  } catch (error) {
    console.error('获取活动列表失败:', error)
    ElMessage.error('获取活动列表失败')
  }
}

const calculateStats = () => {
  totalCount.value = total.value
  let active = 0
  let participants = 0
  tableData.value.forEach(item => {
    if (item.status === '报名中' || item.status === '进行中') active++
    participants += item.currentParticipants || 0
  })
  activeCount.value = active
  totalParticipants.value = participants
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.id = row.id
  form.title = row.title
  form.content = row.content
  form.location = row.location
  form.startTime = row.startTime
  form.endTime = row.endTime
  form.maxParticipants = row.maxParticipants
  form.currentParticipants = row.currentParticipants
  form.status = row.status
  dialogVisible.value = true
}

const handleView = async (row: any) => {
  currentActivity.value = row
  try {
    const response = await axios.get(`/api/activity/${row.id}/signups`)
    if (response.data.code === 200) {
      signupList.value = response.data.data
    }
  } catch (error) {
    console.error('获取报名列表失败:', error)
    signupList.value = []
    ElMessage.error('获取报名列表失败')
  }
  detailVisible.value = true
}

const handleSignup = async (row: any) => {
  try {
    const response = await axios.post(`/api/activity/${row.id}/signup`, {
      memberId: 1,
      memberName: '测试用户'
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

const handleCheckin = async (activityId: number, memberId: number) => {
  try {
    const response = await axios.post(`/api/activity/${activityId}/checkin`, null, {
      params: { memberId }
    })
    if (response.data.code === 200) {
      ElMessage.success('签到成功')
      if (currentActivity.value) {
        await handleView(currentActivity.value)
      }
    }
  } catch (error) {
    console.error('签到失败:', error)
    ElMessage.error('签到失败')
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个活动吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const response = await axios.delete(`/api/activity/${id}`)
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

const formatDateTime = (date: any) => {
  if (!date) return ''
  if (typeof date === 'string') return date
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    
    loading.value = true
    const submitData = {
      ...form,
      startTime: formatDateTime(form.startTime),
      endTime: formatDateTime(form.endTime)
    }
    if (form.id) {
      await axios.put(`/api/activity/${form.id}`, submitData)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/activity', submitData)
      ElMessage.success('发布成功')
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

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.id = ''
  form.title = ''
  form.content = ''
  form.location = ''
  form.startTime = ''
  form.endTime = ''
  form.maxParticipants = 50
  form.currentParticipants = 0
  form.status = '进行中'
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
.activity-page {
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

.activity-detail {
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

.detail-value {
  color: #1f2937;
}

.detail-content {
  background: #f9fafb;
  padding: 10px;
  border-radius: 8px;
  color: #1f2937;
}

.signup-table {
  margin-top: 10px;
}
</style>
