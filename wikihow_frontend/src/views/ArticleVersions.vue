<template>
  <div class="article-versions-page">
    <AppHeader />
    <div class="page-content" v-loading="loading">
      <!-- 导航栏 -->
      <div class="nav-bar">
        <router-link :to="`/article/${articleId}`" class="back-link">
          <el-icon><ArrowLeft /></el-icon> 返回文章
        </router-link>
      </div>

      <!-- 页面标题 -->
      <div class="page-header">
        <h1>版本历史</h1>
        <p class="article-title" v-if="articleTitle">{{ articleTitle }}</p>
      </div>

      <!-- 版本列表 -->
      <div class="versions-list" v-if="versions.length > 0">
        <div
          v-for="version in versions"
          :key="version.id"
          class="version-card"
          :class="{ 'is-selected': selectedVersion?.id === version.id }"
          @click="selectVersion(version)"
        >
          <div class="version-header">
            <div class="version-info">
              <span class="version-number">版本 {{ version.versionNumber }}</span>
              <span class="version-time">{{ formatTime(version.createdAt) }}</span>
            </div>
            <el-tag size="small" v-if="version.versionNumber === latestVersion">最新</el-tag>
          </div>
          <div class="version-meta">
            <el-avatar :size="24" :src="version.userAvatar">
              {{ version.username?.charAt(0) }}
            </el-avatar>
            <span class="editor-name">{{ version.username }}</span>
          </div>
          <p class="change-desc" v-if="version.changeDescription">
            {{ version.changeDescription }}
          </p>
          <div class="version-actions">
            <el-button size="small" @click.stop="viewVersion(version)">
              <el-icon><View /></el-icon> 查看
            </el-button>
            <el-button
              size="small"
              type="primary"
              @click.stop="confirmRevert(version)"
              :disabled="version.versionNumber === latestVersion"
            >
              <el-icon><RefreshLeft /></el-icon> 回滚
            </el-button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else-if="!loading" description="暂无版本历史" />

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadVersions"
        />
      </div>
    </div>

    <!-- 版本详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="版本详情"
      width="800px"
      class="version-detail-dialog"
    >
      <div v-if="versionDetail" class="version-detail">
        <div class="detail-header">
          <h2>{{ versionDetail.title }}</h2>
          <div class="detail-meta">
            <span>版本 {{ versionDetail.versionNumber }}</span>
            <span>{{ formatTime(versionDetail.createdAt) }}</span>
            <span>修改人：{{ versionDetail.username }}</span>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>摘要</h3>
          <p>{{ versionDetail.summary || '无摘要' }}</p>
        </div>

        <div class="detail-section" v-if="versionDetail.steps?.length > 0">
          <h3>步骤内容</h3>
          <div class="steps-preview">
            <div v-for="(step, index) in versionDetail.steps" :key="index" class="step-item">
              <div class="step-header">
                <span class="step-number">步骤 {{ step.order || index + 1 }}</span>
                <span class="step-title">{{ step.title }}</span>
              </div>
              <p class="step-content">{{ step.content }}</p>
              <img v-if="step.image" :src="step.image" class="step-image" />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          type="primary"
          @click="confirmRevert(versionDetail)"
          :disabled="versionDetail?.versionNumber === latestVersion"
        >
          回滚到此版本
        </el-button>
      </template>
    </el-dialog>

    <!-- 回滚确认对话框 -->
    <el-dialog
      v-model="revertDialogVisible"
      title="确认回滚"
      width="400px"
    >
      <p>确定要回滚到版本 {{ revertTarget?.versionNumber }} 吗？</p>
      <p class="warning-text">回滚后，当前版本将被保存为新版本，文章内容将恢复到所选版本的状态。</p>
      <template #footer>
        <el-button @click="revertDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doRevert" :loading="reverting">确认回滚</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, View, RefreshLeft } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import { getArticleVersions, getVersionById, revertToVersion } from '@/api/version'
import { getArticleById } from '@/api/article'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const articleId = computed(() => route.params.id)
const articleTitle = ref('')
const articleAuthorId = ref(null)
const versions = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const latestVersion = ref(0)
const hasPermission = ref(true)

const selectedVersion = ref(null)
const detailDialogVisible = ref(false)
const versionDetail = ref(null)
const revertDialogVisible = ref(false)
const revertTarget = ref(null)
const reverting = ref(false)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 检查用户是否有权限访问版本历史
const checkPermission = () => {
  if (!userStore.isLoggedIn) {
    hasPermission.value = false
    ElMessage.warning('请先登录')
    router.push('/login')
    return false
  }
  return true
}

const loadArticleInfo = async () => {
  try {
    const res = await getArticleById(articleId.value)
    articleTitle.value = res.data.title
    articleAuthorId.value = res.data.author?.id
    
    // 检查权限：只有管理员或文章作者可以访问
    const isAuthor = articleAuthorId.value === userStore.userInfo?.id
    const isAdmin = userStore.userInfo?.role === 'ADMIN'
    
    if (!isAuthor && !isAdmin) {
      hasPermission.value = false
      ElMessage.error('只有管理员或文章作者可以查看版本历史')
      router.push(`/article/${articleId.value}`)
      return false
    }
    return true
  } catch (error) {
    console.error('加载文章信息失败:', error)
    return false
  }
}

const loadVersions = async () => {
  if (!hasPermission.value) return
  
  loading.value = true
  try {
    const res = await getArticleVersions(articleId.value, {
      page: currentPage.value,
      size: pageSize.value
    })
    versions.value = res.data.list || []
    total.value = res.data.total || 0
    
    // 获取最新版本号
    if (versions.value.length > 0) {
      latestVersion.value = Math.max(...versions.value.map(v => v.versionNumber))
    }
  } catch (error) {
    console.error('加载版本历史失败:', error)
    if (error.response?.status === 403) {
      hasPermission.value = false
      ElMessage.error('您没有权限查看版本历史')
      router.push(`/article/${articleId.value}`)
    } else {
      ElMessage.error('加载版本历史失败')
    }
  } finally {
    loading.value = false
  }
}

const selectVersion = (version) => {
  selectedVersion.value = version
}

const viewVersion = async (version) => {
  try {
    const res = await getVersionById(articleId.value, version.id)
    versionDetail.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载版本详情失败:', error)
    ElMessage.error('加载版本详情失败')
  }
}

const confirmRevert = (version) => {
  revertTarget.value = version
  revertDialogVisible.value = true
  detailDialogVisible.value = false
}

const doRevert = async () => {
  if (!revertTarget.value) return
  
  reverting.value = true
  try {
    await revertToVersion(articleId.value, revertTarget.value.id)
    ElMessage.success('回滚成功')
    revertDialogVisible.value = false
    // 重新加载版本列表
    await loadVersions()
    // 跳转到文章详情页
    router.push(`/article/${articleId.value}`)
  } catch (error) {
    console.error('回滚失败:', error)
    ElMessage.error(error.response?.data?.message || '回滚失败')
  } finally {
    reverting.value = false
  }
}

onMounted(async () => {
  if (!checkPermission()) return
  
  const hasAccess = await loadArticleInfo()
  if (hasAccess) {
    loadVersions()
  }
})
</script>


<style scoped>
.article-versions-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  max-width: 900px;
  margin: 0 auto;
  padding: 80px 20px 40px;
}

.nav-bar {
  margin-bottom: 20px;
}

.back-link {
  color: #93b874;
  font-size: 14px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.back-link:hover {
  color: #6a9a4a;
}

.page-header {
  background: #fff;
  padding: 24px 32px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 24px;
  color: #333;
}

.article-title {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.versions-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.version-card {
  background: #fff;
  padding: 20px 24px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.version-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.version-card.is-selected {
  border-color: #93b874;
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.version-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.version-number {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.version-time {
  font-size: 13px;
  color: #999;
}

.version-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.editor-name {
  font-size: 14px;
  color: #666;
}

.change-desc {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.version-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 版本详情对话框 */
.version-detail {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.detail-header h2 {
  margin: 0 0 8px;
  font-size: 20px;
  color: #333;
}

.detail-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section h3 {
  margin: 0 0 12px;
  font-size: 16px;
  color: #333;
}

.detail-section p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.steps-preview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.step-item {
  padding: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.step-number {
  background: #93b874;
  color: #fff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.step-title {
  font-weight: 500;
  color: #333;
}

.step-content {
  margin: 0;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

.step-image {
  max-width: 100%;
  max-height: 200px;
  margin-top: 12px;
  border-radius: 4px;
}

.warning-text {
  color: #e6a23c;
  font-size: 13px;
  margin-top: 8px;
}
</style>
