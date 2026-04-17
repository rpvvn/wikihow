<template>
  <AdminLayout>
    <div class="comment-manage">
      <div class="toolbar">
        <el-input v-model="searchKeyword" placeholder="搜索评论内容" clearable style="width: 300px" @keyup.enter="loadComments">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
      
      <el-table :data="comments" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="评论者" width="140">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="32" :src="row.user?.avatar">{{ row.user?.nickname?.charAt(0) }}</el-avatar>
              <span>{{ row.user?.nickname }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
        <el-table-column label="文章" width="200">
          <template #default="{ row }">
            <el-link type="primary" @click="$router.push('/article/' + row.articleId)">
              {{ row.articleTitle || '查看文章' }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="评论时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadComments" />
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import request from '@/api/index'

const comments = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(15)
const total = ref(0)
const searchKeyword = ref('')

const formatDate = (date) => date ? new Date(date).toLocaleString('zh-CN') : ''

const loadComments = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/comments', {
      params: { page: page.value, size: pageSize.value, keyword: searchKeyword.value }
    })
    comments.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) { console.error(e) }
  finally { loading.value = false }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', { type: 'warning' })
  try {
    await request.delete(`/admin/comments/${row.id}`)
    ElMessage.success('删除成功')
    loadComments()
  } catch (e) { ElMessage.error('删除失败') }
}

onMounted(() => loadComments())
</script>

<style scoped>
.comment-manage { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { margin-bottom: 20px; }
.user-cell { display: flex; align-items: center; gap: 8px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
