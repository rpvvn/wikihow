<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <router-link to="/" class="logo">
          <span class="logo-wiki">wiki</span><span class="logo-how">How</span>
        </router-link>
        <span class="admin-badge">管理后台</span>
      </div>
      
      <nav class="sidebar-nav">
        <router-link v-if="userStore.isAdmin" to="/admin/dashboard" class="nav-item" :class="{ active: currentRoute === 'dashboard' }">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/users" class="nav-item" :class="{ active: currentRoute === 'users' }">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/articles" class="nav-item" :class="{ active: currentRoute === 'articles' }">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/categories" class="nav-item" :class="{ active: currentRoute === 'categories' }">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/comments" class="nav-item" :class="{ active: currentRoute === 'comments' }">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </router-link>
        <router-link v-if="userStore.isAdmin" to="/admin/notifications" class="nav-item" :class="{ active: currentRoute === 'notifications' }">
          <el-icon><Bell /></el-icon>
          <span>通知管理</span>
        </router-link>
        <router-link v-if="userStore.canReview" to="/admin/reviews" class="nav-item" :class="{ active: currentRoute === 'reviews' }">
          <el-icon><Checked /></el-icon>
          <span>审核管理</span>
        </router-link>
        <router-link v-if="userStore.canReview" to="/admin/outdated-reports" class="nav-item" :class="{ active: currentRoute === 'outdated-reports' }">
          <el-icon><WarningFilled /></el-icon>
          <span>过时举报</span>
        </router-link>
      </nav>
      
      <div class="sidebar-footer">
        <div class="user-info">
          <el-avatar :size="36" :src="userStore.userInfo?.avatar">
            {{ userStore.userInfo?.nickname?.charAt(0) }}
          </el-avatar>
          <div class="user-detail">
            <span class="username">{{ userStore.userInfo?.nickname }}</span>
            <span class="role">{{ roleText }}</span>
          </div>
        </div>
        <el-button text @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
        </el-button>
      </div>
    </aside>
    
    <!-- 主内容区 -->
    <main class="admin-main">
      <header class="admin-header">
        <h1>{{ pageTitle }}</h1>
        <router-link to="/" class="back-link">
          <el-icon><Back /></el-icon> 返回前台
        </router-link>
      </header>
      <div class="admin-content">
        <slot></slot>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { DataAnalysis, User, Document, Menu, ChatDotRound, Bell, SwitchButton, Back, Checked, WarningFilled } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const currentRoute = computed(() => {
  const path = route.path
  if (path.includes('dashboard')) return 'dashboard'
  if (path.includes('users')) return 'users'
  if (path.includes('outdated-reports')) return 'outdated-reports'
  if (path.includes('articles')) return 'articles'
  if (path.includes('categories')) return 'categories'
  if (path.includes('comments')) return 'comments'
  if (path.includes('notifications')) return 'notifications'
  if (path.includes('reviews')) return 'reviews'
  return ''
})

const pageTitle = computed(() => {
  const titles = {
    dashboard: '数据概览',
    users: '用户管理',
    articles: '文章管理',
    categories: '分类管理',
    comments: '评论管理',
    notifications: '通知管理',
    reviews: '审核管理',
    'outdated-reports': '过时举报管理'
  }
  return titles[currentRoute.value] || '管理后台'
})

const roleText = computed(() => {
  const role = userStore.userInfo?.role
  if (role === 'ADMIN') return '管理员'
  if (role === 'REVIEWER') return '审核员'
  return '用户'
})

const handleLogout = () => {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

/* 侧边栏 */
.admin-sidebar {
  width: 240px;
  background: #001529;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}

.logo {
  text-decoration: none;
  display: block;
  margin-bottom: 8px;
}

.logo-wiki { font-size: 24px; font-weight: bold; color: #fff; }
.logo-how { font-size: 24px; font-weight: 300; color: #93b874; }

.admin-badge {
  font-size: 12px;
  color: rgba(255,255,255,0.6);
}

.sidebar-nav {
  flex: 1;
  padding: 16px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  color: rgba(255,255,255,0.7);
  text-decoration: none;
  transition: all 0.2s;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255,255,255,0.05);
}

.nav-item.active {
  color: #fff;
  background: #93b874;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255,255,255,0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username { color: #fff; font-size: 14px; }
.role { color: rgba(255,255,255,0.5); font-size: 12px; }

.sidebar-footer .el-button { color: rgba(255,255,255,0.6); }

/* 主内容区 */
.admin-main {
  flex: 1;
  margin-left: 240px;
  display: flex;
  flex-direction: column;
}

.admin-header {
  background: #fff;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.admin-header h1 {
  font-size: 20px;
  font-weight: 500;
  color: #333;
  margin: 0;
}

.back-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.back-link:hover { color: #93b874; }

.admin-content {
  flex: 1;
  padding: 24px;
}
</style>
