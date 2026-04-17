package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vv.wikihow.dto.NotificationResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.Notification;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.NotificationMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通知服务实现
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    @Override
    public void sendWelcomeNotification(Long userId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(null); // 系统通知
        notification.setTitle("欢迎加入 WikiHow 知识库！");
        notification.setContent("感谢您注册成为我们的用户！在这里，您可以浏览各类实用教程，也可以分享您的知识和经验。快来发布您的第一篇文章吧！");
        notification.setType("WELCOME");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    @Transactional
    public void sendAdminNotification(Long senderId, String title, String content) {
        // 获取所有正常状态的用户
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 1));

        // 为每个用户创建通知
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUserId(user.getId());
            notification.setSenderId(senderId);
            notification.setTitle(title);
            notification.setContent(content);
            notification.setType("ADMIN");
            notification.setIsRead(0);
            notificationMapper.insert(notification);
        }
    }

    @Override
    public PageResponse<NotificationResponse> getUserNotifications(Long userId, Integer page, Integer size, Boolean unreadOnly) {
        Page<Notification> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(unreadOnly != null && unreadOnly, Notification::getIsRead, 0)
                .orderByDesc(Notification::getCreatedAt);

        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);

        // 获取发送者信息
        List<Long> senderIds = result.getRecords().stream()
                .map(Notification::getSenderId)
                .filter(id -> id != null)
                .distinct()
                .toList();

        Map<Long, User> senderMap = senderIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(senderIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        List<NotificationResponse> list = result.getRecords().stream()
                .map(n -> {
                    User sender = n.getSenderId() != null ? senderMap.get(n.getSenderId()) : null;
                    return NotificationResponse.fromEntity(n, sender);
                })
                .toList();

        return PageResponse.of(list, result.getTotal(), page, size);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationMapper.selectCount(
                new LambdaQueryWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0));
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在");
        }
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.update(null,
                new LambdaUpdateWrapper<Notification>()
                        .eq(Notification::getUserId, userId)
                        .eq(Notification::getIsRead, 0)
                        .set(Notification::getIsRead, 1));
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在");
        }
        notificationMapper.deleteById(notificationId);
    }

    @Override
    public void sendReviewResultNotification(Long userId, String articleTitle, boolean approved, String comment) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(null); // 系统通知
        
        if (approved) {
            notification.setTitle("文章审核通过");
            notification.setContent("恭喜！您的文章《" + articleTitle + "》已通过审核并发布。" + 
                    (comment != null && !comment.isEmpty() ? " 审核意见：" + comment : ""));
        } else {
            notification.setTitle("文章审核未通过");
            notification.setContent("很抱歉，您的文章《" + articleTitle + "》未通过审核。" + 
                    (comment != null && !comment.isEmpty() ? " 原因：" + comment : " 请修改后重新提交。"));
        }
        
        notification.setType("REVIEW");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public void sendOutdatedNotification(Long userId, String articleTitle, String reason) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(null); // 系统通知
        notification.setTitle("文章被标记为过时");
        notification.setContent("您的文章《" + articleTitle + "》已被标记为过时内容。" + 
                (reason != null && !reason.isEmpty() ? " 原因：" + reason : "") + 
                " 建议您更新文章内容以保持信息的准确性。");
        notification.setType("OUTDATED");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public void sendArticleOfflineNotification(Long userId, String articleTitle, String reason) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setSenderId(null); // 系统通知
        notification.setTitle("文章已被下架");
        notification.setContent("您的文章《" + articleTitle + "》已被管理员下架。" + 
                (reason != null && !reason.isEmpty() ? " 原因：" + reason : " 如有疑问，请联系管理员。"));
        notification.setType("OFFLINE");
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public void sendCommentNotification(Long authorId, Long commenterId, String articleTitle, Long articleId, Long commentId, String commentContent) {
        // 获取评论者信息
        User commenter = userMapper.selectById(commenterId);
        String commenterName = commenter != null ? commenter.getNickname() : "某用户";
        
        // 截取评论内容（最多50字）
        String shortContent = commentContent.length() > 50 
                ? commentContent.substring(0, 50) + "..." 
                : commentContent;

        Notification notification = new Notification();
        notification.setUserId(authorId);
        notification.setSenderId(commenterId);
        notification.setTitle("您的文章收到了新评论");
        notification.setContent(commenterName + " 评论了您的文章《" + articleTitle + "》：" + shortContent);
        notification.setType("COMMENT");
        notification.setArticleId(articleId);
        notification.setCommentId(commentId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }

    @Override
    public void sendReplyNotification(Long commentAuthorId, Long replierId, String articleTitle, Long articleId, Long replyId, String replyContent) {
        // 获取回复者信息
        User replier = userMapper.selectById(replierId);
        String replierName = replier != null ? replier.getNickname() : "某用户";
        
        // 截取回复内容（最多50字）
        String shortContent = replyContent.length() > 50 
                ? replyContent.substring(0, 50) + "..." 
                : replyContent;

        Notification notification = new Notification();
        notification.setUserId(commentAuthorId);
        notification.setSenderId(replierId);
        notification.setTitle("您的评论收到了新回复");
        notification.setContent(replierName + " 在文章《" + articleTitle + "》中回复了您：" + shortContent);
        notification.setType("REPLY");
        notification.setArticleId(articleId);
        notification.setCommentId(replyId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
    }
}
