<template>
  <div class="branch-page">
    <div class="page-header">
      <h2>党支部管理</h2>
      <div class="header-actions">
        <el-button type="success" @click="handleSync">
          <el-icon><component :is="icons.Refresh" /></el-icon>
          同步人数
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><component :is="icons.Plus" /></el-icon>
          新增党支部
        </el-button>
      </div>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入党支部名称"
          class="search-input"
        >
          <template #prefix>
            <el-icon><component :is="icons.Search" /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      <el-table :data="tableData" border>
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (currentPage - 1) * pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="党支部名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="leaderName" label="负责人" />
        <el-table-column prop="leaderPhone" label="联系电话" />
        <el-table-column prop="memberCount" label="党员人数" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.status === '正常' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
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
    <el-dialog :title="form.id ? '编辑党支部' : '新增党支部'" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" ref="formRef" label-width="100px" :rules="rules">
        <el-form-item label="党支部名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入党支部名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="form.description" :rows="3" placeholder="请输入党支部描述" />
        </el-form-item>
        <el-form-item label="负责人" prop="leaderName">
          <el-input v-model="form.leaderName" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="leaderPhone">
          <el-input v-model="form.leaderPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="党员人数">
          <el-input v-model="form.memberCount" type="number" placeholder="请输入党员人数" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElForm } from 'element-plus'
import axios from '@/utils/axios'

const icons = { Plus, Search, Refresh }

const searchForm = reactive({
  name: ''
})

const tableData = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

const dialogVisible = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()
const form = reactive({
  id: '',
  name: '',
  description: '',
  leaderName: '',
  leaderPhone: '',
  memberCount: 0,
  status: '正常'
})

const rules = {
  name: [
    { required: true, message: '请输入党支部名称', trigger: 'blur' },
    { min: 2, max: 100, message: '党支部名称长度在2到100个字符之间', trigger: 'blur' }
  ],
  leaderName: [
    { required: true, message: '请输入负责人姓名', trigger: 'blur' }
  ]
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/branch', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        name: searchForm.name
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.records
      total.value = response.data.data.total
    }
  } catch (error) {
    console.error('获取党支部列表失败:', error)
    ElMessage.error('获取党支部列表失败')
  }
}

const handleSync = async () => {
  try {
    await axios.post('/api/branch/sync')
    ElMessage.success('同步成功')
    fetchData()
  } catch (error) {
    console.error('同步失败:', error)
    ElMessage.error('同步失败')
  }
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  form.id = row.id
  form.name = row.name
  form.description = row.description
  form.leaderName = row.leaderName
  form.leaderPhone = row.leaderPhone
  form.memberCount = row.memberCount
  form.status = row.status
  dialogVisible.value = true
}

const handleDelete = async (id: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除这个党支部吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    const response = await axios.delete(`/api/branch/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchData()
}

const handleReset = () => {
  searchForm.name = ''
  currentPage.value = 1
  fetchData()
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    
    loading.value = true
    if (form.id) {
      await axios.put(`/api/branch/${form.id}`, form)
      ElMessage.success('更新成功')
    } else {
      await axios.post('/api/branch', form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('保存失败:', error)
      ElMessage.error('保存失败')
    }
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  form.id = ''
  form.name = ''
  form.description = ''
  form.leaderName = ''
  form.leaderPhone = ''
  form.memberCount = 0
  form.status = '正常'
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
.branch-page {
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
</style>