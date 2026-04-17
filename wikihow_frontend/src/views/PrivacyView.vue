<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">隐私政策</h1>
        <p class="update-date">最后更新日期：2024年12月1日</p>
        
        <section class="section">
          <h2>1. 信息收集</h2>
          <p>我们可能收集以下类型的信息：</p>
          <ul>
            <li><strong>账号信息：</strong>注册时提供的用户名、邮箱地址等</li>
            <li><strong>个人资料：</strong>您选择填写的昵称、头像、个人简介等</li>
            <li><strong>使用数据：</strong>浏览记录、搜索历史、点赞收藏等行为数据</li>
            <li><strong>设备信息：</strong>IP地址、浏览器类型、操作系统等</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>2. 信息使用</h2>
          <p>我们使用收集的信息用于：</p>
          <ul>
            <li>提供、维护和改进我们的服务</li>
            <li>个性化您的使用体验</li>
            <li>发送服务通知和更新</li>
            <li>防止欺诈和滥用行为</li>
            <li>进行数据分析以改进产品</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>3. 信息共享</h2>
          <p>我们不会出售您的个人信息。在以下情况下，我们可能会共享您的信息：</p>
          <ul>
            <li>经您明确同意</li>
            <li>与服务提供商合作（如云存储服务）</li>
            <li>法律要求或保护权益需要</li>
            <li>公司合并、收购或资产出售时</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>4. 信息安全</h2>
          <p>我们采取合理的技术和管理措施保护您的个人信息，包括：</p>
          <ul>
            <li>使用加密技术传输敏感数据</li>
            <li>密码采用安全的哈希算法存储</li>
            <li>定期进行安全审计和漏洞扫描</li>
            <li>限制员工访问用户数据的权限</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>5. Cookie 使用</h2>
          <p>我们使用 Cookie 和类似技术来：</p>
          <ul>
            <li>保持您的登录状态</li>
            <li>记住您的偏好设置</li>
            <li>分析网站流量和使用情况</li>
          </ul>
          <p>您可以通过浏览器设置管理 Cookie，但这可能影响部分功能的使用。</p>
        </section>
        
        <section class="section">
          <h2>6. 您的权利</h2>
          <p>您对自己的个人信息享有以下权利：</p>
          <ul>
            <li>访问和查看您的个人信息</li>
            <li>更正不准确的信息</li>
            <li>删除您的账号和相关数据</li>
            <li>导出您的数据</li>
          </ul>
        </section>
        
        <section class="section">
          <h2>7. 未成年人保护</h2>
          <p>本平台不面向13岁以下的儿童。如果我们发现收集了儿童的个人信息，将立即删除。</p>
        </section>
        
        <section class="section">
          <h2>8. 政策更新</h2>
          <p>我们可能会不时更新本隐私政策。更新后的政策将在本页面公布，重大变更时我们会通过邮件或站内通知告知您。</p>
        </section>
        
        <section class="section">
          <h2>9. 联系我们</h2>
          <p>如对本隐私政策有任何疑问，请联系：<a href="mailto:privacy@wikihow-kb.com">privacy@wikihow-kb.com</a></p>
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
.section ul { color: #666; padding-left: 20px; line-height: 2; margin-bottom: 10px; }
.section a { color: #93b874; }
</style>
