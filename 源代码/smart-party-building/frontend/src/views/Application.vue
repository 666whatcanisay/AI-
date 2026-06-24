
<template>
  <div class="application-page">
    <div class="page-header">
      <h2>入党申请管理</h2>
      <el-button type="danger" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增申请
      </el-button>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入申请人姓名"
          class="search-input"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="申请状态" clearable>
          <el-option label="全部" value="" />
          <el-option label="无" value="NONE" />
          <el-option label="申请中" value="APPLYING" />
          <el-option label="积极分子" value="ACTIVIST" />
          <el-option label="发展对象" value="DEVELOP_TARGET" />
          <el-option label="预备党员" value="PROBATIONARY" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-table :data="tableData" border stripe>
        <el-table-column label="序号" width="70">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="申请人" width="100" />
        <el-table-column prop="gender" label="性别" width="70">
          <template #default="scope">
            {{ scope.row.gender || '待填写' }}
          </template>
        </el-table-column>
        <el-table-column prop="department" label="所在党支部" width="160">
          <template #default="scope">
            {{ scope.row.department || '待填写' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130">
          <template #default="scope">
            {{ scope.row.phone || '待填写' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="当前状态" width="110">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发展流程" min-width="260">
          <template #default="scope">
            <div class="mini-steps">
              <div
                v-for="(step, index) in statusSteps"
                :key="step.key"
                class="mini-step"
                :class="{
                  'mini-step-completed': isStepCompleted(scope.row.status, step.key),
                  'mini-step-active': isStepActive(scope.row.status, step.key),
                  'mini-step-pending': isStepPending(scope.row.status, step.key)
                }"
              >
                <div class="mini-step-dot">
                  <el-icon v-if="isStepCompleted(scope.row.status, step.key)" :size="12"><CircleCheck /></el-icon>
                  <span v-else class="dot-inner"></span>
                </div>
                <span class="mini-step-text">{{ step.label }}</span>
                <div v-if="index < statusSteps.length - 1" class="mini-step-line" :class="{ 'line-completed': isStepCompleted(scope.row.status, step.key) || isStepActive(scope.row.status, step.key) }"></div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="handleView(scope.row)">查看详情</el-button>
            <el-button size="small" type="primary" @click="handleApprove(scope.row)" v-if="canApprove(scope.row)">审批</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增申请对话框 -->
    <el-dialog :title="dialogTitle" v-model="formVisible" width="500px" destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="110px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入申请人姓名" />
        </el-form-item>
        <div class="form-tip">申请人提交后，可在党员端自行填写详细个人信息</div>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="formLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog title="申请详情" v-model="detailVisible" width="850px" destroy-on-close>
      <div v-if="currentApplication" class="approval-detail">
        <div class="detail-section">
          <h4 class="section-title">基本信息</h4>
          <div class="detail-grid">
            <div class="detail-row">
              <span class="detail-label">申请人：</span>
              <span class="detail-value">{{ currentApplication.name }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">性别：</span>
              <span class="detail-value">{{ currentApplication.gender || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">出生年月：</span>
              <span class="detail-value">{{ currentApplication.birthDate || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">身份证号：</span>
              <span class="detail-value">{{ currentApplication.idCard || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">学号：</span>
              <span class="detail-value">{{ currentApplication.studentNo || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">班级：</span>
              <span class="detail-value">{{ currentApplication.className || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">所在党支部：</span>
              <span class="detail-value">{{ currentApplication.department || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">联系电话：</span>
              <span class="detail-value">{{ currentApplication.phone || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">邮箱：</span>
              <span class="detail-value">{{ currentApplication.email || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">籍贯：</span>
              <span class="detail-value">{{ currentApplication.nativePlace || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">学历：</span>
              <span class="detail-value">{{ currentApplication.education || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">政治面貌：</span>
              <span class="detail-value">{{ currentApplication.politicalStatus || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">介绍人：</span>
              <span class="detail-value">{{ currentApplication.introducer || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">申请时间：</span>
              <span class="detail-value">{{ currentApplication.createTime }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">更新时间：</span>
              <span class="detail-value">{{ currentApplication.updateTime || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">当前状态：</span>
              <el-tag :type="getStatusType(currentApplication.status)" size="large">
                {{ getStatusLabel(currentApplication.status) }}
              </el-tag>
            </div>
            <div class="detail-row">
              <span class="detail-label">审批层级：</span>
              <el-tag :type="getLevelType(currentApplication.currentLevel)" size="large" v-if="currentApplication.currentLevel">
                {{ getLevelLabel(currentApplication.currentLevel) }}
              </el-tag>
              <span v-else class="detail-value">-</span>
            </div>
          </div>
          <div class="detail-row full-row" v-if="currentApplication.reason">
            <span class="detail-label">申请理由：</span>
            <span class="detail-value reason-text">{{ currentApplication.reason }}</span>
          </div>
        </div>

        <div class="process-section">
          <h4 class="section-title">审批流程</h4>
          <!-- 步骤进度条 -->
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
          <!-- 审批记录时间线 -->
          <el-timeline class="mt-4" v-if="approvalRecords.length > 0">
            <el-timeline-item
              v-for="(record, index) in approvalRecords"
              :key="index"
              :status="record.approved ? 'success' : 'warning'"
            >
              <template #dot>
                <el-icon :size="18">
                  <CircleCheck v-if="record.approved" />
                  <Clock v-else />
                </el-icon>
              </template>
              <el-card shadow="never">
                <div class="record-header">
                  <span class="record-title">{{ record.levelName }}</span>
                  <el-tag :type="record.approved ? 'success' : 'warning'" size="small">
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
          <el-empty v-else description="暂无审批记录" :image-size="60" />
        </div>

        <!-- 思想报告区域 -->
        <div class="process-section" v-if="developmentReports.length > 0">
          <h4 class="section-title">思想报告</h4>
          <el-table :data="developmentReports" border size="small">
            <el-table-column prop="title" label="报告标题" />
            <el-table-column prop="reportType" label="报告类型" width="150">
              <template #default="scope">
                <el-tag :type="getReportTypeTag(scope.row.reportType)" size="small">
                  {{ getReportTypeLabel(scope.row.reportType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="author" label="作者" width="100" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'APPROVED' ? 'success' : scope.row.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
                  {{ scope.row.status === 'APPROVED' ? '已通过' : scope.row.status === 'REJECTED' ? '已驳回' : '待审核' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="170" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button size="small" @click="handleViewReport(scope.row)">查看</el-button>
                  <el-button size="small" type="success" @click="handleReviewReport(scope.row, 'APPROVED')" v-if="scope.row.status === 'PENDING'">通过</el-button>
                  <el-button size="small" type="danger" @click="handleReviewReport(scope.row, 'REJECTED')" v-if="scope.row.status === 'PENDING'">驳回</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleApprove(currentApplication)" v-if="canApprove(currentApplication)">审批</el-button>
      </template>
    </el-dialog>

    <!-- 报告详情对话框 -->
    <el-dialog title="思想报告详情" v-model="reportDetailVisible" width="600px" destroy-on-close>
      <div v-if="currentReport" class="report-detail">
        <div class="detail-row">
          <span class="detail-label">报告标题：</span>
          <span class="detail-value">{{ currentReport.title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">报告类型：</span>
          <el-tag :type="getReportTypeTag(currentReport.reportType)" size="small">{{ getReportTypeLabel(currentReport.reportType) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">作者：</span>
          <span class="detail-value">{{ currentReport.author }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态：</span>
          <el-tag :type="currentReport.status === 'APPROVED' ? 'success' : currentReport.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
            {{ currentReport.status === 'APPROVED' ? '已通过' : currentReport.status === 'REJECTED' ? '已驳回' : '待审核' }}
          </el-tag>
        </div>
        <div class="detail-row">
          <span class="detail-label">提交时间：</span>
          <span class="detail-value">{{ currentReport.createTime }}</span>
        </div>
        <div class="report-content-section">
          <span class="detail-label">报告内容：</span>
          <div class="report-content-text">{{ currentReport.content }}</div>
        </div>
        <div v-if="currentReport.reviewComment" class="detail-row full-row">
          <span class="detail-label">审核意见：</span>
          <span class="detail-value">{{ currentReport.reviewComment }}</span>
        </div>
        <!-- 审核区域 -->
        <div v-if="currentReport.status === 'PENDING'" class="review-section">
          <el-divider />
          <h4 style="margin-bottom:12px">审核报告</h4>
          <el-input v-model="reportReviewComment" type="textarea" :rows="2" placeholder="请输入审核意见（可选）" />
          <div style="margin-top:12px;display:flex;gap:12px">
            <el-button type="success" @click="handleReviewReport(currentReport, 'APPROVED')">审核通过</el-button>
            <el-button type="danger" @click="handleReviewReport(currentReport, 'REJECTED')">驳回</el-button>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="reportDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 审批操作对话框 -->
    <el-dialog title="审批操作" v-model="approveVisible" width="500px" destroy-on-close>
      <div class="approve-info" v-if="currentApplication">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="申请人">{{ currentApplication.name }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(currentApplication.status)" size="small">
              {{ getStatusLabel(currentApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审批层级">
            <el-tag :type="getLevelType(currentApplication.currentLevel)" size="small" v-if="currentApplication.currentLevel">
              {{ getLevelLabel(currentApplication.currentLevel) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="所在党支部">{{ currentApplication.department || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-form :model="approveForm" ref="approveFormRef" label-width="100px" class="mt-4">
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.comment" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
        <el-form-item label="审批结果">
          <el-radio-group v-model="approveForm.approved">
            <el-radio :label="true">
              <span style="color: #10b981; font-weight: 600;">通过</span>
            </el-radio>
            <el-radio :label="false">
              <span style="color: #ef4444; font-weight: 600;">驳回</span>
            </el-radio>
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
import { Search, Plus, CircleCheck, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

// 搜索相关
const searchForm = reactive({
  name: '',
  status: ''
})

// 表格相关
const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 表单相关
const formVisible = ref(false)
const formLoading = ref(false)
const formRef = ref<any>(null)
const isEdit = ref(false)
const dialogTitle = ref('新增申请')

const form = reactive({
  id: null as number | null,
  name: ''
})

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

// 详情相关
const detailVisible = ref(false)
const currentApplication = ref<any>(null)
const approvalRecords = ref<any[]>([])
const developmentReports = ref<any[]>([])
const reportDetailVisible = ref(false)
const currentReport = ref<any>(null)

// 审批相关
const approveVisible = ref(false)
const approveLoading = ref(false)
const approveFormRef = ref<any>(null)
const approveForm = reactive({
  approved: true,
  comment: ''
})

// 状态映射
const statusSteps = [
  { key: 'NONE', label: '无' },
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
  NONE: { label: '无', type: 'info' },
  APPLYING: { label: '申请中', type: 'warning' },
  ACTIVIST: { label: '积极分子', type: 'info' },
  DEVELOP_TARGET: { label: '发展对象', type: 'primary' },
  PROBATIONARY: { label: '预备党员', type: 'success' },
  FORMAL: { label: '正式党员', type: 'danger' }
}

const getStatusLabel = (status: string) => statusMap[status]?.label || status
const getStatusType = (status: string) => statusMap[status]?.type || 'info'
const getLevelLabel = (level: string) => levelMap[level]?.label || level
const getLevelType = (level: string) => levelMap[level]?.type || 'info'

const statusOrder = ['NONE', 'APPLYING', 'ACTIVIST', 'DEVELOP_TARGET', 'PROBATIONARY', 'FORMAL']

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
  // NONE状态也可以审批（审核申请报告后进入APPLYING）
  return application.status !== 'FORMAL'
}

// 报告类型映射
const reportTypeMap: Record<string, string> = {
  APPLICATION: '申请报告',
  ACTIVIST: '积极分子思想报告',
  DEVELOP: '发展对象思想报告',
  PROBATIONARY: '预备党员思想报告',
  FORMAL: '转正思想报告'
}

const getReportTypeLabel = (type: string) => reportTypeMap[type] || type
const getReportTypeTag = (type: string) => {
  const map: Record<string, string> = {
    APPLICATION: 'warning',
    ACTIVIST: '',
    DEVELOP: 'info',
    PROBATIONARY: 'success',
    FORMAL: 'danger'
  }
  return map[type] || 'info'
}

const handleViewReport = (report: any) => {
  currentReport.value = report
  reportReviewComment.value = ''
  reportDetailVisible.value = true
}

const reportReviewComment = ref('')

const handleReviewReport = async (report: any, status: string) => {
  try {
    const response = await axios.put(`/api/development-report/${report.id}/review`, {
      status: status,
      reviewComment: reportReviewComment.value,
      reviewer: '管理员'
    })
    if (response.data.code === 200) {
      ElMessage.success(status === 'APPROVED' ? '报告审核通过' : '报告已驳回')
      reportDetailVisible.value = false
      // 刷新报告列表
      if (currentApplication.value) {
        const reportRes = await axios.get(`/api/development-report/application/${currentApplication.value.id}`)
        if (reportRes.data.code === 200) {
          developmentReports.value = reportRes.data.data
        }
      }
    } else {
      ElMessage.error(response.data.message || '审核失败')
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 获取列表数据
const fetchData = async () => {
  try {
    const response = await axios.get('/api/application', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        name: searchForm.name,
        status: searchForm.status
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
      // 为每条记录设置当前审批层级
      tableData.value.forEach(app => {
        if (!app.currentLevel) {
          app.currentLevel = getLevelByStatus(app.status)
        }
      })
    }
  } catch (error) {
    console.error('获取申请列表失败:', error)
    ElMessage.error('获取申请列表失败')
  }
}

const getLevelByStatus = (status: string) => {
  const map: Record<string, string> = {
    NONE: 'LEVEL_1',
    APPLYING: 'LEVEL_1',
    ACTIVIST: 'LEVEL_2',
    DEVELOP_TARGET: 'LEVEL_3',
    PROBATIONARY: 'LEVEL_3'
  }
  return map[status] || ''
}

// 搜索与重置
const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchData()
}

// 分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchData()
}

// 新增申请
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增申请'
  Object.assign(form, {
    id: null,
    name: ''
  })
  formVisible.value = true
}

// 提交表单
const submitForm = async () => {
  if (formRef.value) {
    try {
      await formRef.value.validate()
    } catch {
      return
    }
  }
  try {
    formLoading.value = true
    if (isEdit.value && form.id) {
      const response = await axios.put(`/api/application/${form.id}`, form)
      if (response.data.code === 200) {
        ElMessage.success('更新成功')
        formVisible.value = false
        fetchData()
      }
    } else {
      const response = await axios.post('/api/application', form)
      if (response.data.code === 200) {
        ElMessage.success('提交成功')
        formVisible.value = false
        fetchData()
      }
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    formLoading.value = false
  }
}

// 查看详情
const handleView = async (row: any) => {
  currentApplication.value = { ...row }
  if (!currentApplication.value.currentLevel) {
    currentApplication.value.currentLevel = getLevelByStatus(row.status)
  }
  try {
    const response = await axios.get(`/api/application/${row.id}/records`)
    if (response.data.code === 200) {
      approvalRecords.value = response.data.data
    }
  } catch (error) {
    console.error('获取审批记录失败:', error)
    approvalRecords.value = []
  }
  // 获取思想报告
  try {
    const reportRes = await axios.get(`/api/development-report/application/${row.id}`)
    if (reportRes.data.code === 200) {
      developmentReports.value = reportRes.data.data
    }
  } catch (error) {
    console.error('获取思想报告失败:', error)
    developmentReports.value = []
  }
  detailVisible.value = true
}

// 审批
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
      ElMessage.success(approveForm.approved ? '审批通过' : '已驳回')
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

// 删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定要删除该申请记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await axios.delete(`/api/application/${row.id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch {
    // 取消删除
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.application-page {
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
  flex-wrap: wrap;
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

/* 详情样式 */
.approval-detail {
  padding: 10px;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #DC2626;
  display: inline-block;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px 24px;
}

.detail-row {
  display: flex;
  align-items: center;
}

.detail-row.full-row {
  grid-column: 1 / -1;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
}

.detail-label {
  font-weight: 500;
  color: #6b7280;
  min-width: 80px;
  flex-shrink: 0;
}

.detail-value {
  color: #1f2937;
}

.reason-text {
  background: #f9fafb;
  padding: 12px;
  border-radius: 8px;
  line-height: 1.6;
  width: 100%;
}

/* 审批流程样式 */
.process-section {
  margin-top: 8px;
}

.step-progress {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  margin-bottom: 20px;
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

/* 审批记录样式 */
.mt-4 {
  margin-top: 16px;
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

/* 审批对话框样式 */
.approve-info {
  margin-bottom: 12px;
}

/* 迷你步骤条样式 */
.mini-steps {
  display: flex;
  align-items: center;
  gap: 0;
  padding: 4px 0;
}

.mini-step {
  display: flex;
  align-items: center;
  gap: 4px;
  position: relative;
  flex-shrink: 0;
}

.mini-step-dot {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.mini-step-pending .mini-step-dot {
  background: #f3f4f6;
  border: 2px solid #d1d5db;
}

.mini-step-active .mini-step-dot {
  background: #3b82f6;
  border: 2px solid #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.mini-step-completed .mini-step-dot {
  background: #10b981;
  border: 2px solid #10b981;
  color: #fff;
}

.dot-inner {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  display: block;
}

.mini-step-pending .dot-inner {
  background: #d1d5db;
}

.mini-step-active .dot-inner {
  background: #fff;
}

.mini-step-text {
  font-size: 11px;
  white-space: nowrap;
  color: #9ca3af;
  font-weight: 500;
}

.mini-step-active .mini-step-text {
  color: #3b82f6;
  font-weight: 600;
}

.mini-step-completed .mini-step-text {
  color: #10b981;
}

.mini-step-line {
  width: 16px;
  height: 2px;
  background: #e5e7eb;
  margin: 0 2px;
  flex-shrink: 0;
}

.mini-step-line.line-completed {
  background: #10b981;
}

/* 报告详情样式 */
.report-detail {
  padding: 10px;
}

.report-content-section {
  margin-top: 12px;
}

.report-content-text {
  margin-top: 8px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  line-height: 1.8;
  color: #374151;
  font-size: 14px;
  white-space: pre-wrap;
}

.review-section {
  margin-top: 16px;
}

.form-tip {
  color: #909399;
  font-size: 13px;
  line-height: 1.6;
  padding: 8px 12px;
  background: #f4f4f5;
  border-radius: 6px;
  margin-top: 4px;
}
</style>
