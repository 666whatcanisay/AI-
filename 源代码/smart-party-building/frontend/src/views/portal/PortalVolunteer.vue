<template>
  <div class="portal-volunteer">
    <div class="stats-bar">
      <div class="stat-item">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ totalHours }}</div>
          <div class="stat-label">累计志愿时长(h)</div>
        </div>
      </div>
    </div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>志愿服务</span>
          <el-radio-group v-model="tab" size="small">
            <el-radio-button value="all">志愿活动</el-radio-button>
            <el-radio-button value="my">我参与的</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 志愿活动列表 -->
      <template v-if="tab === 'all'">
        <el-row :gutter="16">
          <el-col :span="8" v-for="item in volunteerList" :key="item.id">
            <div class="volunteer-card-item">
              <div class="volunteer-cover" :style="{ background: getCoverColor(item.id) }">
                <el-icon :size="36"><Promotion /></el-icon>
              </div>
              <div class="volunteer-body">
                <h4>{{ item.title }}</h4>
                <p class="volunteer-meta">
                  <el-icon><Clock /></el-icon> {{ item.serviceDate || '待定' }}
                </p>
                <p class="volunteer-meta">
                  <el-icon><Location /></el-icon> {{ item.location || '待定' }}
                </p>
                <div class="volunteer-footer">
                  <span class="hours-badge">{{ item.serviceHours || 0 }}小时</span>
                  <el-button type="primary" size="small" @click="handleSignup(item)">报名参加</el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <div class="pagination-wrap" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="pageNum"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchVolunteers"
          />
        </div>
      </template>

      <!-- 我参与的 -->
      <template v-else>
        <el-table :data="myVolunteers" stripe>
          <el-table-column prop="volunteerTitle" label="活动名称" />
          <el-table-column prop="signupTime" label="报名时间" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '已完成' ? 'success' : 'warning'" size="small">
                {{ row.status || '已报名' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="serviceHoursActual" label="服务时长" width="100" />
        </el-table>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { Promotion, Clock, Location } from '@element-plus/icons-vue'

const userStore = useUserStore()
const tab = ref('all')
const volunteerList = ref<any[]>([])
const myVolunteers = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(9)
const total = ref(0)
const totalHours = ref(0)

const coverColors = [
  'linear-gradient(135deg, #EC4899, #DB2777)',
  'linear-gradient(135deg, #8B5CF6, #7C3AED)',
  'linear-gradient(135deg, #14B8A6, #0D9488)',
  'linear-gradient(135deg, #F59E0B, #D97706)',
  'linear-gradient(135deg, #EF4444, #DC2626)',
  'linear-gradient(135deg, #3B82F6, #2563EB)',
]

const getCoverColor = (id: number) => coverColors[id % coverColors.length]

const fetchVolunteers = async () => {
  try {
    const res = await axios.get('/api/portal/volunteer', {
      params: { pageNum: pageNum.value, pageSize: pageSize.value }
    })
    if (res.data.code === 200) {
      volunteerList.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchMyVolunteers = async () => {
  const memberId = userStore.memberId
  if (!memberId) return
  try {
    const res = await axios.get(`/api/portal/volunteer/my/${memberId}`)
    if (res.data.code === 200) {
      myVolunteers.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchTotalHours = async () => {
  try {
    const res = await axios.get('/api/portal/volunteer/total-hours')
    if (res.data.code === 200) {
      totalHours.value = res.data.data || 0
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSignup = async (volunteer: any) => {
  const memberId = userStore.memberId
  if (!memberId) {
    ElMessage.warning('未关联党员信息')
    return
  }
  try {
    const res = await axios.post('/api/portal/volunteer/signup', {
      volunteerId: volunteer.id,
      memberId: memberId
    })
    if (res.data.code === 200) {
      ElMessage.success('报名成功')
      fetchVolunteers()
      fetchMyVolunteers()
      fetchTotalHours()
    } else {
      ElMessage.warning(res.data.message || '报名失败')
    }
  } catch (e: any) {
    ElMessage.error('报名失败')
  }
}

onMounted(() => {
  fetchVolunteers()
  fetchMyVolunteers()
  fetchTotalHours()
})
</script>

<style scoped>
.stats-bar {
  margin-bottom: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, #3B82F6, #2563EB);
  padding: 16px 24px;
  border-radius: 12px;
  color: #fff;
}

.stat-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  margin-right: 16px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.volunteer-card-item {
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
  transition: box-shadow 0.2s;
}

.volunteer-card-item:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.volunteer-cover {
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.volunteer-body {
  padding: 14px;
}

.volunteer-body h4 {
  font-size: 15px;
  margin-bottom: 8px;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.volunteer-meta {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.volunteer-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.hours-badge {
  font-size: 13px;
  color: #EC4899;
  font-weight: 600;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
