<template>
  <div class="user-page">
    <AppHeader />
    <div class="page-content" v-loading="loading">
      <template v-if="user">
        <el-card class="user-card">
          <div class="user-header">
            <el-avatar :size="80" :src="user.avatar">
              {{ user.nickname?.charAt(0) }}
            </el-avatar>
            <div class="user-info">
              <h2>{{ user.nickname }}</h2>
              <p>{{ user.bio || '这个人很懒，什么都没写' }}</p>
              <span class="join-time">加入于 {{ formatTime(user.createdAt) }}</span>
            </div>
          </div>
        </el-card>

        <div class="user-articles">
          <h3>TA 的文章</h3>
          <div class="article-list">
            <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
            <el-empty v-if="articles.length === 0" description="暂无文章" />
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from '@/components/AppHeader.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { getUserById } from '@/api/auth'
import { getUserArticles } from '@/api/article'

const route = useRoute()

const user = ref(null)
const articles = ref([])
const loading = ref(false)

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const loadUser = async () => {
  loading.value = true
  try {
    const res = await getUserById(route.params.id)
    user.value = res.data
    
    const articlesRes = await getUserArticles(route.params.id, { page: 1, size: 20 })
    articles.value = articlesRes.data.list || []
  } finally {
    loading.value = false
  }
}

watch(() => route.params.id, loadUser)

onMounted(loadUser)
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 20px 40px;
}

.user-card {
  margin-bottom: 24px;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  flex: 1;
}

.user-info h2 {
  margin: 0 0 8px;
}

.user-info p {
  margin: 0 0 8px;
  color: #666;
}

.join-time {
  font-size: 13px;
  color: #999;
}

.user-articles h3 {
  margin: 0 0 20px;
}

.article-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}
</style>
