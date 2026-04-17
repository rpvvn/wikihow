import request from './index'

/**
 * 点赞/取消点赞
 */
export function toggleLike(articleId) {
  return request.post('/likes', { articleId })
}

/**
 * 收藏/取消收藏
 */
export function toggleFavorite(articleId) {
  return request.post('/favorites', { articleId })
}

/**
 * 获取文章评论（树形结构）
 */
export function getComments(articleId, params) {
  return request.get(`/articles/${articleId}/comments`, { params })
}

/**
 * 发表评论
 */
export function createComment(data) {
  return request.post('/comments', data)
}

/**
 * 回复评论
 * @param {Object} data - { articleId, content, parentId }
 */
export function replyComment(data) {
  return request.post('/comments', data)
}

/**
 * 删除评论
 */
export function deleteComment(id) {
  return request.delete(`/comments/${id}`)
}

/**
 * 获取用户收藏列表
 */
export function getUserFavorites(userId, params) {
  return request.get(`/users/${userId}/favorites`, { params })
}
