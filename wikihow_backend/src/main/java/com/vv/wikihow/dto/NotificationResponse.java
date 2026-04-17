package com.vv.wikihow.dto;

import com.vv.wikihow.entity.Notification;
import com.vv.wikihow.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知响应DTO
 */
@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private LocalDateTime createdAt;
    
    // 发送者信息
    private Long senderId;
    private String senderNickname;
    private String senderAvatar;
    
    // 关联信息（评论通知使用）
    private Long articleId;
    private Long commentId;

    public static NotificationResponse fromEntity(Notification notification, User sender) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setType(notification.getType());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt());
        response.setArticleId(notification.getArticleId());
        response.setCommentId(notification.getCommentId());
        
        if (sender != null) {
            response.setSenderId(sender.getId());
            response.setSenderNickname(sender.getNickname());
            response.setSenderAvatar(sender.getAvatar());
        } else {
            // 系统通知
            response.setSenderNickname("系统");
        }
        
        return response;
    }
}
