<template>
  <div class="not-found-page">
    <AppHeader />
    <div class="content">
      <div class="error-container">
        <h1 class="error-code">404</h1>
        <div class="error-icon">🔍</div>
        <h2 class="error-title">页面未找到</h2>
        <p class="error-desc">抱歉，您访问的页面不存在或已被移除。</p>
        <div class="error-actions">
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
          <el-button @click="$router.back()">返回上页</el-button>
        </div>
        <div class="suggestions">
          <p>您可以尝试：</p>
          <ul>
            <li>检查网址是否正确</li>
            <li>使用搜索功能查找内容</li>
            <li>浏览我们的<router-link to="/sitemap">网站地图</router-link></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 200px);
  padding: 80px 20px 40px;
}

.error-container {
  text-align: center;
  background: #fff;
  padding: 60px 80px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #93b874;
  margin: 0;
  line-height: 1;
}

.error-icon {
  font-size: 60px;
  margin: 20px 0;
}

.error-title {
  font-size: 24px;
  color: #333;
  margin: 0 0 10px;
}

.error-desc {
  color: #666;
  margin-bottom: 30px;
}

.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 30px;
}

.suggestions {
  text-align: left;
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.suggestions p {
  color: #333;
  margin: 0 0 10px;
  font-weight: 500;
}

.suggestions ul {
  color: #666;
  margin: 0;
  padding-left: 20px;
}

.suggestions li {
  margin-bottom: 6px;
}

.suggestions a {
  color: #93b874;
}
</style>
