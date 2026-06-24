<template>
  <div class="approval-page">
    <div class="page-header">
      <h2>审批管理</h2>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入申请人姓名"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="申请状态">
          <el-option label="全部" value="" />
          <el-option label="申请中" value="APPLYING" />
          <el-option label="积极分子" value="ACTIVIST" />
          <el-option label="发展对象" value="DEVELOP_TARGET" />
          <el-option label="预备党员" value="PROBATIONARY" />
        </el-select>
        <el-select v-model="searchForm.level" placeholder="审批层级">
          <el-option label="全部" value="" />
          <el-option label="支部审核" value="LEVEL_1" />
          <el-option label="党委审核" value="LEVEL_2" />
          <el-option label="组织部审核" value="LEVEL_3" />
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
        <el-table-column prop="name" label="申请人" />
        <el-table-column prop="department" label="所在部门" />
        <el-table-column prop="currentLevel" label="当前层级">
          <template #default="scope">
            <el-tag :type="getLevelType(scope.row.currentLevel)">
              {{ getLevelLabel(scope.row.currentLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" />
        <el-table-column prop="lastUpdateTime" label="更新时间" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" @click="handleView(scope.row)">查看详情</el-button>
              <el-button size="small" type="primary" @click="handleApprove(scope.row)" v-if="canApprove(scope.row)">审批</el-button>
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
    <el-dialog title="申请详情" v-model="detailVisible" width="800px">
      <div v-if="currentApplication" class="approval-detail">
        <div class="detail-row">
          <span class="detail-label">申请人：</span>
          <span class="detail-value">{{ currentApplication.name }}</span>
          <span class="detail-label ml-4">性别：</span>
          <span class="detail-value">{{ currentApplication.gender }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">出生年月：</span>
          <span class="detail-value">{{ currentApplication.birthDate }}</span>
          <span class="detail-label ml-4">所在部门：</span>
          <span class="detail-value">{{ currentApplication.department }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话：</span>
          <span class="detail-value">{{ currentApplication.phone }}</span>
          <span class="detail-label ml-4">申请时间：</span>
          <span class="detail-value">{{ currentApplication.createTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">当前状态：</span>
          <el-tag :type="getStatusType(currentApplication.status)" size="large">
            {{ getStatusLabel(currentApplication.status) }}
          </el-tag>
        </div>

        <div class="process-section">
          <h4>审批流程</h4>
          <div class="step-progress">
            <div 
              v-for="(step, index) in statusSteps" 
              :key="step.key"
              class="progress-step"
              :class="{ 
                'step-active': isStepActive(currentApplication.status, step.key),
                'step-completed': isStepCompleted(currentApplication.status, step.key),
                'step-pending': isStepPending(currentApplication.status, step.key)
              }"
            >
              <div class="step-circle">
                <el-icon v-if="isStepCompleted(currentApplication.status, step.key)"><CircleCheck /></el-icon>
                <span v-else>{{ index + 1 }}</span>
              </div>
              <span class="step-label">{{ step.label }}</span>
            </div>
          </div>
          <el-timeline class="mt-4">
            <el-timeline-item
              v-for="(record, index) in approvalRecords"
              :key="index"
              :status="record.approved ? 'success' : 'warning'"
            >
              <template #dot>
                <el-icon :size="18">{{ record.approved ? CircleCheck : Clock }}</el-icon>
              </template>
              <el-card>
                <div class="record-header">
                  <span class="record-title">{{ record.levelName }}</span>
                  <el-tag :type="record.approved ? 'success' : 'warning'">
                    {{ record.approved ? '已通过' : '待审核' }}
                  </el-tag>
                </div>
                <div class="record-info">
                  <span class="info-item">审核人：{{ record.operator || '-' }}</span>
                  <span class="info-item">审核时间：{{ record.approveTime || '-' }}</span>
                </div>
                <div v-if="record.comment" class="record-comment">
                  <span class="comment-label">审核意见：</span>
                  <span>{{ record.comment }}</span>
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleApprove(currentApplication)" v-if="canApprove(currentApplication)">审批</el-button>
      </template>
    </el-dialog>
    <el-dialog title="审批操作" v-model="approveVisible" width="500px">
      <el-form :model="approveForm" ref="approveFormRef" label-width="100px">
        <el-form-item label="审批意见">
          <el-input type="textarea" v-model="approveForm.comment" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item>
          <el-radio-group v-model="approveForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApproval" :loading="approveLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, CircleCheck, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

const searchForm = reactive({
  name: '',
  status: '',
  level: ''
})

const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const approveLoading = ref(false)

const detailVisible = ref(false)
const approveVisible = ref(false)

interface ApprovalApplication {
  id: number
  name: string
  gender: string
  birthDate: string
  department: string
  phone: string
  status: string
  currentLevel: string
  createTime: string
  lastUpdateTime?: string
}

interface ApprovalRecord {
  levelName: string
  approved: boolean
  operator?: string
  approveTime?: string
  comment?: string
}

const currentApplication = ref<ApprovalApplication | null>(null)
const approvalRecords = ref<ApprovalRecord[]>([])

const approveFormRef = ref<InstanceType<typeof import('element-plus').ElForm>>()
const approveForm = reactive({
  approved: true,
  comment: ''
})

const statusSteps = [
  { key: 'APPLYING', label: '提交申请' },
  { key: 'ACTIVIST', label: '积极分子' },
  { key: 'DEVELOP_TARGET', label: '发展对象' },
  { key: 'PROBATIONARY', label: '预备党员' },
  { key: 'FORMAL', label: '正式党员' }
]

const levelMap: Record<string, { label: string; type: string }> = {
  LEVEL_1: { label: '支部审核', type: 'warning' },
  LEVEL_2: { label: '党委审核', type: 'primary' },
  LEVEL_3: { label: '组织部审核', type: 'success' }
}

const statusMap: Record<string, { label: string; type: string }> = {
  APPLYING: { label: '申请中', type: 'warning' },
  ACTIVIST: { label: '积极分子', type: 'info' },
  DEVELOP_TARGET: { label: '发展对象', type: 'primary' },
  PROBATIONARY: { label: '预备党员', type: 'success' },
  FORMAL: { label: '正式党员', type: 'success' }
}

const getStatusLabel = (status: string) => statusMap[status]?.label || status
const getStatusType = (status: string) => statusMap[status]?.type || 'info'
const getLevelLabel = (level: string) => levelMap[level]?.label || level
const getLevelType = (level: string) => levelMap[level]?.type || 'info'

const statusOrder = ['APPLYING', 'ACTIVIST', 'DEVELOP_TARGET', 'PROBATIONARY', 'FORMAL']

const isStepCompleted = (currentStatus: string, stepKey: string) => {
  const currentIndex = statusOrder.indexOf(currentStatus)
  const stepIndex = statusOrder.indexOf(stepKey)
  return stepIndex < currentIndex
}

const isStepActive = (currentStatus: string, stepKey: string) => {
  return currentStatus === stepKey
}

const isStepPending = (currentStatus: string, stepKey: string) => {
  const currentIndex = statusOrder.indexOf(currentStatus)
  const stepIndex = statusOrder.indexOf(stepKey)
  return stepIndex > currentIndex
}

const canApprove = (application: any) => {
  if (!application) return false
  const pendingLevels = ['LEVEL_1', 'LEVEL_2', 'LEVEL_3']
  return pendingLevels.includes(application.currentLevel)
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/approval', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        name: searchForm.name,
        status: searchForm.status,
        level: searchForm.level
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取审批列表失败:', error)
    ElMessage.error('获取审批列表失败')
  }
}

const handleView = async (row: any) => {
  currentApplication.value = row
  try {
    const response = await axios.get(`/api/application/${row.id}/records`)
    if (response.data.code === 200) {
      approvalRecords.value = response.data.data
    }
  } catch (error) {
    console.error('获取审批记录失败:', error)
    approvalRecords.value = []
  }
  detailVisible.value = true
}

const handleApprove = async (application: any) => {
  try {
    // 先从后端获取最新完整数据
    const res = await axios.get(`/api/application/${application.id}`)
    if (res.data.code === 200 && res.data.data) {
      application = res.data.data
    }
  } catch (e) {
    console.error('获取申请信息失败:', e)
  }
  // 检查申请人信息是否完整
  const missingFields: string[] = []
  if (!application.name) missingFields.push('姓名')
  if (!application.gender) missingFields.push('性别')
  if (!application.birthDate) missingFields.push('出生日期')
  if (!application.department) missingFields.push('所在党支部')
  if (!application.phone) missingFields.push('联系电话')
  if (!application.idCard) missingFields.push('身份证号')
  if (!application.studentNo) missingFields.push('学号')
  if (!application.className) missingFields.push('班级')
  if (!application.introducer) missingFields.push('入党介绍人')
  if (missingFields.length > 0) {
    ElMessage.warning(`申请人信息不完整，缺少：${missingFields.join('、')}，请先补充信息后再审批`)
    return
  }
  currentApplication.value = application
  approveForm.approved = true
  approveForm.comment = ''
  approveVisible.value = true
}

const submitApproval = async () => {
  if (!currentApplication.value) return
  try {
    approveLoading.value = true
    const response = await axios.post(`/api/approval/${currentApplication.value.id}`, {
      approved: approveForm.approved,
      comment: approveForm.comment
    })
    if (response.data.code === 200) {
      ElMessage.success('审批成功')
      approveVisible.value = false
      detailVisible.value = false
      fetchData()
    } else {
      ElMessage.warning(response.data.message || '审批失败')
    }
  } catch (error: any) {
    console.error('审批失败:', error)
    ElMessage.error(error.response?.data?.message || '审批失败')
  } finally {
    approveLoading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  searchForm.level = ''
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-page {
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

.approval-detail {
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
  margin-bottom: 10px;
}

.process-section {
  margin-top: 20px;
}

.process-section h4 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
}

.step-progress {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #e5e7eb;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  position: relative;
}

.progress-step:not(:last-child)::after {
  content: '';
  position: absolute;
  top: 15px;
  left: 50%;
  width: 50%;
  height: 2px;
  background: #e5e7eb;
  z-index: 0;
}

.step-completed:not(:last-child)::after {
  background: #10b981;
}

.step-active:not(:last-child)::after {
  background: #3b82f6;
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  background: #f3f4f6;
  color: #9ca3af;
  z-index: 1;
  margin-bottom: 8px;
}

.step-completed .step-circle {
  background: #10b981;
  color: #fff;
}

.step-active .step-circle {
  background: #3b82f6;
  color: #fff;
}

.step-label {
  font-size: 12px;
  color: #6b7280;
  text-align: center;
}

.step-completed .step-label {
  color: #10b981;
}

.step-active .step-label {
  color: #3b82f6;
  font-weight: 600;
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.record-title {
  font-weight: 600;
  color: #1f2937;
}

.record-info {
  display: flex;
  gap: 20px;
  margin-bottom: 8px;
  font-size: 14px;
  color: #6b7280;
}

.info-item {
  display: flex;
  align-items: center;
}

.record-comment {
  font-size: 14px;
  color: #374151;
  padding-top: 8px;
  border-top: 1px dashed #e5e7eb;
}

.comment-label {
  color: #6b7280;
}
</style>