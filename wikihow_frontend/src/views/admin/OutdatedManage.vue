<template>
  <AdminLayout>
    <div class="outdated-manage">
      <!-- 标题和筛选 -->
      <div class="toolbar">
        <h2 class="page-title">过时内容举报管理</h2>
        <div class="filter-box">
          <el-select v-model="filterStatus" placeholder="处理状态" clearable style="width: 140px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
            <el-option label="已忽略" :value="2" />
          </el-select>
        </div>
      </div>
      
      <!-- 举报列表 -->
      <el-table :data="reports" stripe v-loading="loading" class="report-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="文章信息" min-width="280">
          <template #default="{ row }">
            <div class="article-info">
              <el-image v-if="row.articleCoverImage" :src="row.articleCoverImage" fit="cover" class="cover-img" />
              <div v-else class="no-cover">无封面</div>
              <div class="article-detail">
                <div class="article-title">
                  {{ row.articleTitle }}
                  <el-tag v-if="row.articleIsOutdated" type="warning" size="small" class="outdated-tag">已过时</el-tag>
                </div>
                <div class="article-meta">
                  <span class="summary">{{ row.articleSummary }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="举报人" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :src="row.userAvatar" :size="32">{{ row.nickname?.charAt(0) }}</el-avatar>
              <span class="user-name">{{ row.nickname || row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="举报原因" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.reason" placement="top" :disabled="row.reason?.length < 50">
              <span class="reason-text">{{ row.reason }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理人" width="100">
          <template #default="{ row }">
            <span v-if="row.handlerNickname">{{ row.handlerNickname }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="举报时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="previewArticle(row)">预览</el-button>
            <template v-if="row.status === 0">
              <el-button type="warning" link @click="handleMarkOutdated(row)">标记过时</el-button>
              <el-button type="info" link @click="handleIgnore(row)">忽略</el-button>
            </template>
            <el-button v-else type="info" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="userStore.isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadReports" />
      </div>
      
      <!-- 举报详情对话框 -->
      <el-dialog v-model="detailVisible" title="举报详情" width="500px">
        <div v-if="currentReport" class="report-detail">
          <div class="detail-item">
            <label>文章标题：</label>
            <span>{{ currentReport.articleTitle }}</span>
          </div>
          <div class="detail-item">
            <label>举报人：</label>
            <span>{{ currentReport.nickname || currentReport.username }}</span>
          </div>
          <div class="detail-item">
            <label>举报原因：</label>
            <span>{{ currentReport.reason }}</span>
          </div>
          <div class="detail-item">
            <label>处理状态：</label>
            <el-tag :type="statusType(currentReport.status)">{{ currentReport.statusText }}</el-tag>
          </div>
          <div class="detail-item">
            <label>处理人：</label>
            <span>{{ currentReport.handlerNickname || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>处理备注：</label>
            <span>{{ currentReport.handleComment || '无' }}</span>
          </div>
          <div class="detail-item">
            <label>举报时间：</label>
            <span>{{ formatDate(currentReport.createdAt) }}</span>
          </div>
          <div class="detail-item" v-if="currentReport.handleTime">
            <label>处理时间：</label>
            <span>{{ formatDate(currentReport.handleTime) }}</span>
          </div>
        </div>
      </el-dialog>
      
      <!-- 标记过时对话框 -->
      <el-dialog v-model="markOutdatedVisible" title="标记为过时" width="500px">
        <el-form :model="markForm" :rules="markRules" ref="markFormRef">
          <el-form-item label="过时原因" prop="outdatedReason">
            <el-input v-model="markForm.outdatedReason" type="textarea" :rows="3" 
              placeholder="请输入过时原因，将显示在文章详情页" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="处理备注">
            <el-input v-model="markForm.comment" type="textarea" :rows="2" 
              placeholder="处理备注（可选）" maxlength="500" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="markOutdatedVisible = false">取消</el-button>
          <el-button type="warning" @click="confirmMarkOutdated" :loading="submitting">确认标记</el-button>
        </template>
      </el-dialog>
      
      <!-- 忽略举报对话框 -->
      <el-dialog v-model="ignoreVisible" title="忽略举报" width="450px">
        <el-form>
          <el-form-item label="忽略原因">
            <el-input v-model="ignoreComment" type="textarea" :rows="3" 
              placeholder="请输入忽略原因（可选）" maxlength="500" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="ignoreVisible = false">取消</el-button>
          <el-button type="info" @click="confirmIgnore" :loading="submitting">确认忽略</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminLayout from './AdminLayout.vue'
import { getOutdatedReports, handleReport, deleteOutdatedReport } from '@/api/outdated'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const reports = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')

// 对话框状态
const detailVisible = ref(false)
const markOutdatedVisible = ref(false)
const ignoreVisible = ref(false)
const currentReport = ref(null)
const markFormRef = ref(null)
const markForm = ref({
  outdatedReason: '',
  comment: ''
})
const markRules = {
  outdatedReason: [
    { required: true, message: '请输入过时原因', trigger: 'blur' }
  ]
}
const ignoreComment = ref('')
const submitting = ref(false)

const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'info' }[s] || 'info')

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadReports = async () => {
  loading.value = true
  try {
    const res = await getOutdatedReports({ 
      page: page.value, 
      size: pageSize.value, 
      status: filterStatus.value !== '' ? filterStatus.value : undefined 
    })
    reports.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error('加载举报列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  page.value = 1
  loadReports()
}

const previewArticle = (row) => {
  router.push(`/article/${row.articleId}`)
}

const viewDetail = (row) => {
  currentReport.value = row
  detailVisible.value = true
}

const handleMarkOutdated = (row) => {
  currentReport.value = row
  markForm.value = {
    outdatedReason: row.reason, // 默认使用举报原因
    comment: ''
  }
  markOutdatedVisible.value = true
}

const handleIgnore = (row) => {
  currentReport.value = row
  ignoreComment.value = ''
  ignoreVisible.value = true
}

const confirmMarkOutdated = async () => {
  if (!markFormRef.value) return
  
  await markFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await handleReport(currentReport.value.id, {
        status: 1, // 已处理
        comment: markForm.value.comment,
        outdatedReason: markForm.value.outdatedReason
      })
      ElMessage.success('已标记为过时')
      markOutdatedVisible.value = false
      loadReports()
    } catch (e) {
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const confirmIgnore = async () => {
  submitting.value = true
  try {
    await handleReport(currentReport.value.id, {
      status: 2, // 已忽略
      comment: ignoreComment.value
    })
    ElMessage.success('已忽略该举报')
    ignoreVisible.value = false
    loadReports()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该举报记录吗？此操作不可恢复。', '删除确认', { type: 'warning' })
  try {
    await deleteOutdatedReport(row.id)
    ElMessage.success('删除成功')
    loadReports()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadReports()
})
</script>

<style scoped>
.outdated-manage {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.filter-box {
  display: flex;
  gap: 12px;
}

.report-table {
  margin-bottom: 20px;
}

.article-info {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.cover-img {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  flex-shrink: 0;
}

.no-cover {
  width: 80px;
  height: 60px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
  font-size: 12px;
  flex-shrink: 0;
}

.article-detail {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 8px;
}

.outdated-tag {
  flex-shrink: 0;
}

.article-meta {
  font-size: 12px;
  color: #999;
}

.article-meta .summary {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 200px;
  vertical-align: bottom;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 13px;
  color: #666;
}

.reason-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 13px;
  color: #666;
}

.text-muted {
  color: #ccc;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.report-detail .detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.report-detail .detail-item label {
  width: 80px;
  color: #666;
  flex-shrink: 0;
}

.report-detail .detail-item span {
  color: #333;
  flex: 1;
  word-break: break-all;
}
</style>
