<template>
  <div class="page-wrapper">
    <AppHeader />
    <div class="page-content">
      <div class="page-container">
        <h1 class="page-title">联系我们</h1>
        
        <div class="contact-grid">
          <div class="contact-info">
            <h2>联系方式</h2>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <div>
                <h4>电子邮箱</h4>
                <p>contact@wikihow-kb.com</p>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <div>
                <h4>联系电话</h4>
                <p>400-888-9999</p>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Location /></el-icon>
              <div>
                <h4>公司地址</h4>
                <p>北京市海淀区中关村科技园区</p>
              </div>
            </div>
            <div class="info-item">
              <el-icon><Clock /></el-icon>
              <div>
                <h4>工作时间</h4>
                <p>周一至周五 9:00 - 18:00</p>
              </div>
            </div>
          </div>
          
          <div class="contact-form">
            <h2>发送消息</h2>
            <el-form :model="form" label-position="top">
              <el-form-item label="您的姓名">
                <el-input v-model="form.name" placeholder="请输入姓名" />
              </el-form-item>
              <el-form-item label="电子邮箱">
                <el-input v-model="form.email" placeholder="请输入邮箱" />
              </el-form-item>
              <el-form-item label="主题">
                <el-select v-model="form.subject" placeholder="请选择主题" style="width: 100%">
                  <el-option label="一般咨询" value="general" />
                  <el-option label="技术支持" value="support" />
                  <el-option label="商务合作" value="business" />
                  <el-option label="内容反馈" value="feedback" />
                </el-select>
              </el-form-item>
              <el-form-item label="消息内容">
                <el-input v-model="form.message" type="textarea" :rows="4" placeholder="请输入您的消息" />
              </el-form-item>
              <el-button type="primary" class="submit-btn" @click="handleSubmit">发送消息</el-button>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Message, Phone, Location, Clock } from '@element-plus/icons-vue'
import AppHeader from '@/components/AppHeader.vue'

const form = reactive({ name: '', email: '', subject: '', message: '' })

const handleSubmit = () => {
  if (!form.name || !form.email || !form.message) {
    ElMessage.warning('请填写完整信息')
    return
  }
  ElMessage.success('消息已发送，我们会尽快回复您！')
  Object.assign(form, { name: '', email: '', subject: '', message: '' })
}
</script>

<style scoped>
.page-wrapper { min-height: 100vh; background: #f5f5f5; }
.page-content { max-width: 1000px; margin: 0 auto; padding: 80px 20px 40px; }
.page-container { background: #fff; border-radius: 8px; padding: 40px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.page-title { color: #93b874; margin-bottom: 30px; font-size: 28px; }
.contact-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; }
.contact-info h2, .contact-form h2 { color: #333; font-size: 20px; margin-bottom: 20px; }
.info-item { display: flex; gap: 15px; margin-bottom: 20px; padding: 15px; background: #f9f9f9; border-radius: 8px; }
.info-item .el-icon { font-size: 24px; color: #93b874; margin-top: 4px; }
.info-item h4 { color: #333; margin-bottom: 5px; font-size: 14px; }
.info-item p { color: #666; font-size: 14px; }
.submit-btn { width: 100%; background: #93b874; border: none; }
.submit-btn:hover { background: #7aa35e; }
@media (max-width: 768px) { .contact-grid { grid-template-columns: 1fr; } }
</style>
