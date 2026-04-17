import request from './index'

/**
 * 获取文章版本历史
 * @param {number} articleId 文章ID
 * @param {object} params 分页参数 { page, size }
 */
export function getArticleVersions(articleId, params) {
  return request.get(`/articles/${articleId}/versions`, { params })
}

/**
 * 获取指定版本详情
 * @param {number} articleId 文章ID
 * @param {number} versionId 版本ID
 */
export function getVersionById(articleId, versionId) {
  return request.get(`/articles/${articleId}/versions/${versionId}`)
}

/**
 * 回滚到指定版本
 * @param {number} articleId 文章ID
 * @param {number} versionId 版本ID
 */
export function revertToVersion(articleId, versionId) {
  return request.post(`/articles/${articleId}/versions/${versionId}/revert`)
}
