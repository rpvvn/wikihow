<template>
  <div class="editor-page">
    <AppHeader />
    
    <div class="editor-container">
      <!-- 编辑锁定提示 -->
      <el-alert
        v-if="isEdit && lockInfo.locked && !lockInfo.isOwner"
        type="warning"
        :closable="false"
        show-icon
        class="lock-alert"
      >
        <template #title>
          <div class="lock-alert-content">
            <el-avatar :size="28" :src="lockInfo.lockedByAvatar">
              {{ (lockInfo.lockedByNickname || lockInfo.lockedByUsername || '?').charAt(0) }}
            </el-avatar>
            <span>
              <strong>{{ lockInfo.lockedByNickname || lockInfo.lockedByUsername }}</strong>
              正在编辑此文章，预计 {{ formatExpiresTime(lockInfo.expiresAt) }} 后可编辑
            </span>
          </div>
        </template>
        <template #default>
          <div class="lock-alert-actions">
            <el-button size="small" @click="checkLockStatus">刷新状态</el-button>
            <el-button size="small" type="primary" @click="handleBack">返回</el-button>
          </div>
        </template>
      </el-alert>

      <!-- 主编辑区域 -->
      <div class="editor-main" :class="{ 'is-locked': isEdit && lockInfo.locked && !lockInfo.isOwner }">
        <!-- 顶部操作栏 -->
        <div class="editor-toolbar">
          <div class="toolbar-left">
            <el-button :icon="ArrowLeft" @click="handleBack" text>返回</el-button>
            <el-divider direction="vertical" />
            <h1 class="editor-title">{{ isEdit ? '编辑文章' : '创作新文章' }}</h1>
          </div>
          <div class="toolbar-right">
            <!-- 状态标签 -->
            <template v-if="isEdit && articleStatus !== null">
              <el-tag :type="statusType" effect="dark" round>{{ statusText }}</el-tag>
              <el-tag v-if="lockInfo.isOwner" type="success" effect="plain" size="small">
                <el-icon><Lock /></el-icon> 编辑锁定中
              </el-tag>
            </template>
            <!-- 操作按钮 -->
            <el-button @click="handleBack">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              <el-icon><Document /></el-icon>
              {{ isEdit ? '保存' : '保存草稿' }}
            </el-button>
            <el-button
              v-if="!isEdit"
              type="success"
              @click="handleSaveAndSubmitReview"
              :loading="reviewSubmitting"
            >
              <el-icon><Promotion /></el-icon>
              发布
            </el-button>
            <el-button
              v-if="canSubmitReview"
              type="success"
              @click="handleSubmitReview"
              :loading="reviewSubmitting"
            >
              <el-icon><Promotion /></el-icon>
              提交审核
            </el-button>
          </div>
        </div>

        <!-- 被拒绝提示 -->
        <el-alert
          v-if="articleStatus === 3"
          type="error"
          :closable="false"
          class="reject-alert"
        >
          <template #title>文章审核未通过，请根据反馈修改后重新提交</template>
        </el-alert>

        <!-- 编辑表单 -->
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="editor-form">
          <div class="form-grid">
            <!-- 左侧主要内容 -->
            <div class="form-main">
              <el-form-item label="文章标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="输入一个吸引人的标题"
                  size="large"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="文章摘要">
                <el-input
                  v-model="form.summary"
                  type="textarea"
                  :rows="3"
                  placeholder="简要描述文章内容，帮助读者快速了解"
                  maxlength="300"
                  show-word-limit
                />
              </el-form-item>

              <!-- 步骤编辑区 -->
              <div class="steps-section">
                <div class="section-header">
                  <h3><el-icon><List /></el-icon> 步骤内容</h3>
                  <span class="section-hint">详细描述每个步骤，帮助读者轻松学习</span>
                </div>
                <StepEditor v-model="form.steps" />
              </div>

              <!-- 参考文献 -->
              <div class="references-section">
                <div class="section-header">
                  <h3><el-icon><Link /></el-icon> 参考文献</h3>
                  <span class="section-hint">可选，添加相关资料来源</span>
                </div>
                <div class="references-list">
                  <div v-for="(ref, index) in form.references" :key="index" class="reference-item">
                    <div class="ref-inputs">
                      <el-input v-model="ref.title" placeholder="文献标题" />
                      <el-input v-model="ref.url" placeholder="链接地址">
                        <template #prefix><el-icon><Link /></el-icon></template>
                      </el-input>
                      <el-input v-model="ref.author" placeholder="作者" style="width: 150px;" />
                      <el-input v-model="ref.publishDate" placeholder="日期" style="width: 120px;" />
                    </div>
                    <el-button type="danger" :icon="Delete" circle @click="removeReference(index)" />
                  </div>
                </div>
                <el-button type="primary" plain @click="addReference" class="add-ref-btn">
                  <el-icon><Plus /></el-icon> 添加参考文献
                </el-button>
              </div>
            </div>

            <!-- 右侧设置面板 -->
            <div class="form-sidebar">
              <el-card class="sidebar-card" shadow="never">
                <template #header>
                  <span class="sidebar-title"><el-icon><Picture /></el-icon> 封面图片</span>
                </template>
                <el-upload
                  class="cover-uploader"
                  :show-file-list="false"
                  :http-request="handleCoverUpload"
                  accept="image/*"
                >
                  <div v-if="form.coverImage" class="cover-preview-wrapper">
                    <img :src="form.coverImage" class="cover-preview" />
                    <div class="cover-overlay">
                      <el-icon><Upload /></el-icon>
                      <span>更换封面</span>
                    </div>
                  </div>
                  <div v-else class="cover-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>上传封面图</span>
                    <p>建议尺寸 800x450</p>
                  </div>
                </el-upload>
              </el-card>

              <el-card class="sidebar-card" shadow="never">
                <template #header>
                  <span class="sidebar-title"><el-icon><Setting /></el-icon> 文章设置</span>
                </template>
                <el-form-item label="分类" prop="categoryId">
                  <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%;">
                    <el-option
                      v-for="cat in categories"
                      :key="cat.id"
                      :label="cat.name"
                      :value="cat.id"
                    />
                  </el-select>
                </el-form-item>

                <el-form-item label="难度等级">
                  <el-radio-group v-model="form.difficulty" class="difficulty-group">
                    <el-radio-button value="EASY">
                      <el-icon><Sunny /></el-icon> 简单
                    </el-radio-button>
                    <el-radio-button value="MEDIUM">
                      <el-icon><Cloudy /></el-icon> 中等
                    </el-radio-button>
                    <el-radio-button value="HARD">
                      <el-icon><Lightning /></el-icon> 困难
                    </el-radio-button>
                  </el-radio-group>
                </el-form-item>

                <el-form-item label="标签">
                  <el-select
                    v-model="form.tags"
                    multiple
                    filterable
                    allow-create
                    default-first-option
                    placeholder="添加标签"
                    style="width: 100%;"
                  />
                </el-form-item>
              </el-card>
            </div>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Delete, Plus, Lock, ArrowLeft, Document, Promotion,
  List, Link, Picture, Setting, Upload, Sunny, Cloudy, Lightning
} from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import StepEditor from '@/components/StepEditor.vue'
import { getCategories } from '@/api/category'
import { createArticle, updateArticle, getArticleById, uploadFile } from '@/api/article'
import { submitReview } from '@/api/review'
import { acquireLock, releaseLock, renewLock, getLockStatus } from '@/api/editLock'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const categories = ref([])
const submitting = ref(false)
const reviewSubmitting = ref(false)
const articleStatus = ref(null)
const articleLoaded = ref(false)

// 编辑锁相关
const lockInfo = reactive({
  locked: false,
  isOwner: false,
  lockedByUserId: null,
  lockedByUsername: '',
  lockedByNickname: '',
  lockedByAvatar: '',
  lockedAt: null,
  expiresAt: null
})
let lockRenewTimer = null
const LOCK_RENEW_INTERVAL = 5 * 60 * 1000 // 5分钟续期一次

const isEdit = computed(() => !!route.params.id)

const canSubmitReview = computed(() => {
  return isEdit.value && articleLoaded.value && (articleStatus.value === 0 || articleStatus.value === 3)
})

const statusType = computed(() => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return types[articleStatus.value] ?? 'info'
})

const statusText = computed(() => {
  const texts = { 0: '草稿', 1: '待审核', 2: '已发布', 3: '已拒绝', 4: '已下架' }
  return texts[articleStatus.value] ?? '未知'
})

const form = reactive({
  title: '',
  summary: '',
  coverImage: '',
  categoryId: null,
  difficulty: 'MEDIUM',
  tags: [],
  steps: [{ order: 1, title: '', content: '', image: '' }],
  references: []
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

// ========== 数据加载 ==========
const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadArticle = async () => {
  if (!isEdit.value) {
    articleLoaded.value = true
    return
  }
  try {
    const res = await getArticleById(route.params.id)
    const article = res.data
    form.title = article.title
    form.summary = article.summary
    form.coverImage = article.coverImage
    form.categoryId = article.category?.id
    form.difficulty = article.difficulty
    form.tags = article.tags || []
    form.steps = article.steps || []
    form.references = article.references || []
    articleStatus.value = article.status
    articleLoaded.value = true
    // 加载完成后尝试获取编辑锁
    await acquireEditLock()
  } catch (error) {
    console.error('加载文章失败:', error)
    articleLoaded.value = true
  }
}

// ========== 参考文献操作 ==========
const addReference = () => {
  form.references.push({ title: '', url: '', author: '', publishDate: '' })
}

const removeReference = (index) => {
  form.references.splice(index, 1)
}

// ========== 文件上传 ==========
const handleCoverUpload = async (options) => {
  try {
    const res = await uploadFile(options.file)
    form.coverImage = res.data.url
    ElMessage.success('封面上传成功')
  } catch (error) {
    ElMessage.error('上传失败，请重试')
  }
}

// ========== 表单提交 ==========
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (form.steps.length === 0 || !form.steps[0].title) {
    ElMessage.warning('请至少添加一个步骤')
    return
  }

  submitting.value = true
  try {
    if (isEdit.value) {
      await updateArticle(route.params.id, form)
      ElMessage.success('保存成功')
      await loadArticle()
    } else {
      const res = await createArticle(form)
      const newArticleId = res.data.id
      ElMessage.success('文章已保存为草稿')
      // 使用 replace 而不是 push，防止用户返回后重复提交
      await router.replace(`/editor/${newArticleId}`)
      articleStatus.value = 0
    }
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleSubmitReview = async () => {
  reviewSubmitting.value = true
  try {
    await submitReview(route.params.id)
    ElMessage.success('已提交审核，请等待审核结果')
    articleStatus.value = 1
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '提交审核失败')
  } finally {
    reviewSubmitting.value = false
  }
}

const handleSaveAndSubmitReview = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (form.steps.length === 0 || !form.steps[0].title) {
    ElMessage.warning('请至少添加一个步骤')
    return
  }

  reviewSubmitting.value = true
  try {
    const res = await createArticle(form)
    const newArticleId = res.data.id
    await submitReview(newArticleId)
    ElMessage.success('文章已提交审核，请等待审核结果')
    // 使用 replace 而不是 push，防止用户返回后重复提交
    await router.replace(`/editor/${newArticleId}`)
    articleStatus.value = 1
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '提交失败，请重试')
  } finally {
    reviewSubmitting.value = false
  }
}

// ========== 编辑锁相关 ==========
const updateLockInfo = (data) => {
  lockInfo.locked = data.locked || false
  lockInfo.isOwner = data.isOwner || false
  lockInfo.lockedByUserId = data.lockedByUserId
  lockInfo.lockedByUsername = data.lockedByUsername || ''
  lockInfo.lockedByNickname = data.lockedByNickname || ''
  lockInfo.lockedByAvatar = data.lockedByAvatar || ''
  lockInfo.lockedAt = data.lockedAt
  lockInfo.expiresAt = data.expiresAt
}

const acquireEditLock = async () => {
  if (!isEdit.value || !route.params.id) return
  try {
    const res = await acquireLock(route.params.id)
    updateLockInfo(res.data)
    if (lockInfo.locked && lockInfo.isOwner) {
      startLockRenewTimer()
    } else if (lockInfo.locked && !lockInfo.isOwner) {
      ElMessage.warning(`文章正在被 ${lockInfo.lockedByNickname || lockInfo.lockedByUsername} 编辑`)
    }
  } catch (error) {
    console.error('获取编辑锁失败:', error)
  }
}

const releaseEditLock = async (articleId = null) => {
  const id = articleId || route.params.id
  if (!id) return
  // 只有锁的持有者才释放
  if (!lockInfo.isOwner) return
  
  stopLockRenewTimer()
  try {
    await releaseLock(id)
    console.log('编辑锁已释放')
  } catch (error) {
    console.error('释放编辑锁失败:', error)
  }
  updateLockInfo({ locked: false, isOwner: false })
}

const renewEditLock = async () => {
  if (!isEdit.value || !route.params.id || !lockInfo.isOwner) return
  try {
    const res = await renewLock(route.params.id)
    updateLockInfo(res.data)
  } catch (error) {
    console.error('续期编辑锁失败:', error)
    await acquireEditLock()
  }
}

const checkLockStatus = async () => {
  if (!isEdit.value || !route.params.id) return
  try {
    const res = await getLockStatus(route.params.id)
    updateLockInfo(res.data)
    if (!lockInfo.locked) {
      await acquireEditLock()
    }
  } catch (error) {
    console.error('检查锁状态失败:', error)
  }
}

const startLockRenewTimer = () => {
  stopLockRenewTimer()
  lockRenewTimer = setInterval(renewEditLock, LOCK_RENEW_INTERVAL)
}

const stopLockRenewTimer = () => {
  if (lockRenewTimer) {
    clearInterval(lockRenewTimer)
    lockRenewTimer = null
  }
}

const formatExpiresTime = (expiresAt) => {
  if (!expiresAt) return '未知时间'
  const expires = new Date(expiresAt)
  const now = new Date()
  const diffMs = expires - now
  if (diffMs <= 0) return '即将'
  const diffMins = Math.ceil(diffMs / 60000)
  if (diffMins < 60) return `${diffMins} 分钟`
  return `${Math.ceil(diffMins / 60)} 小时`
}

// ========== 返回处理（释放锁）==========
const handleBack = async () => {
  // 先释放编辑锁，再返回
  if (isEdit.value && lockInfo.isOwner) {
    await releaseEditLock()
  }
  router.back()
}

// ========== 生命周期 ==========
onMounted(() => {
  loadCategories()
  loadArticle()
})

// 监听路由参数变化
watch(() => route.params.id, async (newId, oldId) => {
  if (newId && newId !== oldId) {
    articleLoaded.value = false
    if (oldId && lockInfo.isOwner) {
      await releaseEditLock(oldId)
    }
    loadArticle()
  }
}, { immediate: false })

// 组件卸载前释放编辑锁
onBeforeUnmount(async () => {
  stopLockRenewTimer()
  if (isEdit.value && lockInfo.isOwner && route.params.id) {
    // 同步释放锁（使用 fetch + keepalive）
    try {
      // 从 pinia-plugin-persistedstate 存储中获取 token
      const stored = localStorage.getItem('user')
      let token = ''
      if (stored) {
        try {
          const parsed = JSON.parse(stored)
          token = parsed.token || ''
        } catch (e) {
          // ignore
        }
      }
      await fetch(`/api/articles/${route.params.id}/lock`, {
        method: 'DELETE',
        headers: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        keepalive: true
      })
      console.log('编辑锁已释放（组件卸载）')
    } catch (e) {
      console.error('释放锁失败:', e)
    }
  }
  // 移除事件监听
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})

// 页面可见性变化处理
const handleVisibilityChange = () => {
  if (!document.hidden && isEdit.value && lockInfo.isOwner) {
    renewEditLock()
  }
}

// 注册事件监听
if (typeof document !== 'undefined') {
  document.addEventListener('visibilitychange', handleVisibilityChange)
}
</script>

<style scoped>
.editor-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

.editor-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 80px 24px 40px;
}

.lock-alert {
  margin-bottom: 20px;
  border-radius: 12px;
}

.lock-alert-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lock-alert-actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}

.editor-main {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.editor-main.is-locked {
  opacity: 0.6;
  pointer-events: none;
  filter: grayscale(0.3);
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.toolbar-left .el-button {
  color: #fff;
}

.toolbar-left .el-divider {
  background: rgba(255, 255, 255, 0.3);
}

.editor-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reject-alert {
  margin: 16px 24px 0;
  border-radius: 8px;
}

.editor-form {
  padding: 24px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .form-sidebar {
    order: -1;
  }
}

.form-main {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.section-hint {
  color: #909399;
  font-size: 13px;
}

.steps-section,
.references-section {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
}

.references-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 16px;
}

.reference-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.ref-inputs {
  flex: 1;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.ref-inputs .el-input {
  flex: 1;
  min-width: 150px;
}

.add-ref-btn {
  width: 100%;
}

/* 侧边栏 */
.form-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sidebar-card {
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.sidebar-card :deep(.el-card__header) {
  padding: 14px 16px;
  background: #fafafa;
  border-bottom: 1px solid #e4e7ed;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.sidebar-card :deep(.el-card__body) {
  padding: 16px;
}

/* 封面上传 */
.cover-uploader {
  width: 100%;
}

.cover-uploader :deep(.el-upload) {
  width: 100%;
}

.cover-preview-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 16/9;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.cover-preview-wrapper:hover .cover-overlay {
  opacity: 1;
}

.cover-overlay .el-icon {
  font-size: 32px;
}

.cover-placeholder {
  width: 100%;
  aspect-ratio: 16/9;
  border: 2px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  cursor: pointer;
  transition: all 0.3s;
}

.cover-placeholder:hover {
  border-color: #667eea;
  color: #667eea;
}

.cover-placeholder .el-icon {
  font-size: 32px;
}

.cover-placeholder p {
  margin: 0;
  font-size: 12px;
}

/* 难度选择 */
.difficulty-group {
  width: 100%;
}

.difficulty-group :deep(.el-radio-button) {
  flex: 1;
}

.difficulty-group :deep(.el-radio-button__inner) {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

/* 表单项调整 */
.editor-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #303133;
}

.editor-form :deep(.el-input__inner),
.editor-form :deep(.el-textarea__inner) {
  border-radius: 8px;
}

.editor-form :deep(.el-input__inner:focus),
.editor-form :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}
</style>
