<template>
  <AdminLayout>
    <div class="category-manage">
      <div class="toolbar">
        <el-button type="primary" @click="openAddDialog(null)">
          <el-icon><Plus /></el-icon> 添加一级分类
        </el-button>
      </div>
      
      <!-- 分类列表 -->
      <el-table :data="categoryTree" row-key="id" default-expand-all v-loading="loading">
        <el-table-column prop="name" label="分类名称" min-width="180">
          <template #default="{ row }">
            <div class="category-name-cell">
              <el-image 
                v-if="row.coverImage" 
                :src="row.coverImage" 
                class="category-cover-thumb"
                fit="cover"
              />
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="层级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.parentId ? 'info' : 'success'" size="small">
              {{ row.parentId ? '二级分类' : '一级分类' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" min-width="200">
          <template #default="{ row }">
            <el-tooltip v-if="row.description" :content="row.description" placement="top" :show-after="500">
              <span class="description-text">{{ row.description }}</span>
            </el-tooltip>
            <span v-else class="no-description">暂无描述</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="文章数" width="80">
          <template #default="{ row }">{{ row.articleCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="!row.parentId" type="success" link size="small" @click="openAddDialog(row.id)">添加子分类</el-button>
            <el-button type="primary" link size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 添加/编辑弹窗 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入分类名称" />
          </el-form-item>
          <el-form-item label="上级分类">
            <el-select v-model="form.parentId" placeholder="无（一级分类）" clearable style="width: 100%">
              <el-option v-for="cat in parentCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="排序">
            <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
          </el-form-item>
          <el-form-item label="分类描述">
            <el-input 
              v-model="form.description" 
              type="textarea" 
              :rows="4" 
              placeholder="请输入分类描述，将显示在首页分类介绍区域"
              maxlength="500"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="封面图片">
            <div class="cover-upload-area">
              <el-input 
                v-model="form.coverImage" 
                placeholder="请输入封面图片URL或上传图片"
                class="cover-input"
              />
              <el-upload
                class="cover-uploader"
                action="/api/files/upload"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleCoverSuccess"
                :before-upload="beforeCoverUpload"
                accept="image/*"
              >
                <el-button type="primary" size="small">上传图片</el-button>
              </el-upload>
            </div>
            <div class="cover-preview" v-if="form.coverImage">
              <el-image :src="form.coverImage" fit="cover" class="preview-image" />
              <el-button type="danger" link size="small" @click="form.coverImage = ''">移除</el-button>
            </div>
            <div class="cover-tip">建议尺寸：1200x400像素，支持 JPG、PNG 格式</div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import { getCategoriesWithCount, createCategory, updateCategory, deleteCategory } from '@/api/category'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const form = reactive({ 
  id: null, 
  name: '', 
  parentId: null, 
  sortOrder: 0, 
  description: '',
  coverImage: ''
})

const rules = { 
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }] 
}

// 上传请求头
const uploadHeaders = computed(() => ({
  Authorization: userStore.token ? `Bearer ${userStore.token}` : ''
}))

const dialogTitle = computed(() => isEdit.value ? '编辑分类' : '添加分类')
const parentCategories = computed(() => categories.value.filter(c => !c.parentId))

const categoryTree = computed(() => {
  const parents = categories.value.filter(c => !c.parentId).sort((a, b) => a.sortOrder - b.sortOrder)
  return parents.map(p => ({
    ...p,
    children: categories.value.filter(c => c.parentId === p.id).sort((a, b) => a.sortOrder - b.sortOrder)
  }))
})

const loadCategories = async () => {
  loading.value = true
  try {
    const res = await getCategoriesWithCount()
    categories.value = res.data || []
  } finally { loading.value = false }
}

const resetForm = () => {
  Object.assign(form, { 
    id: null, 
    name: '', 
    parentId: null, 
    sortOrder: 0, 
    description: '',
    coverImage: ''
  })
}

const openAddDialog = (parentId) => {
  resetForm()
  form.parentId = parentId
  isEdit.value = false
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  Object.assign(form, { 
    id: row.id, 
    name: row.name, 
    parentId: row.parentId, 
    sortOrder: row.sortOrder, 
    description: row.description || '',
    coverImage: row.coverImage || ''
  })
  isEdit.value = true
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (isEdit.value) {
      await updateCategory(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createCategory(form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadCategories()
  } catch (e) { 
    ElMessage.error('操作失败') 
  }
}

const handleDelete = async (row) => {
  if (row.children?.length) {
    ElMessage.warning('请先删除子分类')
    return
  }
  await ElMessageBox.confirm(`确定要删除分类"${row.name}"吗？`, '提示', { type: 'warning' })
  try {
    await deleteCategory(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (e) { 
    ElMessage.error('删除失败') 
  }
}

// 封面图上传成功
const handleCoverSuccess = (response) => {
  if (response.code === 200) {
    // response.data 是 { url: "xxx" } 对象，需要取 url 字段
    form.coverImage = response.data.url || response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 封面图上传前校验
const beforeCoverUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

onMounted(() => loadCategories())
</script>

<style scoped>
.category-manage { 
  background: #fff; 
  border-radius: 8px; 
  padding: 20px; 
}

.toolbar { 
  margin-bottom: 20px; 
}

.category-name-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-cover-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  flex-shrink: 0;
}

.description-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  color: #666;
  font-size: 13px;
  line-height: 1.4;
}

.no-description {
  color: #ccc;
  font-size: 13px;
}

.cover-upload-area {
  display: flex;
  gap: 10px;
}

.cover-input {
  flex: 1;
}

.cover-preview {
  margin-top: 10px;
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.preview-image {
  width: 200px;
  height: 80px;
  border-radius: 4px;
  border: 1px solid #eee;
}

.cover-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>
