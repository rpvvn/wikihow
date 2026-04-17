<template>
  <div class="home-page">
    <AppHeader />
    
    <!-- 分类横幅区域 -->
    <div class="category-banner" v-if="selectedCategoryInfo" :style="bannerStyle">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1 class="banner-title">{{ selectedCategoryInfo.name }}</h1>
        <div class="banner-search">
          <input type="text" placeholder="I want to learn how to..." class="banner-search-input" />
          <button class="banner-search-btn">
            <el-icon><Search /></el-icon>
          </button>
        </div>
      </div>
    </div>

    <div class="page-content">
      <!-- 分类介绍区域 -->
      <div class="category-intro" v-if="selectedCategoryInfo">
        <div class="intro-header">
          <span class="breadcrumb">首页</span>
          <router-link to="/" class="back-home" @click="clearCategory">
            <el-icon><HomeFilled /></el-icon> 返回首页
          </router-link>
        </div>
        <h2 class="intro-title">{{ selectedCategoryInfo.name }}</h2>
        <p class="intro-description">{{ selectedCategoryInfo.description || getDefaultDescription(selectedCategoryInfo.name) }}</p>
        
        <!-- 特色主题（子分类） -->
        <div class="featured-topics" v-if="childCategories.length > 0">
          <h3 class="topics-title">特色主题</h3>
          <div class="topics-grid">
            <div 
              v-for="(child, index) in childCategories" 
              :key="child.id" 
              class="topic-card"
              @click="selectCategory(child.id)"
            >
              <div class="topic-image" :style="{ background: getTopicGradient(index) }">
                <span class="topic-name">{{ child.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="main-layout">
        <!-- 左侧文章列表区域 -->
        <div class="article-section">
          <!-- 当前分类标题 -->
          <div class="section-header" v-if="selectedCategory">
            <h2 class="current-category-title">
              <span class="category-icon" :style="{ background: getCategoryColor(selectedCategory) }"></span>
              {{ getCurrentCategoryName() }} 相关文章
            </h2>
            <el-button type="text" @click="clearCategory" class="clear-btn">
              <el-icon><Close /></el-icon> 清除筛选
            </el-button>
          </div>
          <div class="section-header" v-else>
            <h2 class="current-category-title">全部文章</h2>
          </div>

          <!-- 文章列表 -->
          <div class="article-list" v-loading="loading">
            <ArticleCard
              v-for="article in articles"
              :key="article.id"
              :article="article"
            />
            <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
          </div>

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

        <!-- 右侧分类浏览列表 -->
        <div class="category-sidebar">
          <div class="category-browse-card">
            <h3 class="sidebar-title">
              <el-icon class="title-icon"><Menu /></el-icon>
              按类别浏览
            </h3>
            <ul class="category-list">
              <li 
                v-for="(cat, index) in parentCategories" 
                :key="cat.id"
                :class="['category-item', { active: isParentCategoryActive(cat.id) }]"
                @click="selectCategory(cat.id)"
              >
                <span class="category-icon" :style="{ background: categoryColors[index % categoryColors.length] }"></span>
                <span class="category-name">{{ cat.name }}</span>
                <span class="category-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Menu, ArrowRight, Close, Search, HomeFilled } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()

const articles = ref([])
const allCategories = ref([])
const selectedCategory = ref(null)
const loading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 分类彩色图标颜色数组
const categoryColors = [
  '#FF6B6B',  // 红色
  '#4ECDC4',  // 青色
  '#45B7D1',  // 蓝色
  '#96CEB4',  // 绿色
  '#FFEAA7',  // 黄色
  '#DDA0DD',  // 紫色
  '#98D8C8',  // 薄荷绿
  '#F7DC6F',  // 金色
  '#BB8FCE',  // 淡紫
  '#85C1E9',  // 天蓝
  '#F8B500',  // 橙黄
  '#E74C3C',  // 深红
]

// 特色主题渐变色
const topicGradients = [
  'linear-gradient(135deg, rgba(0,0,0,0.3), rgba(0,0,0,0.5)), url("https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400")',
  'linear-gradient(135deg, rgba(0,0,0,0.3), rgba(0,0,0,0.5)), url("https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=400")',
  'linear-gradient(135deg, rgba(0,0,0,0.3), rgba(0,0,0,0.5)), url("https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400")',
  'linear-gradient(135deg, rgba(0,0,0,0.3), rgba(0,0,0,0.5)), url("https://images.unsplash.com/photo-1472851294608-062f824d29cc?w=400")',
]

// 只显示一级分类
const parentCategories = computed(() => {
  return allCategories.value.filter(c => !c.parentId)
})

// 当前选中分类的信息
const selectedCategoryInfo = computed(() => {
  if (!selectedCategory.value) return null
  return allCategories.value.find(c => c.id === selectedCategory.value)
})

// 当前选中分类的子分类
const childCategories = computed(() => {
  if (!selectedCategory.value) return []
  const current = allCategories.value.find(c => c.id === selectedCategory.value)
  if (!current) return []
  
  // 如果是一级分类，返回其子分类
  if (!current.parentId) {
    return allCategories.value.filter(c => c.parentId === selectedCategory.value)
  }
  // 如果是二级分类，返回同级分类
  return allCategories.value.filter(c => c.parentId === current.parentId && c.id !== selectedCategory.value)
})

// 横幅背景样式
const bannerStyle = computed(() => {
  const info = selectedCategoryInfo.value
  if (info?.coverImage) {
    return {
      backgroundImage: `url(${info.coverImage})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }
  }
  // 默认渐变背景
  const index = parentCategories.value.findIndex(c => c.id === selectedCategory.value || c.id === selectedCategoryInfo.value?.parentId)
  const colors = [
    ['#667eea', '#764ba2'],
    ['#f093fb', '#f5576c'],
    ['#4facfe', '#00f2fe'],
    ['#43e97b', '#38f9d7'],
    ['#fa709a', '#fee140'],
    ['#a8edea', '#fed6e3'],
  ]
  const [c1, c2] = colors[index % colors.length]
  return {
    background: `linear-gradient(135deg, ${c1} 0%, ${c2} 100%)`
  }
})

const loadCategories = async () => {
  try {
    const res = await getCategories()
    allCategories.value = res.data || []
    appStore.setCategories(res.data)
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await getArticles({
      page: page.value,
      size: pageSize.value,
      categoryId: selectedCategory.value
    })
    articles.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('加载文章失败:', error)
  } finally {
    loading.value = false
  }
}

const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  page.value = 1
  loadArticles()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const clearCategory = () => {
  selectedCategory.value = null
  page.value = 1
  loadArticles()
}

const getCurrentCategoryName = () => {
  const cat = allCategories.value.find(c => c.id === selectedCategory.value)
  return cat ? cat.name : '全部文章'
}

const getCategoryColor = (categoryId) => {
  const cat = allCategories.value.find(c => c.id === categoryId)
  if (!cat) return categoryColors[0]
  
  // 找到对应的一级分类索引
  let parentId = cat.parentId || cat.id
  const index = parentCategories.value.findIndex(c => c.id === parentId)
  return categoryColors[index % categoryColors.length]
}

const isParentCategoryActive = (parentId) => {
  if (selectedCategory.value === parentId) return true
  // 检查是否选中了该一级分类的子分类
  const cat = allCategories.value.find(c => c.id === selectedCategory.value)
  return cat?.parentId === parentId
}

const getTopicGradient = (index) => {
  const colors = [
    'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
  ]
  return colors[index % colors.length]
}

const getDefaultDescription = (name) => {
  const descriptions = {
    '生活技能': '掌握实用的生活技能，让日常生活更加便捷高效。从烹饪美食到家居清洁，从理财规划到人际交往，这里有你需要的一切生活智慧。无论你想学习如何做一道美味的菜肴，还是想了解如何更好地管理时间，生活技能分类都能帮助你成为更好的自己！',
    '技术教程': '探索技术的无限可能，从编程开发到设计创作，从数据分析到人工智能。无论你是初学者还是资深开发者，这里都有适合你的教程。学习最新的技术趋势，掌握实用的开发技巧，让技术成为你实现梦想的工具！',
    '兴趣爱好': '发现生活中的乐趣，培养属于自己的兴趣爱好。从手工制作到运动健身，从摄影绘画到音乐舞蹈，找到让你快乐的事情。兴趣爱好不仅能丰富你的生活，还能帮助你结交志同道合的朋友，让生活更加精彩！',
  }
  return descriptions[name] || `探索${name}的精彩内容，发现更多有趣的知识和技能。`
}

onMounted(() => {
  loadCategories()
  loadArticles()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 分类横幅 */
.category-banner {
  position: relative;
  height: 280px;
  margin-top: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.banner-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.banner-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 24px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.banner-search {
  display: flex;
  max-width: 500px;
  margin: 0 auto;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}

.banner-search-input {
  flex: 1;
  border: none;
  padding: 14px 20px;
  font-size: 14px;
  outline: none;
  color: #333;
}

.banner-search-btn {
  background: #fff;
  border: none;
  border-left: 1px solid #e0e0e0;
  padding: 14px 20px;
  cursor: pointer;
  color: #666;
}

.banner-search-btn:hover {
  background: #f5f5f5;
}

/* 分类介绍区域 */
.category-intro {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.intro-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.breadcrumb {
  color: #999;
  font-size: 13px;
}

.back-home {
  color: #93b874;
  font-size: 13px;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.back-home:hover {
  color: #6a9a4a;
}

.intro-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 16px;
}

.intro-description {
  font-size: 15px;
  line-height: 1.8;
  color: #555;
  margin: 0 0 30px;
}

/* 特色主题 */
.featured-topics {
  margin-top: 30px;
}

.topics-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px;
}

.topics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.topic-card {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.topic-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.topic-image {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: cover;
  background-position: center;
}

.topic-name {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.page-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 30px 20px 40px;
}

/* 没有选中分类时的顶部间距 */
.home-page:not(:has(.category-banner)) .page-content {
  padding-top: 80px;
}

.main-layout {
  display: flex;
  gap: 24px;
}

/* 左侧文章区域 */
.article-section {
  flex: 1;
  min-width: 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #93D5A2;
}

.current-category-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.current-category-title .category-icon {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.clear-btn {
  color: #999;
  font-size: 13px;
}

.clear-btn:hover {
  color: #93D5A2;
}

/* 文章网格 - 每行4个 */
.article-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  min-height: 300px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

/* 右侧分类侧边栏 */
.category-sidebar {
  width: 280px;
  flex-shrink: 0;
}

.category-browse-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: sticky;
  top: 80px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  padding: 16px 20px;
  background: linear-gradient(135deg, #93D5A2 0%, #7BC88C 100%);
  color: #fff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 18px;
}

.category-list {
  list-style: none;
  margin: 0;
  padding: 8px 0;
}

.category-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 3px solid transparent;
}

.category-item:hover {
  background: #f8faf8;
  border-left-color: #93D5A2;
}

.category-item.active {
  background: #e8f5ea;
  border-left-color: #93D5A2;
}

.category-item .category-icon {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-right: 12px;
  flex-shrink: 0;
}

.category-item .category-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.category-item.active .category-name {
  font-weight: 500;
  color: #2e7d32;
}

.category-item .category-arrow {
  color: #ccc;
  font-size: 12px;
  transition: all 0.2s ease;
}

.category-item:hover .category-arrow,
.category-item.active .category-arrow {
  color: #93D5A2;
  transform: translateX(3px);
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .article-list { grid-template-columns: repeat(3, 1fr); }
  .category-sidebar { width: 240px; }
  .topics-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 992px) {
  .main-layout {
    flex-direction: column-reverse;
  }
  
  .category-sidebar {
    width: 100%;
  }
  
  .category-browse-card {
    position: static;
  }
  
  .category-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 4px;
    padding: 12px;
  }
  
  .category-item {
    padding: 10px 12px;
    border-radius: 8px;
    border-left: none;
    background: #f8f8f8;
  }
  
  .category-item:hover,
  .category-item.active {
    border-left: none;
  }
  
  .category-item .category-arrow {
    display: none;
  }
  
  .article-list { grid-template-columns: repeat(3, 1fr); }
  .topics-grid { grid-template-columns: repeat(2, 1fr); }
  
  .banner-title { font-size: 36px; }
}

@media (max-width: 768px) {
  .article-list { grid-template-columns: repeat(2, 1fr); }
  
  .category-list {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .topics-grid { grid-template-columns: repeat(2, 1fr); }
  .category-banner { height: 220px; }
  .banner-title { font-size: 28px; }
}

@media (max-width: 480px) {
  .article-list { grid-template-columns: 1fr; }
  
  .category-list {
    grid-template-columns: 1fr;
  }
  
  .topics-grid { grid-template-columns: 1fr; }
}
</style>
