import request from './index'

/**
 * 获取通知列表
 */
export function getNotifications(params) {
  return request.get('/notifications', { params })
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return request.get('/notifications/unread-count')
}

/**
 * 标记单个通知为已读
 */
export function markAsRead(id) {
  return request.put(`/notifications/${id}/read`)
}

/**
 * 标记所有通知为已读
 */
export function markAllAsRead() {
  return request.put('/notifications/read-all')
}

/**
 * 删除通知
 */
export function deleteNotification(id) {
  return request.delete(`/notifications/${id}`)
}

/**
 * 管理员发送全站通知
 */
export function broadcastNotification(data) {
  return request.post('/notifications/broadcast', data)
}
