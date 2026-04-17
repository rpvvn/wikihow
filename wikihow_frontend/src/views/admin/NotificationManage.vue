<template>
  <AdminLayout>
    <div class="notification-manage">
      <!-- 发送通知卡片 -->
      <el-card class="send-card">
        <template #header>
          <div class="card-header">
            <span>发送全站通知</span>
            <el-tag type="warning" size="small">将发送给所有用户</el-tag>
          </div>
        </template>
        
        <el-form 
          ref="formRef"
          :model="notificationForm" 
          :rules="rules"
          label-width="80px"
        >
          <el-form-item label="通知标题" prop="title">
            <el-input 
              v-model="notificationForm.title" 
              placeholder="请输入通知标题"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="通知内容" prop="content">
            <el-input 
              v-model="notificationForm.content" 
              type="textarea"
              :rows="4"
              placeholder="请输入通知内容"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              :loading="sending"
              @click="handleSend"
            >
              <el-icon><Promotion /></el-icon>
              发送通知
            </el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 发送历史 -->
      <el-card class="history-card">
        <template #header>
          <div class="card-header">
            <span>发送历史</span>
          </div>
        </template>
        
        <el-table :data="historyList" v-loading="loading" stripe>
          <el-table-column prop="title" label="标题" min-width="150" />
          <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
          <el-table-column label="发送者" width="120">
            <template #default="{ row }">
              <div class="sender-info">
                <el-avatar :size="24" :src="row.senderAvatar">
                  {{ row.senderNickname?.charAt(0) }}
                </el-avatar>
                <span>{{ row.senderNickname }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发送时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
        
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadHistory"
            @current-change="loadHistory"
          />
        </div>
      </el-card>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Promotion } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import { broadcastNotification, getNotifications } from '@/api/notification'
import request from '@/api/index'

const formRef = ref(null)
const sending = ref(false)
const loading = ref(false)

const notificationForm = ref({
  title: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入通知标题', trigger: 'blur' },
    { max: 100, message: '标题不能超过100个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入通知内容', trigger: 'blur' },
    { max: 500, message: '内容不能超过500个字符', trigger: 'blur' }
  ]
}

const historyList = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const handleSend = async () => {
  try {
    await formRef.value.validate()
    
    await ElMessageBox.confirm(
      '确定要发送此通知给所有用户吗？此操作不可撤销。',
      '确认发送',
      {
        confirmButtonText: '确定发送',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    sending.value = true
    await broadcastNotification(notificationForm.value)
    ElMessage.success('通知发送成功')
    handleReset()
    loadHistory()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '发送失败')
    }
  } finally {
    sending.value = false
  }
}

const handleReset = () => {
  notificationForm.value = { title: '', content: '' }
  formRef.value?.resetFields()
}

const loadHistory = async () => {
  loading.value = true
  try {
    // 获取管理员发送的通知历史（通过后端接口）
    const res = await request.get('/admin/notifications', {
      params: { page: page.value, size: size.value }
    })
    historyList.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载历史失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped>
.notification-manage {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.send-card {
  max-width: 800px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 500;
}

.history-card {
  width: 100%;
}

.sender-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
