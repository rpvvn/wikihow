<template>
  <footer class="app-footer">
    <div class="footer-content">
      <section class="brand-zone">
        <router-link to="/" class="footer-logo">
          <span class="logo-wiki">wiki</span><span class="logo-how">How</span>
        </router-link>
        <p class="footer-note">让每个人都能把复杂问题拆解成可执行步骤。</p>

        <div class="footer-search glass-panel">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="继续搜索想学的内容..."
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch" aria-label="footer-search">
            <el-icon><Search /></el-icon>
          </button>
        </div>
      </section>

      <section class="link-zone">
        <div class="link-column">
          <h4>探索</h4>
          <router-link to="/">首页</router-link>
          <router-link to="/search">文章搜索</router-link>
          <router-link to="/experts">专家团队</router-link>
          <router-link to="/contribute">投稿指南</router-link>
        </div>
        <div class="link-column">
          <h4>支持</h4>
          <router-link to="/about">关于我们</router-link>
          <router-link to="/contact">联系我们</router-link>
          <router-link to="/jobs">加入我们</router-link>
          <router-link to="/sitemap">网站地图</router-link>
        </div>
        <div class="link-column">
          <h4>条款</h4>
          <router-link to="/terms">使用条款</router-link>
          <router-link to="/privacy">隐私政策</router-link>
        </div>
      </section>

      <section class="social-zone">
        <h4>关注我们</h4>
        <div class="social-links">
          <a href="#" class="social-link" title="分享"><el-icon><Share /></el-icon></a>
          <a href="#" class="social-link" title="社区"><el-icon><ChatDotRound /></el-icon></a>
          <a href="#" class="social-link" title="微信"><el-icon><ChatLineSquare /></el-icon></a>
          <a href="#" class="social-link" title="客服"><el-icon><Service /></el-icon></a>
          <a href="#" class="social-link" title="邮箱"><el-icon><Message /></el-icon></a>
        </div>
      </section>
    </div>

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
  if (!searchKeyword.value.trim()) return
  router.push({ path: '/search', query: { keyword: searchKeyword.value } })
}
</script>

<style scoped>
.app-footer {
  margin-top: auto;
  border-top: 1px solid rgba(194, 217, 201, 0.8);
  background: linear-gradient(180deg, rgba(244, 250, 246, 0.9) 0%, #eef6f1 100%);
}

.footer-content {
  width: min(1320px, calc(100% - 40px));
  margin: 0 auto;
  padding: 42px 0 28px;
  display: grid;
  grid-template-columns: 1.2fr 1fr 0.8fr;
  gap: 32px;
}

.footer-logo {
  display: inline-flex;
  align-items: baseline;
}

.logo-wiki,
.logo-how {
  font-size: 34px;
  line-height: 1;
}

.logo-wiki {
  color: var(--brand-700);
  font-weight: 800;
}

.logo-how {
  color: var(--brand-500);
  font-weight: 500;
}

.footer-note {
  margin-top: 12px;
  max-width: 380px;
  color: var(--ink-500);
}

.footer-search {
  margin-top: 18px;
  display: flex;
  align-items: center;
  border-radius: 999px;
  overflow: hidden;
}

.footer-search input {
  border: none;
  outline: none;
  background: transparent;
  padding: 11px 14px;
  flex: 1;
  color: var(--ink-700);
}

.search-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  color: var(--brand-500);
  margin-right: 3px;
  border-radius: 999px;
  cursor: pointer;
}

.search-btn:hover {
  background: rgba(89, 168, 116, 0.14);
}

.link-zone {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.link-column {
  display: flex;
  flex-direction: column;
  gap: 9px;
}

.link-column h4,
.social-zone h4 {
  font-size: 15px;
  color: var(--ink-700);
  margin-bottom: 6px;
}

.link-column a {
  color: var(--ink-500);
  transition: color 0.2s ease;
}

.link-column a:hover {
  color: var(--brand-500);
}

.social-links {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.social-link {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  border: 1px solid rgba(158, 191, 169, 0.78);
  background: rgba(255, 255, 255, 0.72);
  color: var(--ink-700);
  display: grid;
  place-items: center;
  transition: all 0.2s ease;
}

.social-link:hover {
  transform: translateY(-2px);
  border-color: rgba(95, 155, 117, 0.7);
  color: var(--brand-500);
}

.footer-bottom {
  border-top: 1px solid rgba(197, 217, 203, 0.8);
  text-align: center;
  padding: 14px 20px 18px;
}

.footer-bottom p {
  color: var(--ink-500);
  font-size: 13px;
}

@media (max-width: 980px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 26px;
  }

  .footer-note {
    max-width: none;
  }

  .link-zone {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .footer-content {
    width: calc(100% - 24px);
    padding-top: 34px;
  }

  .footer-search {
    max-width: 100%;
  }

  .footer-search input {
    font-size: 14px;
    padding: 10px 12px;
  }

  .link-zone {
    grid-template-columns: 1fr;
  }

  .logo-wiki,
  .logo-how {
    font-size: 30px;
  }

  .social-zone h4 {
    margin-top: 4px;
  }
}

@media (max-width: 480px) {
  .footer-content {
    padding: 24px 0 18px;
    gap: 20px;
  }

  .brand-zone {
    text-align: center;
  }

  .footer-logo {
    justify-content: center;
  }

  .footer-note {
    font-size: 14px;
    line-height: 1.6;
    text-align: center;
  }

  .footer-search {
    margin-top: 14px;
  }

  .footer-search input {
    font-size: 13px;
    padding: 9px 12px;
  }

  .search-btn {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }

  .link-zone {
    display: none;
  }

  .social-zone {
    margin-top: -4px;
    text-align: center;
  }

  .social-zone h4 {
    font-size: 14px;
    text-align: center;
  }

  .social-links {
    gap: 6px;
    justify-content: center;
  }

  .social-link {
    width: 34px;
    height: 34px;
    font-size: 15px;
  }

  .footer-bottom {
    padding: 12px 16px 16px;
  }

  .footer-bottom p {
    font-size: 11px;
    line-height: 1.6;
    word-break: break-word;
  }
}
</style>
