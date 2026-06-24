<template>
  <div class="portal-activity">
    <div class="stats-row">
      <div class="stat-item">
        <div class="stat-num">{{ ongoingActivities.length + endedActivities.length }}</div>
        <div class="stat-text">活动总数</div>
      </div>
      <div class="stat-item">
        <div class="stat-num">{{ myActivities.length }}</div>
        <div class="stat-text">已报名</div>
      </div>
    </div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>进行中活动</span>
          <el-radio-group v-model="tab" size="small">
            <el-radio-button value="all">全部活动</el-radio-button>
            <el-radio-button value="my">我报名的</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 全部活动 -->
      <template v-if="tab === 'all'">
        <el-row :gutter="16">
          <el-col :span="8" v-for="item in ongoingActivities" :key="item.id">
            <div class="activity-card-item">
              <div class="activity-cover" :style="{ background: getCoverColor(item.id) }">
                <el-icon :size="40"><Calendar /></el-icon>
              </div>
              <div class="activity-body">
                <h4>{{ item.title }}</h4>
                <p class="activity-meta">
                  <el-icon><Clock /></el-icon> {{ item.startTime }}
                </p>
                <p class="activity-meta">
                  <el-icon><Location /></el-icon> {{ item.location || '待定' }}
                </p>
                <div class="activity-footer">
                  <el-tag :type="item.status === '进行中' ? 'success' : 'warning'" size="small">
                    {{ item.status }}
                  </el-tag>
                  <el-button
                    type="primary"
                    size="small"
                    @click="handleSignup(item)"
                    :disabled="item.status === '已结束'"
                  >
                    报名参加
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <div v-if="ongoingActivities.length === 0" class="empty-tip">
          <el-empty description="暂无进行中的活动" />
        </div>
      </template>

      <!-- 我报名的 -->
      <template v-else>
        <el-table :data="myActivities" stripe>
          <el-table-column prop="activityTitle" label="活动名称" />
          <el-table-column prop="signupTime" label="报名时间" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === '已签到' ? 'success' : 'warning'" size="small">
                {{ row.status || '已报名' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </el-card>

    <!-- 已结束活动 -->
    <el-card class="mt-4">
      <template #header>
        <div class="card-header">
          <span>已结束活动</span>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="item in endedActivities" :key="item.id">
          <div class="activity-card-item ended">
            <div class="activity-cover ended-cover">
              <el-icon :size="40"><Calendar /></el-icon>
            </div>
            <div class="activity-body">
              <h4>{{ item.title }}</h4>
              <p class="activity-meta">
                <el-icon><Clock /></el-icon> {{ item.startTime }}
              </p>
              <p class="activity-meta">
                <el-icon><Location /></el-icon> {{ item.location || '待定' }}
              </p>
              <div class="activity-footer">
                <el-tag type="info" size="small">已结束</el-tag>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <div v-if="endedActivities.length === 0" class="empty-tip">
        <el-empty description="暂无已结束的活动" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { Calendar, Clock, Location } from '@element-plus/icons-vue'

const userStore = useUserStore()
const tab = ref('all')
const activityList = ref<any[]>([])
const myActivities = ref<any[]>([])

const ongoingActivities = ref<any[]>([])
const endedActivities = ref<any[]>([])

const coverColors = [
  'linear-gradient(135deg, #DC2626, #B91C1C)',
  'linear-gradient(135deg, #2563EB, #1D4ED8)',
  'linear-gradient(135deg, #059669, #047857)',
  'linear-gradient(135deg, #7C3AED, #6D28D9)',
  'linear-gradient(135deg, #EA580C, #C2410C)',
  'linear-gradient(135deg, #0891B2, #0E7490)',
]

const getCoverColor = (id: number) => coverColors[id % coverColors.length]

interface Activity {
  id: number
  title: string
  status: string
  startTime: string
  location?: string
}

const fetchActivities = async () => {
  try {
    const res = await axios.get('/api/portal/activity')
    if (res.data.code === 200) {
      const activities: Activity[] = res.data.data.records || []
      ongoingActivities.value = activities.filter(item => item.status !== '已结束')
      endedActivities.value = activities.filter(item => item.status === '已结束')
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchMyActivities = async () => {
  const memberId = userStore.memberId
  if (!memberId) return
  try {
    const res = await axios.get(`/api/portal/activity/my/${memberId}`)
    if (res.data.code === 200) {
      myActivities.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSignup = async (activity: any) => {
  const memberId = userStore.memberId
  if (!memberId) {
    ElMessage.warning('未关联党员信息')
    return
  }
  try {
    const res = await axios.post('/api/portal/activity/signup', {
      activityId: activity.id,
      memberId: memberId
    })
    if (res.data.code === 200) {
      ElMessage.success('报名成功')
      fetchMyActivities()
    } else {
      ElMessage.warning(res.data.message || '报名失败')
    }
  } catch (e: any) {
    ElMessage.error('报名失败')
  }
}

onMounted(() => {
  fetchActivities()
  fetchMyActivities()
})
</script>

<style scoped>
.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.stat-item {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-num {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
}

.stat-text {
  font-size: 14px;
  color: #6b7280;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.activity-card-item {
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #e5e7eb;
  margin-bottom: 16px;
  transition: box-shadow 0.2s;
}

.activity-card-item:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}

.activity-cover {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.activity-body {
  padding: 14px;
}

.activity-body h4 {
  font-size: 15px;
  margin-bottom: 8px;
  color: #1f2937;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-meta {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty-tip {
  padding: 40px 0;
  text-align: center;
}

/* 已结束活动卡片样式 */
.activity-card-item.ended {
  opacity: 0.85;
  border-color: #e5e7eb;
}

.activity-card-item.ended:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

.activity-cover.ended-cover {
  background: linear-gradient(135deg, #9CA3AF, #6B7280);
  color: #fff;
}

.activity-card-item.ended .activity-body h4 {
  color: #6b7280;
}
</style>
