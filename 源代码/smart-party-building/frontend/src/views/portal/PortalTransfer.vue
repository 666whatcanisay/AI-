<template>
  <div class="transfer-page">
    <div class="page-header">
      <h2>组织关系转移</h2>
      <el-button type="primary" @click="handleApply" :disabled="hasPending">
        <el-icon><Plus /></el-icon>
        发起转移申请
      </el-button>
    </div>

    <div class="card">
      <div class="info-banner" v-if="memberInfo">
        <div class="info-item">
          <span class="label">姓名：</span>
          <span class="value">{{ memberInfo.name }}</span>
        </div>
        <div class="info-item">
          <span class="label">当前党支部：</span>
          <span class="value">{{ memberInfo.branchName }}</span>
        </div>
        <div class="info-item">
          <span class="label">党员状态：</span>
          <el-tag :type="memberInfo.partyStatus === 'FORMAL' ? 'success' : 'warning'">
            {{ memberInfo.partyStatus === 'FORMAL' ? '正式党员' : '预备党员' }}
          </el-tag>
        </div>
      </div>

      <el-alert
        v-if="memberInfo && memberInfo.partyStatus !== 'FORMAL'"
        title="只有正式党员才能发起组织关系转移"
        type="warning"
        :closable="false"
        show-icon
        class="mt-10"
      />

      <el-table :data="tableData" border class="mt-20">
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="currentBranch" label="当前党支部" />
        <el-table-column prop="targetBranch" label="目标党支部" />
        <el-table-column prop="reason" label="转移原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160" />
        <el-table-column prop="reviewComment" label="审核意见" />
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row.id)"
              v-if="scope.row.status === '待审核'"
            >
              撤销
            </el-button>
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

    <el-dialog title="发起组织关系转移" v-model="dialogVisible" width="500px">
      <el-form :model="form" ref="formRef" label-width="120px" :rules="rules">
        <el-form-item label="当前党支部">
          <el-input v-model="form.currentBranch" disabled />
        </el-form-item>
        <el-form-item label="目标党支部" prop="targetBranch">
          <el-input v-model="form.targetBranch" placeholder="请输入外部党组织名称" />
        </el-form-item>
        <el-form-item label="转移原因" prop="reason">
          <el-input
            type="textarea"
            v-model="form.reason"
            :rows="4"
            placeholder="请输入转移原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import axios from '@/utils/axios'

const memberInfo = ref<any>(null)
const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)

const formRef = ref<FormInstance | null>(null)
const form = reactive({
  currentBranch: '',
  targetBranch: '',
  reason: ''
})

const rules = {
  targetBranch: [
    { required: true, message: '请输入外部党组织名称', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入转移原因', trigger: 'blur' },
    { min: 5, message: '转移原因至少5个字符', trigger: 'blur' }
  ]
}

const hasPending = computed(() => {
  return tableData.value.some(item => item.status === '待审核')
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return map[status] || 'info'
}

const fetchMemberInfo = async () => {
  try {
    const response = await axios.get('/api/portal/transfer/member-info')
    if (response.data.code === 200) {
      memberInfo.value = response.data.data
      form.currentBranch = response.data.data.branchName || ''
    }
  } catch (error) {
    console.error('获取党员信息失败:', error)
  }
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/portal/transfer/my', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取申请记录失败:', error)
  }
}

const handleApply = () => {
  if (memberInfo.value && memberInfo.value.partyStatus !== 'FORMAL') {
    ElMessage.warning('只有正式党员才能发起组织关系转移')
    return
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    await axios.post('/api/portal/transfer', form)
    ElMessage.success('申请提交成功')
    dialogVisible.value = false
    fetchData()
    formRef.value.resetFields()
    form.currentBranch = memberInfo.value?.branchName || ''
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '提交失败')
    }
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要撤销该申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`/api/transfer/${id}`)
    ElMessage.success('撤销成功')
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('撤销失败:', error)
    }
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
  fetchMemberInfo()
  fetchData()
})
</script>

<style scoped>
.transfer-page {
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

.info-banner {
  display: flex;
  gap: 30px;
  padding: 15px 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item .label {
  color: #6b7280;
}

.info-item .value {
  color: #1f2937;
  font-weight: 500;
}

.mt-10 {
  margin-top: 10px;
}

.mt-20 {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
