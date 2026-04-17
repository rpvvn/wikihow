import request from './index'

/**
 * 获取编辑锁
 * @param {number} articleId 文章ID
 */
export function acquireLock(articleId) {
  return request.post(`/articles/${articleId}/lock`)
}

/**
 * 释放编辑锁
 * @param {number} articleId 文章ID
 */
export function releaseLock(articleId) {
  return request.delete(`/articles/${articleId}/lock`)
}

/**
 * 检查锁状态
 * @param {number} articleId 文章ID
 */
export function getLockStatus(articleId) {
  return request.get(`/articles/${articleId}/lock-status`)
}

/**
 * 续期编辑锁
 * @param {number} articleId 文章ID
 */
export function renewLock(articleId) {
  return request.put(`/articles/${articleId}/lock/renew`)
}
