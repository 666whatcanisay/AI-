<template>
  <div class="portal-report">
    <div class="page-header">
      <h2>思想汇报</h2>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">汇报总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">审核中</div>
        </div>
      </div>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的汇报记录</span>
        </div>
      </template>

      <el-table :data="reportList" stripe>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'PENDING' ? 'warning' : 'danger'" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
              <el-button type="warning" link size="small" @click="handleEdit(row)" v-if="row.status === 'REJECTED'">重新提交</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row.id)" v-if="row.status === 'PENDING'">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>待完成任务</span>
        </div>
      </template>

      <div v-if="taskList.length === 0" class="empty-tip">
        <el-icon :size="48" class="empty-icon"><Bell /></el-icon>
        <p>暂无待完成的思想汇报任务</p>
      </div>

      <el-table v-else :data="taskList" stripe>
        <el-table-column prop="title" label="任务标题" />
        <el-table-column prop="year" label="年份" width="100" />
        <el-table-column prop="quarter" label="季度" width="100">
          <template #default="scope">{{ scope.row.quarter }}季度</template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止日期" width="200" />
        <el-table-column prop="publishTime" label="发布时间" width="200">
          <template #default="scope">{{ formatDateTime(scope.row.publishTime) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="任务说明" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              :disabled="isTaskExpired(row) || row.status === '已结束'"
              @click="handleSubmitReportForTask(row)"
            >
              {{ isTaskExpired(row) ? '已过截止时间' : row.status === '已结束' ? '任务已结束' : '立即提交' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 提交汇报对话框 -->
    <el-dialog v-model="showDialog" :title="editingId ? '重新提交思想汇报' : '提交思想汇报'" width="600px" destroy-on-close>
      <el-form :model="form" ref="formRef" label-width="80px" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入汇报标题" />
        </el-form-item>
        <el-form-item label="汇报日期" prop="reportDate">
          <el-date-picker
            v-model="form.reportDate"
            type="date"
            placeholder="选择汇报日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入汇报内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialog" title="思想汇报详情" width="600px">
      <div class="report-detail" v-if="currentReport">
        <h3>{{ currentReport.title }}</h3>
        <div class="report-meta">
          <span>作者：{{ currentReport.author }}</span>
          <span>汇报日期：{{ currentReport.reportDate }}</span>
          <el-tag :type="currentReport.status === 'APPROVED' ? 'success' : currentReport.status === 'PENDING' ? 'warning' : 'danger'" size="small">
            {{ getStatusLabel(currentReport.status) }}
          </el-tag>
        </div>
        <div class="report-meta">
          <span>提交时间：{{ currentReport.createTime }}</span>
          <span v-if="currentReport.reviewTime">审核时间：{{ currentReport.reviewTime }}</span>
        </div>
        <el-divider />
        <div class="report-content">{{ currentReport.content }}</div>
        <div v-if="currentReport.reviewComment" class="review-comment">
          <div class="comment-label">审核意见：</div>
          <div class="comment-content">{{ currentReport.reviewComment }}</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Document, CircleCheck, Clock, Bell } from '@element-plus/icons-vue'

const userStore = useUserStore()
const reportList = ref<any[]>([])
const taskList = ref<any[]>([])
let syncTimer: ReturnType<typeof setInterval> | null = null
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showDialog = ref(false)
const viewDialog = ref(false)
const submitting = ref(false)
const editingId = ref<number | null>(null)
const currentReport = ref<any>(null)

const stats = reactive({
  total: 0,
  pending: 0,
  approved: 0,
  rejected: 0
})

const formRef = ref<InstanceType<typeof import('element-plus').ElForm>>()
const form = reactive({
  title: '',
  reportDate: '',
  content: '',
  taskId: null as number | null,
  memberId: null as number | null
})

const rules = {
  title: [
    { required: true, message: '请输入汇报标题', trigger: 'blur' }
  ],
  reportDate: [
    { required: true, message: '请选择汇报日期', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入汇报内容', trigger: 'blur' }
  ]
}

const getStatusLabel = (status: string) => {
  const map: Record<string, string> = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已退回' }
  return map[status] || status
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return '-'
  try {
    const date = new Date(dateTime)
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
  } catch {
    return dateTime
  }
}

const isTaskExpired = (task: any) => {
  if (!task.deadline) return false
  try {
    const deadline = new Date(task.deadline)
    const now = new Date()
    return now > deadline
  } catch {
    return false
  }
}

const fetchData = async () => {
  try {
    const res = await axios.get('/api/portal/report', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value, memberId: userStore.memberId }
    })
    if (res.data.code === 200) {
      reportList.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchStats = async () => {
  try {
    const res = await axios.get(`/api/portal/report/stats/${userStore.memberId}`)
    if (res.data.code === 200) {
      Object.assign(stats, res.data.data)
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchTasks = async () => {
  try {
    // 先更新过期任务状态
    await axios.put('/api/report/task/update-expired')
    
    const res = await axios.get('/api/portal/report/task', {
      params: { memberId: userStore.memberId }
    })
    if (res.data.code === 200) {
      taskList.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

const syncReportPage = () => {
  fetchData()
  fetchStats()
  fetchTasks()
}

const handleView = (row: any) => {
  currentReport.value = row
  viewDialog.value = true
}

const handleEdit = (row: any) => {
  editingId.value = row.id
  form.title = row.title
  form.reportDate = row.reportDate
  form.content = row.content
  form.taskId = row.taskId
  form.memberId = row.memberId
  showDialog.value = true
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
    submitting.value = true
    let res
    if (editingId.value) {
      res = await axios.put(`/api/report/${editingId.value}`, {
        title: form.title,
        reportDate: form.reportDate,
        content: form.content,
        status: 'PENDING'
      })
    } else {
      res = await axios.post('/api/portal/report', {
        ...form,
        memberId: userStore.memberId
      })
    }
    if (res.data.code === 200) {
      ElMessage.success(editingId.value ? '重新提交成功' : '提交成功')
      showDialog.value = false
      form.title = ''
      form.reportDate = ''
      form.content = ''
      form.taskId = null
      editingId.value = null
      fetchData()
      fetchStats()
      fetchTasks()
    } else {
      ElMessage.error(res.data.message || '提交失败')
    }
  } catch (e: any) {
    if (e.response && e.response.data && e.response.data.message) {
      ElMessage.error(e.response.data.message)
    } else if (e !== 'cancel') {
      ElMessage.error('提交失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleSubmitReportForTask = (task: any) => {
  form.title = `${task.year}年第${task.quarter}季度思想汇报`
  form.reportDate = new Date().toISOString().split('T')[0]
  form.content = ''
  form.taskId = task.id
  form.memberId = userStore.memberId
  showDialog.value = true
}

onMounted(() => {
  syncReportPage()
  window.addEventListener('focus', syncReportPage)
  syncTimer = setInterval(syncReportPage, 15000)
})

onUnmounted(() => {
  window.removeEventListener('focus', syncReportPage)
  if (syncTimer) {
    clearInterval(syncTimer)
    syncTimer = null
  }
})
</script>

<style scoped>
.portal-report {
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

.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.report-detail {
  padding: 10px;
}

.report-detail h3 {
  margin: 0 0 15px 0;
}

.report-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.report-content {
  background: #f9fafb;
  padding: 15px;
  border-radius: 4px;
  min-height: 150px;
  white-space: pre-wrap;
}

.review-comment {
  margin-top: 20px;
  padding: 15px;
  background: #fff3cd;
  border-radius: 4px;
  border-left: 4px solid #ffc107;
}

.comment-label {
  font-weight: bold;
  margin-bottom: 5px;
}

.comment-content {
  white-space: pre-wrap;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #999;
}

.empty-icon {
  margin-bottom: 10px;
}
</style>
