<template>
  <AdminLayout>
    <div class="dashboard">
      <!-- 统计卡片 -->
      <div class="stat-cards">
        <div class="stat-card">
          <div class="stat-icon users"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.userCount }}</span>
            <span class="stat-label">用户总数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon articles"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.articleCount }}</span>
            <span class="stat-label">文章总数</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon categories"><el-icon><Menu /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.categoryCount }}</span>
            <span class="stat-label">分类数量</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon comments"><el-icon><ChatDotRound /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.commentCount }}</span>
            <span class="stat-label">评论总数</span>
          </div>
        </div>
      </div>
      
      <!-- 快捷操作 -->
      <div class="quick-actions">
        <h3>快捷操作</h3>
        <div class="action-buttons">
          <el-button type="primary" @click="$router.push('/admin/users')">
            <el-icon><User /></el-icon> 管理用户
          </el-button>
          <el-button type="success" @click="$router.push('/admin/articles')">
            <el-icon><Document /></el-icon> 管理文章
          </el-button>
          <el-button type="warning" @click="$router.push('/admin/categories')">
            <el-icon><Menu /></el-icon> 管理分类
          </el-button>
          <el-button @click="$router.push('/editor')">
            <el-icon><Edit /></el-icon> 发布文章
          </el-button>
        </div>
      </div>
      
      <!-- 最近文章 -->
      <div class="recent-section">
        <h3>最近发布的文章</h3>
        <el-table :data="recentArticles" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column label="作者" width="120">
            <template #default="{ row }">{{ row.author?.nickname }}</template>
          </el-table-column>
          <el-table-column label="分类" width="120">
            <template #default="{ row }">{{ row.category?.name }}</template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" link @click="$router.push('/article/' + row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, Menu, ChatDotRound, Edit } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import request from '@/api/index'

const stats = ref({ userCount: 0, articleCount: 0, categoryCount: 0, commentCount: 0 })
const recentArticles = ref([])

const loadStats = async () => {
  try {
    const res = await request.get('/admin/stats')
    stats.value = res.data
  } catch (e) {
    // 如果没有统计接口，使用默认值
    const articlesRes = await getArticles({ page: 1, size: 1 })
    const categoriesRes = await getCategories()
    stats.value.articleCount = articlesRes.data.total || 0
    stats.value.categoryCount = categoriesRes.data?.length || 0
  }
}

const loadRecentArticles = async () => {
  const res = await getArticles({ page: 1, size: 5 })
  recentArticles.value = res.data.list || []
}

onMounted(() => {
  loadStats()
  loadRecentArticles()
})
</script>

<style scoped>
.dashboard { display: flex; flex-direction: column; gap: 24px; }

.stat-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.stat-icon.users { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.articles { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.categories { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.comments { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 28px; font-weight: 600; color: #333; }
.stat-label { font-size: 14px; color: #999; }

.quick-actions, .recent-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

.quick-actions h3, .recent-section h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.action-buttons { display: flex; gap: 12px; }

@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
}
</style>
