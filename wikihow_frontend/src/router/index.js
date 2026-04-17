import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 前台页面
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue')
    },
    {
      path: '/article/:id',
      name: 'article-detail',
      component: () => import('@/views/ArticleDetail.vue')
    },
    {
      path: '/article/:id/versions',
      name: 'article-versions',
      component: () => import('@/views/ArticleVersions.vue')
    },
    {
      path: '/category/:id',
      name: 'category',
      component: () => import('@/views/CategoryView.vue')
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('@/views/SearchView.vue')
    },
    {
      path: '/editor',
      name: 'editor',
      component: () => import('@/views/ArticleEditor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/editor/:id',
      name: 'editor-edit',
      component: () => import('@/views/ArticleEditor.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/ProfileView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/user/:id',
      name: 'user',
      component: () => import('@/views/UserView.vue')
    },
    // 静态页面
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue')
    },
    {
      path: '/experts',
      name: 'experts',
      component: () => import('@/views/ExpertsView.vue')
    },
    {
      path: '/jobs',
      name: 'jobs',
      component: () => import('@/views/JobsView.vue')
    },
    {
      path: '/contact',
      name: 'contact',
      component: () => import('@/views/ContactView.vue')
    },
    {
      path: '/sitemap',
      name: 'sitemap',
      component: () => import('@/views/SitemapView.vue')
    },
    {
      path: '/terms',
      name: 'terms',
      component: () => import('@/views/TermsView.vue')
    },
    {
      path: '/privacy',
      name: 'privacy',
      component: () => import('@/views/PrivacyView.vue')
    },
    {
      path: '/contribute',
      name: 'contribute',
      component: () => import('@/views/ContributeView.vue')
    },
    // 后台管理页面
    {
      path: '/admin',
      redirect: to => {
        // 根据用户角色重定向到不同页面
        const userStore = useUserStore()
        if (userStore.isAdmin) {
          return '/admin/dashboard'
        } else if (userStore.canReview) {
          return '/admin/reviews'
        }
        return '/'
      },
      meta: { requiresAuth: true, requiresReviewer: true }
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('@/views/admin/DashboardView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: () => import('@/views/admin/UserManage.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/articles',
      name: 'admin-articles',
      component: () => import('@/views/admin/ArticleManage.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/categories',
      name: 'admin-categories',
      component: () => import('@/views/admin/CategoryManage.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/comments',
      name: 'admin-comments',
      component: () => import('@/views/admin/CommentManage.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/notifications',
      name: 'admin-notifications',
      component: () => import('@/views/admin/NotificationManage.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/reviews',
      name: 'admin-reviews',
      component: () => import('@/views/admin/ReviewManage.vue'),
      meta: { requiresAuth: true, requiresReviewer: true }
    },
    {
      path: '/admin/outdated-reports',
      name: 'admin-outdated-reports',
      component: () => import('@/views/admin/OutdatedManage.vue'),
      meta: { requiresAuth: true, requiresReviewer: true }
    },
    // 404 页面（必须放在最后）
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFoundView.vue')
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  
  // 需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    next({ name: 'home' })
    return
  }
  
  // 需要审核员权限（审核员或管理员）
  if (to.meta.requiresReviewer && !userStore.canReview) {
    next({ name: 'home' })
    return
  }
  
  next()
})

export default router
