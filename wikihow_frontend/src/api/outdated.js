import request from './index'

/**
 * 用户举报过时内容
 * @param {number} articleId 文章ID
 * @param {string} reason 举报原因
 */
export function reportOutdated(articleId, reason) {
  return request.post(`/articles/${articleId}/report-outdated`, { reason })
}

/**
 * 检查用户是否已举报过该文章
 * @param {number} articleId 文章ID
 */
export function checkReportStatus(articleId) {
  return request.get(`/articles/${articleId}/report-status`)
}

/**
 * 管理员标记文章为过时
 * @param {number} articleId 文章ID
 * @param {string} reason 过时原因
 */
export function markOutdated(articleId, reason) {
  return request.put(`/admin/articles/${articleId}/mark-outdated`, { reason })
}

/**
 * 管理员取消过时标记
 * @param {number} articleId 文章ID
 */
export function unmarkOutdated(articleId) {
  return request.put(`/admin/articles/${articleId}/unmark-outdated`)
}

/**
 * 获取过时举报列表（审核员/管理员）
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页数量
 * @param {number} params.status 处理状态（可选）: 0待处理/1已处理/2已忽略
 */
export function getOutdatedReports(params) {
  return request.get('/admin/outdated-reports', { params })
}

/**
 * 获取举报详情
 * @param {number} reportId 举报记录ID
 */
export function getReportById(reportId) {
  return request.get(`/admin/outdated-reports/${reportId}`)
}

/**
 * 处理举报
 * @param {number} reportId 举报记录ID
 * @param {Object} data 处理数据
 * @param {number} data.status 处理结果: 1已处理/2已忽略
 * @param {number} data.handleType 处理类型: 1标记过时/2删除文章（当status=1时需要）
 * @param {string} data.reason 过时原因或删除原因（当status=1时需要）
 * @param {string} data.comment 处理备注
 */
export function handleReport(reportId, data) {
  return request.put(`/admin/outdated-reports/${reportId}/handle`, data)
}

/**
 * 删除举报记录（仅管理员）
 * @param {number} reportId 举报记录ID
 */
export function deleteOutdatedReport(reportId) {
  return request.delete(`/admin/outdated-reports/${reportId}`)
}

/**
 * 作者申请复核（移除过时标记）
 * @param {number} articleId 文章ID
 * @param {string} reason 申请原因
 */
export function requestReview(articleId, reason) {
  return request.post(`/articles/${articleId}/request-review`, { reason })
}

/**
 * 检查文章是否有待处理的复核申请
 * @param {number} articleId 文章ID
 */
export function checkReviewStatus(articleId) {
  return request.get(`/articles/${articleId}/review-status`)
}
