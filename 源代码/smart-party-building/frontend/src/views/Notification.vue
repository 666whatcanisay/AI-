<template>
  <div class="notification-page">
    <div class="page-header">
      <h2>通知公告管理</h2>
    </div>

    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="请输入通知标题"
          class="search-input"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
        <el-button @click="handleReset" :icon="Refresh">重置</el-button>
        <el-button type="success" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          发布公告
        </el-button>
      </div>

      <el-table :data="tableData" border stripe>
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ (pagination.pageNum - 1) * pagination.pageSize + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="通知标题" />
        <el-table-column prop="content" label="通知内容" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.content?.substring(0, 50) }}{{ scope.row.content?.length > 50 ? '...' : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <div class="action-buttons">
              <el-button size="small" type="primary" @click="editNotification(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteNotification(scope.row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.pageNum"
        :page-sizes="[10, 20, 50]"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        class="pagination"
      />
    </div>

    <el-dialog title="发布公告" v-model="dialogVisible" width="600px" destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="通知标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="form.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            value-format="yyyy-MM-dd HH:mm:ss"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import axios from '@/utils/axios'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'

const tableData = ref<any[]>([])
const dialogVisible = ref(false)
const formRef = ref<FormInstance | null>(null)
const isEdit = ref(false)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const searchForm = reactive({
  keyword: ''
})

const form = reactive({
  id: null,
  title: '',
  content: '',
  publishTime: ''
})

const formRules = {
  title: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
}

const fetchData = async () => {
  try {
    const response = await axios.get('/api/notification', {
      params: {
        ...pagination,
        keyword: searchForm.keyword
      }
    })
    tableData.value = response.data.data.records
    pagination.total = response.data.data.total
  } catch (error) {
    console.error('获取通知列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

const handleReset = () => {
  searchForm.keyword = ''
  pagination.pageNum = 1
  fetchData()
}

const showAddDialog = () => {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.content = ''
  form.publishTime = ''
  dialogVisible.value = true
}

const editNotification = (row: any) => {
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.content = row.content
  form.publishTime = row.publishTime
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await axios.put(`/api/notification/${form.id}`, form)
      ElMessage.success('修改成功')
    } else {
      await axios.post('/api/notification', form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error: any) {
    console.error('保存通知失败:', error)
    ElMessage.error(error.response?.data?.message || '保存失败')
  }
}

const deleteNotification = async (id: number) => {
  try {
    await axios.delete(`/api/notification/${id}`)
    fetchData()
  } catch (error) {
    console.error('删除通知失败:', error)
  }
}

const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  fetchData()
}

fetchData()
</script>

<style scoped>
.notification-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.search-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
