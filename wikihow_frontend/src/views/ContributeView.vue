<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">投稿指南</h1>
        
        <section class="intro">
          <p>感谢您有兴趣为 WikiHow 知识库贡献内容！我们欢迎各领域的专家和爱好者分享知识。请阅读以下指南，了解如何创建高质量的教程文章。</p>
        </section>
        
        <section class="section">
          <h2>📝 文章要求</h2>
          <div class="requirement-list">
            <div class="requirement-item">
              <h4>原创性</h4>
              <p>所有投稿必须是原创内容，不得抄袭或未经授权转载他人作品。</p>
            </div>
            <div class="requirement-item">
              <h4>实用性</h4>
              <p>教程应该解决实际问题，帮助读者学会某项技能或完成某个任务。</p>
            </div>
            <div class="requirement-item">
              <h4>准确性</h4>
              <p>确保内容准确无误，特别是涉及安全、健康等领域的内容。</p>
            </div>
            <div class="requirement-item">
              <h4>完整性</h4>
              <p>步骤要完整详细，让初学者也能按照教程顺利完成。</p>
            </div>
          </div>
        </section>
        
        <section class="section">
          <h2>📋 写作规范</h2>
          <ul class="guide-list">
            <li><strong>标题：</strong>使用"如何..."的格式，简洁明了地描述教程内容</li>
            <li><strong>摘要：</strong>用1-2句话概括文章内容，吸引读者点击</li>
            <li><strong>步骤：</strong>每个步骤应该是一个独立的操作，标题简短，内容详细</li>
            <li><strong>图片：</strong>建议为每个步骤配图，图片清晰、相关性强</li>
            <li><strong>语言：</strong>使用简洁易懂的语言，避免过多专业术语</li>
            <li><strong>分类：</strong>选择最相关的分类，便于读者查找</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>🚫 禁止内容</h2>
          <ul class="forbidden-list">
            <li>违反法律法规的内容</li>
            <li>侵犯他人知识产权的内容</li>
            <li>虚假、误导性信息</li>
            <li>涉及危险活动且无安全提示的内容</li>
            <li>广告、推销性质的内容</li>
            <li>包含个人隐私信息的内容</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>✅ 审核流程</h2>
          <div class="process-steps">
            <div class="process-step">
              <span class="step-num">1</span>
              <div>
                <h4>提交文章</h4>
                <p>登录后点击"写文章"，按照模板填写内容并提交</p>
              </div>
            </div>
            <div class="process-step">
              <span class="step-num">2</span>
              <div>
                <h4>初审</h4>
                <p>编辑团队会在1-3个工作日内进行初步审核</p>
              </div>
            </div>
            <div class="process-step">
              <span class="step-num">3</span>
              <div>
                <h4>专家审核</h4>
                <p>专业领域的文章会由相关专家进行内容审核</p>
              </div>
            </div>
            <div class="process-step">
              <span class="step-num">4</span>
              <div>
                <h4>发布</h4>
                <p>审核通过后文章将正式发布，您会收到通知</p>
              </div>
            </div>
          </div>
        </section>
        
        <section class="cta-section">
          <h2>准备好了吗？</h2>
          <p>开始创作您的第一篇教程吧！</p>
          <router-link to="/editor" class="cta-btn">开始写文章</router-link>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
</script>

<style scoped>
.page-wrapper { min-height: 100vh; background: #f5f5f5; }
.page-content { max-width: 900px; margin: 0 auto; padding: 80px 20px 40px; }
.page-container { background: #fff; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.page-title { color: #93b874; margin-bottom: 20px; font-size: 28px; }
.intro { color: #666; line-height: 1.8; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #eee; }
.section { margin-bottom: 30px; }
.section h2 { color: #333; font-size: 20px; margin-bottom: 20px; }
.requirement-list { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; }
.requirement-item { background: #f9f9f9; padding: 15px; border-radius: 8px; }
.requirement-item h4 { color: #93b874; margin-bottom: 8px; }
.requirement-item p { color: #666; font-size: 14px; line-height: 1.6; }
.guide-list { color: #666; padding-left: 20px; line-height: 2.2; }
.guide-list strong { color: #333; }
.forbidden-list { color: #666; padding-left: 20px; line-height: 2; }
.forbidden-list li::marker { color: #f56c6c; }
.process-steps { display: flex; flex-direction: column; gap: 15px; }
.process-step { display: flex; gap: 15px; align-items: flex-start; }
.step-num { width: 32px; height: 32px; background: #93b874; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: bold; flex-shrink: 0; }
.process-step h4 { color: #333; margin-bottom: 4px; }
.process-step p { color: #666; font-size: 14px; }
.cta-section { text-align: center; padding-top: 30px; border-top: 1px solid #eee; }
.cta-section h2 { color: #333; margin-bottom: 10px; }
.cta-section p { color: #666; margin-bottom: 20px; }
.cta-btn { display: inline-block; background: #93b874; color: #fff; padding: 12px 40px; border-radius: 4px; text-decoration: none; font-size: 16px; }
.cta-btn:hover { background: #7aa35e; }
@media (max-width: 768px) { .requirement-list { grid-template-columns: 1fr; } }
</style>
