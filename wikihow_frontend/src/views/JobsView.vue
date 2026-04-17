<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">加入我们</h1>
        
        <section class="intro-section">
          <h2>为什么选择 WikiHow？</h2>
          <p>加入 WikiHow 知识库，与一群充满热情的人一起，帮助全球用户学习新技能、解决问题。我们提供有竞争力的薪酬、灵活的工作方式和广阔的发展空间。</p>
          <div class="benefits">
            <div class="benefit"><span>🏠</span>远程办公</div>
            <div class="benefit"><span>📈</span>职业发展</div>
            <div class="benefit"><span>🎯</span>弹性工作</div>
            <div class="benefit"><span>🎁</span>丰厚福利</div>
          </div>
        </section>
        
        <section class="jobs-section">
          <h2>开放职位</h2>
          <div class="job-list">
            <div class="job-card" v-for="job in jobs" :key="job.title">
              <div class="job-header">
                <h3>{{ job.title }}</h3>
                <span class="job-type">{{ job.type }}</span>
              </div>
              <p class="job-location">📍 {{ job.location }}</p>
              <p class="job-desc">{{ job.description }}</p>
              <div class="job-tags">
                <span v-for="tag in job.requirements" :key="tag">{{ tag }}</span>
              </div>
              <button class="apply-btn">申请职位</button>
            </div>
          </div>
        </section>
        
        <section class="contact-section">
          <h2>没有找到合适的职位？</h2>
          <p>我们始终欢迎优秀人才。请将您的简历发送至 <a href="mailto:hr@wikihow-kb.com">hr@wikihow-kb.com</a>，我们会在有合适机会时与您联系。</p>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import AppHeader from '@/components/AppHeader.vue'

const jobs = ref([
  { title: '前端开发工程师', type: '全职', location: '北京 / 远程', description: '负责平台前端开发，优化用户体验，参与技术方案设计。', requirements: ['Vue.js', 'TypeScript', '3年+经验'] },
  { title: '后端开发工程师', type: '全职', location: '北京 / 远程', description: '负责后端服务开发，API设计，数据库优化。', requirements: ['Java', 'Spring Boot', 'MySQL'] },
  { title: '内容运营', type: '全职', location: '北京', description: '负责平台内容审核、优质内容挖掘和作者运营。', requirements: ['内容运营', '数据分析', '沟通能力'] },
  { title: 'UI/UX 设计师', type: '全职', location: '北京 / 远程', description: '负责产品界面设计，提升用户体验。', requirements: ['Figma', '交互设计', '用户研究'] }
])
</script>

<style scoped>
.page-wrapper { min-height: 100vh; background: #f5f5f5; }
.page-content { max-width: 900px; margin: 0 auto; padding: 80px 20px 40px; }
.page-container { background: #fff; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.page-title { color: #93b874; margin-bottom: 30px; font-size: 28px; }
.intro-section { margin-bottom: 40px; }
.intro-section h2 { color: #333; font-size: 20px; margin-bottom: 15px; }
.intro-section p { color: #666; line-height: 1.8; margin-bottom: 20px; }
.benefits { display: flex; gap: 20px; flex-wrap: wrap; }
.benefit { background: #f0f7eb; padding: 12px 20px; border-radius: 8px; color: #5a8a3a; }
.benefit span { margin-right: 8px; }
.jobs-section h2 { color: #333; font-size: 20px; margin-bottom: 20px; border-left: 4px solid #93b874; padding-left: 12px; }
.job-list { display: flex; flex-direction: column; gap: 20px; }
.job-card { border: 1px solid #e0e0e0; border-radius: 8px; padding: 20px; }
.job-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.job-header h3 { color: #333; font-size: 18px; }
.job-type { background: #93b874; color: #fff; padding: 4px 12px; border-radius: 4px; font-size: 12px; }
.job-location { color: #999; font-size: 14px; margin-bottom: 10px; }
.job-desc { color: #666; line-height: 1.6; margin-bottom: 12px; }
.job-tags { display: flex; gap: 8px; margin-bottom: 15px; flex-wrap: wrap; }
.job-tags span { background: #f5f5f5; color: #666; padding: 4px 10px; border-radius: 4px; font-size: 12px; }
.apply-btn { background: #93b874; color: #fff; border: none; padding: 8px 24px; border-radius: 4px; cursor: pointer; }
.apply-btn:hover { background: #7aa35e; }
.contact-section { margin-top: 40px; padding-top: 30px; border-top: 1px solid #eee; }
.contact-section h2 { color: #333; font-size: 18px; margin-bottom: 10px; }
.contact-section p { color: #666; }
.contact-section a { color: #93b874; }
</style>
