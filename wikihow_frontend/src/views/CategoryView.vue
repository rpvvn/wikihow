<template>
  <div class="category-page">
    <AppHeader />
    
    <!-- 分类头部横幅 -->
    <div class="category-banner">
      <div class="banner-content">
        <h1 class="category-title">{{ category?.name }}</h1>
        <div class="banner-search">
          <input 
            v-model="searchKeyword" 
            type="text" 
            :placeholder="`我想学习如何......`"
            @keyup.enter="handleSearch"
          />
          <el-icon class="search-icon"><Search /></el-icon>
        </div>
      </div>
    </div>
    
    <div class="page-content">
      <!-- 导航栏 -->
      <div class="nav-bar">
        <router-link to="/" class="back-home">
          <el-icon><HomeFilled /></el-icon> 返回首页
        </router-link>
      </div>

      <!-- 分类描述 -->
      <div class="category-description">
        <h2>{{ category?.name }}</h2>
        <p>{{ categoryDescription }}</p>
      </div>

      <!-- 子分类导航 -->
      <div v-if="subCategories.length" class="sub-categories">
        <router-link
          v-for="sub in subCategories"
          :key="sub.id"
          :to="`/category/${sub.id}`"
          class="sub-link"
        >
          {{ sub.name }}
        </router-link>
      </div>

      <!-- 文章列表 -->
      <div class="article-grid">
        <ArticleCard
          v-for="article in articles"
          :key="article.id"
          :article="article"
        />
      </div>
      
      <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />

      <!-- 分页 -->
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
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, HomeFilled } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { getCategoryById, getCategories } from '@/api/category'
import { getArticles } from '@/api/article'

const route = useRoute()
const router = useRouter()

const category = ref(null)
const parentCategory = ref(null)
const subCategories = ref([])
const articles = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(15)
const total = ref(0)
const searchKeyword = ref('')

// 分类描述映射
const categoryDescriptions = {
  '生活技能': 'WikiHow 生活技能板块拥有丰富的实用指南，帮助您掌握日常生活中的各种技巧。从时间管理到人际交往，从理财规划到自我提升，我们提供详细的步骤指导，让您的生活更加高效、充实。',
  '技术教程': 'WikiHow 技术教程板块汇集了编程开发、软件使用、系统配置等各类技术指南。无论您是初学者还是专业人士，都能在这里找到清晰易懂的教程，助您快速掌握新技能。',
  '兴趣爱好': 'WikiHow 兴趣爱好板块涵盖摄影、绘画、音乐、手工等多种创意领域。我们提供从入门到进阶的完整教程，帮助您发现和培养自己的兴趣爱好，丰富业余生活。',
  '烹饪美食': '探索美食的世界！这里有各种菜系的烹饪教程，从家常小炒到精致甜点，每道菜都配有详细的步骤说明和技巧提示，让您轻松成为厨房达人。',
  '家居清洁': '打造整洁舒适的居住环境。我们提供专业的清洁整理技巧，帮助您高效完成家务，让家焕然一新。',
  '编程开发': '从零开始学编程！涵盖多种编程语言和开发框架的入门到进阶教程，配有实战项目指导，助您成为优秀的开发者。',
  '设计创作': '释放您的创意潜能。学习平面设计、UI设计、视频剪辑等创作技能，掌握专业工具的使用方法。',
  '手工制作': '动手创造属于自己的作品。从简单的折纸到复杂的木工，我们提供各种手工制作教程，让您体验创作的乐趣。',
  '运动健身': '科学健身，健康生活。这里有专业的运动指导和健身计划，帮助您达成健身目标，保持最佳状态。'
}

const categoryDescription = computed(() => {
  if (!category.value) return ''
  return categoryDescriptions[category.value.name] || 
    `探索${category.value.name}相关的精彩教程，学习实用技能，提升自我。WikiHow 为您提供详细的步骤指导和专业建议。`
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value, categoryId: route.params.id } })
  }
}

const loadCategory = async () => {
  const id = route.params.id
  const res = await getCategoryById(id)
  category.value = res.data

  const allRes = await getCategories()
  const allCategories = allRes.data

  if (category.value.parentId) {
    parentCategory.value = allCategories.find(c => c.id === category.value.parentId)
  }

  subCategories.value = allCategories.filter(c => c.parentId === category.value.id)
}

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await getArticles({
      categoryId: route.params.id,
      page: page.value,
      size: pageSize.value
    })
    articles.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

watch(() => route.params.id, () => {
  page.value = 1
  loadCategory()
  loadArticles()
})

onMounted(() => {
  loadCategory()
  loadArticles()
})
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  background: #fff;
}

/* 分类横幅 */
.category-banner {
  background: linear-gradient(135deg, #93b874 0%, #7aa35e 100%);
  padding: 80px 20px 60px;
  margin-top: 56px;
}

.banner-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.category-title {
  color: #fff;
  font-size: 48px;
  font-weight: 300;
  margin-bottom: 30px;
}

.banner-search {
  position: relative;
  max-width: 500px;
  margin: 0 auto;
}

.banner-search input {
  width: 100%;
  padding: 14px 50px 14px 20px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  outline: none;
}

.banner-search input::placeholder {
  color: #999;
}

.search-icon {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  font-size: 20px;
}

/* 页面内容 */
.page-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

/* 导航栏 */
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

/* 分类描述 */
.category-description {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.category-description h2 {
  color: #333;
  font-size: 24px;
  margin-bottom: 15px;
}

.category-description p {
  color: #666;
  line-height: 1.8;
  font-size: 15px;
}

/* 子分类 */
.sub-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 30px;
}

.sub-link {
  color: #93b874;
  text-decoration: none;
  padding: 6px 16px;
  border: 1px solid #93b874;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.2s;
}

.sub-link:hover {
  background: #93b874;
  color: #fff;
}

/* 文章网格 - 每行5个 */
.article-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.pagination {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

@media (max-width: 1200px) {
  .article-grid { grid-template-columns: repeat(4, 1fr); }
}

@media (max-width: 992px) {
  .article-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
  .article-grid { grid-template-columns: repeat(2, 1fr); }
  .category-title { font-size: 32px; }
}

@media (max-width: 480px) {
  .article-grid { grid-template-columns: 1fr; }
}
</style>
