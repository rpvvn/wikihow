<template>
  <div class="article-step">
    <div class="step-header">
      <div class="step-number">{{ step.order }}</div>
      <h3 class="step-title">{{ step.title }}</h3>
    </div>
    
    <div class="step-body">
      <div class="step-content" v-html="formattedContent"></div>
      <div v-if="step.image" class="step-image">
        <img :src="step.image" :alt="step.title" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  step: {
    type: Object,
    required: true
  }
})

// 简单的内容格式化（换行转换）
const formattedContent = computed(() => {
  if (!props.step.content) return ''
  return props.step.content.replace(/\n/g, '<br>')
})
</script>

<style scoped>
.article-step {
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #eee;
}

.article-step:last-child {
  border-bottom: none;
}

.step-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.step-number {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  flex-shrink: 0;
}

.step-title {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.step-body {
  display: flex;
  gap: 24px;
  align-items: flex-start;
}

.step-content {
  flex: 1;
  font-size: 16px;
  line-height: 1.8;
  color: #444;
}

.step-image {
  flex-shrink: 0;
  width: 300px;
}

.step-image img {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .step-body {
    flex-direction: column;
  }
  
  .step-image {
    width: 100%;
  }
}
</style>
