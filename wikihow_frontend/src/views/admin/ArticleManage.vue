<template>
  <AdminLayout>
    <div class="article-manage">
      <!-- 搜索和筛选 -->
      <div class="toolbar">
        <div class="search-box">
          <el-input v-model="searchKeyword" placeholder="搜索文章标题" clearable @keyup.enter="handleSearch">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-select v-model="filterCategory" placeholder="分类筛选" clearable style="width: 140px">
            <el-option label="全部分类" value="" />
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
          <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px">
            <el-option label="全部" value="" />
            <el-option label="草稿" :value="0" />
            <el-option label="待审核" :value="1" />
            <el-option label="已发布" :value="2" />
            <el-option label="已拒绝" :value="3" />
            <el-option label="已下架" :value="4" />
          </el-select>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </div>
        <el-button type="success" @click="$router.push('/editor')">
          <el-icon><Plus /></el-icon> 发布文章
        </el-button>
      </div>
      
      <!-- 文章列表 -->
      <el-table :data="articles" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <el-image v-if="row.coverImage" :src="row.coverImage" fit="cover" style="width: 60px; height: 45px; border-radius: 4px;" />
            <span v-else class="no-cover">无封面</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column label="作者" width="120">
          <template #default="{ row }">{{ row.author?.nickname }}</template>
        </el-table-column>
        <el-table-column label="分类" width="100">
          <template #default="{ row }">{{ row.category?.name }}</template>
        </el-table-column>
        <el-table-column label="难度" width="80">
          <template #default="{ row }">
            <el-tag :type="difficultyType(row.difficulty)" size="small">{{ difficultyText(row.difficulty) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80" />
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="$router.push('/article/' + row.id)">查看</el-button>
            <el-button type="warning" link @click="$router.push('/editor/' + row.id)">编辑</el-button>
            <el-dropdown @command="(cmd) => handleCommand(cmd, row)">
              <el-button type="info" link>更多</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="row.status !== 1 && row.status !== 2" command="publish">发布</el-dropdown-item>
                  <el-dropdown-item v-if="row.status === 1 || row.status === 2" command="offline">下架</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadArticles" />
      </div>
    </div>
  </AdminLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import { getArticles, deleteArticle } from '@/api/article'
import { getCategories } from '@/api/category'
import request from '@/api/index'

const articles = ref([])
const categories = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterCategory = ref('')
const filterStatus = ref('')

const difficultyType = (d) => ({ EASY: 'success', MEDIUM: 'warning', HARD: 'danger' }[d] || 'info')
const difficultyText = (d) => ({ EASY: '简单', MEDIUM: '中等', HARD: '困难' }[d] || '中等')

// 状态类型映射（新状态系统：0草稿/1待审核/2已发布/3已拒绝/4已下架）
const statusType = (s) => {
  const map = { 0: 'info', 1: 'warning', 2: 'success', 3: 'danger', 4: 'info' }
  return map[s] !== undefined ? map[s] : 'info'
}

// 状态文本映射
const statusText = (s) => {
  const map = { 0: '草稿', 1: '待审核', 2: '已发布', 3: '已拒绝', 4: '已下架' }
  return map[s] !== undefined ? map[s] : '未知'
}

const loadCategories = async () => {
  const res = await getCategories()
  categories.value = res.data || []
}

const loadArticles = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/articles', {
      params: { page: page.value, size: pageSize.value, keyword: searchKeyword.value, categoryId: filterCategory.value, status: filterStatus.value }
    })
    articles.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    // fallback to public API
    const res = await getArticles({ page: page.value, size: pageSize.value, keyword: searchKeyword.value, categoryId: filterCategory.value })
    articles.value = res.data.list || []
    total.value = res.data.total || 0
  }
  finally { loading.value = false }
}

const handleSearch = () => { page.value = 1; loadArticles() }

const handleCommand = async (cmd, row) => {
  if (cmd === 'delete') {
    await ElMessageBox.confirm(`确定要删除文章《${row.title}》吗？`, '提示', { type: 'warning' })
    try {
      await deleteArticle(row.id)
      ElMessage.success('删除成功')
      loadArticles()
    } catch (e) { ElMessage.error('删除失败') }
  } else if (cmd === 'publish' || cmd === 'offline') {
    const status = cmd === 'publish' ? 2 : 4
    try {
      await request.put(`/admin/articles/${row.id}/status`, { status })
      ElMessage.success(cmd === 'publish' ? '发布成功' : '下架成功')
      loadArticles()
    } catch (e) { ElMessage.error('操作失败') }
  }
}

onMounted(() => { loadCategories(); loadArticles() })
</script>

<style scoped>
.article-manage { background: #fff; border-radius: 8px; padding: 20px; }
.toolbar { margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; }
.search-box { display: flex; gap: 12px; }
.search-box .el-input { width: 200px; }
.no-cover { color: #ccc; font-size: 12px; }
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
