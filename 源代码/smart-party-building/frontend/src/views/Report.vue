<template>
  <div class="report-page">
    <div class="page-header">
      <h2>思想汇报管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handlePublishTask">
          <el-icon><component :is="icons.Bell" /></el-icon>
          发布汇报任务
        </el-button>
        <el-button type="danger" @click="handleCleanData">
          <el-icon><component :is="icons.Delete" /></el-icon>
          清理数据
        </el-button>
      </div>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><component :is="icons.FileText" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">汇报总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><component :is="icons.CheckCircle" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><component :is="icons.Warning" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <el-icon><component :is="icons.Calendar" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.taskCount }}</div>
          <div class="stat-label">发布任务</div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-select v-model="taskSearch.year" placeholder="选择年份" style="width:120px">
          <el-option v-for="year in years" :key="year" :label="year" :value="year" />
        </el-select>
        <el-select v-model="taskSearch.status" placeholder="任务状态" clearable style="width:120px">
          <el-option label="已发布" value="已发布" />
          <el-option label="已结束" value="已结束" />
        </el-select>
        <el-button type="primary" @click="fetchTasks">搜索</el-button>
        <el-button @click="taskSearch.year = new Date().getFullYear().toString(); taskSearch.status = ''; fetchTasks()">重置</el-button>
      </div>
      <el-table :data="taskTableData" border stripe style="width: 100%">
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ (taskCurrentPage - 1) * taskPageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="任务标题" min-width="150" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="quarter" label="季度" width="80">
          <template #default="scope">{{ scope.row.quarter }}季度</template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="200">
          <template #default="scope">
            {{ formatDateTime(scope.row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="handleViewTaskStatus(scope.row)">查看完成情况</el-button>
              <el-button 
                v-if="scope.row.status === '已发布'" 
                size="small" 
                type="warning" 
                @click="handleStopTask(scope.row.id)"
              >
                停止任务
              </el-button>
              <el-button size="small" type="danger" @click="handleDeleteTask(scope.row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          @size-change="handleTaskSizeChange"
          @current-change="handleTaskCurrentChange"
          :current-page="taskCurrentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="taskPageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="taskTotal"
        />
      </div>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入汇报标题"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="审核状态" clearable>
          <el-option label="全部" value="" />
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="APPROVED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
        <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
        <el-button @click="handleReset" :icon="Refresh">重置</el-button>
      </div>
      <el-table :data="tableData" border>
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="任务标题" min-width="200" />
        <el-table-column prop="author" label="汇报人" width="100" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column prop="reviewTime" label="审核时间" width="170" />
        <el-table-column label="操作" width="440">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" @click="handleView(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="handleEdit(scope.row)" v-if="scope.row.status === 'PENDING'">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)" v-if="scope.row.status === 'PENDING'">删除</el-button>
              <el-button size="small" type="success" @click="handleReview(scope.row, 'APPROVED')" v-if="scope.row.status === 'PENDING'">通过</el-button>
              <el-button size="small" type="danger" @click="handleReview(scope.row, 'REJECTED')" v-if="scope.row.status === 'PENDING'">驳回</el-button>
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

    <el-dialog title="发布汇报任务" v-model="taskDialogVisible" width="450px" destroy-on-close>
      <el-form :model="taskForm" ref="taskFormRef" label-width="100px" :rules="taskRules">
        <el-form-item label="任务标题" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-select v-model="taskForm.year" placeholder="请选择年份" style="width:100%">
            <el-option v-for="year in years" :key="year" :label="year" :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="季度" prop="quarter">
          <el-select v-model="taskForm.quarter" placeholder="请选择季度" style="width:100%">
            <el-option v-for="q in quarters" :key="q" :label="q + '季度'" :value="q" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker
            v-model="taskForm.deadline"
            type="date"
            placeholder="选择截止日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="任务说明" prop="content">
          <el-input v-model="taskForm.content" type="textarea" :rows="3" placeholder="请输入任务说明（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublishTaskSubmit" :loading="taskLoading">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog title="任务完成情况" v-model="taskStatusDialogVisible" width="900px" destroy-on-close>
      <div v-if="currentTask" class="task-status">
        <div class="status-header">
          <h3>{{ currentTask.title }}</h3>
          <div class="status-summary">
            <span>总党员数：<strong>{{ taskStatus.totalMembers }}</strong></span>
            <span>已完成：<strong style="color:#22c55e">{{ taskStatus.completedCount }}</strong></span>
            <span>待提交：<strong style="color:#f59e0b">{{ taskStatus.unfinishedCount }}</strong></span>
            <span>审核中：<strong style="color:#3b82f6">{{ taskStatus.pendingCount }}</strong></span>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-dialog :title="form.id ? '编辑汇报' : '提交思想汇报'" v-model="dialogVisible" width="600px">
      <el-form :model="form" ref="formRef" label-width="100px" :rules="rules">
        <el-form-item label="汇报标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入汇报标题" />
        </el-form-item>
        <el-form-item label="汇报人" prop="author">
          <el-input v-model="form.author" placeholder="请输入汇报人姓名" />
        </el-form-item>
        <el-form-item label="汇报日期" prop="reportDate">
          <el-date-picker
            v-model="form.reportDate"
            type="date"
            placeholder="选择汇报日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="汇报内容" prop="content">
          <el-input type="textarea" v-model="form.content" :rows="6" placeholder="请输入思想汇报内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="汇报详情" v-model="detailVisible" width="700px">
      <div v-if="currentReport" class="report-detail">
        <div class="detail-row">
          <span class="detail-label">标题：</span>
          <span class="detail-value">{{ currentReport.title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">汇报人：</span>
          <span class="detail-value">{{ currentReport.author }}</span>
          <span class="detail-label ml-4">汇报日期：</span>
          <span class="detail-value">{{ currentReport.reportDate }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">提交时间：</span>
          <span class="detail-value">{{ currentReport.createTime }}</span>
          <span class="detail-label ml-4">状态：</span>
          <el-tag :type="getStatusType(currentReport.status)" size="medium">
            {{ getStatusLabel(currentReport.status) }}
          </el-tag>
        </div>
        <div v-if="currentReport.reviewTime" class="detail-row">
          <span class="detail-label">审核时间：</span>
          <span class="detail-value">{{ currentReport.reviewTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">汇报内容：</span>
        </div>
        <div class="detail-content">{{ currentReport.content }}</div>
        <div v-if="currentReport.reviewComment" class="detail-row mt-2">
          <span class="detail-label">审核意见：</span>
        </div>
        <div v-if="currentReport.reviewComment" class="detail-comment">
          {{ currentReport.reviewComment }}
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog title="审核汇报" v-model="reviewDialogVisible" width="500px">
      <el-form :model="reviewForm" ref="reviewFormRef" label-width="80px">
        <el-form-item label="审核结果">
          <el-select v-model="reviewForm.status" placeholder="请选择审核结果">
            <el-option label="通过" value="APPROVED" />
            <el-option label="驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input type="textarea" v-model="reviewForm.comment" :rows="4" placeholder="请输入审核意见（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleReviewSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Bell, Document, CircleCheck, Warning, Calendar, Delete, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

const icons = {
  Bell,
  FileText: Document,
  CheckCircle: CircleCheck,
  Warning,
  Calendar,
  Delete
}

const years = Array.from({ length: 5 }, (_, i) => String(new Date().getFullYear() - i))
const quarters = ['1', '2', '3', '4']

const stats = reactive({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0,
  taskCount: 0
})

const searchForm = reactive({
  keyword: '',
  status: ''
})

const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const detailVisible = ref(false)
const reviewDialogVisible = ref(false)

interface Report {
  id: number
  title: string
  author: string
  reportDate: string
  content: string
  status: string
  createTime: string
  reviewTime?: string
  reviewComment?: string
  fileUrl?: string
}

const currentReport = ref<Report | null>(null)
const reviewTargetId = ref<number>(0)

const formRef = ref<InstanceType<typeof import('element-plus').ElForm>>()
const form = reactive({
  id: '',
  title: '',
  author: '',
  reportDate: '',
  content: '',
  fileUrl: ''
})

const reviewForm = reactive({
  status: '',
  comment: ''
})

const rules = {
  title: [
    { required: true, message: '请输入汇报标题', trigger: 'blur' }
  ],
  author: [
    { required: true, message: '请输入汇报人姓名', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入汇报内容', trigger: 'blur' }
  ]
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return map[status] || 'info'
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回'
  }
  return map[status] || status
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const fetchData = async () => {
  try {
    const res = await axios.get('/api/report', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        keyword: searchForm.keyword,
        status: searchForm.status
      }
    })
    if (res.data.code === 200) {
      tableData.value = res.data.data.records
      total.value = res.data.data.total
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchStats = async () => {
  try {
    const res = await axios.get('/api/report/stats')
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchData()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

const handleAdd = () => {
  form.id = ''
  form.title = ''
  form.author = ''
  form.reportDate = ''
  form.content = ''
  form.fileUrl = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.id = row.id
  form.title = row.title
  form.author = row.author
  form.reportDate = row.reportDate
  form.content = row.content
  form.fileUrl = row.fileUrl || ''
  dialogVisible.value = true
}

const handleView = (row: any) => {
  currentReport.value = row
  detailVisible.value = true
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该汇报吗？', '提示', {
      type: 'warning'
    })
    const res = await axios.delete(`/api/report/${id}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
      fetchStats()
    }
  } catch (e) {
    ElMessage.info('已取消删除')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    if (form.id) {
      await axios.put(`/api/report/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/report', form)
      ElMessage.success('提交成功')
    }
    dialogVisible.value = false
    fetchData()
    fetchStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
      ElMessage.error(error.message || '保存失败')
    }
  } finally {
    loading.value = false
  }
}

const handleReview = (row: any, status: string) => {
  reviewTargetId.value = row.id
  reviewForm.status = status
  reviewForm.comment = ''
  reviewDialogVisible.value = true
}

const handleReviewSubmit = async () => {
  try {
    const res = await axios.put(`/api/report/${reviewTargetId.value}/review`, {
      status: reviewForm.status,
      reviewComment: reviewForm.comment
    })
    if (res.data.code === 200) {
      ElMessage.success('审核成功')
      reviewDialogVisible.value = false
      fetchData()
      fetchStats()
    }
  } catch (e) {
    ElMessage.error('审核失败')
  }
}

const taskSearch = reactive({
  year: new Date().getFullYear().toString(),
  status: ''
})

const taskTableData = ref<any[]>([])
const taskCurrentPage = ref(1)
const taskPageSize = ref(10)
const taskTotal = ref(0)
const taskLoading = ref(false)
const taskDialogVisible = ref(false)
const taskStatusDialogVisible = ref(false)

const currentTask = ref<any>(null)
const taskStatus = reactive({
  totalMembers: 0,
  completedCount: 0,
  pendingCount: 0,
  unfinishedCount: 0
})

const taskFormRef = ref<InstanceType<typeof import('element-plus').ElForm>>()
const taskForm = reactive({
  id: '',
  title: '',
  year: new Date().getFullYear().toString(),
  quarter: '',
  deadline: '',
  content: ''
})

const taskRules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' }
  ],
  year: [
    { required: true, message: '请选择年份', trigger: 'change' }
  ],
  quarter: [
    { required: true, message: '请选择季度', trigger: 'change' }
  ],
  deadline: [
    { required: true, message: '请选择截止日期', trigger: 'change' }
  ]
}

const fetchTasks = async () => {
  try {
    // 先更新过期任务状态
    await axios.put('/api/report/task/update-expired')
    
    const res = await axios.get('/api/report/task', {
      params: {
        pageNum: taskCurrentPage.value,
        pageSize: taskPageSize.value,
        year: taskSearch.year,
        status: taskSearch.status
      }
    })
    if (res.data.code === 200) {
      taskTableData.value = res.data.data.records
      taskTotal.value = res.data.data.total
    }
  } catch (e) {
    console.error(e)
  }
}

const handleStopTask = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要停止此任务吗？停止后将无法再提交思想汇报。', '提示', {
      type: 'warning'
    })
    const res = await axios.put(`/api/report/task/${id}/stop`)
    if (res.data.code === 200) {
      ElMessage.success('任务已停止')
      fetchTasks()
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('停止任务失败')
    }
  }
}

const handlePublishTask = () => {
  taskForm.id = ''
  taskForm.title = ''
  taskForm.year = new Date().getFullYear().toString()
  taskForm.quarter = ''
  taskForm.deadline = ''
  taskForm.content = ''
  taskDialogVisible.value = true
}

const formatDate = (date: any) => {
  if (!date) return ''
  if (date instanceof Date) {
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  }
  // 如果是字符串，直接返回
  if (typeof date === 'string') {
    // 如果已经是 YYYY-MM-DD 格式
    if (date.match(/^\d{4}-\d{2}-\d{2}$/)) {
      return date
    }
    // 如果是 ISO 格式，提取日期部分
    const match = date.match(/^(\d{4}-\d{2}-\d{2})/)
    if (match) {
      return match[1]
    }
  }
  return date
}

const handlePublishTaskSubmit = async () => {
  if (!taskFormRef.value) return
  try {
    await taskFormRef.value.validate()
    
    // 格式化日期
    const submitData = {
      ...taskForm,
      deadline: formatDate(taskForm.deadline)
    }
    
    taskLoading.value = true
    console.log('提交的表单数据:', submitData)
    const response = await axios.post('/api/report/task', submitData)
    console.log('后端响应:', response.data)
    
    if (response.data.code === 200) {
      ElMessage.success('发布成功')
      taskDialogVisible.value = false
      fetchTasks()
      fetchStats()
    } else {
      ElMessage.error(response.data.message || '发布失败')
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
      console.error('错误详情:', error.response?.data)
      ElMessage.error(error.response?.data?.message || error.message || '发布失败')
    }
  } finally {
    taskLoading.value = false
  }
}

const handleViewTaskStatus = async (row: any) => {
  currentTask.value = row
  try {
    const res = await axios.get(`/api/report/task/${row.id}/status`)
    if (res.data.code === 200) {
      Object.assign(taskStatus, res.data.data)
    }
  } catch (e) {
    console.error(e)
  }
  taskStatusDialogVisible.value = true
}

const handleDeleteTask = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
      type: 'warning'
    })
    const res = await axios.delete(`/api/report/task/${id}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      fetchTasks()
      fetchStats()
    }
  } catch (e) {
    ElMessage.info('已取消删除')
  }
}

const handleCleanData = async () => {
  try {
    await ElMessageBox.confirm('确定要清理所有汇报数据吗？此操作不可恢复！', '提示', {
      type: 'error'
    })
    const res = await axios.delete('/api/report/clean')
    if (res.data.code === 200) {
      ElMessage.success('清理成功')
      fetchData()
      fetchStats()
      fetchTasks()
    }
  } catch (e) {
    ElMessage.info('已取消清理')
  }
}

const handleTaskSizeChange = (val: number) => {
  taskPageSize.value = val
  fetchTasks()
}

const handleTaskCurrentChange = (val: number) => {
  taskCurrentPage.value = val
  fetchTasks()
}

onMounted(() => {
  fetchData()
  fetchStats()
  fetchTasks()
})
</script>

<style scoped>
.report-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.blue {
  background: #dbeafe;
  color: #3b82f6;
}

.stat-icon.green {
  background: #dcfce7;
  color: #22c55e;
}

.stat-icon.orange {
  background: #fef3c7;
  color: #f59e0b;
}

.stat-icon.purple {
  background: #ede9fe;
  color: #8b5cf6;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 250px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.report-detail {
  padding: 10px;
}

.detail-row {
  margin-bottom: 10px;
}

.detail-label {
  font-weight: bold;
  color: #666;
}

.detail-value {
  color: #333;
}

.detail-content {
  background: #f9fafb;
  padding: 15px;
  border-radius: 4px;
  min-height: 100px;
  white-space: pre-wrap;
}

.detail-comment {
  background: #fff3cd;
  padding: 15px;
  border-radius: 4px;
  border-left: 4px solid #ffc107;
}

.ml-4 {
  margin-left: 16px;
}

.mt-2 {
  margin-top: 8px;
}

.task-status {
  padding: 10px;
}

.status-header {
  margin-bottom: 20px;
}

.status-header h3 {
  margin: 0 0 10px 0;
}

.status-summary {
  display: flex;
  gap: 20px;
}

.status-summary span {
  font-size: 14px;
}
</style>
