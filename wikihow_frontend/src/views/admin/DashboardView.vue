<!--
  管理后台仪表板页面
  
  功能说明：
  1. 展示系统核心统计数据（用户数、文章数、下架文章数、过时文章数）
  2. 显示数据可视化图表（文章状态分布、发布趋势、用户注册趋势、分类统计）
  3. 提供快捷操作入口（用户管理、文章管理、分类管理、过时举报管理）
  4. 展示最近发布的文章列表
  
  权限要求：仅管理员可访问
-->
<template>
  <AdminLayout>
    <div class="dashboard">
      <!-- 统计卡片区域：展示系统核心数据概览 -->
      <div class="stat-cards">
        <!-- 用户总数统计卡片 -->
        <div class="stat-card">
          <div class="stat-icon users"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.userCount || 0 }}</span>
            <span class="stat-label">用户总数</span>
          </div>
        </div>
        
        <!-- 文章总数统计卡片 -->
        <div class="stat-card">
          <div class="stat-icon articles"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.articleCount || 0 }}</span>
            <span class="stat-label">文章总数</span>
          </div>
        </div>
        
        <!-- 已下架文章统计卡片 -->
        <div class="stat-card">
          <div class="stat-icon offline"><el-icon><WarningFilled /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.offlineCount || 0 }}</span>
            <span class="stat-label">已下架文章</span>
          </div>
        </div>
        
        <!-- 过时文章统计卡片 -->
        <div class="stat-card">
          <div class="stat-icon outdated"><el-icon><Clock /></el-icon></div>
          <div class="stat-info">
            <span class="stat-value">{{ stats.outdatedCount || 0 }}</span>
            <span class="stat-label">过时文章</span>
          </div>
        </div>
      </div>

      <!-- 数据可视化图表区域 -->
      <div class="charts-section">
        <!-- 文章状态分布饼图：展示不同状态文章的占比 -->
        <div class="chart-card">
          <h3>文章状态分布</h3>
          <div class="chart-container">
            <v-chart 
              v-if="pieChartOption" 
              :option="pieChartOption" 
              style="height: 300px;"
            />
            <div v-else class="chart-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
          </div>
        </div>

        <!-- 文章发布趋势曲线图：展示最近7天的文章发布数量变化 -->
        <div class="chart-card">
          <h3>最近7天文章发布趋势</h3>
          <div class="chart-container">
            <v-chart 
              v-if="articleTrendOption" 
              :option="articleTrendOption" 
              style="height: 300px;"
            />
            <div v-else class="chart-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 第二行图表：用户和分类相关统计 -->
      <div class="charts-section">
        <!-- 用户注册趋势曲线图：展示最近7天的用户注册数量变化 -->
        <div class="chart-card">
          <h3>最近7天用户注册趋势</h3>
          <div class="chart-container">
            <v-chart 
              v-if="userTrendOption" 
              :option="userTrendOption" 
              style="height: 300px;"
            />
            <div v-else class="chart-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
          </div>
        </div>

        <!-- 分类文章数量柱状图：展示各分类下的文章数量分布 -->
        <div class="chart-card">
          <h3>分类文章数量统计</h3>
          <div class="chart-container">
            <v-chart 
              v-if="categoryChartOption" 
              :option="categoryChartOption" 
              style="height: 300px;"
            />
            <div v-else class="chart-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
              <span>加载中...</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 快捷操作区域：提供常用管理功能的快速入口 -->
      <div class="quick-actions">
        <h3>快捷操作</h3>
        <div class="action-buttons">
          <!-- 用户管理入口 -->
          <el-button type="primary" @click="$router.push('/admin/users')">
            <el-icon><User /></el-icon> 管理用户
          </el-button>
          <!-- 文章管理入口 -->
          <el-button type="success" @click="$router.push('/admin/articles')">
            <el-icon><Document /></el-icon> 管理文章
          </el-button>
          <!-- 分类管理入口 -->
          <el-button type="warning" @click="$router.push('/admin/categories')">
            <el-icon><Menu /></el-icon> 管理分类
          </el-button>
          <!-- 过时举报管理入口 -->
          <el-button type="danger" @click="$router.push('/admin/outdated-reports')">
            <el-icon><WarningFilled /></el-icon> 过时举报
          </el-button>
          <!-- 发布文章入口 -->
          <el-button @click="$router.push('/editor')">
            <el-icon><Edit /></el-icon> 发布文章
          </el-button>
        </div>
      </div>
      
      <!-- 最近文章列表：展示系统中最新发布的文章 -->
      <div class="recent-section">
        <h3>最近发布的文章</h3>
        <el-table :data="recentArticles" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column label="作者" width="120">
            <template #default="{ row }">{{ row.author?.nickname }}</template>
          </el-table-column>
          <el-table-column label="分类" width="120">
            <template #default="{ row }">{{ row.category?.name }}</template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="primary" link @click="$router.push('/article/' + row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </AdminLayout>
</template>

/**
 * 管理后台仪表板页面组件
 * 
 * 功能说明：
 * 1. 展示系统核心统计数据概览（用户数、文章数、下架文章数、过时文章数）
 * 2. 提供数据可视化图表展示（饼图、趋势图、柱状图）
 * 3. 提供快捷操作入口（用户管理、文章管理、分类管理、过时举报管理）
 * 4. 展示最近发布的文章列表
 * 
 * 权限要求：仅管理员可访问
 * 
 * 数据来源：
 * - 基础统计数据：/admin/stats API
 * - 详细图表数据：/admin/dashboard-stats API
 * - 最近文章：/api/articles API
 */

<script setup>
import { ref, onMounted, computed } from 'vue'
import { User, Document, Menu, Edit, WarningFilled, Clock, Loading } from '@element-plus/icons-vue'
import AdminLayout from './AdminLayout.vue'
import { getArticles } from '@/api/article'
import { getCategories } from '@/api/category'
import request from '@/api/index'

// ECharts 相关导入和配置
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  PieChart,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// ==================== 响应式数据定义 ====================

/** 
 * 系统基础统计数据
 * @type {Object}
 * @property {number} userCount - 用户总数
 * @property {number} articleCount - 文章总数
 * @property {number} categoryCount - 分类数量
 * @property {number} commentCount - 评论总数
 * @property {number} offlineCount - 已下架文章数
 * @property {number} outdatedCount - 过时文章数
 */
const stats = ref({
  userCount: 0,
  articleCount: 0,
  categoryCount: 0,
  commentCount: 0,
  offlineCount: 0,
  outdatedCount: 0
})

/** 
 * 最近发布的文章列表
 * @type {Array} 包含文章基本信息的数组
 */
const recentArticles = ref([])

/** 
 * 仪表板详细统计数据（用于图表展示）
 * @type {Object}
 * @property {Array} articleStatusChart - 文章状态分布数据
 * @property {Array} articleTrendChart - 文章发布趋势数据
 * @property {Array} userTrendChart - 用户注册趋势数据
 * @property {Array} categoryChart - 分类文章数量数据
 */
const dashboardStats = ref(null)

// ==================== 计算属性（图表配置） ====================

/**
 * 文章状态分布饼图配置
 * 
 * 功能说明：
 * 1. 展示不同状态文章的数量和占比
 * 2. 包括已发布、待审核、已拒绝、已下架等状态
 * 3. 过滤掉数量为0的状态，避免图表混乱
 * 
 * @returns {Object|null} ECharts 饼图配置对象
 */
const pieChartOption = computed(() => {
  if (!dashboardStats.value?.articleStatusChart) return null
  
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '文章状态',
        type: 'pie',
        radius: '50%',
        data: dashboardStats.value.articleStatusChart.filter(item => item.value > 0),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

/**
 * 文章发布趋势曲线图配置
 * 
 * 功能说明：
 * 1. 展示最近7天的文章发布数量变化
 * 2. 使用平滑曲线展示趋势
 * 3. 帮助管理员了解内容发布活跃度
 * 
 * @returns {Object|null} ECharts 折线图配置对象
 */
const articleTrendOption = computed(() => {
  if (!dashboardStats.value?.articleTrendChart) return null
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dashboardStats.value.articleTrendChart.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '发布文章数',
        type: 'line',
        stack: 'Total',
        data: dashboardStats.value.articleTrendChart.map(item => item.value),
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        }
      }
    ]
  }
})

/**
 * 用户注册趋势曲线图配置
 * 
 * 功能说明：
 * 1. 展示最近7天的用户注册数量变化
 * 2. 使用平滑曲线展示趋势
 * 3. 帮助管理员了解用户增长情况
 * 
 * @returns {Object|null} ECharts 折线图配置对象
 */
const userTrendOption = computed(() => {
  if (!dashboardStats.value?.userTrendChart) return null
  
  return {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dashboardStats.value.userTrendChart.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '注册用户数',
        type: 'line',
        stack: 'Total',
        data: dashboardStats.value.userTrendChart.map(item => item.value),
        smooth: true,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
})

/**
 * 分类文章数量柱状图配置
 * 
 * 功能说明：
 * 1. 展示各分类下的文章数量分布
 * 2. 使用柱状图直观对比各分类的内容丰富程度
 * 3. 帮助管理员了解内容分布情况
 * 
 * @returns {Object|null} ECharts 柱状图配置对象
 */
const categoryChartOption = computed(() => {
  if (!dashboardStats.value?.categoryChart) return null
  
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dashboardStats.value.categoryChart.map(item => item.name),
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '文章数量',
        type: 'bar',
        barWidth: '60%',
        data: dashboardStats.value.categoryChart.map(item => item.value),
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
})

// ==================== 业务方法 ====================

/**
 * 加载系统基础统计数据
 * 
 * 流程说明：
 * 1. 优先尝试调用专用的统计接口 /admin/stats
 * 2. 如果接口不存在，则通过现有接口计算统计数据
 * 3. 更新 stats 响应式数据
 * 
 * 降级策略：
 * - 如果统计接口不可用，通过文章列表和分类列表接口获取部分数据
 * - 其他数据保持默认值0，不影响页面正常显示
 */
const loadStats = async () => {
  try {
    // 尝试调用专用统计接口
    const res = await request.get('/admin/stats')
    stats.value = res.data
  } catch (e) {
    // 降级方案：通过现有接口计算统计数据
    console.warn('统计接口不可用，使用降级方案:', e)
    try {
      const articlesRes = await getArticles({ page: 1, size: 1 })
      const categoriesRes = await getCategories()
      
      // 更新可获取的统计数据
      stats.value.articleCount = articlesRes.data.total || 0
      stats.value.categoryCount = categoriesRes.data?.length || 0
      // 其他数据保持默认值 0
    } catch (fallbackError) {
      console.error('加载统计数据失败:', fallbackError)
    }
  }
}

/**
 * 加载仪表板详细统计数据（用于图表展示）
 * 
 * 功能说明：
 * 1. 获取图表所需的详细数据
 * 2. 包括文章状态分布、发布趋势、用户注册趋势、分类统计等
 * 3. 同时更新基础统计数据
 * 
 * 数据结构：
 * - articleStatusChart: [{ name: '已发布', value: 100 }, ...]
 * - articleTrendChart: [{ date: '2024-01-01', value: 5 }, ...]
 * - userTrendChart: [{ date: '2024-01-01', value: 3 }, ...]
 * - categoryChart: [{ name: '技术', value: 50 }, ...]
 */
const loadDashboardStats = async () => {
  try {
    const res = await request.get('/admin/dashboard-stats')
    dashboardStats.value = res.data
    
    // 更新基础统计数据（如果详细接口返回了更完整的数据）
    stats.value = {
      ...stats.value,
      ...res.data
    }
  } catch (error) {
    console.error('加载详细统计数据失败:', error)
    // 图表数据加载失败不影响基础功能，仅在控制台记录错误
  }
}

/**
 * 加载最近发布的文章列表
 * 
 * 功能说明：
 * 1. 获取最新发布的 5 篇文章
 * 2. 包含文章基本信息、作者信息、分类信息
 * 3. 用于在仪表板中快速查看最新内容
 * 4. 提供快速跳转到文章详情的功能
 */
const loadRecentArticles = async () => {
  try {
    const res = await getArticles({ page: 1, size: 5 })
    recentArticles.value = res.data.list || []
  } catch (error) {
    console.error('加载最近文章失败:', error)
    recentArticles.value = []
  }
}

// ==================== 生命周期钩子 ====================

/**
 * 组件挂载时执行的初始化操作
 * 
 * 执行顺序：
 * 1. 加载系统基础统计数据
 * 2. 加载仪表板详细统计数据（用于图表）
 * 3. 加载最近文章列表
 * 
 * 注意：各个数据加载操作相互独立，单个失败不影响其他功能
 */
onMounted(() => {
  loadStats()
  loadDashboardStats()
  loadRecentArticles()
})
</script>

<style scoped>
/* ==================== 整体布局样式 ==================== */

/* 仪表板主容器：垂直布局，各区域间距24px */
.dashboard { 
  display: flex; 
  flex-direction: column; 
  gap: 24px; 
}

/* ==================== 统计卡片样式 ==================== */

/* 统计卡片网格布局：4列等宽，间距20px */
.stat-cards { 
  display: grid; 
  grid-template-columns: repeat(4, 1fr); 
  gap: 20px; 
}

/* 单个统计卡片样式 */
.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

/* 统计图标样式 */
.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

/* 各类统计图标的渐变背景色 */
.stat-icon.users { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.stat-icon.articles { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.stat-icon.categories { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.stat-icon.comments { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
.stat-icon.offline { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.stat-icon.outdated { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }

/* 统计信息文字样式 */
.stat-info { display: flex; flex-direction: column; }
.stat-value { font-size: 28px; font-weight: 600; color: #333; }
.stat-label { font-size: 14px; color: #999; }

/* ==================== 图表区域样式 ==================== */

/* 图表区域网格布局：2列等宽，间距20px */
.charts-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* 单个图表卡片样式 */
.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
}

/* 图表标题样式 */
.chart-card h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

/* 图表容器样式 */
.chart-container {
  position: relative;
}

/* 图表加载状态样式 */
.chart-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
  color: #999;
  gap: 8px;
}

.chart-loading .el-icon {
  font-size: 24px;
}

/* ==================== 快捷操作和最近文章样式 ==================== */

/* 快捷操作和最近文章区域的通用样式 */
.quick-actions, .recent-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  width: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

/* 区域标题样式 */
.quick-actions h3, .recent-section h3 {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

/* 快捷操作按钮容器：支持换行 */
.action-buttons { 
  display: flex; 
  gap: 12px; 
  flex-wrap: wrap;
}

/* ==================== 响应式布局 ==================== */

/* 中等屏幕适配：统计卡片改为2列，图表改为单列 */
@media (max-width: 1200px) {
  .stat-cards { grid-template-columns: repeat(2, 1fr); }
  .charts-section { grid-template-columns: 1fr; }
}

/* 小屏幕适配（屏幕宽度 ≤ 768px）：统计卡片改为单列，按钮改为垂直布局
 * 修改说明（2026-04-25）：
 * 1. 为容器和按钮添加 width: 100% 确保元素占满父容器宽度
 * 2. 添加 box-sizing: border-box 防止内边距和边框导致宽度溢出
 * 3. 修复移动端按钮布局问题，避免横向滚动条出现
 */
@media (max-width: 768px) {
  /* 统计卡片：改为单列布局 */
  .stat-cards { 
    grid-template-columns: 1fr; 
  }
  
  /* 图表区域：改为单列布局 */
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  /* 图表卡片：减小内边距，防止内容溢出 */
  .chart-card {
    padding: 16px;
    overflow: hidden;
  }
  
  /* 图表容器：限制宽度，防止溢出 */
  .chart-container {
    width: 100%;
    max-width: 100%;
    overflow: hidden;
  }
  
  /* 快捷操作和最近文章区域：确保容器宽度正确
   * width: 100% - 占满父容器宽度
   * box-sizing: border-box - 将内边距包含在宽度内，防止溢出
   */
  .quick-actions, .recent-section {
    padding: 16px;
    overflow: hidden;
    width: 100%;              /* 占满父容器宽度 */
    box-sizing: border-box;   /* 包含内边距在宽度内 */
  }
  
  /* 按钮容器：改为垂直布局并占满宽度
   * flex-direction: column - 按钮垂直排列
   * width: 100% - 占满父容器宽度
   */
  .action-buttons { 
    flex-direction: column;
    width: 100%;              /* 占满父容器宽度 */
  }
  
  /* 按钮元素：确保宽度和盒模型正确
   * width: 100% - 按钮占满容器宽度
   * box-sizing: border-box - 将内边距和边框包含在宽度内
   * justify-content: center - 按钮内容居中显示
   */
  .action-buttons .el-button { 
    width: 100%;
    max-width: 100%;
    justify-content: center;
    box-sizing: border-box;   /* 包含内边距和边框在宽度内 */
  }
  
  /* Element Plus 按钮深层样式：确保内部元素也占满宽度
   * 使用 :deep() 穿透 scoped 样式，修改 Element Plus 组件内部样式
   * width: 100% - 按钮内部元素占满宽度
   * box-sizing: border-box - 将内边距和边框包含在宽度内
   */
  .action-buttons :deep(.el-button) {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;              /* 占满父容器宽度 */
    box-sizing: border-box;   /* 包含内边距和边框在宽度内 */
  }
  
  /* 按钮图标：添加右边距，与文字保持间距 */
  .action-buttons :deep(.el-icon) {
    margin-right: 8px;
  }
}

@media (max-width: 480px) {
  .stat-card {
    padding: 16px;
  }
  
  .stat-value {
    font-size: 24px;
  }
  
  .chart-card {
    padding: 12px;
  }
  
  .chart-card h3 {
    font-size: 14px;
  }
  
  .quick-actions, .recent-section {
    padding: 12px;
  }
  
  .quick-actions h3, .recent-section h3 {
    font-size: 14px;
  }
}
</style>
