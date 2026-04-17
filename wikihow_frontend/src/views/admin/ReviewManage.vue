<template>
  <AdminLayout>
    <div class="review-manage">
      <!-- 标题和筛选 -->
      <div class="toolbar">
        <h2 class="page-title">文章审核管理</h2>
        <div class="filter-box">
          <el-select v-model="filterStatus" placeholder="审核状态" clearable style="width: 140px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </div>
      </div>
      
      <!-- 审核列表 -->
      <el-table :data="reviews" stripe v-loading="loading" class="review-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="文章信息" min-width="280">
          <template #default="{ row }">
            <div class="article-info">
              <el-image v-if="row.articleCoverImage" :src="row.articleCoverImage" fit="cover" class="cover-img" />
              <div v-else class="no-cover">无封面</div>
              <div class="article-detail">
                <div class="article-title">{{ row.articleTitle }}</div>
                <div class="article-meta">
                  <span class="category">{{ row.categoryName }}</span>
                  <span class="summary">{{ row.articleSummary }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="作者" width="150">
          <template #default="{ row }">
            <div class="author-info">
              <el-avatar :src="row.authorAvatar" :size="32">{{ row.authorNickname?.charAt(0) }}</el-avatar>
              <span class="author-name">{{ row.authorNickname || row.authorUsername }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核员" width="120">
          <template #default="{ row }">
            <span v-if="row.reviewerNickname">{{ row.reviewerNickname }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="previewArticle(row)">预览</el-button>
            <template v-if="row.status === 1">
              <el-button type="success" link @click="handleApprove(row)">通过</el-button>
              <el-button type="danger" link @click="handleReject(row)">拒绝</el-button>
            </template>
            <el-button v-else type="info" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="userStore.isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadReviews" />
      </div>
      
      <!-- 审核详情对话框 -->
      <el-dialog v-model="detailVisible" title="审核详情" width="500px">
        <div v-if="currentReview" class="review-detail">
          <div class="detail-item">
            <label>文章标题：</label>
            <span>{{ currentReview.articleTitle }}</span>
          </div>
          <div class="detail-item">
            <label>审核状态：</label>
            <el-tag :type="statusType(currentReview.status)">{{ currentReview.statusText }}</el-tag>
          </div>
          <div class="detail-item">
            <label>审核员：</label>
            <span>{{ currentReview.reviewerNickname || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>审核意见：</label>
            <span>{{ currentReview.comment || '无' }}</span>
          </div>
          <div class="detail-item">
            <label>提交时间：</label>
            <span>{{ formatDate(currentReview.createdAt) }}</span>
          </div>
        </div>
      </el-dialog>
      
      <!-- 审核通过对话框 -->
      <el-dialog v-model="approveVisible" title="审核通过" width="450px">
        <el-form>
          <el-form-item label="审核意见（可选）">
            <el-input v-model="approveComment" type="textarea" :rows="3" placeholder="请输入审核意见（可选）" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="approveVisible = false">取消</el-button>
          <el-button type="success" @click="confirmApprove" :loading="submitting">确认通过</el-button>
        </template>
      </el-dialog>
      
      <!-- 审核拒绝对话框 -->
      <el-dialog v-model="rejectVisible" title="审核拒绝" width="450px">
        <el-form>
          <el-form-item label="拒绝原因" required>
            <el-input v-model="rejectComment" type="textarea" :rows="3" placeholder="请输入拒绝原因（必填）" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="rejectVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject" :loading="submitting">确认拒绝</el-button>
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
import { getPendingReviews, approveReview, rejectReview, deleteReview } from '@/api/review'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const reviews = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')

// 对话框状态
const detailVisible = ref(false)
const approveVisible = ref(false)
const rejectVisible = ref(false)
const currentReview = ref(null)
const approveComment = ref('')
const rejectComment = ref('')
const submitting = ref(false)

const statusType = (s) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await getPendingReviews({ 
      page: page.value, 
      size: pageSize.value, 
      status: filterStatus.value || undefined 
    })
    reviews.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error('加载审核列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  page.value = 1
  loadReviews()
}

const previewArticle = (row) => {
  router.push(`/article/${row.articleId}`)
}

const viewDetail = (row) => {
  currentReview.value = row
  detailVisible.value = true
}

const handleApprove = (row) => {
  currentReview.value = row
  approveComment.value = ''
  approveVisible.value = true
}

const handleReject = (row) => {
  currentReview.value = row
  rejectComment.value = ''
  rejectVisible.value = true
}

const confirmApprove = async () => {
  submitting.value = true
  try {
    await approveReview(currentReview.value.id, approveComment.value)
    ElMessage.success('审核通过')
    approveVisible.value = false
    loadReviews()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const confirmReject = async () => {
  if (!rejectComment.value.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  submitting.value = true
  try {
    await rejectReview(currentReview.value.id, rejectComment.value)
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    loadReviews()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该审核记录吗？此操作不可恢复。', '删除确认', { type: 'warning' })
  try {
    await deleteReview(row.id)
    ElMessage.success('删除成功')
    loadReviews()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.review-manage {
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

.review-table {
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
}

.article-meta {
  font-size: 12px;
  color: #999;
}

.article-meta .category {
  color: #93c5fd;
  margin-right: 8px;
}

.article-meta .summary {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 150px;
  vertical-align: bottom;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
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

.review-detail .detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.review-detail .detail-item label {
  width: 80px;
  color: #666;
  flex-shrink: 0;
}

.review-detail .detail-item span {
  color: #333;
}
</style>
