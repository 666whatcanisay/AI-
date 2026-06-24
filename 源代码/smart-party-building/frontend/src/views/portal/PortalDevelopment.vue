<template>
  <div class="portal-development">
    <el-card class="process-card">
      <template #header>
        <div class="card-header">
          <span>党员发展流程</span>
        </div>
      </template>

      <!-- 当前状态提示 -->
      <div class="status-banner" :class="getStatusBannerClass(currentStep)">
        <div class="status-icon">
          <el-icon :size="48"><Flag /></el-icon>
        </div>
        <div class="status-text">
          <h3>{{ getStatusTitle(currentStep) }}</h3>
          <p>{{ getStatusDesc(currentStep) }}</p>
        </div>
      </div>

      <!-- 步骤条 -->
      <div class="steps-section">
        <el-steps :active="currentStep" align-center finish-status="success">
          <el-step v-for="step in steps" :key="step.step" :title="step.title" :description="step.description" />
        </el-steps>
      </div>

      <!-- step=0: 无状态，显示提交入党申请表单 -->
      <el-card v-if="currentStep === 0 && !application" class="apply-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>提交入党申请书</span>
          </div>
        </template>
        <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="120px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="applyForm.name" disabled />
          </el-form-item>
          <div class="apply-tip">
            <el-alert
              title="提交申请后，请在「个人信息」页面完善个人资料，管理员审核通过后即可进入发展流程"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
          <el-form-item>
            <el-button type="danger" @click="handleSubmitApplication" :loading="submitting">提交入党申请</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 已提交申请后，各阶段的思想报告填写区域 -->
      <el-card v-if="application" class="report-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>思想报告</span>
            <el-tag v-if="requiredReportType" type="warning" size="small">
              当前需提交：{{ getReportTypeLabel(requiredReportType) }}
            </el-tag>
          </div>
        </template>

        <!-- 提示信息 -->
        <div class="report-tip" v-if="requiredReportType && !hasApprovedReport(requiredReportType)">
          <el-alert
            title="请提交本阶段所需的思想报告，管理员审核通过后才能进行审批"
            type="warning"
            :closable="false"
            show-icon
          />
        </div>
        <div class="report-tip" v-else-if="requiredReportType && hasApprovedReport(requiredReportType)">
          <el-alert
            title="本阶段思想报告已审核通过，请等待管理员审批"
            type="success"
            :closable="false"
            show-icon
          />
        </div>

        <!-- 提交报告按钮 -->
        <div class="report-actions" v-if="requiredReportType && !hasPendingReport(requiredReportType)">
          <el-button type="primary" @click="handleOpenReportDialog">
            <el-icon><Edit /></el-icon>
            提交{{ getReportTypeLabel(requiredReportType) }}
          </el-button>
        </div>
        <div class="report-actions" v-else-if="requiredReportType && hasPendingReport(requiredReportType)">
          <el-tag type="warning">已提交，等待审核</el-tag>
        </div>

        <!-- 已提交的报告列表 -->
        <div class="report-list" v-if="developmentReports.length > 0">
          <h4 class="section-subtitle">已提交的报告</h4>
          <el-table :data="developmentReports" border size="small">
            <el-table-column prop="title" label="报告标题" />
            <el-table-column prop="reportType" label="报告类型" width="160">
              <template #default="scope">
                <el-tag :type="getReportTypeTag(scope.row.reportType)" size="small">
                  {{ getReportTypeLabel(scope.row.reportType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'APPROVED' ? 'success' : scope.row.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
                  {{ scope.row.status === 'APPROVED' ? '已通过' : scope.row.status === 'REJECTED' ? '已驳回' : '待审核' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="170" />
            <el-table-column label="操作" width="80">
              <template #default="scope">
                <el-button size="small" link type="primary" @click="handleViewReport(scope.row)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>

      <!-- 详细步骤卡片 -->
      <div class="steps-detail">
        <div
          v-for="step in steps"
          :key="step.step"
          class="step-card"
          :class="{
            'step-completed': currentStep > step.step,
            'step-current': currentStep === step.step,
            'step-pending': currentStep < step.step
          }"
        >
          <div class="step-number">
            <el-icon v-if="currentStep > step.step" :size="24"><CircleCheck /></el-icon>
            <span v-else>{{ step.step }}</span>
          </div>
          <div class="step-content">
            <h4>{{ step.title }}</h4>
            <p>{{ step.description }}</p>
            <div class="step-extra" v-if="currentStep >= step.step">
              <template v-if="step.step === 1 && application">
                <div class="info-grid">
                  <div class="info-item">
                    <span class="label">申请人</span>
                    <span class="value">{{ application.name }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">申请时间</span>
                    <span class="value">{{ application.createTime }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">所在部门</span>
                    <span class="value">{{ application.department || '-' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="label">联系电话</span>
                    <span class="value">{{ application.phone || '-' }}</span>
                  </div>
                </div>
              </template>
              <template v-if="step.step >= 1 && approvalRecords.length > 0">
                <div class="approval-list">
                  <div
                    v-for="record in getApprovalForStep(step.step)"
                    :key="record.id"
                    class="approval-item"
                    :class="{ 'approval-passed': record.approved }"
                  >
                    <div class="approval-icon">
                      <el-icon v-if="record.approved" :size="18"><CircleCheck /></el-icon>
                      <el-icon v-else :size="18"><Clock /></el-icon>
                    </div>
                    <div class="approval-info">
                      <div class="approval-header">
                        <span class="approval-level">{{ record.levelName }}</span>
                        <el-tag :type="record.approved ? 'success' : 'warning'" size="small">
                          {{ record.approved ? '已通过' : '待审批' }}
                        </el-tag>
                      </div>
                      <div class="approval-detail" v-if="record.operator">
                        <span>审核人：{{ record.operator }}</span>
                        <span v-if="record.approveTime" style="margin-left:12px">{{ record.approveTime }}</span>
                      </div>
                      <div class="approval-comment" v-if="record.comment">
                        {{ record.comment }}
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 入党申请信息（有申请记录时显示） -->
      <el-card class="application-card" v-if="application" shadow="never">
        <template #header>
          <div class="card-header">
            <span>入党申请详情</span>
            <el-tag :type="getApplicationStatusType(application.status)" size="small">
              {{ getApplicationStatusLabel(application.status) }}
            </el-tag>
          </div>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请人">{{ application.name }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ application.gender || '-' }}</el-descriptions-item>
          <el-descriptions-item label="出生年月">{{ application.birthDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ application.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ application.studentNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ application.className || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ application.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ application.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="籍贯">{{ application.nativePlace || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学历">{{ application.education || '-' }}</el-descriptions-item>
          <el-descriptions-item label="政治面貌">{{ application.politicalStatus || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所在部门">{{ application.department || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ application.createTime }}</el-descriptions-item>
          <el-descriptions-item label="申请理由" :span="2">{{ application.reason || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </el-card>

    <!-- 提交思想报告对话框 -->
    <el-dialog :title="'提交' + getReportTypeLabel(requiredReportType || '')" v-model="reportDialogVisible" width="600px" destroy-on-close>
      <el-form :model="reportForm" :rules="reportRules" ref="reportFormRef" label-width="100px">
        <el-form-item label="报告标题" prop="title">
          <el-input v-model="reportForm.title" placeholder="请输入报告标题" />
        </el-form-item>
        <el-form-item label="报告内容" prop="content">
          <el-input v-model="reportForm.content" type="textarea" :rows="8" placeholder="请输入思想报告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitReport" :loading="reportSubmitting">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看报告详情对话框 -->
    <el-dialog title="思想报告详情" v-model="reportDetailVisible" width="600px" destroy-on-close>
      <div v-if="currentReport" class="report-detail-content">
        <div class="report-detail-row">
          <span class="report-detail-label">报告标题：</span>
          <span>{{ currentReport.title }}</span>
        </div>
        <div class="report-detail-row">
          <span class="report-detail-label">报告类型：</span>
          <el-tag :type="getReportTypeTag(currentReport.reportType)" size="small">{{ getReportTypeLabel(currentReport.reportType) }}</el-tag>
        </div>
        <div class="report-detail-row">
          <span class="report-detail-label">状态：</span>
          <el-tag :type="currentReport.status === 'APPROVED' ? 'success' : currentReport.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
            {{ currentReport.status === 'APPROVED' ? '已通过' : currentReport.status === 'REJECTED' ? '已驳回' : '待审核' }}
          </el-tag>
        </div>
        <div class="report-detail-row">
          <span class="report-detail-label">提交时间：</span>
          <span>{{ currentReport.createTime }}</span>
        </div>
        <div class="report-detail-body">
          <span class="report-detail-label">报告内容：</span>
          <div class="report-detail-text">{{ currentReport.content }}</div>
        </div>
        <div class="report-detail-row" v-if="currentReport.reviewComment">
          <span class="report-detail-label">审核意见：</span>
          <span>{{ currentReport.reviewComment }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="reportDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { Flag, CircleCheck, Clock, Edit } from '@element-plus/icons-vue'

const userStore = useUserStore()
const currentStep = ref(0)
const steps = ref<any[]>([])
const application = ref<any>(null)
const approvalRecords = ref<any[]>([])
const submitting = ref(false)
const applyFormRef = ref<any>(null)
const developmentReports = ref<any[]>([])

// 报告相关
const reportDialogVisible = ref(false)
const reportDetailVisible = ref(false)
const reportSubmitting = ref(false)
const currentReport = ref<any>(null)
const reportFormRef = ref<any>(null)
const reportForm = ref({
  title: '',
  content: ''
})
const reportRules = {
  title: [{ required: true, message: '请输入报告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入报告内容', trigger: 'blur' }]
}

const applyForm = ref<any>({
  name: userStore.realName || userStore.username || ''
})

const applyRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
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

// 根据当前状态计算需要的报告类型
const requiredReportType = computed(() => {
  if (!application.value) return null
  const status = application.value.status
  const map: Record<string, string> = {
    NONE: 'APPLICATION',
    APPLYING: 'ACTIVIST',
    ACTIVIST: 'DEVELOP',
    DEVELOP_TARGET: 'PROBATIONARY',
    PROBATIONARY: 'FORMAL'
  }
  return map[status] || null
})

const hasApprovedReport = (type: string) => {
  return developmentReports.value.some(r => r.reportType === type && r.status === 'APPROVED')
}

const hasPendingReport = (type: string) => {
  return developmentReports.value.some(r => r.reportType === type && r.status === 'PENDING')
}

const getStatusTitle = (step: number) => {
  const titles: Record<number, string> = {
    0: '尚未提交入党申请',
    1: '已提交入党申请，等待审核',
    2: '申请已通过，等待确定为积极分子',
    3: '已确定为入党积极分子',
    4: '已确定为发展对象',
    5: '已接收为预备党员',
    6: '已转为正式党员'
  }
  return titles[step] || '未知状态'
}

const getStatusDesc = (step: number) => {
  const descs: Record<number, string> = {
    0: '您可以向党组织提交入党申请书，开启党员发展之路',
    1: '您的入党申请已提交，请提交申请报告并等待党组织审核',
    2: '您的申请已通过审核，请提交积极分子思想报告并等待确定',
    3: '您已被确定为入党积极分子，请提交思想报告并继续努力',
    4: '您已被确定为发展对象，请提交思想报告并做好接收准备',
    5: '您已被接收为预备党员，请提交转正思想报告并继续发挥模范作用',
    6: '恭喜您已成为正式党员！请不忘初心，牢记使命'
  }
  return descs[step] || ''
}

const getStatusBannerClass = (step: number) => {
  if (step === 0) return 'status-pending'
  if (step === 6) return 'status-formal'
  return 'status-progress'
}

const getApplicationStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    NONE: '无',
    APPLYING: '申请中',
    ACTIVIST: '积极分子',
    DEVELOP_TARGET: '发展对象',
    PROBATIONARY: '预备党员',
    FORMAL: '正式党员'
  }
  return map[status] || status
}

const getApplicationStatusType = (status: string) => {
  const map: Record<string, string> = {
    NONE: 'info',
    APPLYING: 'warning',
    ACTIVIST: '',
    DEVELOP_TARGET: 'info',
    PROBATIONARY: 'success',
    FORMAL: 'danger'
  }
  return map[status] || 'info'
}

const getApprovalForStep = (step: number) => {
  const levelMap: Record<number, string> = {
    1: '支部审核',
    2: '支部审核',
    3: '党委审批',
    4: '支部大会',
    5: '支部大会'
  }
  const targetLevel = levelMap[step]
  if (!targetLevel) return []
  // 过滤掉支部审核记录
  if (targetLevel === '支部审核') return []
  return approvalRecords.value.filter(r => r.levelName === targetLevel)
}

// 提交入党申请
const handleSubmitApplication = async () => {
  if (applyFormRef.value) {
    try {
      await applyFormRef.value.validate()
    } catch {
      return
    }
  }
  try {
    submitting.value = true
    const response = await axios.post('/api/portal/application', applyForm.value)
    if (response.data.code === 200) {
      ElMessage.success('入党申请提交成功')
      fetchDevelopmentData()
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '提交失败')
  } finally {
    submitting.value = false
  }
}

// 打开报告对话框
const handleOpenReportDialog = () => {
  reportForm.value = { title: '', content: '' }
  reportDialogVisible.value = true
}

// 提交思想报告
const handleSubmitReport = async () => {
  if (reportFormRef.value) {
    try {
      await reportFormRef.value.validate()
    } catch {
      return
    }
  }
  if (!application.value || !requiredReportType.value) return
  try {
    reportSubmitting.value = true
    const response = await axios.post('/api/development-report', {
      applicationId: application.value.id,
      reportType: requiredReportType.value,
      title: reportForm.value.title,
      content: reportForm.value.content,
      author: application.value.name
    })
    if (response.data.code === 200) {
      ElMessage.success('思想报告提交成功')
      reportDialogVisible.value = false
      fetchReports()
    } else {
      ElMessage.error(response.data.message || '提交失败')
    }
  } catch (error: any) {
    ElMessage.error(error.response?.data?.message || '提交失败')
  } finally {
    reportSubmitting.value = false
  }
}

// 查看报告
const handleViewReport = (report: any) => {
  currentReport.value = report
  reportDetailVisible.value = true
}

// 获取发展流程数据
const fetchDevelopmentData = async () => {
  try {
    const name = userStore.realName || userStore.username
    const response = await axios.get(`/api/portal/development/application/${name}`)
    if (response.data.code === 200) {
      const data = response.data.data
      currentStep.value = data.currentStep || 0
      steps.value = data.steps || []
      application.value = data.application || null
      approvalRecords.value = data.approvalRecords || []
      // 获取思想报告
      if (application.value) {
        fetchReports()
      }
    }
  } catch (error) {
    console.error('获取发展流程失败:', error)
  }
}

// 获取思想报告列表
const fetchReports = async () => {
  if (!application.value) return
  try {
    const response = await axios.get(`/api/development-report/application/${application.value.id}`)
    if (response.data.code === 200) {
      developmentReports.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取思想报告失败:', error)
    developmentReports.value = []
  }
}

onMounted(() => {
  fetchDevelopmentData()
})
</script>

<style scoped>
.portal-development {
  padding: 20px;
}

.process-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
}

.status-banner {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
}

.status-pending {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #92400e;
}

.status-progress {
  background: linear-gradient(135deg, #dbeafe, #93c5fd);
  color: #1e40af;
}

.status-formal {
  background: linear-gradient(135deg, #d1fae5, #6ee7b7);
  color: #065f46;
}

.status-icon {
  flex-shrink: 0;
}

.status-text h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
}

.status-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

.steps-section {
  margin-bottom: 24px;
}

.apply-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.form-row {
  display: flex;
  gap: 20px;
}

.form-row .el-form-item {
  flex: 1;
}

.report-card {
  margin-bottom: 20px;
  border-radius: 12px;
}

.report-tip {
  margin-bottom: 16px;
}

.report-actions {
  margin-bottom: 16px;
}

.report-list {
  margin-top: 16px;
}

.section-subtitle {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
}

.steps-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 24px;
}

.step-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s;
}

.step-completed {
  background: #f0fdf4;
  border-color: #86efac;
}

.step-current {
  background: #eff6ff;
  border-color: #93c5fd;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.step-pending {
  background: #f9fafb;
  border-color: #e5e7eb;
  opacity: 0.7;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
  flex-shrink: 0;
}

.step-completed .step-number {
  background: #10b981;
  color: #fff;
}

.step-current .step-number {
  background: #3b82f6;
  color: #fff;
}

.step-pending .step-number {
  background: #e5e7eb;
  color: #9ca3af;
}

.step-content {
  flex: 1;
}

.step-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
}

.step-content p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}

.step-extra {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #d1d5db;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.info-item {
  display: flex;
  gap: 8px;
}

.info-item .label {
  color: #6b7280;
  font-size: 13px;
  white-space: nowrap;
}

.info-item .value {
  font-size: 13px;
  color: #374151;
}

.reason-section {
  margin-top: 8px;
}

.reason-section .label {
  color: #6b7280;
  font-size: 13px;
}

.reason-text {
  margin: 4px 0 0 0;
  padding: 8px 12px;
  background: #f9fafb;
  border-radius: 6px;
  font-size: 13px;
  color: #374151;
  line-height: 1.6;
}

.approval-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.approval-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #e5e7eb;
}

.approval-passed {
  border-color: #86efac;
  background: #f0fdf4;
}

.approval-icon {
  flex-shrink: 0;
  color: #d1d5db;
}

.approval-passed .approval-icon {
  color: #10b981;
}

.approval-info {
  flex: 1;
}

.approval-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.approval-level {
  font-weight: 600;
  font-size: 14px;
}

.approval-detail {
  font-size: 12px;
  color: #6b7280;
}

.approval-comment {
  margin-top: 4px;
  font-size: 13px;
  color: #374151;
  padding-top: 4px;
  border-top: 1px dashed #e5e7eb;
}

.application-card {
  margin-top: 20px;
  border-radius: 12px;
}

/* 报告详情样式 */
.report-detail-content {
  padding: 10px;
}

.report-detail-row {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.report-detail-label {
  font-weight: 600;
  color: #374151;
  white-space: nowrap;
}

.report-detail-body {
  margin-top: 12px;
}

.report-detail-text {
  margin-top: 8px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  line-height: 1.8;
  color: #374151;
  font-size: 14px;
  white-space: pre-wrap;
}

.apply-tip {
  margin-bottom: 16px;
}
</style>
