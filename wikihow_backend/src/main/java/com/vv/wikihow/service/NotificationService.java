package com.vv.wikihow.service;

import com.vv.wikihow.dto.NotificationResponse;
import com.vv.wikihow.dto.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 发送欢迎通知给新用户
     */
    void sendWelcomeNotification(Long userId);

    /**
     * 管理员发送全站通知
     */
    void sendAdminNotification(Long senderId, String title, String content);

    /**
     * 获取用户通知列表（按日期分组）
     */
    PageResponse<NotificationResponse> getUserNotifications(Long userId, Integer page, Integer size, Boolean unreadOnly);

    /**
     * 获取用户未读通知数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 发送审核结果通知
     * @param userId 接收用户ID
     * @param articleTitle 文章标题
     * @param approved 是否通过
     * @param comment 审核意见
     */
    void sendReviewResultNotification(Long userId, String articleTitle, boolean approved, String comment);

    /**
     * 发送过时内容通知
     * @param userId 接收用户ID（文章作者）
     * @param articleTitle 文章标题
     * @param reason 过时原因
     */
    void sendOutdatedNotification(Long userId, String articleTitle, String reason);

    /**
     * 发送文章下架通知
     * @param userId 接收用户ID（文章作者）
     * @param articleTitle 文章标题
     * @param reason 下架原因
     */
    void sendArticleOfflineNotification(Long userId, String articleTitle, String reason);

    /**
     * 发送评论通知（当用户的文章被评论时）
     * @param authorId 文章作者ID（接收通知）
     * @param commenterId 评论者ID
     * @param articleTitle 文章标题
     * @param articleId 文章ID
     * @param commentId 评论ID
     * @param commentContent 评论内容
     */
    void sendCommentNotification(Long authorId, Long commenterId, String articleTitle, Long articleId, Long commentId, String commentContent);

    /**
     * 发送回复通知（当用户的评论被回复时）
     * @param commentAuthorId 被回复评论的作者ID（接收通知）
     * @param replierId 回复者ID
     * @param articleTitle 文章标题
     * @param articleId 文章ID
     * @param replyId 回复评论ID
     * @param replyContent 回复内容
     */
    void sendReplyNotification(Long commentAuthorId, Long replierId, String articleTitle, Long articleId, Long replyId, String replyContent);
}
