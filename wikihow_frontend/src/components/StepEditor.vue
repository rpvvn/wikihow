<template>
  <div class="step-editor">
    <div v-for="(step, index) in modelValue" :key="index" class="step-item">
      <div class="step-header">
        <span class="step-number">步骤 {{ index + 1 }}</span>
        <el-button type="danger" text @click="removeStep(index)">删除</el-button>
      </div>
      
      <el-form-item label="步骤标题">
        <el-input v-model="step.title" placeholder="输入步骤标题" />
      </el-form-item>
      
      <el-form-item label="步骤内容">
        <el-input
          v-model="step.content"
          type="textarea"
          :rows="4"
          placeholder="详细描述这一步骤"
        />
      </el-form-item>
      
      <el-form-item label="步骤图片">
        <el-upload
          class="step-upload"
          :show-file-list="false"
          :http-request="(options) => handleUpload(options, index)"
        >
          <img v-if="step.image" :src="step.image" class="step-image" />
          <el-button v-else type="primary" plain>上传图片</el-button>
        </el-upload>
      </el-form-item>
    </div>
    
    <el-button type="primary" plain @click="addStep">
      <el-icon><Plus /></el-icon> 添加步骤
    </el-button>
  </div>
</template>

<script setup>
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { uploadFile } from '@/api/article'

const props = defineProps({
  modelValue: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue'])

const addStep = () => {
  const newSteps = [...props.modelValue, { order: props.modelValue.length + 1, title: '', content: '', image: '' }]
  emit('update:modelValue', newSteps)
}

const removeStep = (index) => {
  const newSteps = props.modelValue.filter((_, i) => i !== index)
  newSteps.forEach((step, i) => step.order = i + 1)
  emit('update:modelValue', newSteps)
}

const handleUpload = async (options, index) => {
  try {
    const res = await uploadFile(options.file)
    const newSteps = [...props.modelValue]
    newSteps[index].image = res.data.url
    emit('update:modelValue', newSteps)
    ElMessage.success('上传成功')
  } catch (error) {
    ElMessage.error('上传失败')
  }
}
</script>

<style scoped>
.step-item {
  padding: 20px;
  margin-bottom: 16px;
  background: #f9f9f9;
  border-radius: 8px;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.step-number {
  font-weight: bold;
  color: #409eff;
}

.step-image {
  max-width: 200px;
  max-height: 150px;
  border-radius: 4px;
}
</style>
