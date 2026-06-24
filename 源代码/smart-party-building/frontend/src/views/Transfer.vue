<template>
  <div class="transfer-page">
    <div class="page-header">
      <h2>组织关系转移</h2>
    </div>

    <div class="card">
      <el-tabs v-model="activeTab" type="card">
        <!-- 转出审批 -->
        <el-tab-pane label="转出审批" name="out">
          <div class="search-bar">
            <el-input
              v-model="outSearchForm.name"
              placeholder="请输入姓名"
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="outSearchForm.status" placeholder="申请状态">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="待审核" />
              <el-option label="已通过" value="已通过" />
              <el-option label="已拒绝" value="已拒绝" />
            </el-select>
            <el-button type="primary" @click="handleOutSearch">搜索</el-button>
            <el-button @click="handleOutReset">重置</el-button>
          </div>

          <el-table :data="outTableData" border class="mt-20">
            <el-table-column label="序号" width="80">
              <template #default="scope">
                {{ (outCurrentPage - 1) * outPageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="memberName" label="姓名" />
            <el-table-column prop="currentBranch" label="当前党支部" />
            <el-table-column prop="targetBranch" label="目标党支部" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180" />
            <el-table-column prop="reason" label="转移原因" />
            <el-table-column label="操作" width="160">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button
                    size="small"
                    type="primary"
                    @click="handleOutApprove(scope.row.id)"
                    v-if="scope.row.status === '待审核'"
                  >
                    同意
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleOutReject(scope.row.id)"
                    v-if="scope.row.status === '待审核'"
                  >
                    拒绝
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              @size-change="handleOutSizeChange"
              @current-change="handleOutCurrentChange"
              :current-page="outCurrentPage"
              :page-sizes="[10, 20, 30, 40]"
              :page-size="outPageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="outTotal"
            />
          </div>
        </el-tab-pane>

        <!-- 转入审批 -->
        <el-tab-pane label="转入审批" name="in">
          <div class="search-bar">
            <el-input
              v-model="inSearchForm.name"
              placeholder="请输入姓名"
              class="search-input"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select v-model="inSearchForm.status" placeholder="申请状态">
              <el-option label="全部" value="" />
              <el-option label="待审核" value="待审核" />
              <el-option label="已通过" value="已通过" />
              <el-option label="已拒绝" value="已拒绝" />
            </el-select>
            <el-button type="primary" @click="handleInSearch">搜索</el-button>
            <el-button @click="handleInReset">重置</el-button>
          </div>

          <el-table :data="inTableData" border class="mt-20">
            <el-table-column label="序号" width="80">
              <template #default="scope">
                {{ (inCurrentPage - 1) * inPageSize + scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="branchName" label="转入党支部" />
            <el-table-column prop="phone" label="联系电话" />
            <el-table-column prop="idCard" label="身份证号" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180" />
            <el-table-column label="操作" width="220">
              <template #default="scope">
                <div class="action-buttons">
                  <el-button size="small" @click="handleInView(scope.row)">查看详情</el-button>
                  <el-button
                    size="small"
                    type="primary"
                    @click="handleInApprove(scope.row.id)"
                    v-if="scope.row.status === '待审核'"
                  >
                    同意
                  </el-button>
                  <el-button
                    size="small"
                    type="danger"
                    @click="handleInReject(scope.row.id)"
                    v-if="scope.row.status === '待审核'"
                  >
                    拒绝
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination">
            <el-pagination
              @size-change="handleInSizeChange"
              @current-change="handleInCurrentChange"
              :current-page="inCurrentPage"
              :page-sizes="[10, 20, 30, 40]"
              :page-size="inPageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="inTotal"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 转出审批对话框 -->
    <el-dialog :title="approvalType === 'out_approve' ? '同意转出并移出系统' : '拒绝转出'" v-model="outApprovalVisible" width="450px">
      <el-form :model="outApprovalForm" label-width="100px">
        <el-form-item label="审核意见">
          <el-input type="textarea" v-model="outApprovalForm.comment" :rows="3" placeholder="请输入审核意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="outApprovalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleOutApprovalSubmit" :loading="outApprovalLoading">
          {{ approvalType === 'out_approve' ? '同意并移出系统' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 转入审批对话框 -->
    <el-dialog :title="approvalType === 'in_approve' ? '同意转入' : '拒绝转入'" v-model="inApprovalVisible" width="450px">
      <el-form :model="inApprovalForm" label-width="100px">
        <el-form-item label="审核意见">
          <el-input type="textarea" v-model="inApprovalForm.comment" :rows="3" placeholder="请输入审核意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inApprovalVisible = false">取消</el-button>
        <el-button type="primary" @click="handleInApprovalSubmit" :loading="inApprovalLoading">
          {{ approvalType === 'in_approve' ? '同意' : '拒绝' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 转入详情对话框 -->
    <el-dialog title="转入申请详情" v-model="inDetailVisible" width="600px">
      <el-form :model="inDetailForm" label-width="120px" v-if="inDetailForm">
        <div class="form-row">
          <el-form-item label="姓名">
            <el-input v-model="inDetailForm.name" disabled />
          </el-form-item>
          <el-form-item label="性别">
            <el-input v-model="inDetailForm.gender" disabled />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="身份证号">
            <el-input v-model="inDetailForm.idCard" disabled />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="inDetailForm.phone" disabled />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="转入党支部">
            <el-input v-model="inDetailForm.branchName" disabled />
          </el-form-item>
          <el-form-item label="入党时间">
            <el-input v-model="inDetailForm.joinPartyTime" disabled />
          </el-form-item>
        </div>
        <el-form-item label="转入原因">
          <el-input type="textarea" v-model="inDetailForm.reason" disabled :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="inDetailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const activeTab = ref('out')

// 转出审批数据
const outTableData = ref<any[]>([])
const outCurrentPage = ref(1)
const outPageSize = ref(10)
const outTotal = ref(0)
const outApprovalVisible = ref(false)
const outApprovalLoading = ref(false)
const outApprovalId = ref<number | null>(null)
const outApprovalForm = reactive({ comment: '' })

const outSearchForm = reactive({
  name: '',
  status: ''
})

// 转入审批数据
const inTableData = ref<any[]>([])
const inCurrentPage = ref(1)
const inPageSize = ref(10)
const inTotal = ref(0)
const inApprovalVisible = ref(false)
const inApprovalLoading = ref(false)
const inApprovalId = ref<number | null>(null)
const inApprovalForm = reactive({ comment: '' })
const inDetailVisible = ref(false)
const inDetailForm = ref<any>(null)

const inSearchForm = reactive({
  name: '',
  status: ''
})

const approvalType = ref<'out_approve' | 'out_reject' | 'in_approve' | 'in_reject'>('out_approve')

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return map[status] || 'info'
}

// 转出审批方法
const fetchOutData = async () => {
  try {
    const response = await axios.get('/api/transfer', {
      params: {
        pageNum: outCurrentPage.value,
        pageSize: outPageSize.value,
        memberName: outSearchForm.name,
        status: outSearchForm.status
      }
    })
    if (response.data.code === 200) {
      outTableData.value = response.data.data.records
      outTotal.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取转出申请列表失败:', error)
  }
}

const handleOutSearch = () => {
  outCurrentPage.value = 1
  fetchOutData()
}

const handleOutReset = () => {
  outSearchForm.name = ''
  outSearchForm.status = ''
  outCurrentPage.value = 1
  fetchOutData()
}

const handleOutApprove = (id: number) => {
  outApprovalId.value = id
  approvalType.value = 'out_approve'
  outApprovalForm.comment = ''
  outApprovalVisible.value = true
}

const handleOutReject = (id: number) => {
  outApprovalId.value = id
  approvalType.value = 'out_reject'
  outApprovalForm.comment = ''
  outApprovalVisible.value = true
}

const handleOutApprovalSubmit = async () => {
  if (!outApprovalId.value) return
  outApprovalLoading.value = true
  try {
    const url = approvalType.value === 'out_approve'
      ? `/api/transfer/${outApprovalId.value}/approve`
      : `/api/transfer/${outApprovalId.value}/reject`
    await axios.put(url, { reviewComment: outApprovalForm.comment })
    ElMessage.success(approvalType.value === 'out_approve' ? '审批通过，党员已移出系统' : '已拒绝')
    outApprovalVisible.value = false
    fetchOutData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    outApprovalLoading.value = false
  }
}

const handleOutSizeChange = (val: number) => {
  outPageSize.value = val
  fetchOutData()
}

const handleOutCurrentChange = (val: number) => {
  outCurrentPage.value = val
  fetchOutData()
}

// 转入审批方法
const fetchInData = async () => {
  try {
    const response = await axios.get('/api/transfer/in', {
      params: {
        pageNum: inCurrentPage.value,
        pageSize: inPageSize.value,
        name: inSearchForm.name,
        status: inSearchForm.status
      }
    })
    if (response.data.code === 200) {
      inTableData.value = response.data.data.records
      inTotal.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取转入申请列表失败:', error)
  }
}

const handleInSearch = () => {
  inCurrentPage.value = 1
  fetchInData()
}

const handleInReset = () => {
  inSearchForm.name = ''
  inSearchForm.status = ''
  inCurrentPage.value = 1
  fetchInData()
}

const handleInView = async (row: any) => {
  try {
    const response = await axios.get(`/api/transfer/in/${row.id}`)
    if (response.data.code === 200) {
      inDetailForm.value = response.data.data
      inDetailVisible.value = true
    }
  } catch (error) {
    console.error('获取转入详情失败:', error)
  }
}

const handleInApprove = (id: number) => {
  inApprovalId.value = id
  approvalType.value = 'in_approve'
  inApprovalForm.comment = ''
  inApprovalVisible.value = true
}

const handleInReject = (id: number) => {
  inApprovalId.value = id
  approvalType.value = 'in_reject'
  inApprovalForm.comment = ''
  inApprovalVisible.value = true
}

const handleInApprovalSubmit = async () => {
  if (!inApprovalId.value) return
  inApprovalLoading.value = true
  try {
    const url = approvalType.value === 'in_approve'
      ? `/api/transfer/in/${inApprovalId.value}/approve`
      : `/api/transfer/in/${inApprovalId.value}/reject`
    await axios.put(url, { reviewComment: inApprovalForm.comment })
    ElMessage.success(approvalType.value === 'in_approve' ? '审批通过' : '已拒绝')
    inApprovalVisible.value = false
    fetchInData()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    inApprovalLoading.value = false
  }
}

const handleInSizeChange = (val: number) => {
  inPageSize.value = val
  fetchInData()
}

const handleInCurrentChange = (val: number) => {
  inCurrentPage.value = val
  fetchInData()
}

onMounted(() => {
  fetchOutData()
  fetchInData()
})
</script>

<style scoped>
.transfer-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
}

.card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-input {
  width: 200px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.form-row .el-form-item {
  flex: 1;
}

.mt-20 {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
