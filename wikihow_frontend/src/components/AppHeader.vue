<template>
  <el-header class="app-header">
    <div class="header-content">
      <div class="brand-block">
        <router-link to="/" class="logo" @click="mobileMenuOpen = false">
          <span class="logo-wiki">wiki</span><span class="logo-how">How</span>
        </router-link>
        <span class="logo-tagline">把经验沉淀成可执行的方法</span>
      </div>

      <div class="search-wrapper" ref="searchWrapperRef">
        <div class="search-box glass-panel">
          <input
            v-model="searchKeyword"
            type="text"
            class="search-input"
            placeholder="搜一搜：我想学会..."
            @focus="showDropdown = true"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch" aria-label="搜索">
            <el-icon><Search /></el-icon>
          </button>
        </div>

        <div v-show="showDropdown" class="search-dropdown glass-panel">
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

      <div class="header-right">
        <template v-if="userStore.isLoggedIn">
          <el-button type="primary" class="write-btn" @click="router.push('/editor')">写文章</el-button>

          <div class="notification-wrapper" ref="notificationWrapperRef">
            <el-badge :value="unreadNotificationCount" :hidden="unreadNotificationCount === 0" :max="99">
              <el-button class="notification-btn" circle @click="toggleNotificationPanel" aria-label="通知">
                <el-icon :size="18"><Bell /></el-icon>
              </el-button>
            </el-badge>

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
              <el-avatar :size="34" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item v-if="userStore.canReview" command="admin" divided>后台管理</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button class="ghost-btn" @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </template>

        <button class="menu-toggle" @click.stop="mobileMenuOpen = !mobileMenuOpen" aria-label="移动菜单">
          <el-icon><Operation /></el-icon>
        </button>
      </div>
    </div>

    <transition name="fade-slide">
      <div v-show="mobileMenuOpen" class="mobile-panel glass-panel" ref="mobilePanelRef">
        <div class="mobile-search">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜一搜：我想学会..."
            @keyup.enter="handleSearch"
          />
          <button @click="handleSearch" aria-label="搜索">
            <el-icon><Search /></el-icon>
          </button>
        </div>

        <template v-if="userStore.isLoggedIn">
          <div class="mobile-user-info">
            <el-avatar :size="40" :src="userStore.userInfo?.avatar">
              {{ userStore.userInfo?.nickname?.charAt(0) }}
            </el-avatar>
            <span class="mobile-username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
          </div>
          
          <router-link class="mobile-link" to="/editor" @click="mobileMenuOpen = false">
            <el-icon><EditPen /></el-icon>
            写文章
          </router-link>
          
          <div class="mobile-link" @click.stop="toggleMobileNotification">
            <el-icon><Bell /></el-icon>
            通知
            <el-badge v-if="unreadNotificationCount > 0" :value="unreadNotificationCount" :max="99" class="mobile-badge" />
          </div>
          
          <router-link class="mobile-link" to="/profile" @click="mobileMenuOpen = false">
            <el-icon><User /></el-icon>
            个人中心
          </router-link>
          
          <router-link v-if="userStore.canReview" class="mobile-link" to="/admin" @click="mobileMenuOpen = false">
            <el-icon><Setting /></el-icon>
            后台管理
          </router-link>
        </template>

        <template v-else>
          <router-link class="mobile-link mobile-link-primary" to="/login" @click="mobileMenuOpen = false">
            <el-icon><User /></el-icon>
            登录
          </router-link>
          <router-link class="mobile-link mobile-link-primary" to="/register" @click="mobileMenuOpen = false">
            <el-icon><UserFilled /></el-icon>
            注册
          </router-link>
        </template>

        <div class="mobile-divider"></div>

        <router-link class="mobile-link" to="/" @click="mobileMenuOpen = false">首页</router-link>
        <router-link class="mobile-link" to="/contribute" @click="mobileMenuOpen = false">投稿指南</router-link>

        <router-link class="mobile-link" to="/about" @click="mobileMenuOpen = false">关于我们</router-link>

        <template v-if="userStore.isLoggedIn">
          <div class="mobile-divider"></div>
          <div class="mobile-link mobile-link-danger" @click="handleMobileLogout">
            <el-icon><SwitchButton /></el-icon>
            退出登录
          </div>
        </template>
      </div>
    </transition>

    <!-- 移动端通知面板 -->
    <transition name="fade-slide">
      <div v-show="showMobileNotification" class="mobile-notification-panel" ref="mobileNotificationPanelWrapperRef" @click.stop>
        <NotificationPanel
          ref="mobileNotificationPanelRef"
          @update:unread-count="handleNotificationCountUpdate"
          @close="showMobileNotification = false"
        />
      </div>
    </transition>
  </el-header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Bell, Operation, EditPen, User, UserFilled, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCategories } from '@/api/category'
import { getUnreadCount } from '@/api/notification'
import NotificationPanel from './NotificationPanel.vue'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')
const showDropdown = ref(false)
const showNotificationPanel = ref(false)
const showMobileNotification = ref(false)
const unreadNotificationCount = ref(0)
const categories = ref([])
const mobileMenuOpen = ref(false)

const searchWrapperRef = ref(null)
const notificationPanelRef = ref(null)
const mobileNotificationPanelRef = ref(null)
const mobileNotificationPanelWrapperRef = ref(null)
const notificationWrapperRef = ref(null)
const mobilePanelRef = ref(null)

const categoryGroups = computed(() => {
  const allCategories = categories.value
  const columns = [[], [], [], []]
  const parentCategories = allCategories.filter((item) => !item.parentId)

  parentCategories.forEach((parent, index) => {
    const columnIndex = index % 4
    columns[columnIndex].push(parent)

    const children = allCategories.filter((item) => item.parentId === parent.id)
    children.forEach((child) => {
      columns[columnIndex].push(child)
    })
  })

  return columns.filter((col) => col.length > 0)
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
    console.error('获取未读通知失败', error)
  }
}

const handleNotificationCountUpdate = (count) => {
  unreadNotificationCount.value = count
}

const toggleNotificationPanel = () => {
  showNotificationPanel.value = !showNotificationPanel.value
  if (showNotificationPanel.value && notificationPanelRef.value) {
    notificationPanelRef.value.refresh()
  }
}

const toggleMobileNotification = () => {
  mobileMenuOpen.value = false
  showMobileNotification.value = !showMobileNotification.value
  if (showMobileNotification.value && mobileNotificationPanelRef.value) {
    mobileNotificationPanelRef.value.refresh()
  }
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) return

  showDropdown.value = false
  mobileMenuOpen.value = false
  router.push({ path: '/search', query: { keyword: searchKeyword.value } })
}

const goToCategory = (category) => {
  showDropdown.value = false
  mobileMenuOpen.value = false
  router.push({ path: '/category/' + category.id })
}

const handleClickOutside = (event) => {
  if (searchWrapperRef.value && !searchWrapperRef.value.contains(event.target)) {
    showDropdown.value = false
  }

  if (notificationWrapperRef.value && !notificationWrapperRef.value.contains(event.target)) {
    showNotificationPanel.value = false
  }

  if (mobilePanelRef.value && !mobilePanelRef.value.contains(event.target)) {
    mobileMenuOpen.value = false
  }

  // 移动端通知面板点击外部关闭
  if (mobileNotificationPanelWrapperRef.value && 
      !mobileNotificationPanelWrapperRef.value.contains(event.target)) {
    showMobileNotification.value = false
  }
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
    return
  }

  if (command === 'admin') {
    router.push('/admin')
    return
  }

  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/')
  }
}

const handleMobileLogout = () => {
  mobileMenuOpen.value = false
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
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
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 56px;
  padding: 0;
  border-bottom: 1px solid rgba(205, 223, 210, 0.92);
  background: rgba(248, 252, 249, 0.8);
  backdrop-filter: blur(12px);
  overflow: visible;
}

.header-content {
  width: min(1320px, calc(100% - 40px));
  height: 100%;
  margin: 0 auto;
  display: grid;
  grid-template-columns: auto minmax(180px, 520px) auto;
  align-items: center;
  gap: 18px;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo {
  display: inline-flex;
  align-items: baseline;
}

.logo-wiki,
.logo-how {
  font-size: 30px;
  line-height: 1;
}

.logo-wiki {
  color: var(--brand-700);
  font-weight: 800;
}

.logo-how {
  color: var(--brand-500);
  font-weight: 500;
}

.logo-tagline {
  font-size: 12px;
  color: var(--ink-500);
  letter-spacing: 0.08em;
  white-space: nowrap;
}

.search-wrapper {
  position: relative;
}

.search-box {
  display: flex;
  align-items: center;
  overflow: hidden;
  border-radius: 999px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  color: var(--ink-700);
  padding: 10px 16px;
  font-size: 14px;
}

.search-input::placeholder {
  color: #7b9184;
}

.search-btn {
  border: none;
  background: transparent;
  color: var(--brand-500);
  width: 42px;
  height: 42px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  cursor: pointer;
  margin-right: 2px;
  transition: background-color 0.2s ease;
}

.search-btn:hover {
  background: rgba(89, 168, 116, 0.14);
}

.search-dropdown {
  position: absolute;
  top: calc(100% + 12px);
  left: 0;
  width: 100%;
  min-width: 300px;
  max-width: 520px;
  padding: 14px;
  border-radius: 14px;
  z-index: 1001;
  box-sizing: border-box;
}

.dropdown-columns {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 16px;
  min-width: 0;
}

.dropdown-column {
  min-width: 0;
}

.category-item {
  font-size: 13px;
  color: var(--ink-500);
  cursor: pointer;
  padding: 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-item:hover {
  color: var(--brand-500);
}

.category-item.is-parent {
  margin-top: 4px;
  font-weight: 700;
  color: var(--brand-700);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.write-btn {
  border-radius: 999px;
  background: linear-gradient(135deg, var(--brand-500), var(--brand-400));
  border: none;
}

.ghost-btn {
  border-radius: 999px;
  border-color: rgba(83, 130, 99, 0.35);
  color: var(--ink-700);
}

.notification-wrapper {
  position: relative;
}

.notification-btn {
  border: none;
  background: rgba(89, 168, 116, 0.16);
  color: var(--brand-700);
}

.notification-btn:hover {
  background: rgba(89, 168, 116, 0.26);
  color: var(--brand-700);
}

.notification-dropdown {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  border-radius: var(--radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
}

.user-info {
  cursor: pointer;
  border-radius: 999px;
  border: 2px solid rgba(85, 151, 108, 0.26);
  padding: 2px;
}

.menu-toggle {
  display: none;
  width: 38px;
  height: 38px;
  border: 1px solid rgba(83, 130, 99, 0.35);
  background: rgba(255, 255, 255, 0.7);
  border-radius: 10px;
  color: var(--ink-700);
}

/* 移动端菜单面板
 * 修改说明（2026-04-25）：
 * 1. 使用 fixed 定位替代 absolute，避免滚动时面板位置错乱
 * 2. 固定距离顶部 66px（header 高度 56px + 间距 10px）
 * 3. 限制最大宽度为视口宽度减去左右边距（24px）
 * 4. 添加 overflow-x: hidden 防止内容溢出导致横向滚动
 * 5. 设置 z-index: 99 确保面板在其他元素之上
 */
.mobile-panel {
  display: none;
  position: fixed;           /* 固定定位，不随页面滚动 */
  top: 66px;                 /* 距离顶部固定距离 */
  left: 12px;
  right: 12px;
  width: auto;
  max-width: calc(100vw - 24px);  /* 限制最大宽度，防止溢出 */
  padding: 14px;
  gap: 0;
  border-radius: 14px;
  max-height: calc(100vh - 80px);
  overflow-y: auto;
  overflow-x: hidden;        /* 防止横向滚动 */
  box-sizing: border-box;
  z-index: 99;               /* 确保面板层级正确 */
}

.mobile-search {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(83, 130, 99, 0.25);
  border-radius: 999px;
  overflow: hidden;
  margin-bottom: 12px;
  width: 100%;
  box-sizing: border-box;
}

.mobile-search input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 10px 16px;
  font-size: 14px;
  color: var(--ink-700);
  min-width: 0;
  width: 100%;
}

.mobile-search button {
  border: none;
  background: transparent;
  color: var(--brand-500);
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  cursor: pointer;
}

.mobile-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: rgba(89, 168, 116, 0.08);
  border-radius: 10px;
  margin-bottom: 8px;
  box-sizing: border-box;
  min-width: 0;
}

.mobile-username {
  font-size: 15px;
  font-weight: 600;
  color: var(--ink-700);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-link {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 12px;
  border-radius: 10px;
  color: var(--ink-700);
  font-size: 14px;
  position: relative;
  transition: background-color 0.2s ease;
  box-sizing: border-box;
  min-width: 0;
}

.mobile-link:hover {
  background: rgba(88, 140, 106, 0.12);
}

.mobile-link-primary {
  background: linear-gradient(135deg, var(--brand-500), var(--brand-400));
  color: white;
  font-weight: 600;
  justify-content: center;
  margin-bottom: 6px;
}

.mobile-link-primary:hover {
  background: linear-gradient(135deg, var(--brand-600), var(--brand-500));
}

.mobile-link-danger {
  color: var(--danger);
}

.mobile-link-danger:hover {
  background: rgba(245, 108, 108, 0.1);
}

.mobile-badge {
  margin-left: auto;
}

.mobile-divider {
  height: 1px;
  background: rgba(83, 130, 99, 0.15);
  margin: 8px 0;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.2s ease;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.notification-wrapper :deep(.el-badge__content) {
  background-color: var(--danger);
}

.mobile-notification-panel {
  position: fixed;
  top: 66px;
  left: 12px;
  right: 12px;
  max-width: calc(100vw - 24px);
  max-height: calc(100vh - 80px);
  overflow: hidden;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-soft);
  z-index: 98;
}

@media (max-width: 1120px) {
  .logo-tagline {
    display: none;
  }

  .header-content {
    grid-template-columns: auto 1fr auto;
  }
}

@media (max-width: 860px) {
  .header-content {
    gap: 10px;
    width: calc(100% - 24px);
  }

  .search-wrapper {
    display: none;
  }

  .menu-toggle {
    display: inline-grid;
    place-items: center;
  }

  .mobile-panel {
    display: grid;
  }

  .header-right :deep(.el-button),
  .notification-wrapper,
  .user-info {
    display: none;
  }

  .logo-wiki,
  .logo-how {
    font-size: 26px;
  }
}

@media (max-width: 480px) {
  .search-dropdown {
    left: -12px;
    right: -12px;
    width: auto;
    max-width: none;
  }

  .dropdown-columns {
    grid-template-columns: 1fr;
  }
}
</style>
