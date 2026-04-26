<script setup>
import { RouterView, useRoute } from 'vue-router'
import { computed } from 'vue'
import AppFooter from '@/components/AppFooter.vue'

const route = useRoute()

const showFooter = computed(() => {
  // 隐藏底部栏的页面：管理后台、登录、注册
  const hideFooterPaths = ['/admin', '/login', '/register']
  return !hideFooterPaths.some(path => route.path.startsWith(path))
})
</script>

<template>
  <div class="app-container">
    <main class="app-main">
      <RouterView />
    </main>
    <AppFooter v-if="showFooter" />
  </div>
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-main {
  flex: 1;
}
</style>
