<template>
  <div class="profile-page">
    <AppHeader />
    
    <!-- 导航栏 -->
    <div class="nav-bar">
      <router-link to="/" class="back-home">
        <el-icon><HomeFilled /></el-icon> 返回首页
      </router-link>
    </div>

    <!-- 个人信息卡片 -->
    <div class="profile-hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="triggerAvatarUpload">
            <el-avatar :size="120" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) }}
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon :size="24"><Camera /></el-icon>
              <span>更换头像</span>
            </div>
            <input
              ref="avatarInput"
              type="file"
              accept="image/*"
              style="display: none"
              @change="handleAvatarChange"
            />
          </div>
        </div>
        
        <div class="user-info">
          <h1 class="nickname">{{ userStore.userInfo?.nickname }}</h1>
          <p class="username">@{{ userStore.userInfo?.username }}</p>
          <p class="bio">{{ userStore.userInfo?.bio || '这个人很懒，什么都没写~' }}</p>
          <div class="user-meta">
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              加入于 {{ formatDate(userStore.userInfo?.createdAt) }}
            </span>
          </div>
          <el-button type="primary" round @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计数据卡片 -->
    <div class="stats-section">
      <div class="stats-container">
        <div class="stat-card">
          <div class="stat-icon articles">
            <el-icon :size="28"><Document /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.articleCount || 0 }}</span>
            <span class="stat-label">发布文章</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon views">
            <el-icon :size="28"><View /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ formatNumber(stats.totalViews) }}</span>
            <span class="stat-label">总浏览量</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon likes">
            <el-icon :size="28"><Star /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ formatNumber(stats.totalLikes) }}</span>
            <span class="stat-label">获得点赞</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon favorites">
            <el-icon :size="28"><Collection /></el-icon>
          </div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.favoriteCount || 0 }}</span>
            <span class="stat-label">我的收藏</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-section">
      <el-tabs v-model="activeTab" class="profile-tabs">
        <el-tab-pane label="我的文章" name="articles">
          <div class="tab-header">
            <div class="tab-header-left">
              <span class="tab-count">共 {{ filteredArticles.length }} 篇文章</span>
              <el-select 
                v-model="articleStatusFilter" 
                placeholder="全部状态" 
                clearable 
                size="small"
                style="width: 120px; margin-left: 12px;"
                @change="filterArticles"
              >
                <el-option label="全部" :value="null" />
                <el-option label="草稿" :value="0" />
                <el-option label="待审核" :value="1" />
                <el-option label="已发布" :value="2" />
                <el-option label="已拒绝" :value="3" />
                <el-option label="已下架" :value="4" />
              </el-select>
            </div>
            <el-button type="primary" size="small" @click="$router.push('/editor')">
              <el-icon><Plus /></el-icon>
              写文章
            </el-button>
          </div>
          <div class="article-list" v-loading="loading">
            <div v-for="article in filteredArticles" :key="article.id" class="article-item">
              <div class="article-cover" @click="$router.push(`/article/${article.id}`)">
                <img v-if="article.coverImage" :src="article.coverImage" :alt="article.title" />
                <div v-else class="cover-placeholder">
                  <el-icon :size="32"><Document /></el-icon>
                </div>
              </div>
              <div class="article-content">
                <div class="article-title-row">
                  <h3 class="article-title" @click="$router.push(`/article/${article.id}`)">
                    {{ article.title }}
                  </h3>
                  <el-tag :type="getStatusType(article.status)" size="small" class="status-tag">
                    {{ getStatusText(article.status) }}
                  </el-tag>
                </div>
                <p class="article-summary">{{ article.summary }}</p>
                <div class="article-meta">
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ article.viewCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Star /></el-icon>
                    {{ article.likeCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    {{ formatDate(article.createdAt) }}
                  </span>
                </div>
              </div>
              <div class="article-actions">
                <el-button type="primary" text @click="editArticle(article.id)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="danger" text @click="confirmDeleteArticle(article)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </div>
            <el-empty v-if="!loading && filteredArticles.length === 0" description="暂无文章，快去写一篇吧~">
              <el-button type="primary" @click="$router.push('/editor')">写文章</el-button>
            </el-empty>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="我的收藏" name="favorites">
          <div class="tab-header">
            <span class="tab-count">共 {{ favorites.length }} 篇收藏</span>
          </div>
          <div class="article-grid" v-loading="loadingFavorites">
            <ArticleCard v-for="article in favorites" :key="article.id" :article="article" />
            <el-empty v-if="!loadingFavorites && favorites.length === 0" description="暂无收藏" />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="账号设置" name="settings">
          <div class="settings-section">
            <div class="settings-card">
              <div class="settings-header">
                <el-icon :size="24"><Lock /></el-icon>
                <div class="settings-title">
                  <h3>修改密码</h3>
                  <p>定期更换密码可以提高账号安全性</p>
                </div>
              </div>
              <el-form 
                ref="passwordFormRef"
                :model="passwordForm" 
                :rules="passwordRules"
                label-width="100px" 
                label-position="top"
                class="password-form"
              >
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input 
                    v-model="passwordForm.oldPassword" 
                    type="password" 
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input 
                    v-model="passwordForm.newPassword" 
                    type="password" 
                    placeholder="请输入新密码（至少6位）"
                    show-password
                  />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                  <el-input 
                    v-model="passwordForm.confirmPassword" 
                    type="password" 
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
                    确认修改
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="离线缓存" name="offline">
          <div class="tab-header">
            <div class="tab-header-left">
              <span class="tab-count">共 {{ offlineArticles.length }} 篇离线文章</span>
            </div>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleClearAllOffline"
              :disabled="offlineArticles.length === 0"
            >
              <el-icon><Delete /></el-icon>
              清除全部缓存
            </el-button>
          </div>
          <div class="offline-info">
            <el-alert
              title="离线缓存说明"
              type="info"
              :closable="false"
              show-icon
            >
              <template #default>
                收藏的文章会自动缓存到本地，即使没有网络也可以查看。您也可以在文章详情页手动缓存任意文章。
              </template>
            </el-alert>
          </div>
          <div class="article-list" v-loading="loadingOffline">
            <div v-for="article in offlineArticles" :key="article.id" class="article-item offline-item">
              <div class="article-cover" @click="$router.push(`/article/${article.id}`)">
                <img v-if="article.coverImage" :src="article.coverImage" :alt="article.title" />
                <div v-else class="cover-placeholder">
                  <el-icon :size="32"><Document /></el-icon>
                </div>
              </div>
              <div class="article-content">
                <div class="article-title-row">
                  <h3 class="article-title" @click="$router.push(`/article/${article.id}`)">
                    {{ article.title }}
                  </h3>
                  <el-tag type="success" size="small">已缓存</el-tag>
                </div>
                <p class="article-summary">{{ article.summary }}</p>
                <div class="article-meta">
                  <span class="meta-item">
                    <el-icon><User /></el-icon>
                    {{ article.author?.nickname || '未知作者' }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Clock /></el-icon>
                    缓存于 {{ formatDateTime(article.cachedAt) }}
                  </span>
                </div>
              </div>
              <div class="article-actions">
                <el-button type="primary" text @click="$router.push(`/article/${article.id}`)">
                  <el-icon><View /></el-icon>
                  查看
                </el-button>
                <el-button type="danger" text @click="handleRemoveOffline(article)">
                  <el-icon><Delete /></el-icon>
                  删除缓存
                </el-button>
              </div>
            </div>
            <el-empty v-if="!loadingOffline && offlineArticles.length === 0" description="暂无离线缓存的文章">
              <p class="empty-tip">收藏文章后会自动缓存，或在文章详情页点击"缓存离线"按钮</p>
            </el-empty>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 编辑资料弹窗 -->
    <el-dialog v-model="showEditDialog" title="编辑个人资料" width="500px" class="edit-dialog">
      <el-form :model="editForm" label-width="80px" label-position="top">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="editForm.bio"
            type="textarea"
            :rows="4"
            placeholder="介绍一下自己吧~"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认弹窗 -->
    <el-dialog v-model="showDeleteDialog" title="确认删除" width="400px">
      <p>确定要删除文章「{{ articleToDelete?.title }}」吗？此操作不可恢复。</p>
      <template #footer>
        <el-button @click="showDeleteDialog = false">取消</el-button>
        <el-button type="danger" @click="handleDeleteArticle" :loading="deleting">确认删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Camera, Edit, Calendar, Document, View, Star, Collection,
  Plus, Clock, Delete, Lock, HomeFilled, User
} from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { useUserStore } from '@/stores/user'
import { getUserArticles, deleteArticle, uploadFile } from '@/api/article'
import { getUserFavorites } from '@/api/interaction'
import { updateUser, getUserStats, changePassword } from '@/api/auth'
import { getAllOfflineArticles, removeArticleOffline, clearAllOfflineArticles } from '@/utils/offlineStorage'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('articles')
const articles = ref([])
const favorites = ref([])
const offlineArticles = ref([])
const stats = ref({})
const loading = ref(false)
const loadingFavorites = ref(false)
const loadingOffline = ref(false)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const saving = ref(false)
const deleting = ref(false)
const changingPassword = ref(false)
const articleToDelete = ref(null)
const avatarInput = ref(null)
const passwordFormRef = ref(null)
const articleStatusFilter = ref(null)

const editForm = reactive({
  nickname: '',
  bio: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证规则
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 筛选后的文章列表
const filteredArticles = computed(() => {
  if (articleStatusFilter.value === null || articleStatusFilter.value === undefined) {
    return articles.value
  }
  return articles.value.filter(article => article.status === articleStatusFilter.value)
})

// 筛选文章
const filterArticles = () => {
  // 计算属性会自动更新，这里可以添加额外逻辑
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 获取文章状态类型（新状态系统：0草稿/1待审核/2已发布/3已拒绝/4已下架）
const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return types[status] !== undefined ? types[status] : 'info'
}

// 获取文章状态文本
const getStatusText = (status) => {
  const texts = { 0: '草稿', 1: '待审核', 2: '已发布', 3: '已拒绝', 4: '已下架' }
  return texts[status] !== undefined ? texts[status] : '未知'
}

// 加载用户统计数据
const loadStats = async () => {
  try {
    const res = await getUserStats(userStore.userInfo.id)
    stats.value = res.data || {}
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载用户文章
const loadArticles = async () => {
  loading.value = true
  try {
    const res = await getUserArticles(userStore.userInfo.id, { page: 1, size: 100 })
    articles.value = res.data.list || []
  } finally {
    loading.value = false
  }
}

// 加载用户收藏
const loadFavorites = async () => {
  loadingFavorites.value = true
  try {
    const res = await getUserFavorites(userStore.userInfo.id, { page: 1, size: 100 })
    favorites.value = res.data.list || []
  } finally {
    loadingFavorites.value = false
  }
}

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 处理头像上传
const handleAvatarChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  // 验证文件大小（最大5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }
  
  try {
    // 上传图片
    const uploadRes = await uploadFile(file)
    const avatarUrl = uploadRes.data.url
    
    // 更新用户头像
    const res = await updateUser(userStore.userInfo.id, { avatar: avatarUrl })
    userStore.setUserInfo(res.data)
    ElMessage.success('头像更新成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
  
  // 清空input
  e.target.value = ''
}

// 保存个人资料
const handleSaveProfile = async () => {
  saving.value = true
  try {
    const res = await updateUser(userStore.userInfo.id, editForm)
    userStore.setUserInfo(res.data)
    showEditDialog.value = false
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 编辑文章
const editArticle = (id) => {
  router.push(`/editor/${id}`)
}

// 确认删除文章
const confirmDeleteArticle = (article) => {
  articleToDelete.value = article
  showDeleteDialog.value = true
}

// 删除文章
const handleDeleteArticle = async () => {
  if (!articleToDelete.value) return
  
  deleting.value = true
  try {
    await deleteArticle(articleToDelete.value.id)
    ElMessage.success('删除成功')
    showDeleteDialog.value = false
    articleToDelete.value = null
    // 重新加载文章列表和统计数据
    loadArticles()
    loadStats()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  } finally {
    deleting.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    changingPassword.value = true
    try {
      await changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('密码修改成功')
      // 清空表单
      passwordForm.oldPassword = ''
      passwordForm.newPassword = ''
      passwordForm.confirmPassword = ''
      passwordFormRef.value.resetFields()
    } catch (error) {
      console.error('密码修改失败:', error)
      ElMessage.error(error.response?.data?.message || '密码修改失败')
    } finally {
      changingPassword.value = false
    }
  })
}

// 监听tab切换
watch(activeTab, (tab) => {
  if (tab === 'favorites' && favorites.value.length === 0) {
    loadFavorites()
  }
  if (tab === 'offline') {
    loadOfflineArticles()
  }
})

// 格式化日期时间
const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 加载离线缓存的文章
const loadOfflineArticles = async () => {
  loadingOffline.value = true
  try {
    offlineArticles.value = await getAllOfflineArticles()
    // 按缓存时间倒序排列
    offlineArticles.value.sort((a, b) => new Date(b.cachedAt) - new Date(a.cachedAt))
  } catch (error) {
    console.error('加载离线文章失败:', error)
  } finally {
    loadingOffline.value = false
  }
}

// 删除单个离线缓存
const handleRemoveOffline = async (article) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除「${article.title}」的离线缓存吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await removeArticleOffline(article.id)
    offlineArticles.value = offlineArticles.value.filter(a => a.id !== article.id)
    ElMessage.success('已删除离线缓存')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除离线缓存失败:', error)
    }
  }
}

// 清除所有离线缓存
const handleClearAllOffline = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要清除所有离线缓存吗？此操作不可恢复。',
      '清除确认',
      {
        confirmButtonText: '确定清除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await clearAllOfflineArticles()
    offlineArticles.value = []
    ElMessage.success('已清除所有离线缓存')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('清除离线缓存失败:', error)
    }
  }
}

onMounted(() => {
  editForm.nickname = userStore.userInfo?.nickname || ''
  editForm.bio = userStore.userInfo?.bio || ''
  
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  
  loadStats()
  loadArticles()
})
</script>


<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.nav-bar {
  position: fixed;
  top: 70px;
  left: 20px;
  z-index: 10;
}

.back-home {
  color: #fff;
  font-size: 14px;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: rgba(0, 0, 0, 0.3);
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.2s;
}

.back-home:hover {
  background: rgba(0, 0, 0, 0.5);
}

/* 个人信息区域 */
.profile-hero {
  position: relative;
  padding-top: 60px;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 200px;
  background: linear-gradient(135deg, #93b874 0%, #6b9b4d 100%);
}

.hero-content {
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
  padding: 60px 20px 40px;
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.avatar-section {
  flex-shrink: 0;
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: 4px solid #fff;
}

.avatar-wrapper .el-avatar {
  font-size: 48px;
  background: linear-gradient(135deg, #93b874 0%, #6b9b4d 100%);
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-overlay span {
  font-size: 12px;
  margin-top: 4px;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-info {
  flex: 1;
  padding-top: 20px;
}

.nickname {
  margin: 0 0 4px;
  font-size: 28px;
  font-weight: 600;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.username {
  margin: 0 0 12px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

.bio {
  margin: 0 0 16px;
  font-size: 15px;
  color: #333;
  line-height: 1.6;
  max-width: 500px;
}

.user-meta {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

/* 统计数据区域 */
.stats-section {
  max-width: 1000px;
  margin: -20px auto 0;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.stat-card:hover {
  background: #f5f7fa;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon.articles {
  background: linear-gradient(135deg, #93b874 0%, #6b9b4d 100%);
}

.stat-icon.views {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
}

.stat-icon.likes {
  background: linear-gradient(135deg, #f56c6c 0%, #c45656 100%);
}

.stat-icon.favorites {
  background: linear-gradient(135deg, #e6a23c 0%, #b88230 100%);
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 2px;
}

/* 内容区域 */
.content-section {
  max-width: 1000px;
  margin: 24px auto;
  padding: 0 20px;
}

.profile-tabs {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 20px;
}

.profile-tabs :deep(.el-tabs__item) {
  font-size: 15px;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  color: #93b874;
}

.profile-tabs :deep(.el-tabs__active-bar) {
  background-color: #93b874;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.tab-header-left {
  display: flex;
  align-items: center;
}

.tab-count {
  font-size: 14px;
  color: #666;
}

/* 文章列表样式 */
.article-list {
  min-height: 200px;
}

.article-item {
  display: flex;
  gap: 20px;
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
}

.article-item:last-child {
  border-bottom: none;
}

.article-cover {
  flex-shrink: 0;
  width: 160px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.article-cover:hover img {
  transform: scale(1.05);
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8f5e0 0%, #d4e8c7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #93b874;
}

.article-content {
  flex: 1;
  min-width: 0;
}

.article-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.article-title {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: color 0.3s;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.status-tag {
  flex-shrink: 0;
}

.article-title:hover {
  color: #93b874;
}

.article-summary {
  margin: 0 0 12px;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 16px;
}

.article-meta .meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.article-actions {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
  justify-content: center;
}

/* 文章网格（收藏） */
.article-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  min-height: 200px;
}

/* 编辑弹窗 */
.edit-dialog :deep(.el-dialog__body) {
  padding-top: 10px;
}

/* 账号设置区域 */
.settings-section {
  padding: 10px 0;
  display: flex;
  justify-content: center;
}

.settings-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  width: 100%;
  max-width: 500px;
}

.settings-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.settings-header .el-icon {
  color: #93b874;
  background: #e8f5e0;
  padding: 12px;
  border-radius: 10px;
  flex-shrink: 0;
}

.settings-title h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.settings-title p {
  margin: 0;
  font-size: 13px;
  color: #999;
}

.password-form {
  max-width: 400px;
}

.password-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #333;
}

.password-form :deep(.el-input__wrapper) {
  border-radius: 8px;
}

.password-form :deep(.el-button--primary) {
  background: #93b874;
  border-color: #93b874;
  border-radius: 8px;
  padding: 10px 32px;
}

.password-form :deep(.el-button--primary:hover) {
  background: #7da862;
  border-color: #7da862;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 100px 20px 40px;
  }
  
  .user-info {
    padding-top: 0;
  }
  
  .nickname {
    color: #333;
  }
  
  .username {
    color: #666;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .bio {
    max-width: 100%;
  }
  
  .stats-container {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .article-item {
    flex-direction: column;
  }
  
  .article-cover {
    width: 100%;
    height: 180px;
  }
  
  .article-actions {
    flex-direction: row;
    justify-content: flex-start;
  }
}

/* 离线缓存相关样式 */
.offline-info {
  margin-bottom: 20px;
}

.offline-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #e8f5e0;
}

.offline-item:hover {
  border-color: #93b874;
  box-shadow: 0 2px 8px rgba(147, 184, 116, 0.15);
}

.empty-tip {
  color: #999;
  font-size: 13px;
  margin-top: 8px;
}
</style>
