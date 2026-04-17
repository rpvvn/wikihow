import request from './index'

/**
 * 提交文章审核
 * @param {number} articleId 文章ID
 */
export function submitReview(articleId) {
  return request.post(`/articles/${articleId}/submit-review`)
}

/**
 * 获取待审核文章列表（审核员/管理员）
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页数量
 * @param {number} params.status 审核状态（可选）
 */
export function getPendingReviews(params) {
  return request.get('/admin/reviews', { params })
}

/**
 * 获取审核记录详情
 * @param {number} reviewId 审核记录ID
 */
export function getReviewById(reviewId) {
  return request.get(`/admin/reviews/${reviewId}`)
}

/**
 * 审核通过
 * @param {number} reviewId 审核记录ID
 * @param {string} comment 审核意见（可选）
 */
export function approveReview(reviewId, comment = '') {
  return request.put(`/admin/reviews/${reviewId}/approve`, { comment })
}

/**
 * 审核拒绝
 * @param {number} reviewId 审核记录ID
 * @param {string} comment 拒绝原因（必填）
 */
export function rejectReview(reviewId, comment) {
  return request.put(`/admin/reviews/${reviewId}/reject`, { comment })
}

/**
 * 获取文章的审核历史
 * @param {number} articleId 文章ID
 */
export function getArticleReviewHistory(articleId) {
  return request.get(`/articles/${articleId}/review-history`)
}

/**
 * 删除审核记录（仅管理员）
 * @param {number} reviewId 审核记录ID
 */
export function deleteReview(reviewId) {
  return request.delete(`/admin/reviews/${reviewId}`)
}
