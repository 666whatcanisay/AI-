<template>
  <div class="fee-page">
    <div class="page-header">
      <h2>党费管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handlePublishNotification">
          <el-icon><component :is="icons.Bell" /></el-icon>
          发布缴费通知
        </el-button>
        <el-button type="danger" @click="handleCleanAll">
          <el-icon><component :is="icons.Trash" /></el-icon>
          清理所有数据
        </el-button>
      </div>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><component :is="icons.Wallet" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalAmount }}</div>
          <div class="stat-label">本年累计缴费</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><component :is="icons.CheckCircle" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ paidCount }}</div>
          <div class="stat-label">已缴费人次</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><component :is="icons.AlertCircle" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ unpaidCount }}</div>
          <div class="stat-label">待缴费人次</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon purple">
          <el-icon><component :is="icons.Calendar" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ notificationCount }}</div>
          <div class="stat-label">已发布通知</div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-select v-model="notificationSearch.year" placeholder="选择年份" style="width:120px">
          <el-option v-for="year in years" :key="year" :label="year" :value="year" />
        </el-select>
        <el-select v-model="notificationSearch.status" placeholder="通知状态" clearable style="width:120px">
          <el-option label="已发布" value="已发布" />
          <el-option label="草稿" value="草稿" />
        </el-select>
        <el-button type="primary" @click="fetchNotifications">搜索</el-button>
        <el-button @click="notificationSearch.year = new Date().getFullYear().toString(); notificationSearch.status = ''; fetchNotifications()">重置</el-button>
      </div>
      <el-table :data="notificationTableData" border stripe>
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (notificationCurrentPage - 1) * notificationPageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="通知标题" min-width="180" />
        <el-table-column prop="year" label="年份" width="100" />
        <el-table-column prop="month" label="月份" width="100">
          <template #default="scope">{{ scope.row.month }}月</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '已发布' ? 'success' : 'info'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleViewNotificationStatus(scope.row)">查看缴费状态</el-button>
            <el-button size="small" type="danger" @click="handleDeleteNotification(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          @size-change="handleNotificationSizeChange"
          @current-change="handleNotificationCurrentChange"
          :current-page="notificationCurrentPage"
          :page-sizes="[10, 20, 30, 40]"
          :page-size="notificationPageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="notificationTotal"
        />
      </div>
    </div>

    <el-dialog title="发布缴费通知" v-model="notificationDialogVisible" width="450px" destroy-on-close>
      <el-form :model="notificationForm" ref="notificationFormRef" label-width="100px" :rules="notificationRules">
        <el-form-item label="年份" prop="year">
          <el-select v-model="notificationForm.year" placeholder="请选择年份" style="width:100%">
            <el-option v-for="year in years" :key="year" :label="year" :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="notificationForm.month" placeholder="请选择月份" style="width:100%">
            <el-option v-for="month in months" :key="month" :label="month + '月'" :value="month" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知说明" prop="content">
          <el-input v-model="notificationForm.content" type="textarea" :rows="3" placeholder="请输入缴费通知说明（选填）" />
        </el-form-item>
        <el-alert type="info" :closable="false" show-icon>
          <template #title>
            发布通知后，系统将自动为所有党员创建待缴费记录
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="notificationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublishNotificationSubmit" :loading="notificationLoading">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog title="缴费状态" v-model="statusDialogVisible" width="900px" destroy-on-close>
      <div v-if="currentNotification" class="notification-status">
        <div class="status-header">
          <h3>{{ currentNotification.title }}</h3>
          <div class="status-summary">
            <span>总党员数：<strong>{{ notificationStatus.totalMembers }}</strong></span>
            <span>已缴费：<strong style="color:#22c55e">{{ notificationStatus.paidCount }}</strong></span>
            <span>待缴费：<strong style="color:#f59e0b">{{ notificationStatus.unpaidCount }}</strong></span>
            <span>缴费总额：<strong style="color:#3b82f6">{{ notificationStatus.totalAmount }}元</strong></span>
          </div>
        </div>
        <el-table :data="notificationStatus.feeList" border stripe max-height="400">
          <el-table-column prop="memberName" label="党员姓名" width="120" />
          <el-table-column prop="year" label="年份" width="100" />
          <el-table-column prop="month" label="月份" width="100" />
          <el-table-column prop="amount" label="缴费金额" width="120">
            <template #default="scope">{{ scope.row.amount || '-' }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === '已缴费' ? 'success' : 'warning'">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="缴费时间" width="180">
            <template #default="scope">
              {{ scope.row.status === '已缴费' ? scope.row.payTime : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button size="small" type="primary" @click="handleViewMemberDetail(scope.row.memberId)">
                查看详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <el-dialog title="党员详情" v-model="memberDetailDialogVisible" width="600px" destroy-on-close>
      <div v-if="currentMember" class="member-detail">
        <h4>基本信息</h4>
        <div class="detail-row">
          <span class="label">姓名：</span>
          <span>{{ currentMember.name || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">性别：</span>
          <span>{{ currentMember.gender || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">出生年月：</span>
          <span>{{ currentMember.birthDate || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">联系电话：</span>
          <span>{{ currentMember.phone || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">入党时间：</span>
          <span>{{ currentMember.joinPartyTime || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">党员状态：</span>
          <span>{{ currentMember.partyStatus === 'FORMAL' ? '正式党员' : currentMember.partyStatus === 'PROBATIONARY' ? '预备党员' : currentMember.partyStatus || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">备注：</span>
          <span>{{ currentMember.remark || '-' }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Bell, Wallet, CircleCheck, Warning, Calendar, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import axios from '@/utils/axios'

const icons = { Bell, Wallet, CheckCircle: CircleCheck, AlertCircle: Warning, Calendar, Trash: Delete }

const years = ['2024', '2025', '2026', '2027', '2028']
const months = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']

const notificationSearch = reactive({
  year: new Date().getFullYear().toString(),
  status: ''
})

const notificationTableData = ref<any[]>([])
const notificationCurrentPage = ref(1)
const notificationPageSize = ref(10)
const notificationTotal = ref(0)
const notificationCount = ref(0)

const totalAmount = ref('0.00')
const paidCount = ref(0)
const unpaidCount = ref(0)

const notificationDialogVisible = ref(false)
const notificationFormRef = ref<FormInstance | null>(null)
const notificationForm = reactive({
  year: new Date().getFullYear().toString(),
  month: String(new Date().getMonth() + 1).padStart(2, '0'),
  content: ''
})
const notificationLoading = ref(false)
const notificationRules = {
  year: [{ required: true, message: '请选择年份', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }]
}

const statusDialogVisible = ref(false)
const currentNotification = ref<any>(null)
const notificationStatus = reactive({
  totalMembers: 0,
  paidCount: 0,
  unpaidCount: 0,
  totalAmount: '0.00',
  feeList: [] as any[]
})

const memberDetailDialogVisible = ref(false)
const currentMember = ref<any>(null)

const fetchNotifications = async () => {
  try {
    const response = await axios.get('/api/fee-notification', {
      params: {
        pageNum: notificationCurrentPage.value,
        pageSize: notificationPageSize.value,
        year: notificationSearch.year,
        status: notificationSearch.status
      }
    })
    if (response.data.code === 200) {
      notificationTableData.value = response.data.data.records
      notificationTotal.value = response.data.data.total
      notificationCount.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取缴费通知失败:', error)
    ElMessage.error('获取缴费通知失败')
  }
}

const fetchStats = async () => {
  try {
    const response = await axios.get(`/api/fee/statistics/${new Date().getFullYear().toString()}`)
    if (response.data.code === 200) {
      totalAmount.value = response.data.data.totalAmount ? parseFloat(response.data.data.totalAmount).toFixed(2) : '0.00'
      paidCount.value = response.data.data.paidCount || 0
      unpaidCount.value = response.data.data.unpaidCount || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const handlePublishNotification = () => {
  notificationForm.year = new Date().getFullYear().toString()
  notificationForm.month = String(new Date().getMonth() + 1).padStart(2, '0')
  notificationForm.content = ''
  notificationDialogVisible.value = true
}

const handlePublishNotificationSubmit = async () => {
  if (!notificationFormRef.value) return
  try {
    const valid = await notificationFormRef.value.validate()
    if (!valid) return

    notificationLoading.value = true
    await axios.post('/api/fee-notification', {
      year: notificationForm.year,
      month: notificationForm.month,
      content: notificationForm.content
    })
    ElMessage.success('发布成功')
    notificationDialogVisible.value = false
    fetchNotifications()
    fetchStats()
  } catch (error: any) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('发布失败')
    }
  } finally {
    notificationLoading.value = false
  }
}

const handleViewNotificationStatus = async (row: any) => {
  currentNotification.value = row
  try {
    const response = await axios.get(`/api/fee-notification/status/${row.id}`)
    if (response.data.code === 200) {
      notificationStatus.totalMembers = response.data.data.totalMembers
      notificationStatus.paidCount = response.data.data.paidCount
      notificationStatus.unpaidCount = response.data.data.unpaidCount
      notificationStatus.totalAmount = response.data.data.totalAmount
      notificationStatus.feeList = response.data.data.feeList
      statusDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取缴费状态失败:', error)
    ElMessage.error('获取缴费状态失败')
  }
}

const handleDeleteNotification = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条缴费通知吗？删除后将同时删除对应的缴费记录。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await axios.delete(`/api/fee-notification/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      fetchNotifications()
      fetchStats()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleViewMemberDetail = async (memberId: number) => {
  try {
    const response = await axios.get(`/api/member/${memberId}`)
    if (response.data.code === 200) {
      currentMember.value = response.data.data
      memberDetailDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取党员信息失败:', error)
    ElMessage.error('获取党员信息失败')
  }
}

const handleCleanAll = async () => {
  try {
    await ElMessageBox.confirm('确定要清理所有缴费数据吗？此操作将删除所有缴费通知和缴费记录，且不可恢复！', '危险操作', {
      confirmButtonText: '确定清理',
      cancelButtonText: '取消',
      type: 'error'
    })
    const response = await axios.delete('/api/fee-notification/clean-all')
    if (response.data.code === 200) {
      ElMessage.success('清理成功')
      fetchNotifications()
      fetchStats()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('清理失败:', error)
      ElMessage.error('清理失败')
    }
  }
}

const handleNotificationSizeChange = (val: number) => {
  notificationPageSize.value = val
  fetchNotifications()
}

const handleNotificationCurrentChange = (val: number) => {
  notificationCurrentPage.value = val
  fetchNotifications()
}

onMounted(() => {
  fetchNotifications()
  fetchStats()
})
</script>

<style scoped>
.fee-page {
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

.header-actions {
  display: flex;
  gap: 10px;
}

.stats-cards {
  display: flex;
  gap: 20px;
}

.stat-card {
  flex: 1;
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
  background: #e9d5ff;
  color: #9333ea;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.notification-status {
  padding: 10px 0;
}

.status-header {
  margin-bottom: 20px;
}

.status-header h3 {
  margin: 0 0 15px 0;
  color: #1f2937;
}

.status-summary {
  display: flex;
  gap: 30px;
  padding: 15px;
  background: #f9fafb;
  border-radius: 8px;
}

.status-summary span {
  color: #6b7280;
  font-size: 14px;
}

.member-detail {
  padding: 10px 0;
}

.member-detail h4 {
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
  color: #1f2937;
}

.detail-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  width: 100px;
  color: #6b7280;
  font-weight: 500;
  flex-shrink: 0;
}

.detail-row span:last-child {
  color: #1f2937;
}

.status-summary strong {
  color: #1f2937;
  font-size: 16px;
}
</style>
