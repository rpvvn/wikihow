<template>
  <el-header class="app-header">
    <div class="header-content">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <span class="logo-wiki">wiki</span><span class="logo-how">How</span>
      </router-link>

      <!-- 搜索框 -->
      <div class="search-wrapper" ref="searchWrapperRef">
        <div class="search-box">
          <input
            v-model="searchKeyword"
            type="text"
            class="search-input"
            placeholder="to do anything..."
            @focus="showDropdown = true"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <el-icon><Search /></el-icon>
          </button>
        </div>
        
        <!-- 热门分类下拉 -->
        <div v-show="showDropdown" class="search-dropdown">
          <div class="dropdown-section">
            <div class="dropdown-columns">
              <div class="dropdown-column" v-for="(group, index) in categoryGroups" :key="index">
                <div 
                  v-for="category in group" 
                  :key="category.id"
                  class="category-item"
                  :class="{ 'is-parent': !category.parentId }"
                  @click="goToCategory(category)"
                >
                  {{ category.name }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧菜单 -->
      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-button type="primary" class="write-btn" @click="router.push('/editor')">
            写文章
          </el-button>

          <!-- 通知按钮 -->
          <div class="notification-wrapper">
            <el-badge :value="unreadNotificationCount" :hidden="unreadNotificationCount === 0" :max="99">
              <el-button class="notification-btn" circle @click="toggleNotificationPanel">
                <el-icon :size="18"><Bell /></el-icon>
              </el-button>
            </el-badge>
            
            <!-- 通知面板 -->
            <transition name="fade-slide">
              <div v-show="showNotificationPanel" class="notification-dropdown" @click.stop>
                <NotificationPanel 
                  ref="notificationPanelRef"
                  @update:unread-count="handleNotificationCountUpdate"
                  @close="showNotificationPanel = false"
                />
              </div>
            </transition>
          </div>

          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="userStore.canReview" command="admin" divided>
                  后台管理
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </el-header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCategories } from '@/api/category'
import { getUnreadCount } from '@/api/notification'
import NotificationPanel from './NotificationPanel.vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')
const showDropdown = ref(false)
const searchWrapperRef = ref(null)
const categories = ref([])
const showNotificationPanel = ref(false)
const unreadNotificationCount = ref(0)
const notificationPanelRef = ref(null)

// 将分类分成多列显示
const categoryGroups = computed(() => {
  const allCategories = categories.value
  const columns = [[], [], [], []]
  
  // 先获取一级分类
  const parentCategories = allCategories.filter(c => !c.parentId)
  
  parentCategories.forEach((parent, index) => {
    const columnIndex = index % 4
    columns[columnIndex].push(parent)
    
    // 添加该一级分类下的二级分类
    const children = allCategories.filter(c => c.parentId === parent.id)
    children.forEach(child => {
      columns[columnIndex].push(child)
    })
  })
  
  return columns.filter(col => col.length > 0)
})

const loadCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadUnreadNotificationCount = async () => {
  if (!userStore.isLoggedIn) return
  try {
    const res = await getUnreadCount()
    unreadNotificationCount.value = res.data || 0
  } catch (error) {
    console.error('获取未读通知数量失败', error)
  }
}

const toggleNotificationPanel = () => {
  showNotificationPanel.value = !showNotificationPanel.value
  if (showNotificationPanel.value && notificationPanelRef.value) {
    notificationPanelRef.value.refresh()
  }
}

const handleNotificationCountUpdate = (count) => {
  unreadNotificationCount.value = count
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    showDropdown.value = false
    router.push({ path: '/search', query: { keyword: searchKeyword.value } })
  }
}

const goToCategory = (category) => {
  showDropdown.value = false
  router.push({ path: '/category/' + category.id })
}

const handleClickOutside = (event) => {
  if (searchWrapperRef.value && !searchWrapperRef.value.contains(event.target)) {
    showDropdown.value = false
  }
  // 点击外部关闭通知面板
  const notificationWrapper = document.querySelector('.notification-wrapper')
  if (notificationWrapper && !notificationWrapper.contains(event.target)) {
    showNotificationPanel.value = false
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}

onMounted(() => {
  loadCategories()
  loadUnreadNotificationCount()
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>


<style scoped>
.app-header {
  background: #93b874;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 56px;
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  gap: 30px;
}

/* Logo 样式 - WikiHow 风格 */
.logo {
  text-decoration: none;
  display: flex;
  align-items: baseline;
  flex-shrink: 0;
}

.logo-wiki {
  font-size: 28px;
  font-weight: bold;
  color: #fff;
}

.logo-how {
  font-size: 28px;
  font-weight: 300;
  color: #fff;
}

/* 搜索框容器 */
.search-wrapper {
  flex: 0 1 500px;
  position: relative;
}

.search-box {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  padding: 10px 16px;
  font-size: 14px;
  color: #666;
}

.search-input::placeholder {
  color: #999;
}

.search-btn {
  background: #fff;
  border: none;
  border-left: 1px solid #e0e0e0;
  padding: 10px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #f5f5f5;
}

/* 下拉菜单 - 与搜索框对齐 */
.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: #fff;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 16px;
  z-index: 1000;
  box-sizing: border-box;
}

.dropdown-columns {
  display: flex;
  gap: 16px;
}

.dropdown-column {
  flex: 1;
  min-width: 0;
}

.category-item {
  padding: 5px 0;
  color: #93b874;
  cursor: pointer;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: color 0.2s;
}

.category-item:hover {
  color: #6a9a4a;
  text-decoration: underline;
}

.category-item.is-parent {
  font-weight: bold;
  font-size: 13px;
  color: #5a8a3a;
  margin-top: 6px;
}

.category-item.is-parent:first-child {
  margin-top: 0;
}

/* 右侧菜单 */
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.write-btn {
  background: #fff;
  color: #93b874;
  border: none;
}

.write-btn:hover {
  background: #f0f0f0;
  color: #6a9a4a;
}

.user-info {
  cursor: pointer;
}

/* 按钮样式覆盖 */
.header-right :deep(.el-button) {
  border-radius: 4px;
}

.header-right :deep(.el-button--primary) {
  background: #fff;
  color: #93b874;
  border: none;
}

.header-right :deep(.el-button--primary:hover) {
  background: #f0f0f0;
  color: #6a9a4a;
}

.header-right :deep(.el-button--default) {
  background: transparent;
  color: #fff;
  border: 1px solid #fff;
}

.header-right :deep(.el-button--default:hover) {
  background: rgba(255, 255, 255, 0.1);
}

/* 通知按钮样式 */
.notification-wrapper {
  position: relative;
}

.notification-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}

.notification-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
}

.notification-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  z-index: 1000;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border-radius: 8px;
  overflow: hidden;
}

/* 通知面板动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Badge 样式覆盖 */
.notification-wrapper :deep(.el-badge__content) {
  background-color: #f56c6c;
}
</style>
