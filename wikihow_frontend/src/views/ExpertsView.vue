<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">专家团队</h1>
        
        <p class="intro">我们的专家团队由各领域的资深人士组成，他们负责审核内容质量，确保每篇教程的准确性和实用性。</p>
        
        <div class="experts-grid">
          <div class="expert-card" v-for="expert in experts" :key="expert.name">
            <div class="expert-avatar">{{ expert.name.charAt(0) }}</div>
            <h3>{{ expert.name }}</h3>
            <p class="expert-title">{{ expert.title }}</p>
            <p class="expert-desc">{{ expert.description }}</p>
            <div class="expert-tags">
              <span v-for="tag in expert.tags" :key="tag">{{ tag }}</span>
            </div>
          </div>
        </div>
        
        <section class="join-section">
          <h2>成为专家</h2>
          <p>如果您在某个领域有丰富的经验和专业知识，欢迎申请加入我们的专家团队。</p>
          <router-link to="/contact" class="join-btn">申请加入</router-link>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'

const experts = ref([
  { name: '张明华', title: '高级软件工程师', description: '10年软件开发经验，专注于Web开发和系统架构设计。', tags: ['编程', '技术'] },
  { name: '李雪梅', title: '营养师', description: '国家注册营养师，擅长健康饮食和营养搭配指导。', tags: ['烹饪', '健康'] },
  { name: '王建国', title: '健身教练', description: '国家一级健身教练，专业运动康复和体能训练。', tags: ['运动', '健身'] },
  { name: '陈艺文', title: '设计总监', description: '15年设计从业经验，曾服务多家知名企业。', tags: ['设计', '创意'] },
  { name: '刘小燕', title: '手工艺人', description: '非遗传承人，精通多种传统手工艺制作。', tags: ['手工', 'DIY'] },
  { name: '赵志强', title: '家居顾问', description: '资深家居整理师，帮助上千家庭改善居住环境。', tags: ['家居', '整理'] }
])
</script>

<style scoped>
.page-wrapper { min-height: 100vh; background: #f5f5f5; }
.page-content { max-width: 1000px; margin: 0 auto; padding: 80px 20px 40px; }
.page-container { background: #fff; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.page-title { color: #93b874; margin-bottom: 20px; font-size: 28px; }
.intro { color: #666; line-height: 1.8; margin-bottom: 30px; }
.experts-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.expert-card { background: #f9f9f9; padding: 24px; border-radius: 8px; text-align: center; }
.expert-avatar { width: 60px; height: 60px; background: #93b874; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; margin: 0 auto 15px; }
.expert-card h3 { color: #333; margin-bottom: 5px; }
.expert-title { color: #93b874; font-size: 14px; margin-bottom: 10px; }
.expert-desc { color: #666; font-size: 13px; line-height: 1.6; margin-bottom: 12px; }
.expert-tags { display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; }
.expert-tags span { background: #e8f5e0; color: #5a8a3a; padding: 2px 10px; border-radius: 12px; font-size: 12px; }
.join-section { margin-top: 40px; padding-top: 30px; border-top: 1px solid #eee; text-align: center; }
.join-section h2 { color: #333; margin-bottom: 15px; }
.join-section p { color: #666; margin-bottom: 20px; }
.join-btn { display: inline-block; background: #93b874; color: #fff; padding: 10px 30px; border-radius: 4px; text-decoration: none; }
.join-btn:hover { background: #7aa35e; }
@media (max-width: 768px) { .experts-grid { grid-template-columns: 1fr; } }
</style>
