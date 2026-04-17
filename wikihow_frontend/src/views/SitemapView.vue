<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">网站地图</h1>
        
        <div class="sitemap-grid">
          <div class="sitemap-section">
            <h2>主要页面</h2>
            <ul>
              <li><router-link to="/">首页</router-link></li>
              <li><router-link to="/search">搜索文章</router-link></li>
              <li><router-link to="/login">登录</router-link></li>
              <li><router-link to="/register">注册</router-link></li>
            </ul>
          </div>
          
          <div class="sitemap-section">
            <h2>文章分类</h2>
            <ul>
              <li v-for="category in categories" :key="category.id">
                <router-link :to="'/category/' + category.id">{{ category.name }}</router-link>
              </li>
            </ul>
          </div>
          
          <div class="sitemap-section">
            <h2>用户中心</h2>
            <ul>
              <li><router-link to="/profile">个人中心</router-link></li>
              <li><router-link to="/editor">写文章</router-link></li>
              <li><router-link to="/profile?tab=articles">我的文章</router-link></li>
              <li><router-link to="/profile?tab=favorites">我的收藏</router-link></li>
            </ul>
          </div>
          
          <div class="sitemap-section">
            <h2>关于我们</h2>
            <ul>
              <li><router-link to="/about">关于我们</router-link></li>
              <li><router-link to="/experts">专家团队</router-link></li>
              <li><router-link to="/jobs">加入我们</router-link></li>
              <li><router-link to="/contact">联系我们</router-link></li>
            </ul>
          </div>
          
          <div class="sitemap-section">
            <h2>帮助与政策</h2>
            <ul>
              <li><router-link to="/terms">使用条款</router-link></li>
              <li><router-link to="/privacy">隐私政策</router-link></li>
              <li><router-link to="/contribute">投稿指南</router-link></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppHeader from '@/components/AppHeader.vue'
import { getCategories } from '@/api/category'

const categories = ref([])

onMounted(async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) { console.error(e) }
})
</script>

<style scoped>
.page-wrapper { min-height: 100vh; background: #f5f5f5; }
.page-content { max-width: 1000px; margin: 0 auto; padding: 80px 20px 40px; }
.page-container { background: #fff; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.page-title { color: #93b874; margin-bottom: 30px; font-size: 28px; }
.sitemap-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 30px; }
.sitemap-section h2 { color: #333; font-size: 18px; margin-bottom: 15px; padding-bottom: 10px; border-bottom: 2px solid #93b874; }
.sitemap-section ul { list-style: none; padding: 0; }
.sitemap-section li { margin-bottom: 10px; }
.sitemap-section a { color: #666; text-decoration: none; transition: color 0.2s; }
.sitemap-section a:hover { color: #93b874; }
@media (max-width: 768px) { .sitemap-grid { grid-template-columns: 1fr; } }
</style>
