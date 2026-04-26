<template>
  <div class="notification-panel">
    <!-- 头部 -->
    <div class="panel-header">
      <h3>消息通知</h3>
      <div class="header-actions">
        <el-button 
          v-if="hasUnread" 
          type="primary" 
          link 
          size="small"
          @click="handleMarkAllRead"
        >
          全部已读
        </el-button>
      </div>
    </div>

    <!-- 通知分类标签 -->
    <div class="category-tabs">
      <span 
        :class="['category-tab', { active: notificationCategory === 'all' }]"
        @click="notificationCategory = 'all'; loadNotifications()"
      >
        <el-icon><Bell /></el-icon>
        全部通知
      </span>
      <span 
        :class="['category-tab', { active: notificationCategory === 'system' }]"
        @click="notificationCategory = 'system'; loadNotifications()"
      >
        <el-icon><Monitor /></el-icon>
        系统通知
      </span>
      <span 
        :class="['category-tab', { active: notificationCategory === 'user' }]"
        @click="notificationCategory = 'user'; loadNotifications()"
      >
        <el-icon><ChatDotRound /></el-icon>
        用户通知
      </span>
    </div>

    <!-- 筛选标签 -->
    <div class="filter-tabs">
      <span 
        :class="['tab', { active: !unreadOnly }]"
        @click="unreadOnly = false; loadNotifications()"
      >
        全部
      </span>
      <span 
        :class="['tab', { active: unreadOnly }]"
        @click="unreadOnly = true; loadNotifications()"
      >
        未读 <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>
      </span>
    </div>

    <!-- 通知列表 -->
    <div class="notification-list" v-loading="loading">
      <template v-if="groupedNotifications.length > 0">
        <div 
          v-for="group in groupedNotifications" 
          :key="group.date"
          class="notification-group"
        >
          <div class="group-date" @click="toggleGroup(group.date)">
            <el-icon class="collapse-icon" :class="{ collapsed: collapsedGroups[group.date] }">
              <ArrowDown />
            </el-icon>
            <span>{{ group.date }}</span>
            <span class="group-count">{{ group.items.length }} 条</span>
          </div>
          
          <transition name="collapse">
            <div v-show="!collapsedGroups[group.date]" class="group-items">
              <div 
                v-for="notification in group.items" 
                :key="notification.id"
                :class="['notification-item', { unread: notification.isRead === 0 }]"
                @click="handleItemClick(notification)"
              >
                <div class="item-avatar">
                  <el-avatar :size="40" :src="notification.senderAvatar">
                    {{ notification.senderNickname?.charAt(0) || '系' }}
                  </el-avatar>
                  <span v-if="notification.isRead === 0" class="unread-dot"></span>
                  <span :class="['type-badge', getTypeBadgeClass(notification.type)]">
                    <el-icon v-if="notification.type === 'COMMENT'"><ChatDotRound /></el-icon>
                    <el-icon v-else-if="notification.type === 'REPLY'"><ChatLineSquare /></el-icon>
                    <el-icon v-else><Bell /></el-icon>
                  </span>
                </div>
                <div class="item-content">
                  <div class="item-header">
                    <span class="sender-name">{{ notification.senderNickname || '系统' }}</span>
                    <span class="item-time">{{ formatTime(notification.createdAt) }}</span>
                  </div>
                  <div class="item-title">{{ notification.title }}</div>
                  <div class="item-text">{{ notification.content }}</div>
                </div>
                <div class="item-actions">
                  <el-button 
                    type="danger" 
                    link 
                    size="small"
                    @click.stop="handleDelete(notification.id)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </template>
      
      <el-empty v-else description="暂无通知" :image-size="80" />
    </div>

    <!-- 加载更多 -->
    <div v-if="hasMore" class="load-more">
      <el-button type="primary" link @click="loadMore">加载更多</el-button>
    </div>

    <!-- 通知详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentNotification?.title"
      width="480px"
      class="notification-detail-dialog"
      :modal="true"
      :append-to-body="true"
      :close-on-click-modal="true"
      @close="currentNotification = null"
    >
      <div class="detail-content" v-if="currentNotification">
        <div class="detail-meta">
          <div class="detail-sender">
            <el-avatar :size="32" :src="currentNotification.senderAvatar">
              {{ currentNotification.senderNickname?.charAt(0) || '系' }}
            </el-avatar>
            <span class="sender-name">{{ currentNotification.senderNickname || '系统' }}</span>
          </div>
          <span class="detail-time">{{ formatFullTime(currentNotification.createdAt) }}</span>
        </div>
        <div class="detail-body">
          {{ currentNotification.content }}
        </div>
      </div>
      <template #footer>
        <el-button 
          v-if="isCommentNotification(currentNotification)" 
          type="primary" 
          @click="handleGoToComment"
        >
          <el-icon><Position /></el-icon>
          立即前往
        </el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="danger" @click="handleDeleteFromDetail">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, Delete, Bell, Monitor, ChatDotRound, ChatLineSquare, Position } from '@element-plus/icons-vue'
import { 
  getNotifications, 
  getUnreadCount, 
  markAsRead, 
  markAllAsRead, 
  deleteNotification 
} from '@/api/notification'

const emit = defineEmits(['update:unreadCount', 'close'])
const router = useRouter()

const loading = ref(false)
const notifications = ref([])
const unreadCount = ref(0)
const unreadOnly = ref(true) // 默认显示未读
const notificationCategory = ref('all') // 通知分类: all/system/user
const page = ref(1)
const total = ref(0)
const collapsedGroups = ref({})
const detailVisible = ref(false)
const currentNotification = ref(null)

// 系统通知类型
const systemTypes = ['SYSTEM', 'WELCOME', 'ADMIN', 'REVIEW', 'OUTDATED', 'OFFLINE']
// 用户通知类型
const userTypes = ['COMMENT', 'REPLY']

const hasUnread = computed(() => unreadCount.value > 0)
const hasMore = computed(() => notifications.value.length < total.value)

// 根据分类筛选通知
const filteredNotifications = computed(() => {
  if (notificationCategory.value === 'all') {
    return notifications.value
  } else if (notificationCategory.value === 'system') {
    return notifications.value.filter(n => systemTypes.includes(n.type))
  } else if (notificationCategory.value === 'user') {
    return notifications.value.filter(n => userTypes.includes(n.type))
  }
  return notifications.value
})

// 按日期分组通知
const groupedNotifications = computed(() => {
  const groups = {}
  
  filteredNotifications.value.forEach(n => {
    const date = formatDate(n.createdAt)
    if (!groups[date]) {
      groups[date] = []
    }
    groups[date].push(n)
  })
  
  return Object.keys(groups)
    .sort((a, b) => new Date(b) - new Date(a))
    .map(date => ({
      date,
      items: groups[date]
    }))
})

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (date.toDateString() === today.toDateString()) {
    return '今天'
  } else if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

const formatTime = (dateStr) => {
  const date = new Date(dateStr)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatFullTime = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const getTypeBadgeClass = (type) => {
  if (userTypes.includes(type)) {
    return 'user-type'
  }
  return 'system-type'
}

const toggleGroup = (date) => {
  collapsedGroups.value[date] = !collapsedGroups.value[date]
}

const loadNotifications = async () => {
  loading.value = true
  page.value = 1
  try {
    const res = await getNotifications({ 
      page: 1, 
      size: 20, 
      unreadOnly: unreadOnly.value || undefined 
    })
    notifications.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载通知失败', error)
  } finally {
    loading.value = false
  }
}

const loadMore = async () => {
  page.value++
  try {
    const res = await getNotifications({ 
      page: page.value, 
      size: 20, 
      unreadOnly: unreadOnly.value || undefined 
    })
    notifications.value.push(...(res.data?.list || []))
  } catch (error) {
    console.error('加载更多失败', error)
  }
}

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
    emit('update:unreadCount', unreadCount.value)
  } catch (error) {
    console.error('获取未读数量失败', error)
  }
}

const handleItemClick = async (notification) => {
  // 标记为已读
  if (notification.isRead === 0) {
    try {
      await markAsRead(notification.id)
      notification.isRead = 1
      unreadCount.value = Math.max(0, unreadCount.value - 1)
      emit('update:unreadCount', unreadCount.value)
    } catch (error) {
      console.error('标记已读失败', error)
    }
  }
  // 显示详情弹窗
  currentNotification.value = notification
  detailVisible.value = true
}

const handleDeleteFromDetail = async () => {
  if (!currentNotification.value) return
  const id = currentNotification.value.id
  try {
    await deleteNotification(id)
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      if (notifications.value[index].isRead === 0) {
        unreadCount.value = Math.max(0, unreadCount.value - 1)
        emit('update:unreadCount', unreadCount.value)
      }
      notifications.value.splice(index, 1)
      total.value--
    }
    detailVisible.value = false
    currentNotification.value = null
    ElMessage.success('删除成功')
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleMarkAllRead = async () => {
  try {
    await markAllAsRead()
    notifications.value.forEach(n => n.isRead = 1)
    unreadCount.value = 0
    emit('update:unreadCount', 0)
    ElMessage.success('已全部标记为已读')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteNotification(id)
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      if (notifications.value[index].isRead === 0) {
        unreadCount.value = Math.max(0, unreadCount.value - 1)
        emit('update:unreadCount', unreadCount.value)
      }
      notifications.value.splice(index, 1)
      total.value--
    }
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 判断是否为评论相关通知
const isCommentNotification = (notification) => {
  if (!notification) return false
  return (notification.type === 'COMMENT' || notification.type === 'REPLY') && notification.articleId
}

// 跳转到评论
const handleGoToComment = () => {
  if (!currentNotification.value) return
  
  const { articleId, commentId } = currentNotification.value
  if (!articleId) return
  
  // 关闭弹窗
  detailVisible.value = false
  currentNotification.value = null
  
  // 通知父组件关闭面板
  emit('close')
  
  // 跳转到文章详情页，带上评论ID参数
  router.push({
    path: `/article/${articleId}`,
    query: commentId ? { highlightComment: commentId } : {}
  })
}

// 暴露刷新方法
const refresh = () => {
  loadNotifications()
  loadUnreadCount()
}

defineExpose({ refresh, loadUnreadCount })

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
})
</script>

<style scoped>
.notification-panel {
  width: 380px;
  max-height: 500px;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.filter-tabs {
  display: flex;
  gap: 20px;
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.category-tabs {
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.category-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid #e0e0e0;
  transition: all 0.2s;
}

.category-tab:hover {
  color: #93b874;
  border-color: #93b874;
}

.category-tab.active {
  color: #fff;
  background: #93b874;
  border-color: #93b874;
}

.category-tab .el-icon {
  font-size: 14px;
}

.tab {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding-bottom: 4px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.tab:hover {
  color: #93b874;
}

.tab.active {
  color: #93b874;
  border-bottom-color: #93b874;
}

.badge {
  display: inline-block;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  padding: 0 6px;
  font-size: 12px;
  color: #fff;
  background: #f56c6c;
  border-radius: 9px;
  text-align: center;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
  min-height: 200px;
}

.notification-group {
  border-bottom: 1px solid #f5f5f5;
}

.group-date {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  font-size: 13px;
  color: #999;
  background: #fafafa;
  cursor: pointer;
  user-select: none;
}

.group-date:hover {
  background: #f5f5f5;
}

.collapse-icon {
  transition: transform 0.2s;
}

.collapse-icon.collapsed {
  transform: rotate(-90deg);
}

.group-count {
  margin-left: auto;
  font-size: 12px;
}

.group-items {
  overflow: hidden;
}

.collapse-enter-active,
.collapse-leave-active {
  transition: all 0.2s ease;
}

.collapse-enter-from,
.collapse-leave-to {
  opacity: 0;
  max-height: 0;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 14px 20px;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f9f9f9;
}

.notification-item.unread {
  background: #f0f9eb;
}

.notification-item.unread:hover {
  background: #e8f5e0;
}

.item-avatar {
  position: relative;
  flex-shrink: 0;
}

.unread-dot {
  position: absolute;
  top: 0;
  right: 0;
  width: 10px;
  height: 10px;
  background: #f56c6c;
  border-radius: 50%;
  border: 2px solid #fff;
}

.type-badge {
  position: absolute;
  bottom: -2px;
  right: -2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

.type-badge .el-icon {
  font-size: 10px;
  color: #fff;
}

.type-badge.system-type {
  background: #409eff;
}

.type-badge.user-type {
  background: #67c23a;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.sender-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.item-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-text {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-actions {
  flex-shrink: 0;
  opacity: 0;
  transition: opacity 0.2s;
}

.notification-item:hover .item-actions {
  opacity: 1;
}

.load-more {
  padding: 12px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}

/* 滚动条样式 */
.notification-list::-webkit-scrollbar {
  width: 6px;
}

.notification-list::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.notification-list::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* 通知详情弹窗样式 */
.notification-detail-dialog {
  border-radius: 8px;
}

:deep(.notification-detail-dialog .el-dialog__header) {
  padding: 20px 20px 16px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.notification-detail-dialog .el-dialog__body) {
  padding: 20px;
}

:deep(.notification-detail-dialog .el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

/* 调整遮罩层透明度 */
:deep(.el-overlay) {
  background-color: rgba(0, 0, 0, 0.5);
}

.detail-content {
  padding: 0;
}

.detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.detail-sender {
  display: flex;
  align-items: center;
  gap: 10px;
}

.detail-sender .sender-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.detail-time {
  font-size: 13px;
  color: #999;
}

.detail-body {
  font-size: 14px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
