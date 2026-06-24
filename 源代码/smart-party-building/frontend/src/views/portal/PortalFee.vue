<template>
  <div class="portal-fee">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>党费缴费</span>
        </div>
      </template>

      <div class="fee-summary">
        <div class="summary-item">
          <div class="summary-value">{{ feeSummary.totalAmount }}</div>
          <div class="summary-label">累计缴费总额（元）</div>
        </div>
        <div class="summary-item">
          <div class="summary-value">{{ feeSummary.paidCount }}</div>
          <div class="summary-label">已缴月数</div>
        </div>
        <div class="summary-item">
          <div class="summary-value">{{ feeSummary.unpaidCount }}</div>
          <div class="summary-label">待缴月数</div>
        </div>
      </div>
    </el-card>

    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>缴费通知</span>
        </div>
      </template>

      <el-table :data="notifications" stripe v-if="notifications.length > 0">
        <el-table-column prop="title" label="通知标题" min-width="180">
          <template #default="{ row }">
            <div class="notification-title">
              <el-icon color="#f59e0b"><Bell /></el-icon>
              <span>{{ row.title }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="说明" min-width="120">
          <template #default="{ row }">{{ row.content || '-' }}</template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getNotificationStatus(row.id).status === '已缴费' ? 'success' : 'warning'" size="small">
              {{ getNotificationStatus(row.id).status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="getNotificationStatus(row.id).status === '待缴费'"
              type="primary"
              size="small"
              @click="handlePay(row)"
            >
              去缴费
            </el-button>
            <el-button
              v-else
              type="success"
              size="small"
              @click="handleViewDetail(row)"
            >
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无缴费通知" />
    </el-card>

    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>缴费记录</span>
          <el-select v-model="queryYear" placeholder="选择年份" style="width:120px" @change="fetchFeeSummary">
            <el-option v-for="y in years" :key="y" :label="y + '年'" :value="y" />
          </el-select>
        </div>
      </template>

      <el-table :data="feeRecords" stripe>
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="month" label="月份" width="80">
          <template #default="{ row }">{{ row.month }}月</template>
        </el-table-column>
        <el-table-column prop="amount" label="缴费金额（元）" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '已缴费' ? 'success' : 'warning'" size="small">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费时间">
          <template #default="{ row }">
            {{ row.status === '已缴费' ? row.payTime : '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="党费缴纳" v-model="payDialogVisible" width="450px" destroy-on-close>
      <div v-if="currentNotification" class="pay-dialog-content">
        <div class="pay-notification-info">
          <h4>{{ currentNotification.title }}</h4>
          <p v-if="currentNotification.content">{{ currentNotification.content }}</p>
        </div>
        <el-form :model="payForm" ref="payFormRef" label-width="100px" :rules="payRules">
          <el-form-item label="缴费金额" prop="amount">
            <el-input-number
              v-model="payForm.amount"
              :min="0.01"
              :precision="2"
              :step="1"
              style="width: 100%"
            />
          </el-form-item>
          <div class="pay-tip">
            <el-icon><InfoFilled /></el-icon>
            <span>请确认缴费金额后点击"确认缴费"按钮完成缴费</span>
          </div>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="payDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmPay" :loading="payLoading">确认缴费</el-button>
      </template>
    </el-dialog>

    <el-dialog title="缴费详情" v-model="detailDialogVisible" width="450px" destroy-on-close>
      <div v-if="currentFee" class="detail-content">
        <div class="detail-row">
          <span class="detail-label">通知标题：</span>
          <span class="detail-value">{{ currentFee.notification?.title || '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">缴费年份：</span>
          <span class="detail-value">{{ currentFee.year }}年</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">缴费月份：</span>
          <span class="detail-value">{{ currentFee.month }}月</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">缴费金额：</span>
          <span class="detail-value" style="color: #22c55e; font-weight: 600;">{{ currentFee.amount }}元</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">缴费状态：</span>
          <el-tag :type="currentFee.status === '已缴费' ? 'success' : 'warning'">
            {{ currentFee.status }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">缴费时间：</span>
          <span class="detail-value">{{ currentFee.payTime || '-' }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Bell, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import axios from '@/utils/axios'

const userStore = useUserStore()

interface FeeNotification {
  id: number
  title: string
  content: string
  year: string
  month: string
  publishTime: string
}

interface FeeRecord {
  id: number
  year: string
  month: string
  amount: string
  status: string
  payTime: string
  notification?: FeeNotification
}

interface FeeSummary {
  totalAmount: string
  paidCount: number
  unpaidCount: number
  allFees: FeeRecord[]
}

const notifications = ref<FeeNotification[]>([])
const feeRecords = ref<FeeRecord[]>([])
const notificationStatuses = ref<Record<number, { feeId: number; status: string; amount: string; payTime: string }>>({})

const queryYear = ref(new Date().getFullYear())

const years = computed(() => {
  const current = new Date().getFullYear()
  return Array.from({ length: 5 }, (_, i) => current - i)
})

const feeSummary = computed<FeeSummary>(() => {
  let totalAmount = 0
  let paidCount = 0
  let unpaidCount = 0

  feeRecords.value.forEach(f => {
    if (f.status === '已缴费') {
      totalAmount += parseFloat(f.amount) || 0
      paidCount++
    } else {
      unpaidCount++
    }
  })

  return {
    totalAmount: totalAmount.toFixed(2),
    paidCount,
    unpaidCount,
    allFees: feeRecords.value
  }
})

const payDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentNotification = ref<FeeNotification | null>(null)
const currentFee = ref<FeeRecord | null>(null)
const payFormRef = ref<FormInstance | null>(null)
const payLoading = ref(false)
const payForm = reactive({
  amount: 0
})

const payRules = {
  amount: [
    { required: true, message: '请输入缴费金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '金额必须大于0', trigger: 'blur' }
  ]
}

const fetchNotifications = async () => {
  const memberId = userStore.memberId
  if (!memberId) return
  
  try {
    const res = await axios.get(`/api/portal/fee/notifications/${memberId}`)
    if (res.data.code === 200) {
      notifications.value = res.data.data || []

      for (const notification of notifications.value) {
        await fetchNotificationStatus(notification.id)
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchNotificationStatus = async (notificationId: number) => {
  const memberId = userStore.memberId
  if (!memberId) return

  try {
    const res = await axios.get(`/api/portal/fee/notification/${notificationId}/status`, {
      params: { memberId }
    })
    if (res.data.code === 200) {
      notificationStatuses.value[notificationId] = {
        feeId: res.data.data.feeId,
        status: res.data.data.status,
        amount: res.data.data.amount,
        payTime: res.data.data.payTime
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const getNotificationStatus = (notificationId: number) => {
  return notificationStatuses.value[notificationId] || { feeId: null, status: '待缴费', amount: '0', payTime: '' }
}

const fetchFeeSummary = async () => {
  const memberId = userStore.memberId
  if (!memberId) return

  try {
    const res = await axios.get(`/api/portal/fee/summary/${memberId}`)
    if (res.data.code === 200) {
      const allFees: FeeRecord[] = res.data.data.allFees || []
      feeRecords.value = allFees.filter((f: FeeRecord) => parseInt(f.year) === queryYear.value)
    }
  } catch (e) {
    console.error(e)
  }
}

const handlePay = (notification: FeeNotification) => {
  currentNotification.value = notification
  const status = getNotificationStatus(notification.id)
  payForm.amount = status.amount && status.amount !== '0' ? parseFloat(status.amount) : 0.01
  payDialogVisible.value = true
}

const handleConfirmPay = async () => {
  if (!payFormRef.value) return

  try {
    await payFormRef.value.validate()
  } catch {
    return
  }

  const notificationId = currentNotification.value?.id
  if (!notificationId) return

  const status = getNotificationStatus(notificationId)
  if (!status.feeId) {
    ElMessage.error('缴费记录不存在')
    return
  }

  payLoading.value = true
  try {
    const res = await axios.put(`/api/portal/fee/pay/${status.feeId}`, {
      amount: payForm.amount
    })

    if (res.data.code === 200) {
      ElMessage.success('缴费成功')
      payDialogVisible.value = false
      await fetchNotifications()
      await fetchFeeSummary()
    } else {
      ElMessage.error(res.data.message || '缴费失败')
    }
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '缴费失败')
  } finally {
    payLoading.value = false
  }
}

const handleViewDetail = (notification: FeeNotification) => {
  const status = getNotificationStatus(notification.id)

  currentFee.value = {
    id: status.feeId,
    year: notification.year,
    month: notification.month,
    amount: status.amount || '0',
    status: status.status,
    payTime: status.payTime,
    notification: notification
  }

  detailDialogVisible.value = true
}

onMounted(async () => {
  await fetchNotifications()
  await fetchFeeSummary()
})
</script>

<style scoped>
.portal-fee {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.fee-summary {
  display: flex;
  gap: 40px;
  padding: 20px;
  background: linear-gradient(135deg, #FEF3C7, #FDE68A);
  border-radius: 12px;
}

.summary-item {
  text-align: center;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #D97706;
}

.summary-label {
  font-size: 13px;
  color: #92400E;
  margin-top: 4px;
}

.notification-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pay-dialog-content {
  padding: 10px 0;
}

.pay-notification-info {
  background: #f9fafb;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.pay-notification-info h4 {
  margin: 0 0 8px 0;
  color: #1f2937;
}

.pay-notification-info p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.pay-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  background: #eff6ff;
  border-radius: 6px;
  color: #3b82f6;
  font-size: 13px;
  margin-top: 15px;
}

.detail-content {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-label {
  width: 100px;
  color: #6b7280;
  flex-shrink: 0;
}

.detail-value {
  color: #1f2937;
  font-weight: 500;
}
</style>
