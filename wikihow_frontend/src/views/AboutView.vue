<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">关于我们</h1>
        
        <section class="section">
          <h2>我们的使命</h2>
          <p>WikiHow 知识库致力于帮助每个人学习如何做任何事情。我们相信知识应该是免费、开放且易于获取的。通过汇集全球用户的智慧，我们创建了一个涵盖生活各个方面的实用教程平台。</p>
        </section>
        
        <section class="section">
          <h2>我们的故事</h2>
          <p>WikiHow 知识库成立于 2024 年，由一群热爱分享知识的技术爱好者创建。我们注意到，虽然互联网上有大量信息，但找到清晰、可靠、步骤明确的教程却并不容易。</p>
          <p>因此，我们决定创建一个平台，让任何人都可以分享他们的专业知识，同时让学习者能够轻松找到他们需要的指导。</p>
        </section>
        
        <section class="section">
          <h2>我们的价值观</h2>
          <div class="values-grid">
            <div class="value-card">
              <div class="value-icon">📚</div>
              <h3>知识共享</h3>
              <p>我们相信知识的力量，鼓励每个人分享自己的专长。</p>
            </div>
            <div class="value-card">
              <div class="value-icon">✅</div>
              <h3>质量至上</h3>
              <p>每篇教程都经过审核，确保内容准确、实用。</p>
            </div>
            <div class="value-card">
              <div class="value-icon">🌍</div>
              <h3>开放包容</h3>
              <p>欢迎来自不同背景的贡献者，共建多元化社区。</p>
            </div>
            <div class="value-card">
              <div class="value-icon">🤝</div>
              <h3>用户至上</h3>
              <p>始终以用户需求为中心，持续改进用户体验。</p>
            </div>
          </div>
        </section>
        
        <section class="section">
          <h2>联系我们</h2>
          <p>如果您有任何问题、建议或合作意向，欢迎通过以下方式联系我们：</p>
          <ul class="contact-list">
            <li>邮箱：contact@wikihow-kb.com</li>
            <li>地址：北京市海淀区中关村科技园</li>
          </ul>
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
.page-title { color: #93b874; margin-bottom: 30px; font-size: 28px; }
.section { margin-bottom: 30px; }
.section h2 { color: #333; font-size: 20px; margin-bottom: 15px; border-left: 4px solid #93b874; padding-left: 12px; }
.section p { color: #666; line-height: 1.8; margin-bottom: 10px; }
.values-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; margin-top: 20px; }
.value-card { background: #f9f9f9; padding: 20px; border-radius: 8px; text-align: center; }
.value-icon { font-size: 32px; margin-bottom: 10px; }
.value-card h3 { color: #333; margin-bottom: 8px; }
.value-card p { color: #666; font-size: 14px; }
.contact-list { color: #666; padding-left: 20px; line-height: 2; }
</style>
