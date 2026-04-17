<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import AppFooter from '@/components/AppFooter.vue'

const route = useRoute()

// 管理后台页面不显示 Footer
const showFooter = computed(() => {
  return !route.path.startsWith('/admin')
})
</script>

<template>
  <div class="app-container">
    <RouterView />
    <AppFooter v-if="showFooter" />
  </div>
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

a {
  text-decoration: none;
  color: inherit;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
