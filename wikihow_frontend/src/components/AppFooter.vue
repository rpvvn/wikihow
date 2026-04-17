<template>
  <footer class="app-footer">
    <div class="footer-content">
      <!-- 左侧区域 -->
      <div class="footer-left">
        <router-link to="/" class="footer-logo">
          <span class="logo-wiki">wiki</span><span class="logo-how">How</span>
        </router-link>
        
        <div class="footer-search">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="Search how to..."
            @keyup.enter="handleSearch"
          />
          <el-icon class="search-icon"><Search /></el-icon>
        </div>
        
        <div class="social-links">
          <a href="#" class="social-link" title="关注我们">
            <el-icon><Share /></el-icon>
          </a>
          <a href="#" class="social-link" title="微博">
            <el-icon><ChatDotRound /></el-icon>
          </a>
          <a href="#" class="social-link" title="微信">
            <el-icon><ChatLineSquare /></el-icon>
          </a>
          <a href="#" class="social-link" title="QQ">
            <el-icon><Service /></el-icon>
          </a>
          <a href="#" class="social-link" title="邮箱">
            <el-icon><Message /></el-icon>
          </a>
        </div>
      </div>
      
      <!-- 中间链接区域 -->
      <div class="footer-links">
        <div class="link-column">
          <router-link to="/">首页</router-link>
          <router-link to="/about">关于我们</router-link>
          <router-link to="/experts">专家团队</router-link>
          <router-link to="/jobs">加入我们</router-link>
          <router-link to="/contact">联系我们</router-link>
        </div>
        <div class="link-column">
          <router-link to="/sitemap">网站地图</router-link>
          <router-link to="/terms">使用条款</router-link>
          <router-link to="/privacy">隐私政策</router-link>
          <router-link to="/contribute">投稿指南</router-link>
        </div>
      </div>
    </div>
    
    <!-- 底部版权 -->
    <div class="footer-bottom">
      <p>© {{ currentYear }} WikiHow 知识库. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Share, ChatDotRound, ChatLineSquare, Service, Message } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')

const currentYear = computed(() => new Date().getFullYear())

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { keyword: searchKeyword.value } })
  }
}
</script>

<style scoped>
.app-footer {
  background: #93b874;
  padding: 40px 0 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  gap: 60px;
}

/* 左侧区域 */
.footer-left {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.footer-logo {
  text-decoration: none;
  display: inline-block;
}

.logo-wiki {
  font-size: 32px;
  font-weight: bold;
  color: #fff;
}

.logo-how {
  font-size: 32px;
  font-weight: 300;
  color: #fff;
}

/* 搜索框 */
.footer-search {
  position: relative;
  width: 280px;
}

.footer-search input {
  width: 100%;
  padding: 10px 40px 10px 16px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 4px;
  background: transparent;
  color: #fff;
  font-size: 14px;
  outline: none;
}

.footer-search input::placeholder {
  color: rgba(255, 255, 255, 0.7);
}

.footer-search input:focus {
  border-color: #fff;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.7);
  font-size: 18px;
}

/* 社交链接 */
.social-links {
  display: flex;
  gap: 8px;
}

.social-link {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  text-decoration: none;
  transition: background 0.2s;
}

.social-link:hover {
  background: rgba(0, 0, 0, 0.4);
}

/* 链接区域 */
.footer-links {
  display: flex;
  gap: 80px;
}

.link-column {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.link-column a {
  color: #fff;
  text-decoration: none;
  font-size: 14px;
  transition: opacity 0.2s;
}

.link-column a:hover {
  opacity: 0.8;
  text-decoration: underline;
}

/* 底部版权 */
.footer-bottom {
  margin-top: 40px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.1);
  text-align: center;
}

.footer-bottom p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  margin: 0;
}

/* 响应式 */
@media (max-width: 768px) {
  .footer-content {
    flex-direction: column;
    gap: 30px;
  }
  
  .footer-links {
    gap: 40px;
  }
  
  .footer-search {
    width: 100%;
  }
}
</style>
