<template>
  <div class="home-page">
    <AppHeader />

    <section class="discover-hero" v-if="!selectedCategory">
      <div class="hero-content main-shell">
        <p class="hero-kicker">WikiHow Knowledge Base</p>
        <h1>把复杂问题拆成人人都能执行的步骤</h1>
        <p class="hero-subtitle">从生活技能到技术教程，查找你需要的可操作答案。</p>

        <div class="hero-categories" v-if="parentCategories.length">
          <button
            v-for="(cat, index) in parentCategories.slice(0, 8)"
            :key="cat.id"
            class="hero-chip"
            :style="{ '--chip-color': categoryColors[index % categoryColors.length] }"
            @click="selectCategory(cat.id)"
          >
            {{ cat.name }}
          </button>
        </div>
      </div>
    </section>

    <div class="category-banner" v-if="selectedCategoryInfo" :style="bannerStyle">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h2 class="banner-title">{{ selectedCategoryInfo.name }}</h2>
        <p class="banner-text">{{ selectedCategoryInfo.description || getDefaultDescription(selectedCategoryInfo.name) }}</p>
      </div>
    </div>

    <div class="page-content main-shell">
      <div class="category-intro" v-if="selectedCategoryInfo">
        <div class="intro-header">
          <span class="breadcrumb">首页 / 分类浏览</span>
          <router-link to="/" class="back-home" @click="clearCategory">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </router-link>
        </div>

        <h3 class="intro-title">{{ selectedCategoryInfo.name }}</h3>

        <div class="featured-topics" v-if="childCategories.length > 0">
          <h4 class="topics-title">相关子分类</h4>
          <div class="topics-grid">
            <button
              v-for="(child, index) in childCategories"
              :key="child.id"
              class="topic-card"
              :style="{ '--topic-color': categoryColors[index % categoryColors.length] }"
              @click="selectCategory(child.id)"
            >
              <span class="topic-name">{{ child.name }}</span>
            </button>
          </div>
        </div>
      </div>

      <div class="main-layout">
        <div class="article-section glass-panel">
          <div class="section-header" v-if="selectedCategory">
            <h2 class="current-category-title">
              <span class="category-dot" :style="{ background: getCategoryColor(selectedCategory) }"></span>
              {{ getCurrentCategoryName() }} 相关文章
            </h2>
            <el-button type="text" @click="clearCategory" class="clear-btn">
              <el-icon><Close /></el-icon>
              清除筛选
            </el-button>
          </div>

          <div class="section-header" v-else>
            <h2 class="current-category-title">全部文章</h2>
          </div>

          <div class="article-list" v-loading="loading">
            <ArticleCard
              v-for="article in articles"
              :key="article.id"
              :article="article"
            />
            <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
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

        <aside class="category-sidebar glass-panel">
          <h3 class="sidebar-title">
            <el-icon class="title-icon"><Menu /></el-icon>
            分类导航
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
              <span class="category-arrow"><el-icon><ArrowRight /></el-icon></span>
            </li>
          </ul>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Menu, ArrowRight, Close, HomeFilled } from '@element-plus/icons-vue'
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

const categoryColors = [
  '#6AA784',
  '#4D9CB7',
  '#D48C5A',
  '#8F6AB0',
  '#D25D79',
  '#628DB8',
  '#58A18D',
  '#B78A57',
]

const parentCategories = computed(() => allCategories.value.filter((item) => !item.parentId))

const selectedCategoryInfo = computed(() => {
  if (!selectedCategory.value) return null
  return allCategories.value.find((item) => item.id === selectedCategory.value) || null
})

const childCategories = computed(() => {
  if (!selectedCategory.value) return []

  const current = allCategories.value.find((item) => item.id === selectedCategory.value)
  if (!current) return []

  if (!current.parentId) {
    return allCategories.value.filter((item) => item.parentId === selectedCategory.value)
  }

  return allCategories.value.filter(
    (item) => item.parentId === current.parentId && item.id !== selectedCategory.value,
  )
})

const bannerStyle = computed(() => {
  const current = selectedCategoryInfo.value
  if (current?.coverImage) {
    return {
      backgroundImage: `url(${current.coverImage})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center',
    }
  }

  const palette = [
    ['#355C4A', '#5FA173'],
    ['#21485A', '#3D87AA'],
    ['#734F38', '#BF865C'],
    ['#413A6A', '#7B61A9'],
  ]

  const parentId = current?.parentId || current?.id
  const index = parentCategories.value.findIndex((item) => item.id === parentId)
  const [from, to] = palette[(index < 0 ? 0 : index) % palette.length]
  return { background: `linear-gradient(120deg, ${from} 0%, ${to} 100%)` }
})

const loadCategories = async () => {
  try {
    const res = await getCategories()
    allCategories.value = res.data || []
    appStore.setCategories(allCategories.value)
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
      categoryId: selectedCategory.value,
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
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const clearCategory = () => {
  selectedCategory.value = null
  page.value = 1
  loadArticles()
}

const getCurrentCategoryName = () => {
  const category = allCategories.value.find((item) => item.id === selectedCategory.value)
  return category ? category.name : '全部文章'
}

const getCategoryColor = (categoryId) => {
  const category = allCategories.value.find((item) => item.id === categoryId)
  if (!category) return categoryColors[0]

  const parentId = category.parentId || category.id
  const index = parentCategories.value.findIndex((item) => item.id === parentId)
  return categoryColors[(index < 0 ? 0 : index) % categoryColors.length]
}

const isParentCategoryActive = (parentId) => {
  if (selectedCategory.value === parentId) return true

  const category = allCategories.value.find((item) => item.id === selectedCategory.value)
  return category?.parentId === parentId
}

const getDefaultDescription = (name) => {
  return `探索「${name}」中的精选指南，快速找到清晰、可执行的解决方案。`
}

onMounted(() => {
  loadCategories()
  loadArticles()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.discover-hero {
  margin-top: 56px;
  padding: 52px 0 36px;
}

.hero-content {
  border: 1px solid rgba(190, 213, 197, 0.9);
  border-radius: var(--radius-lg);
  background:
    radial-gradient(circle at 88% 4%, rgba(187, 228, 199, 0.6), transparent 28%),
    linear-gradient(145deg, rgba(255, 255, 255, 0.88), rgba(242, 249, 244, 0.8));
  box-shadow: var(--shadow-soft);
  padding: clamp(26px, 5vw, 44px);
}

.hero-kicker {
  color: var(--brand-500);
  letter-spacing: 0.08em;
  font-weight: 700;
  text-transform: uppercase;
  font-size: 12px;
}

.hero-content h1 {
  margin-top: 10px;
  font-size: clamp(28px, 4vw, 42px);
  line-height: 1.2;
  max-width: 760px;
}

.hero-subtitle {
  margin-top: 14px;
  color: var(--ink-500);
  max-width: 620px;
}

.hero-categories {
  margin-top: 24px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.hero-chip {
  border: 1px solid color-mix(in srgb, var(--chip-color), #ffffff 58%);
  color: var(--ink-700);
  background: color-mix(in srgb, var(--chip-color), #ffffff 86%);
  padding: 8px 14px;
  border-radius: 999px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.hero-chip:hover {
  transform: translateY(-2px);
}

.category-banner {
  position: relative;
  min-height: 200px;
  margin-top: 56px;
  display: grid;
  place-items: center;
  overflow: hidden;
}

.banner-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.32);
}

.banner-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #ffffff;
  width: min(860px, calc(100% - 40px));
}

.banner-title {
  font-size: clamp(28px, 5vw, 42px);
}

.banner-text {
  margin-top: 14px;
  opacity: 0.92;
}

.page-content {
  padding: 22px 0 44px;
}

.category-intro {
  margin-bottom: 18px;
  border: 1px solid rgba(203, 223, 210, 0.92);
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.82);
  backdrop-filter: blur(8px);
  padding: 20px;
}

.intro-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.breadcrumb {
  color: var(--ink-500);
  font-size: 13px;
}

.back-home {
  color: var(--brand-500);
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.intro-title {
  margin-top: 8px;
  font-size: 30px;
}

.featured-topics {
  margin-top: 16px;
}

.topics-title {
  font-size: 16px;
  color: var(--ink-700);
}

.topics-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.topic-card {
  border: 1px solid color-mix(in srgb, var(--topic-color), #ffffff 65%);
  background: color-mix(in srgb, var(--topic-color), #ffffff 85%);
  border-radius: 12px;
  padding: 14px;
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.topic-card:hover {
  transform: translateY(-2px);
}

.topic-name {
  color: var(--ink-700);
  font-weight: 600;
}

.main-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 290px;
  gap: 20px;
}

.article-section {
  border-radius: var(--radius-md);
  padding: 18px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--line-200);
}

.current-category-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 22px;
}

.category-dot {
  width: 11px;
  height: 11px;
  border-radius: 999px;
}

.clear-btn {
  color: var(--ink-500);
}

.article-list {
  margin-top: 18px;
  min-height: 300px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.category-sidebar {
  border-radius: var(--radius-md);
  padding: 14px;
  height: max-content;
  position: sticky;
  top: 76px;
}

.sidebar-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--ink-700);
  font-size: 16px;
}

.category-list {
  margin-top: 10px;
  list-style: none;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
}

.category-item:hover {
  background: rgba(83, 160, 111, 0.08);
}

.category-item.active {
  background: rgba(83, 160, 111, 0.14);
}

.category-icon {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  flex-shrink: 0;
}

.category-name {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.category-arrow {
  color: var(--ink-500);
}

@media (max-width: 1200px) {
  .article-list {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .topics-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 980px) {
  .main-layout {
    grid-template-columns: 1fr;
  }

  .category-sidebar {
    position: static;
  }

  .category-list {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 8px;
  }

  .article-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .topics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 720px) {
  .hero-content,
  .category-intro,
  .article-section,
  .category-sidebar {
    border-radius: 14px;
  }

  .page-content {
    padding-bottom: 30px;
  }

  .main-shell {
    width: calc(100% - 24px);
  }

  .article-list,
  .topics-grid,
  .category-list {
    grid-template-columns: 1fr;
  }
}
</style>
