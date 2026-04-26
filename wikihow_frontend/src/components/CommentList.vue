<template>
  <div class="comment-list">
    <!-- 发表评论 -->
    <div v-if="userStore.isLoggedIn" class="comment-form">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="写下你的评论..."
        maxlength="500"
        show-word-limit
      />
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        发表评论
      </el-button>
    </div>
    <div v-else class="login-tip">
      <router-link to="/login">登录</router-link> 后发表评论
    </div>

    <!-- 评论列表 -->
    <div class="comments" v-loading="loading">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <CommentItem 
          :comment="comment" 
          :article-id="articleId"
          :current-user-id="userStore.userInfo?.id"
          :is-logged-in="userStore.isLoggedIn"
          :highlight-id="highlightCommentId"
          @reply="handleReply"
          @delete="handleDelete"
        />
        
        <!-- 子评论（回复） -->
        <div v-if="comment.replies && comment.replies.length > 0" class="replies-container">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <CommentItem 
              :comment="reply" 
              :article-id="articleId"
              :current-user-id="userStore.userInfo?.id"
              :is-logged-in="userStore.isLoggedIn"
              :is-reply="true"
              :highlight-id="highlightCommentId"
              @reply="handleReply"
              @delete="handleDelete"
            />
          </div>
        </div>
      </div>
      <el-empty v-if="!loading && comments.length === 0" description="暂无评论" />
    </div>

    <!-- 回复对话框 -->
    <el-dialog 
      v-model="replyDialogVisible" 
      title="回复评论" 
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="reply-to-info" v-if="replyToComment">
        <span class="reply-to-label">回复</span>
        <span class="reply-to-user">@{{ replyToComment.user?.nickname }}</span>
        <span class="reply-to-content">{{ truncateContent(replyToComment.content) }}</span>
      </div>
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="4"
        placeholder="写下你的回复..."
        maxlength="500"
        show-word-limit
      />
      <template #footer>
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">
          发送回复
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

/**
 * 评论列表组件
 * 
 * 功能说明：
 * 1. 展示文章的评论列表（支持嵌套回复）
 * 2. 支持用户发表新评论和回复评论
 * 3. 支持删除自己的评论（作者权限控制）
 * 4. 支持评论高亮和自动滚动定位
 * 5. 字数限制和实时字数统计
 * 
 * 权限控制：
 * - 游客：只能查看评论，不能发表或删除
 * - 登录用户：可以发表评论和回复，只能删除自己的评论
 * 
 * 特殊功能：
 * - 通过 URL 参数 highlightComment 支持评论高亮定位
 * - 评论内容限制 500 字符，带实时字数统计
 * - 支持嵌套回复结构展示
 */
<script setup>
import { ref, onMounted, defineComponent, h, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, ElAvatar, ElButton } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getComments, createComment, replyComment, deleteComment } from '@/api/interaction'

/**
 * 评论项子组件
 * 使用 defineComponent + render 函数实现，支持评论和回复的统一渲染
 */
const CommentItem = defineComponent({
  name: 'CommentItem',
  props: {
    comment: { type: Object, required: true },
    articleId: { type: [Number, String], required: true },
    currentUserId: { type: Number, default: null },
    isLoggedIn: { type: Boolean, default: false },
    isReply: { type: Boolean, default: false },
    highlightId: { type: [Number, String], default: null }
  },
  emits: ['reply', 'delete'],
  setup(props, { emit }) {
    /**
     * 格式化时间显示
     * @param {string} time - ISO 时间字符串
     * @returns {string} 格式化后的本地时间字符串
     */
    const formatTime = (time) => {
      if (!time) return ''
      return new Date(time).toLocaleString('zh-CN')
    }

    /**
     * 判断当前评论是否需要高亮显示
     * @returns {boolean} 是否高亮
     */
    const isHighlighted = () => {
      return props.highlightId && String(props.comment.id) === String(props.highlightId)
    }

    return () => h('div', { 
      class: ['comment-content-wrapper', { 
        'is-reply': props.isReply,
        'highlight-comment': isHighlighted()
      }],
      id: `comment-${props.comment.id}`,
      style: {
        display: 'flex',
        gap: '12px',
        padding: '16px',
        background: props.isReply ? '#f5f5f5' : '#fafafa',
        borderRadius: '8px',
        minWidth: '0'
      }
    }, [
      h(ElAvatar, { 
        size: props.isReply ? 32 : 40, 
        src: props.comment.user?.avatar 
      }, () => props.comment.user?.nickname?.charAt(0)),
      h('div', { 
        class: 'comment-content',
        style: {
          flex: '1',
          minWidth: '0'
        }
      }, [
        h('div', { class: 'comment-header' }, [
          h('span', { class: 'username' }, props.comment.user?.nickname),
          h('span', { class: 'time' }, formatTime(props.comment.createdAt))
        ]),
        h('p', { 
          class: 'content',
          style: {
            margin: '0 0 8px 0',
            color: '#444',
            lineHeight: '1.6',
            wordBreak: 'break-all',
            overflowWrap: 'anywhere',
            maxWidth: '100%'
          }
        }, props.comment.content),
        h('div', { class: 'comment-actions' }, [
          props.isLoggedIn ? h(ElButton, {
            type: 'primary',
            text: true,
            size: 'small',
            onClick: () => emit('reply', props.comment)
          }, () => '回复') : null,
          props.comment.user?.id === props.currentUserId ? h(ElButton, {
            type: 'danger',
            text: true,
            size: 'small',
            onClick: () => emit('delete', props.comment.id)
          }, () => '删除') : null
        ])
      ])
    ])
  }
})

// ==================== 组件属性定义 ====================
const props = defineProps({
  articleId: { type: [Number, String], required: true } // 文章ID，用于获取该文章的评论列表
})

// ==================== 响应式数据状态 ====================
const route = useRoute()
const userStore = useUserStore()

/** 评论列表数据 */
const comments = ref([])
/** 新评论内容 */
const newComment = ref('')
/** 评论列表加载状态 */
const loading = ref(false)
/** 提交评论/回复的加载状态 */
const submitting = ref(false)
/** 当前需要高亮的评论ID */
const highlightCommentId = ref(null)

// ==================== 回复功能相关状态 ====================
/** 回复对话框显示状态 */
const replyDialogVisible = ref(false)
/** 当前回复的目标评论 */
const replyToComment = ref(null)
/** 回复内容 */
const replyContent = ref('')

// ==================== 工具函数 ====================
/**
 * 截断评论内容用于显示
 * @param {string} content - 原始评论内容
 * @returns {string} 截断后的内容（超过30字符显示省略号）
 */
const truncateContent = (content) => {
  if (!content) return ''
  return content.length > 30 ? content.substring(0, 30) + '...' : content
}

// ==================== 核心业务方法 ====================
/**
 * 加载文章的评论列表
 * 
 * 功能说明：
 * 1. 从服务器获取指定文章的所有评论
 * 2. 评论数据包含嵌套的回复结构
 * 3. 处理加载状态和错误情况
 */

const loadComments = async () => {
  loading.value = true
  try {
    const res = await getComments(props.articleId)
    comments.value = res.data || []
  } catch (error) {
    console.error('加载评论失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (newComment.value.length > 500) {
    ElMessage.warning('评论内容不能超过500个字符')
    return
  }
  submitting.value = true
  try {
    await createComment({
      articleId: props.articleId,
      content: newComment.value
    })
    ElMessage.success('评论成功')
    newComment.value = ''
    loadComments()
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error(error.response?.data?.message || '评论失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleReply = (comment) => {
  replyToComment.value = comment
  replyContent.value = ''
  replyDialogVisible.value = true
}

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  if (replyContent.value.length > 500) {
    ElMessage.warning('回复内容不能超过500个字符')
    return
  }
  submitting.value = true
  try {
    await replyComment({
      articleId: props.articleId,
      content: replyContent.value,
      parentId: replyToComment.value.id
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    replyContent.value = ''
    replyToComment.value = null
    loadComments()
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error(error.response?.data?.message || '回复失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？删除后其下的所有回复也将被删除。', '提示', {
      type: 'warning'
    })
    await deleteComment(id)
    ElMessage.success('删除成功')
    loadComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 滚动到指定评论并高亮
const scrollToComment = (commentId) => {
  nextTick(() => {
    const element = document.getElementById(`comment-${commentId}`)
    if (element) {
      element.scrollIntoView({ behavior: 'smooth', block: 'center' })
      // 设置高亮ID，触发动画
      highlightCommentId.value = commentId
      // 3秒后移除高亮
      setTimeout(() => {
        highlightCommentId.value = null
      }, 3000)
    }
  })
}

// 监听路由参数变化
watch(() => route.query.highlightComment, (newVal) => {
  if (newVal) {
    scrollToComment(newVal)
  }
}, { immediate: true })

onMounted(() => {
  loadComments().then(() => {
    // 加载完成后检查是否需要高亮
    if (route.query.highlightComment) {
      scrollToComment(route.query.highlightComment)
    }
  })
})
</script>

<style scoped>
.comment-form {
  margin-bottom: 24px;
}

.comment-form .el-button {
  margin-top: 12px;
}

.login-tip {
  padding: 16px;
  background: #f5f5f5;
  border-radius: 4px;
  text-align: center;
  margin-bottom: 24px;
}

.login-tip a {
  color: #409eff;
}

.comment-item {
  margin-bottom: 16px;
}

.comment-content-wrapper {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  transition: all 0.3s ease;
  min-width: 0;
}

.comment-content-wrapper.is-reply {
  background: #f5f5f5;
  padding: 12px;
}

/* 评论高亮动画 */
.comment-content-wrapper.highlight-comment {
  animation: highlightPulse 1.5s ease-in-out 2;
  background: #e6f7ff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3);
}

@keyframes highlightPulse {
  0% {
    background: #e6f7ff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3);
  }
  50% {
    background: #bae7ff;
    box-shadow: 0 0 0 6px rgba(64, 158, 255, 0.5);
  }
  100% {
    background: #e6f7ff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.3);
  }
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.username {
  font-weight: 500;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.content {
  margin: 0 0 8px 0;
  color: #444;
  line-height: 1.6;
  word-break: break-all;
  overflow-wrap: anywhere;
  max-width: 100%;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

/* 回复列表样式 */
.replies-container {
  margin-left: 52px;
  margin-top: 8px;
  border-left: 2px solid #e4e7ed;
  padding-left: 16px;
}

.reply-item {
  margin-bottom: 8px;
}

.reply-item:last-child {
  margin-bottom: 0;
}

/* 回复对话框样式 */
.reply-to-info {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
  font-size: 14px;
}

.reply-to-label {
  color: #909399;
  margin-right: 4px;
}

.reply-to-user {
  color: #409eff;
  font-weight: 500;
  margin-right: 8px;
}

.reply-to-content {
  color: #606266;
}
</style>
