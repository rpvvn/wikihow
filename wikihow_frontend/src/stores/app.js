import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 分类列表缓存
  const categories = ref([])
  
  // 全局加载状态
  const loading = ref(false)

  function setCategories(list) {
    categories.value = list
  }

  function setLoading(status) {
    loading.value = status
  }

  return {
    categories,
    loading,
    setCategories,
    setLoading
  }
})
