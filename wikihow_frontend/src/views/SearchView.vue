<template>
  <div class="search-page">
    <AppHeader />
    <div class="page-content">
      <div class="search-header">
        <div class="nav-bar">
          <router-link to="/" class="back-home">
            <el-icon><HomeFilled /></el-icon> 返回首页
          </router-link>
        </div>
        <el-input
          v-model="keyword"
          placeholder="搜索教程..."
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <div class="search-results" v-loading="loading">
        <p class="result-count" v-if="keyword">
          找到 {{ total }} 篇相关文章
        </p>
        
        <div class="article-list">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
          />
        </div>
        
        <el-empty v-if="!loading && articles.length === 0 && keyword" description="未找到相关文章" />
      </div>

      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadArticles"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { getArticles } from '@/api/article'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const articles = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const handleSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: keyword.value } })
  }
}

const loadArticles = async () => {
  if (!keyword.value) return
  loading.value = true
  try {
    const res = await getArticles({
      keyword: keyword.value,
      page: page.value,
      size: pageSize.value
    })
    articles.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

watch(() => route.query.keyword, (newKeyword) => {
  keyword.value = newKeyword || ''
  page.value = 1
  loadArticles()
}, { immediate: true })

onMounted(() => {
  keyword.value = route.query.keyword || ''
  if (keyword.value) loadArticles()
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 80px 20px 40px;
}

.search-header {
  max-width: 600px;
  margin: 0 auto 30px;
}

.nav-bar {
  margin-bottom: 16px;
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

.result-count {
  color: #666;
  margin-bottom: 20px;
}

.article-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
