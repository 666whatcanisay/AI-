<template>
  <div class="evaluation-page">
    <div class="page-header">
      <h2>评议考核管理</h2>
    </div>

    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon green">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.excellentCount || 0 }}</div>
          <div class="stat-label">优秀</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon blue">
          <el-icon><CircleCheck /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.goodCount || 0 }}</div>
          <div class="stat-label">良好</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon orange">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.qualifiedCount || 0 }}</div>
          <div class="stat-label">合格</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon red">
          <el-icon><Close /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.unqualifiedCount || 0 }}</div>
          <div class="stat-label">不合格</div>
        </div>
      </div>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-select v-model="searchForm.year" placeholder="选择年份" style="width:120px">
          <el-option v-for="year in years" :key="year" :label="year + '年'" :value="year" />
        </el-select>
        <el-select v-model="searchForm.month" placeholder="选择月份" style="width:120px">
          <el-option v-for="month in months" :key="month" :label="month + '月'" :value="month" />
        </el-select>
        <el-button type="primary" @click="fetchData">查询</el-button>
        <el-button type="success" @click="showEvaluateDialog">
          <el-icon><Plus /></el-icon>
          考核党员
        </el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column label="序号" width="70">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="memberName" label="党员姓名" width="120" />
        <el-table-column prop="year" label="年份" width="100" />
        <el-table-column prop="month" label="月份" width="100">
          <template #default="scope">{{ scope.row.month }}月</template>
        </el-table-column>
        <el-table-column prop="grade" label="考核等级" width="120">
          <template #default="scope">
            <el-tag :type="getGradeType(scope.row.grade)">
              {{ scope.row.grade }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评语" min-width="200" show-overflow-tooltip />
        <el-table-column prop="evaluator" label="考核人" width="100" />
        <el-table-column prop="evaluateTime" label="考核时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
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

    <el-dialog title="考核党员" v-model="dialogVisible" width="500px" destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="选择党员" prop="memberId">
          <el-select v-model="form.memberId" placeholder="请选择党员" style="width:100%">
            <el-option v-for="member in memberList" :key="member.id" :label="member.name" :value="member.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核年份" prop="year">
          <el-select v-model="form.year" placeholder="请选择年份" style="width:100%">
            <el-option v-for="year in years" :key="year" :label="year + '年'" :value="year" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核月份" prop="month">
          <el-select v-model="form.month" placeholder="请选择月份" style="width:100%">
            <el-option v-for="month in months" :key="month" :label="month + '月'" :value="month" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核等级" prop="grade">
          <el-select v-model="form.grade" placeholder="请选择考核等级" style="width:100%">
            <el-option label="优秀" value="优秀" />
            <el-option label="良好" value="良好" />
            <el-option label="合格" value="合格" />
            <el-option label="不合格" value="不合格" />
          </el-select>
        </el-form-item>
        <el-form-item label="评语" prop="comment">
          <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入评语" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="formLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, CircleCheck, Check, Close } from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const currentYear = new Date().getFullYear()
const years = Array.from({ length: 10 }, (_, i) => String(currentYear - i))
const months = Array.from({ length: 12 }, (_, i) => String(i + 1))

const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  year: String(currentYear),
  month: String(new Date().getMonth() + 1)
})

const dialogVisible = ref(false)
const formLoading = ref(false)
const formRef = ref<any>()
const isEdit = ref(false)

const form = reactive({
  id: null as number | null,
  memberId: null as number | null,
  memberName: '',
  year: String(currentYear),
  month: String(new Date().getMonth() + 1),
  grade: '',
  comment: '',
  evaluator: '管理员'
})

const formRules = {
  memberId: [{ required: true, message: '请选择党员', trigger: 'change' }],
  year: [{ required: true, message: '请选择年份', trigger: 'change' }],
  month: [{ required: true, message: '请选择月份', trigger: 'change' }],
  grade: [{ required: true, message: '请选择考核等级', trigger: 'change' }]
}

const stats = ref<any>({})
const memberList = ref<any[]>([])

const gradeTypeMap: Record<string, string> = {
  '优秀': 'success',
  '良好': 'primary',
  '合格': 'warning',
  '不合格': 'danger'
}

const getGradeType = (grade: string) => gradeTypeMap[grade] || 'info'

const fetchData = async () => {
  try {
    const response = await axios.get('/api/evaluation', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        year: searchForm.year,
        month: searchForm.month
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取考核数据失败:', error)
  }
}

const fetchStats = async () => {
  try {
    const response = await axios.get('/api/evaluation/stats', {
      params: {
        year: searchForm.year,
        month: searchForm.month
      }
    })
    if (response.data.code === 200) {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchUnevaluatedMembers = async () => {
  try {
    const response = await axios.get('/api/evaluation/unevaluated', {
      params: {
        year: form.year,
        month: form.month
      }
    })
    if (response.data.code === 200) {
      memberList.value = response.data.data
    }
  } catch (error) {
    console.error('获取未考核党员失败:', error)
  }
}

const showEvaluateDialog = () => {
  isEdit.value = false
  form.id = null
  form.memberId = null
  form.memberName = ''
  form.year = searchForm.year
  form.month = searchForm.month
  form.grade = ''
  form.comment = ''
  form.evaluator = '管理员'
  fetchUnevaluatedMembers()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  form.id = row.id
  form.memberId = row.memberId
  form.memberName = row.memberName
  form.year = row.year
  form.month = row.month
  form.grade = row.grade
  form.comment = row.comment || ''
  form.evaluator = row.evaluator || '管理员'
  memberList.value = [{ id: row.memberId, name: row.memberName }]
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    await formRef.value.validate()
    formLoading.value = true
    
    const member = memberList.value.find(m => m.id === form.memberId)
    if (member) {
      form.memberName = member.name
    }
    
    let response
    if (isEdit.value) {
      response = await axios.put(`/api/evaluation/${form.id}`, form)
    } else {
      response = await axios.post('/api/evaluation', form)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      fetchData()
      fetchStats()
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    formLoading.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该考核记录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await axios.delete(`/api/evaluation/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
      fetchStats()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
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
  fetchData()
  fetchStats()
})
</script>

<style scoped>
.evaluation-page {
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
  font-size: 20px;
  color: #303133;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
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
  color: #fff;
}

.stat-icon.green { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); }
.stat-icon.blue { background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%); }
.stat-icon.orange { background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%); }
.stat-icon.red { background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%); }

.stat-info { flex: 1; }

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin: 4px 0 0;
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
  justify-content: flex-end;
}
</style>
