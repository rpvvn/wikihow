<template>
  <AdminLayout>
    <div class="outdated-manage">
      <!-- 标题和筛选 -->
      <div class="toolbar">
        <h2 class="page-title">过时内容举报管理</h2>
        <div class="filter-box">
          <el-select v-model="filterStatus" placeholder="处理状态" clearable style="width: 140px" @change="handleFilter">
            <el-option label="全部" value="" />
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
            <el-option label="已忽略" :value="2" />
          </el-select>
        </div>
      </div>
      
      <!-- 举报列表 -->
      <el-table :data="reports" stripe v-loading="loading" class="report-table">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="文章信息" min-width="280">
          <template #default="{ row }">
            <div class="article-info">
              <el-image v-if="row.articleCoverImage" :src="row.articleCoverImage" fit="cover" class="cover-img" />
              <div v-else class="no-cover">无封面</div>
              <div class="article-detail">
                <div class="article-title">
                  {{ row.articleTitle }}
                  <el-tag v-if="row.articleIsOutdated" type="warning" size="small" class="outdated-tag">已过时</el-tag>
                </div>
                <div class="article-meta">
                  <span class="summary">{{ row.articleSummary }}</span>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="举报人" width="150">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :src="row.userAvatar" :size="32">{{ row.nickname?.charAt(0) }}</el-avatar>
              <span class="user-name">{{ row.nickname || row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.reportType === 1 ? 'success' : 'warning'" size="small">
              {{ row.reportTypeText || '过时举报' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="举报原因" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.reason" placement="top" :disabled="row.reason?.length < 50">
              <span class="reason-text">{{ row.reason }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理人" width="100">
          <template #default="{ row }">
            <span v-if="row.handlerNickname">{{ row.handlerNickname }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="举报时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="previewArticle(row)">预览</el-button>
            <template v-if="row.status === 0">
              <el-button type="warning" link @click="handleProcess(row)">处理</el-button>
              <el-button type="info" link @click="handleIgnore(row)">忽略</el-button>
            </template>
            <el-button v-else type="info" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="userStore.isAdmin" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination v-model:current-page="page" :page-size="pageSize" :total="total"
          layout="total, prev, pager, next" @current-change="loadReports" />
      </div>
      
      <!-- 举报详情对话框 -->
      <el-dialog v-model="detailVisible" title="举报详情" width="500px">
        <div v-if="currentReport" class="report-detail">
          <div class="detail-item">
            <label>文章标题：</label>
            <span>{{ currentReport.articleTitle }}</span>
          </div>
          <div class="detail-item">
            <label>举报人：</label>
            <span>{{ currentReport.nickname || currentReport.username }}</span>
          </div>
          <div class="detail-item">
            <label>举报原因：</label>
            <span>{{ currentReport.reason }}</span>
          </div>
          <div class="detail-item">
            <label>处理状态：</label>
            <el-tag :type="statusType(currentReport.status)">{{ currentReport.statusText }}</el-tag>
          </div>
          <div class="detail-item">
            <label>处理人：</label>
            <span>{{ currentReport.handlerNickname || '-' }}</span>
          </div>
          <div class="detail-item">
            <label>处理备注：</label>
            <span>{{ currentReport.handleComment || '无' }}</span>
          </div>
          <div class="detail-item">
            <label>举报时间：</label>
            <span>{{ formatDate(currentReport.createdAt) }}</span>
          </div>
          <div class="detail-item" v-if="currentReport.handleTime">
            <label>处理时间：</label>
            <span>{{ formatDate(currentReport.handleTime) }}</span>
          </div>
        </div>
      </el-dialog>
      
      <!-- 处理举报对话框 -->
      <el-dialog v-model="processVisible" :title="isReviewRequest ? '处理复核申请' : '处理举报'" width="550px">
        <div v-if="currentReport" class="process-info">
          <el-alert :type="isReviewRequest ? 'success' : 'info'" :closable="false" style="margin-bottom: 20px">
            <template #title>
              <div style="font-size: 14px">
                <strong>类型：</strong>{{ currentReport.reportTypeText || '过时举报' }}<br/>
                <strong>文章：</strong>{{ currentReport.articleTitle }}<br/>
                <strong>{{ isReviewRequest ? '申请人' : '举报人' }}：</strong>{{ currentReport.nickname || currentReport.username }}<br/>
                <strong>{{ isReviewRequest ? '更新说明' : '举报原因' }}：</strong>{{ currentReport.reason }}
              </div>
            </template>
          </el-alert>
        </div>
        
        <el-form :model="processForm" :rules="processRules" ref="processFormRef" label-width="100px">
          <!-- 如果是复核申请，显示通过/拒绝选项 -->
          <template v-if="isReviewRequest">
            <el-form-item label="处理结果" prop="status">
              <el-radio-group v-model="processForm.status">
                <el-radio :label="1">
                  <span style="font-weight: 500; color: #67c23a">通过复核</span>
                  <el-tooltip content="移除文章的过时标记，作者会收到通过通知" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399; cursor: help"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </el-radio>
                <el-radio :label="2">
                  <span style="font-weight: 500; color: #f56c6c">拒绝复核</span>
                  <el-tooltip content="保持文章的过时标记，作者会收到拒绝通知" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399; cursor: help"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="审核意见">
              <el-input 
                v-model="processForm.comment" 
                type="textarea" 
                :rows="3" 
                :placeholder="processForm.status === 1 ? '可选，说明通过原因' : '请说明拒绝原因'"
                maxlength="500" 
                show-word-limit 
              />
            </el-form-item>
          </template>
          
          <!-- 如果是过时举报，显示标记过时/删除文章选项 -->
          <template v-else>
            <el-form-item label="处理方式" prop="handleType">
              <el-radio-group v-model="processForm.handleType">
                <el-radio :label="1">
                  <span style="font-weight: 500">标记过时</span>
                  <el-tooltip content="将文章标记为过时，作者会收到过时通知，建议更新内容" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399; cursor: help"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </el-radio>
                <el-radio :label="2">
                  <span style="font-weight: 500; color: #f56c6c">删除文章</span>
                  <el-tooltip content="将文章下架，作者会收到违规删除通知，文章从列表中移除" placement="top">
                    <el-icon style="margin-left: 4px; color: #909399; cursor: help"><QuestionFilled /></el-icon>
                  </el-tooltip>
                </el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item :label="processForm.handleType === 1 ? '过时原因' : '删除原因'" prop="reason">
              <el-input 
                v-model="processForm.reason" 
                type="textarea" 
                :rows="3" 
                :placeholder="processForm.handleType === 1 ? '请输入过时原因，将显示在文章详情页' : '请输入删除原因，将发送给作者'"
                maxlength="500" 
                show-word-limit 
              />
            </el-form-item>
            
            <el-form-item label="处理备注">
              <el-input 
                v-model="processForm.comment" 
                type="textarea" 
                :rows="2" 
                placeholder="处理备注（可选，仅内部可见）" 
                maxlength="500" 
              />
            </el-form-item>
          </template>
        </el-form>
        
        <!-- 警告提示 -->
        <el-alert 
          v-if="!isReviewRequest && processForm.handleType === 2" 
          type="warning" 
          :closable="false"
          style="margin-top: 10px"
        >
          <template #title>
            <div style="font-size: 13px">
              <strong>⚠️ 警告：</strong>删除文章是不可逆操作，请确保举报内容确实违规。文章将被下架，作者会收到违规删除通知。
            </div>
          </template>
        </el-alert>
        
        <template #footer>
          <el-button @click="processVisible = false">取消</el-button>
          <el-button 
            v-if="isReviewRequest"
            :type="processForm.status === 1 ? 'success' : 'danger'" 
            @click="confirmProcess" 
            :loading="submitting"
          >
            {{ processForm.status === 1 ? '确认通过' : '确认拒绝' }}
          </el-button>
          <el-button 
            v-else
            :type="processForm.handleType === 1 ? 'warning' : 'danger'" 
            @click="confirmProcess" 
            :loading="submitting"
          >
            {{ processForm.handleType === 1 ? '确认' : '确认删除文章' }}
          </el-button>
        </template>
      </el-dialog>
      
      <!-- 标记过时对话框 -->
      <el-dialog v-model="markOutdatedVisible" title="标记为过时" width="500px">
        <el-form :model="markForm" :rules="markRules" ref="markFormRef">
          <el-form-item label="过时原因" prop="outdatedReason">
            <el-input v-model="markForm.outdatedReason" type="textarea" :rows="3" 
              placeholder="请输入过时原因，将显示在文章详情页" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="处理备注">
            <el-input v-model="markForm.comment" type="textarea" :rows="2" 
              placeholder="处理备注（可选）" maxlength="500" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="markOutdatedVisible = false">取消</el-button>
          <el-button type="warning" @click="confirmMarkOutdated" :loading="submitting">确认标记</el-button>
        </template>
      </el-dialog>
      
      <!-- 忽略举报对话框 -->
      <el-dialog v-model="ignoreVisible" title="忽略举报" width="450px">
        <el-form>
          <el-form-item label="忽略原因">
            <el-input v-model="ignoreComment" type="textarea" :rows="3" 
              placeholder="请输入忽略原因（可选）" maxlength="500" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="ignoreVisible = false">取消</el-button>
          <el-button type="info" @click="confirmIgnore" :loading="submitting">确认忽略</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup>
/**
 * 过时内容举报管理页面
 * 
 * 功能：
 * 1. 查看和筛选过时内容举报列表
 * 2. 处理举报（标记过时或删除文章）
 * 3. 忽略举报
 * 4. 查看举报详情
 * 5. 预览被举报的文章
 * 
 * 权限：仅管理员和审核员可访问
 */

import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled } from '@element-plus/icons-vue' // 问号图标，用于帮助提示
import AdminLayout from './AdminLayout.vue'
import { getOutdatedReports, handleReport, deleteOutdatedReport } from '@/api/outdated'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// ==================== 数据状态 ====================
const reports = ref([]) // 举报列表
const loading = ref(false) // 加载状态
const page = ref(1) // 当前页码
const pageSize = ref(10) // 每页条数
const total = ref(0) // 总记录数
const filterStatus = ref('') // 筛选状态：''=全部, 0=待处理, 1=已处理, 2=已忽略

// ==================== 对话框状态 ====================
const detailVisible = ref(false) // 详情对话框
const processVisible = ref(false) // 处理举报对话框（新增）
const markOutdatedVisible = ref(false) // 标记过时对话框（旧版，保留兼容）
const ignoreVisible = ref(false) // 忽略举报对话框
const currentReport = ref(null) // 当前操作的举报记录
const processFormRef = ref(null) // 处理表单引用
const markFormRef = ref(null) // 标记过时表单引用

// ==================== 处理举报表单（新增） ====================
/**
 * 处理举报表单数据
 * status: 1=已处理, 2=已忽略（复核申请时：1=通过，2=拒绝）
 * handleType: 1=标记过时（文章保留，显示过时警告）, 2=删除文章（文章下架）
 * reason: 处理原因（标记过时时显示在文章页，删除时发送给作者）
 * comment: 处理备注（仅内部可见）
 */
const processForm = ref({
  status: 1, // 默认已处理
  handleType: 1, // 默认标记过时
  reason: '', // 处理原因
  comment: '' // 处理备注
})

/**
 * 判断当前处理的是否是复核申请
 */
const isReviewRequest = computed(() => {
  return currentReport.value?.reportType === 1
})

/**
 * 处理表单验证规则（动态根据类型变化）
 */
const processRules = computed(() => {
  if (isReviewRequest.value) {
    // 复核申请只需要选择处理结果
    return {
      status: [{ required: true, message: '请选择处理结果', trigger: 'change' }]
    }
  } else {
    // 过时举报需要选择处理方式和填写原因
    return {
      handleType: [{ required: true, message: '请选择处理方式', trigger: 'change' }],
      reason: [
        { required: true, message: '请输入原因', trigger: 'blur' },
        { min: 5, message: '原因至少5个字', trigger: 'blur' }
      ]
    }
  }
})

// ==================== 标记过时表单（旧版） ====================
const markForm = ref({
  outdatedReason: '',
  comment: ''
})
const markRules = {
  outdatedReason: [
    { required: true, message: '请输入过时原因', trigger: 'blur' }
  ]
}

// ==================== 忽略举报 ====================
const ignoreComment = ref('') // 忽略原因
const submitting = ref(false) // 提交状态

// ==================== 工具函数 ====================
/**
 * 根据状态返回对应的标签类型
 * @param {number} s - 状态值：0=待处理, 1=已处理, 2=已忽略
 * @returns {string} Element Plus 标签类型
 */
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'info' }[s] || 'info')

/**
 * 格式化日期时间
 * @param {string} dateStr - ISO 日期字符串
 * @returns {string} 格式化后的日期时间字符串
 */
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
}

// ==================== 数据加载 ====================
/**
 * 加载举报列表
 * 支持分页和状态筛选
 */
const loadReports = async () => {
  loading.value = true
  try {
    const res = await getOutdatedReports({ 
      page: page.value, 
      size: pageSize.value, 
      status: filterStatus.value !== '' ? filterStatus.value : undefined 
    })
    reports.value = res.data.list || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error('加载举报列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 筛选状态变化时重置页码并重新加载
 */
const handleFilter = () => {
  page.value = 1
  loadReports()
}

// ==================== 查看操作 ====================
/**
 * 预览被举报的文章
 * @param {Object} row - 举报记录
 */
const previewArticle = (row) => {
  router.push(`/article/${row.articleId}`)
}

/**
 * 查看举报详情
 * @param {Object} row - 举报记录
 */
const viewDetail = (row) => {
  currentReport.value = row
  detailVisible.value = true
}

// ==================== 处理举报（新增统一入口） ====================
/**
 * 打开处理举报对话框
 * 提供两种处理方式：标记过时 或 删除文章
 * 
 * @param {Object} row - 举报记录
 */
const handleProcess = (row) => {
  currentReport.value = row
  
  // 根据举报类型初始化不同的表单
  if (row.reportType === 1) {
    // 复核申请：默认通过
    processForm.value = {
      status: 1,  // 默认通过
      handleType: null,
      reason: '',
      comment: ''
    }
  } else {
    // 过时举报：默认标记过时
    processForm.value = {
      status: 1,  // 已处理
      handleType: 1,  // 默认标记过时
      reason: row.reason,  // 默认使用举报原因
      comment: ''
    }
  }
  
  processVisible.value = true
}

/**
 * 确认处理举报
 * 
 * 处理流程：
 * 1. 表单验证
 * 2. 如果是删除文章，显示二次确认对话框
 * 3. 调用 API 处理举报
 * 4. 根据处理类型执行不同操作：
 *    - 过时举报：handleType=1 标记文章为过时，handleType=2 删除文章
 *    - 复核申请：status=1 通过复核（移除过时标记），status=2 拒绝复核
 * 5. 刷新列表
 */
const confirmProcess = async () => {
  if (!processFormRef.value) return
  
  await processFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    // 如果是删除文章，显示二次确认对话框
    if (!isReviewRequest.value && processForm.value.handleType === 2) {
      try {
        await ElMessageBox.confirm(
          '确定要删除该文章吗？此操作不可逆，文章将被下架。',
          '删除确认',
          {
            confirmButtonText: '确定删除',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
      } catch {
        return // 用户取消操作
      }
    }
    
    submitting.value = true
    try {
      // 构建请求数据
      const data = {
        status: processForm.value.status,
        comment: processForm.value.comment
      }
      
      // 只有过时举报需要 handleType 和 reason
      if (!isReviewRequest.value) {
        data.handleType = processForm.value.handleType
        data.reason = processForm.value.reason
      }
      
      // 调用 API 处理举报
      await handleReport(currentReport.value.id, data)
      
      // 根据类型和处理结果显示不同的成功提示
      let successMsg = ''
      if (isReviewRequest.value) {
        successMsg = processForm.value.status === 1 ? '已通过复核' : '已拒绝复核'
      } else {
        successMsg = processForm.value.handleType === 1 ? '已标记为过时' : '文章已删除'
      }
      
      ElMessage.success(successMsg)
      processVisible.value = false
      loadReports() // 刷新列表
    } catch (e) {
      ElMessage.error(e.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// ==================== 标记过时（旧版方法，保留兼容） ====================
/**
 * 打开标记过时对话框（旧版）
 * @param {Object} row - 举报记录
 */
const handleMarkOutdated = (row) => {
  currentReport.value = row
  markForm.value = {
    outdatedReason: row.reason, // 默认使用举报原因
    comment: ''
  }
  markOutdatedVisible.value = true
}

/**
 * 确认标记为过时（旧版）
 */
const confirmMarkOutdated = async () => {
  if (!markFormRef.value) return
  
  await markFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await handleReport(currentReport.value.id, {
        status: 1, // 已处理
        handleType: 1, // 标记过时
        reason: markForm.value.outdatedReason,
        comment: markForm.value.comment
      })
      ElMessage.success('已标记为过时')
      markOutdatedVisible.value = false
      loadReports()
    } catch (e) {
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

// ==================== 忽略举报 ====================
/**
 * 打开忽略举报对话框
 * @param {Object} row - 举报记录
 */
const handleIgnore = (row) => {
  currentReport.value = row
  ignoreComment.value = ''
  ignoreVisible.value = true
}

/**
 * 确认忽略举报
 * 将举报状态设为"已忽略"，不对文章做任何处理
 */
const confirmIgnore = async () => {
  submitting.value = true
  try {
    await handleReport(currentReport.value.id, {
      status: 2, // 已忽略
      comment: ignoreComment.value
    })
    ElMessage.success('已忽略该举报')
    ignoreVisible.value = false
    loadReports()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

// ==================== 删除举报记录 ====================
/**
 * 删除举报记录（仅管理员）
 * 注意：这是删除举报记录本身，不是删除文章
 * 
 * @param {Object} row - 举报记录
 */
const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该举报记录吗？此操作不可恢复。', '删除确认', { type: 'warning' })
  try {
    await deleteOutdatedReport(row.id)
    ElMessage.success('删除成功')
    loadReports()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

// ==================== 生命周期 ====================
onMounted(() => {
  loadReports() // 页面加载时获取举报列表
})
</script>

<style scoped>
.outdated-manage {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.filter-box {
  display: flex;
  gap: 12px;
}

.report-table {
  margin-bottom: 20px;
}

.article-info {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.cover-img {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  flex-shrink: 0;
}

.no-cover {
  width: 80px;
  height: 60px;
  background: #f5f5f5;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
  font-size: 12px;
  flex-shrink: 0;
}

.article-detail {
  flex: 1;
  min-width: 0;
}

.article-title {
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 8px;
}

.outdated-tag {
  flex-shrink: 0;
}

.article-meta {
  font-size: 12px;
  color: #999;
}

.article-meta .summary {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
  max-width: 200px;
  vertical-align: bottom;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 13px;
  color: #666;
}

.reason-text {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 13px;
  color: #666;
}

.text-muted {
  color: #ccc;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

.report-detail .detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.report-detail .detail-item label {
  width: 80px;
  color: #666;
  flex-shrink: 0;
}

.report-detail .detail-item span {
  color: #333;
  flex: 1;
  word-break: break-all;
}

/* 处理举报对话框样式 */
.process-info {
  margin-bottom: 20px;
}

:deep(.el-radio) {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  height: auto;
  white-space: normal;
}

:deep(.el-radio__label) {
  display: flex;
  align-items: center;
  white-space: normal;
}
</style>
