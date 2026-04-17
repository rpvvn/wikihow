import request from './index'

/**
 * 用户登录
 */
export function login(data) {
  return request.post('/auth/login', data)
}

/**
 * 用户注册
 */
export function register(data) {
  return request.post('/auth/register', data)
}

/**
 * 获取当前用户信息
 */
export function getCurrentUser() {
  return request.get('/users/me')
}

/**
 * 获取用户信息
 */
export function getUserById(id) {
  return request.get(`/users/${id}`)
}

/**
 * 更新用户信息
 */
export function updateUser(id, data) {
  return request.put(`/users/${id}`, data)
}

/**
 * 获取用户统计数据
 */
export function getUserStats(id) {
  return request.get(`/users/${id}/stats`)
}

/**
 * 修改密码
 */
export function changePassword(data) {
  return request.put('/users/password', data)
}
