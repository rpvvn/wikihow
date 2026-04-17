<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">使用条款</h1>
        <p class="update-date">最后更新日期：2024年12月1日</p>
        
        <section class="section">
          <h2>1. 服务条款的接受</h2>
          <p>欢迎使用 WikiHow 知识库（以下简称"本平台"）。通过访问或使用本平台，您同意受本使用条款的约束。如果您不同意这些条款，请勿使用本平台。</p>
        </section>
        
        <section class="section">
          <h2>2. 服务描述</h2>
          <p>本平台是一个知识分享社区，用户可以：</p>
          <ul>
            <li>浏览和搜索各类教程文章</li>
            <li>注册账号并发布原创教程内容</li>
            <li>对文章进行评论、点赞和收藏</li>
            <li>与其他用户进行互动交流</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>3. 用户账号</h2>
          <p>3.1 您需要注册账号才能使用本平台的部分功能。注册时请提供真实、准确的信息。</p>
          <p>3.2 您有责任保管好自己的账号和密码，因账号泄露造成的损失由您自行承担。</p>
          <p>3.3 我们保留在您违反本条款时暂停或终止您账号的权利。</p>
        </section>
        
        <section class="section">
          <h2>4. 用户内容</h2>
          <p>4.1 您发布的内容必须是原创或已获得合法授权。</p>
          <p>4.2 您不得发布违法、侵权、虚假、有害或不当的内容。</p>
          <p>4.3 您授予本平台非独占的、免费的、可转授权的许可，以使用、复制、修改和展示您发布的内容。</p>
        </section>
        
        <section class="section">
          <h2>5. 知识产权</h2>
          <p>5.1 本平台的商标、标识、界面设计等均为本平台所有。</p>
          <p>5.2 用户发布的原创内容，其知识产权归用户所有。</p>
        </section>
        
        <section class="section">
          <h2>6. 免责声明</h2>
          <p>6.1 本平台上的教程内容仅供参考，不构成专业建议。</p>
          <p>6.2 我们不对用户发布内容的准确性、完整性承担责任。</p>
          <p>6.3 因使用本平台内容造成的任何损失，本平台不承担责任。</p>
        </section>
        
        <section class="section">
          <h2>7. 条款修改</h2>
          <p>我们保留随时修改本条款的权利。修改后的条款将在本页面公布，继续使用本平台即表示您接受修改后的条款。</p>
        </section>
        
        <section class="section">
          <h2>8. 联系我们</h2>
          <p>如对本条款有任何疑问，请联系：<a href="mailto:legal@wikihow-kb.com">legal@wikihow-kb.com</a></p>
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
.page-title { color: #93b874; margin-bottom: 10px; font-size: 28px; }
.update-date { color: #999; font-size: 14px; margin-bottom: 30px; }
.section { margin-bottom: 25px; }
.section h2 { color: #333; font-size: 18px; margin-bottom: 12px; }
.section p { color: #666; line-height: 1.8; margin-bottom: 8px; }
.section ul { color: #666; padding-left: 20px; line-height: 2; }
.section a { color: #93b874; }
</style>
