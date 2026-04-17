import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref('')
  const userInfo = ref(null)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')
  const isReviewer = computed(() => userInfo.value?.role === 'REVIEWER' || userInfo.value?.role === 'ADMIN')
  const canReview = computed(() => userInfo.value?.role === 'REVIEWER' || userInfo.value?.role === 'ADMIN')

  // 方法
  function setToken(newToken) {
    token.value = newToken
  }

  function setUserInfo(info) {
    userInfo.value = info
  }

  function login(data) {
    token.value = data.token
    userInfo.value = data.user
  }

  function logout() {
    token.value = ''
    userInfo.value = null
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isReviewer,
    canReview,
    setToken,
    setUserInfo,
    login,
    logout
  }
}, {
  persist: true
})
