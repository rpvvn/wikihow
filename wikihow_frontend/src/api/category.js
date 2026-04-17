import request from './index'

/**
 * 获取分类列表
 */
export function getCategories() {
  return request.get('/categories')
}

/**
 * 获取分类列表（带文章数量）
 */
export function getCategoriesWithCount() {
  return request.get('/categories/with-count')
}

/**
 * 获取单个分类
 */
export function getCategoryById(id) {
  return request.get(`/categories/${id}`)
}

/**
 * 创建分类（管理员）
 */
export function createCategory(data) {
  return request.post('/categories', data)
}

/**
 * 更新分类（管理员）
 */
export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

/**
 * 删除分类（管理员）
 */
export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}
