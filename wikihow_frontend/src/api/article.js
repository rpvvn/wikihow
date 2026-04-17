import request from './index'

/**
 * 获取文章列表
 */
export function getArticles(params) {
  return request.get('/articles', { params })
}

/**
 * 获取文章详情
 */
export function getArticleById(id) {
  return request.get(`/articles/${id}`)
}

/**
 * 创建文章
 */
export function createArticle(data) {
  return request.post('/articles', data)
}

/**
 * 更新文章
 */
export function updateArticle(id, data) {
  return request.put(`/articles/${id}`, data)
}

/**
 * 删除文章
 */
export function deleteArticle(id) {
  return request.delete(`/articles/${id}`)
}

/**
 * 获取用户的文章列表
 */
export function getUserArticles(userId, params) {
  return request.get(`/articles/user/${userId}`, { params })
}

/**
 * 上传文件
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 获取随机推荐文章
 */
export function getRandomArticles(excludeId, count = 4) {
  return request.get('/articles/random', {
    params: { excludeId, count }
  })
}
