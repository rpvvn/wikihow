<template>
  <div class="article-detail-page">
    <AppHeader />
    <div class="page-content" v-loading="loading">
      <!-- 导航栏 -->
      <div class="nav-bar">
        <router-link to="/" class="back-home">
          <el-icon><HomeFilled /></el-icon> 返回首页
        </router-link>
      </div>

      <template v-if="article">
        <!-- 离线模式提示 -->
        <div v-if="isOfflineMode" class="offline-banner">
          <div class="offline-icon">
            <el-icon :size="20"><Download /></el-icon>
          </div>
          <div class="offline-content">
            <span class="offline-title">离线模式</span>
            <span v-if="isFromCache" class="offline-desc">正在显示缓存内容</span>
          </div>
        </div>

        <!-- 过时内容警告横幅 -->
        <div v-if="article.isOutdated" class="outdated-banner">
          <div class="outdated-icon">
            <el-icon :size="24"><WarningFilled /></el-icon>
          </div>
          <div class="outdated-content">
            <h4 class="outdated-title">⚠️ 此内容可能已过时</h4>
            <p class="outdated-reason" v-if="article.outdatedReason">{{ article.outdatedReason }}</p>
            <p class="outdated-tip">建议您查阅最新资料或联系作者获取更新信息。</p>
          </div>
        </div>
        <!-- 文章头部 -->
        <div class="article-header">
          <el-tag :type="difficultyType" size="small">{{ difficultyText }}</el-tag>
          <h1 class="title">{{ article.title }}</h1>
          <p class="summary">{{ article.summary }}</p>
          
          <!-- 作者信息 -->
          <div class="author-info">
            <el-avatar :size="48" :src="article.author?.avatar">
              {{ article.author?.nickname?.charAt(0) }}
            </el-avatar>
            <div class="author-detail">
              <router-link :to="`/user/${article.author?.id}`" class="author-name">
                {{ article.author?.nickname }}
              </router-link>
              <span class="publish-time">{{ formatTime(article.createdAt) }}</span>
            </div>
          </div>

          <!-- 统计和操作 -->
          <div class="article-actions">
            <div class="stats">
              <span><el-icon><View /></el-icon> {{ article.viewCount }}</span>
              <span><el-icon><ChatDotRound /></el-icon> {{ article.commentCount }}</span>
            </div>
            <div class="actions">
              <el-button
                :type="article.isLiked ? 'primary' : 'default'"
                @click="handleLike"
              >
                <el-icon><Star /></el-icon>
                {{ article.isLiked ? '已点赞' : '点赞' }} {{ article.likeCount }}
              </el-button>
              <el-button
                :type="article.isFavorited ? 'warning' : 'default'"
                @click="handleFavorite"
              >
                <el-icon><Collection /></el-icon>
                {{ article.isFavorited ? '已收藏' : '收藏' }} {{ article.favoriteCount }}
              </el-button>
              <el-button
                class="share-btn"
                @click="handleShare"
              >
                <el-icon><Share /></el-icon>
                分享
              </el-button>
              <el-button
                v-if="canViewVersionHistory"
                class="version-btn"
                @click="goToVersions"
              >
                <el-icon><Clock /></el-icon>
                版本历史
              </el-button>
              <el-button
                v-if="userStore.isLoggedIn && !article.isOutdated"
                class="report-btn"
                @click="showReportDialog"
                :disabled="hasReported || isOfflineMode"
              >
                <el-icon><Flag /></el-icon>
                {{ hasReported ? '已举报' : '标记过时' }}
              </el-button>
              <el-button
                class="cache-btn"
                :type="isCached ? 'success' : 'default'"
                @click="handleCacheToggle"
                :disabled="isOfflineMode"
              >
                <el-icon><Download /></el-icon>
                {{ isCached ? '已缓存' : '缓存离线' }}
              </el-button>
            </div>
          </div>
        </div>

        <!-- 步骤列表 -->
        <div class="steps-section">
          <h2>步骤</h2>
          <ArticleStep
            v-for="step in article.steps"
            :key="step.id"
            :step="step"
          />
        </div>

        <!-- 你也许感兴趣 -->
        <div v-if="relatedArticles.length > 0" class="related-section">
          <h2>你也许感兴趣</h2>
          <div class="related-grid">
            <router-link
              v-for="item in relatedArticles"
              :key="item.id"
              :to="`/article/${item.id}`"
              class="related-card"
            >
              <div class="related-cover">
                <img
                  v-if="item.coverImage"
                  :src="item.coverImage"
                  :alt="item.title"
                />
                <div v-else class="cover-placeholder">
                  <el-icon :size="32"><Document /></el-icon>
                </div>
              </div>
              <div class="related-info">
                <h3 class="related-title">{{ item.title }}</h3>
                <div class="related-meta">
                  <span class="related-author">{{ item.author?.nickname }}</span>
                  <span class="related-views">
                    <el-icon><View /></el-icon>
                    {{ item.viewCount }}
                  </span>
                </div>
              </div>
            </router-link>
          </div>
        </div>

        <!-- 参考文献 -->
        <div v-if="article.references && article.references.length > 0" class="references-section">
          <h2>参考文献</h2>
          <ul class="references-list">
            <li v-for="(ref, index) in article.references" :key="index" class="reference-item">
              <span class="ref-number">[{{ index + 1 }}]</span>
              <span class="ref-content">
                <template v-if="ref.url">
                  <a :href="ref.url" target="_blank" rel="noopener noreferrer">{{ ref.title }}</a>
                </template>
                <template v-else>
                  {{ ref.title }}
                </template>
                <span v-if="ref.author" class="ref-author"> - {{ ref.author }}</span>
                <span v-if="ref.publishDate" class="ref-date"> ({{ ref.publishDate }})</span>
              </span>
            </li>
          </ul>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h2>评论 ({{ article.commentCount }})</h2>
          <CommentList :articleId="article.id" />
        </div>
      </template>
    </div>

    <!-- 分享成功对话框 -->
    <el-dialog
      v-model="shareDialogVisible"
      width="400px"
      :show-close="false"
      class="share-dialog"
      center
    >
      <div class="share-success-content">
        <div class="success-icon">
          <el-icon :size="64" color="#67c23a"><CircleCheckFilled /></el-icon>
        </div>
        <h3 class="success-title">链接已复制！</h3>
        <p class="success-desc">文章链接已复制到剪贴板，快去分享给好友吧~</p>
        <div class="article-preview">
          <div class="preview-icon">📖</div>
          <div class="preview-info">
            <span class="preview-title">{{ article?.title }}</span>
            <span class="preview-author">作者：{{ article?.author?.nickname }}</span>
          </div>
        </div>
        <el-button type="primary" @click="shareDialogVisible = false" class="close-btn">
          知道了
        </el-button>
      </div>
    </el-dialog>

    <!-- 举报过时对话框 -->
    <el-dialog
      v-model="reportDialogVisible"
      title="举报过时内容"
      width="500px"
      class="report-dialog"
    >
      <div class="report-form">
        <p class="report-tip">
          如果您认为这篇文章的内容已经过时或不再准确，请告诉我们原因。
          我们会认真审核您的反馈。
        </p>
        <el-form :model="reportForm" :rules="reportRules" ref="reportFormRef">
          <el-form-item prop="reason">
            <el-input
              v-model="reportForm.reason"
              type="textarea"
              :rows="4"
              placeholder="请描述为什么您认为这篇文章已过时（例如：技术已更新、信息不准确等）"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="submitReport" :loading="reportLoading">
          提交举报
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, Star, Collection, ChatDotRound, Document, Share, CircleCheckFilled, HomeFilled, Clock, WarningFilled, Flag, Download } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import ArticleStep from '@/components/ArticleStep.vue'
import CommentList from '@/components/CommentList.vue'
import { getArticleById, getRandomArticles } from '@/api/article'
import { toggleLike, toggleFavorite } from '@/api/interaction'
import { reportOutdated, checkReportStatus } from '@/api/outdated'
import { useUserStore } from '@/stores/user'
import { 
  saveArticleOffline, 
  getArticleOffline, 
  removeArticleOffline, 
  isArticleCached,
  isOffline,
  watchNetworkStatus 
} from '@/utils/offlineStorage'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const article = ref(null)
const loading = ref(false)
const relatedArticles = ref([])
const isOfflineMode = ref(false)
const isCached = ref(false)
const isFromCache = ref(false)

// 举报相关
const reportDialogVisible = ref(false)
const reportLoading = ref(false)
const hasReported = ref(false)
const reportFormRef = ref(null)
const reportForm = ref({
  reason: ''
})
const reportRules = {
  reason: [
    { required: true, message: '请填写举报原因', trigger: 'blur' },
    { min: 10, message: '举报原因至少10个字符', trigger: 'blur' }
  ]
}

// 网络状态监听清理函数
let cleanupNetworkWatch = null

const difficultyType = computed(() => {
  const map = { EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }
  return map[article.value?.difficulty] || 'info'
})

const difficultyText = computed(() => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[article.value?.difficulty] || '中等'
})

// 判断当前用户是否可以查看版本历史（管理员或文章作者）
const canViewVersionHistory = computed(() => {
  if (!userStore.isLoggedIn || !article.value) return false
  const isAuthor = article.value.author?.id === userStore.userInfo?.id
  const isAdmin = userStore.userInfo?.role === 'ADMIN'
  return isAuthor || isAdmin
})

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const loadArticle = async () => {
  loading.value = true
  isFromCache.value = false

  try {
    // 检查是否离线
    if (isOffline()) {
      isOfflineMode.value = true
      // 尝试从离线缓存加载
      const cachedArticle = await getArticleOffline(route.params.id)
      if (cachedArticle) {
        article.value = cachedArticle
        isFromCache.value = true
        isCached.value = true
        ElMessage.info('当前处于离线模式，显示缓存内容')
      } else {
        ElMessage.warning('离线模式下无法加载此文章，请连接网络后重试')
      }
    } else {
      // 在线模式，从服务器加载
      const res = await getArticleById(route.params.id)
      article.value = res.data

      // 检查是否已缓存
      isCached.value = await isArticleCached(route.params.id)

      // 如果文章已收藏，自动更新缓存（静默处理错误）
      if (article.value.isFavorited) {
        try {
          await saveArticleOffline(article.value)
          isCached.value = true
        } catch (cacheError) {
          console.warn('自动缓存文章失败:', cacheError)
          // 不影响主流程
        }
      }

      // 加载推荐文章
      loadRelatedArticles()
      // 检查是否已举报
      if (userStore.isLoggedIn) {
        checkUserReportStatus()
      }
    }
  } catch (error) {
    console.error('加载文章失败:', error)
    // 网络错误时尝试从缓存加载
    if (!navigator.onLine) {
      const cachedArticle = await getArticleOffline(route.params.id)
      if (cachedArticle) {
        article.value = cachedArticle
        isFromCache.value = true
        isCached.value = true
        isOfflineMode.value = true
        ElMessage.info('网络不可用，显示缓存内容')
      }
    }
  } finally {
    loading.value = false
  }
}

const checkUserReportStatus = async () => {
  try {
    const res = await checkReportStatus(route.params.id)
    hasReported.value = res.data
  } catch (error) {
    console.error('检查举报状态失败:', error)
  }
}

const loadRelatedArticles = async () => {
  try {
    const res = await getRandomArticles(route.params.id, 4)
    relatedArticles.value = res.data || []
  } catch (error) {
    console.error('加载推荐文章失败:', error)
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (isOfflineMode.value) {
    ElMessage.warning('离线模式下无法操作')
    return
  }
  try {
    await toggleLike(article.value.id)
    article.value.isLiked = !article.value.isLiked
    article.value.likeCount += article.value.isLiked ? 1 : -1
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleFavorite = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (isOfflineMode.value) {
    ElMessage.warning('离线模式下无法操作')
    return
  }
  try {
    await toggleFavorite(article.value.id)
    article.value.isFavorited = !article.value.isFavorited
    article.value.favoriteCount += article.value.isFavorited ? 1 : -1

    // 收藏时自动缓存文章，取消收藏时删除缓存
    if (article.value.isFavorited) {
      try {
        await saveArticleOffline(article.value)
        isCached.value = true
        ElMessage.success('已收藏并缓存，支持离线查看')
      } catch (cacheError) {
        console.warn('缓存文章失败:', cacheError)
        ElMessage.success('已收藏')
      }
    } else {
      try {
        await removeArticleOffline(article.value.id)
        isCached.value = false
        ElMessage.info('已取消收藏，离线缓存已清除')
      } catch (cacheError) {
        console.warn('清除缓存失败:', cacheError)
        ElMessage.info('已取消收藏')
      }
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 手动缓存/取消缓存
const handleCacheToggle = async () => {
  if (isOfflineMode.value) {
    ElMessage.warning('离线模式下无法操作')
    return
  }

  try {
    if (isCached.value) {
      await removeArticleOffline(article.value.id)
      isCached.value = false
      ElMessage.success('已清除离线缓存')
    } else {
      await saveArticleOffline(article.value)
      isCached.value = true
      ElMessage.success('已缓存，支持离线查看')
    }
  } catch (error) {
    console.error('缓存操作失败:', error)
    ElMessage.error('缓存操作失败，请稍后重试')
  }
}

// 分享功能相关
const shareDialogVisible = ref(false)

const handleShare = async () => {
  const shareUrl = window.location.href
  
  try {
    await navigator.clipboard.writeText(shareUrl)
    shareDialogVisible.value = true
  } catch (error) {
    // 降级方案：使用传统方式复制
    const textArea = document.createElement('textarea')
    textArea.value = shareUrl
    textArea.style.position = 'fixed'
    textArea.style.left = '-9999px'
    document.body.appendChild(textArea)
    textArea.select()
    try {
      document.execCommand('copy')
      shareDialogVisible.value = true
    } catch (err) {
      ElMessage.error('复制链接失败，请手动复制')
    }
    document.body.removeChild(textArea)
  }
}

const goToVersions = () => {
  if (isOfflineMode.value) {
    ElMessage.warning('离线模式下无法查看版本历史')
    return
  }
  router.push(`/article/${route.params.id}/versions`)
}

// 举报过时内容
const showReportDialog = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (isOfflineMode.value) {
    ElMessage.warning('离线模式下无法举报')
    return
  }
  reportForm.value.reason = ''
  reportDialogVisible.value = true
}

const submitReport = async () => {
  if (!reportFormRef.value) return
  
  await reportFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    reportLoading.value = true
    try {
      await reportOutdated(article.value.id, reportForm.value.reason)
      ElMessage.success('举报成功，我们会尽快处理')
      reportDialogVisible.value = false
      hasReported.value = true
    } catch (error) {
      console.error('举报失败:', error)
    } finally {
      reportLoading.value = false
    }
  })
}

// 网络状态变化处理
const handleOnline = () => {
  isOfflineMode.value = false
  ElMessage.success('网络已恢复')
  // 重新加载文章以获取最新数据
  loadArticle()
}

const handleOffline = () => {
  isOfflineMode.value = true
  ElMessage.warning('网络已断开，进入离线模式')
}

onMounted(() => {
  loadArticle()
  // 监听网络状态
  isOfflineMode.value = isOffline()
  cleanupNetworkWatch = watchNetworkStatus(handleOnline, handleOffline)
})

onUnmounted(() => {
  // 清理网络状态监听
  if (cleanupNetworkWatch) {
    cleanupNetworkWatch()
  }
})
</script>

<style scoped>
.article-detail-page {
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

.back-home {
  color: #93b874;
  font-size: 14px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.back-home:hover {
  color: #6a9a4a;
}

.article-header {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.title {
  margin: 16px 0;
  font-size: 28px;
  color: #333;
}

.summary {
  color: #666;
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.author-detail {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: 500;
  color: #333;
  text-decoration: none;
}

.author-name:hover {
  color: #409eff;
}

.publish-time {
  font-size: 13px;
  color: #999;
}

.article-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
}

.stats {
  display: flex;
  gap: 20px;
  color: #666;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.actions {
  display: flex;
  gap: 12px;
}

.steps-section,
.references-section,
.comments-section {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.steps-section h2,
.references-section h2,
.comments-section h2 {
  margin: 0 0 24px;
  font-size: 20px;
  color: #333;
}

.references-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.reference-item {
  display: flex;
  gap: 8px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  line-height: 1.6;
}

.reference-item:last-child {
  border-bottom: none;
}

.ref-number {
  color: #409eff;
  font-weight: 500;
  flex-shrink: 0;
}

.ref-content a {
  color: #409eff;
  text-decoration: none;
}

.ref-content a:hover {
  text-decoration: underline;
}

.ref-author {
  color: #666;
}

.ref-date {
  color: #999;
  font-size: 13px;
}

/* 你也许感兴趣 */
.related-section {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  margin-bottom: 24px;
}

.related-section h2 {
  margin: 0 0 24px;
  font-size: 20px;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.related-section h2::before {
  content: '';
  width: 4px;
  height: 20px;
  background: linear-gradient(135deg, #93d5dc 0%, #6ec1c8 100%);
  border-radius: 2px;
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

@media (max-width: 600px) {
  .related-grid {
    grid-template-columns: 1fr;
  }
}

.related-card {
  display: flex;
  flex-direction: column;
  background: #fafafa;
  border-radius: 8px;
  overflow: hidden;
  text-decoration: none;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.related-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: #93d5dc;
}

.related-cover {
  width: 100%;
  height: 120px;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
}

.related-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.related-card:hover .related-cover img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.related-info {
  padding: 12px;
}

.related-title {
  margin: 0 0 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.related-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.related-author {
  max-width: 60%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.related-views {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 分享按钮样式 */
.share-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  transition: all 0.3s ease;
}

.share-btn:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

/* 版本历史按钮样式 */
.version-btn {
  background: linear-gradient(135deg, #36d1dc 0%, #5b86e5 100%);
  border: none;
  color: #fff;
  transition: all 0.3s ease;
}

.version-btn:hover {
  background: linear-gradient(135deg, #2bc4cf 0%, #4a75d4 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(91, 134, 229, 0.4);
}

/* 分享成功对话框样式 */
.share-success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  margin-bottom: 16px;
  animation: bounceIn 0.6s ease;
}

@keyframes bounceIn {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.success-title {
  margin: 0 0 8px;
  font-size: 22px;
  color: #333;
  font-weight: 600;
}

.success-desc {
  margin: 0 0 24px;
  color: #666;
  font-size: 14px;
}

.article-preview {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%);
  border-radius: 12px;
  margin-bottom: 24px;
  border: 1px solid #e0e8ed;
}

.preview-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.preview-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  text-align: left;
  overflow: hidden;
}

.preview-title {
  font-weight: 500;
  color: #333;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
}

.preview-author {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.close-btn {
  width: 120px;
  height: 40px;
  font-size: 15px;
  border-radius: 20px;
  background: linear-gradient(135deg, #93d5dc 0%, #6ec1c8 100%);
  border: none;
}

.close-btn:hover {
  background: linear-gradient(135deg, #7fc9d1 0%, #5ab3bb 100%);
}

/* 过时内容警告横幅 */
.outdated-banner {
  display: flex;
  gap: 16px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #fff9e6 0%, #fff3cd 100%);
  border: 1px solid #ffc107;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(255, 193, 7, 0.2);
}

.outdated-icon {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffc107;
  border-radius: 50%;
  color: #fff;
}

.outdated-content {
  flex: 1;
}

.outdated-title {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #856404;
}

.outdated-reason {
  margin: 0 0 8px;
  color: #856404;
  font-size: 14px;
  line-height: 1.6;
  padding: 8px 12px;
  background: rgba(255, 193, 7, 0.2);
  border-radius: 6px;
}

.outdated-tip {
  margin: 0;
  color: #997a00;
  font-size: 13px;
}

/* 举报按钮样式 */
.report-btn {
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  border: none;
  color: #fff;
  transition: all 0.3s ease;
}

.report-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #fb8c00 0%, #ef6c00 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 152, 0, 0.4);
}

.report-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 举报对话框样式 */
.report-form {
  padding: 10px 0;
}

.report-tip {
  margin: 0 0 16px;
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

/* 离线模式横幅 */
.offline-banner {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  border: 1px solid #64b5f6;
  border-radius: 8px;
  margin-bottom: 16px;
}

.offline-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2196f3;
  border-radius: 50%;
  color: #fff;
}

.offline-content {
  display: flex;
  flex-direction: column;
}

.offline-title {
  font-weight: 600;
  color: #1565c0;
  font-size: 14px;
}

.offline-desc {
  font-size: 12px;
  color: #1976d2;
}

/* 缓存按钮样式 */
.cache-btn {
  transition: all 0.3s ease;
}

.cache-btn.el-button--success {
  background: linear-gradient(135deg, #4caf50 0%, #43a047 100%);
  border: none;
  color: #fff;
}

.cache-btn.el-button--success:hover {
  background: linear-gradient(135deg, #43a047 0%, #388e3c 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
}

.cache-btn:not(.el-button--success) {
  background: linear-gradient(135deg, #78909c 0%, #607d8b 100%);
  border: none;
  color: #fff;
}

.cache-btn:not(.el-button--success):hover:not(:disabled) {
  background: linear-gradient(135deg, #607d8b 0%, #546e7a 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(96, 125, 139, 0.4);
}
</style>
