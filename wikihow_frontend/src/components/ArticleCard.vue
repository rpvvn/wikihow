<template>
  <div class="article-card" @click="goToDetail">
    <!-- 封面图 -->
    <div class="cover-wrapper">
      <img
        v-if="article.coverImage"
        :src="article.coverImage"
        :alt="article.title"
        class="cover-image"
      />
      <div v-else class="cover-placeholder">
        <el-icon :size="32"><Document /></el-icon>
      </div>
      <!-- 难度标签 -->
      <span :class="['difficulty-tag', difficultyClass]">
        {{ difficultyText }}
      </span>
    </div>

    <!-- 文章标题 -->
    <div class="article-info">
      <h3 class="title">{{ article.title }}</h3>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Document } from '@element-plus/icons-vue'

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const difficultyClass = computed(() => {
  const map = { EASY: 'easy', MEDIUM: 'medium', HARD: 'hard' }
  return map[props.article.difficulty] || 'medium'
})

const difficultyText = computed(() => {
  const map = { EASY: '简单', MEDIUM: '中等', HARD: '困难' }
  return map[props.article.difficulty] || '中等'
})

const goToDetail = () => {
  router.push(`/article/${props.article.id}`)
}
</script>

<style scoped>
.article-card {
  cursor: pointer;
  transition: transform 0.2s;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cover-wrapper {
  position: relative;
  padding-top: 75%; /* 4:3 比例 */
  overflow: hidden;
}

.cover-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8f5e0 0%, #d4e8c7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #93b874;
}

.difficulty-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 500;
  color: #fff;
}

.difficulty-tag.easy {
  background: #67c23a;
}

.difficulty-tag.medium {
  background: #e6a23c;
}

.difficulty-tag.hard {
  background: #f56c6c;
}

.article-info {
  padding: 12px;
}

.title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
}

.article-card:hover .title {
  color: #93b874;
}
</style>
