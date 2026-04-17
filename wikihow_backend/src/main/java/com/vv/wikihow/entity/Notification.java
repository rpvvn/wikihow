package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统通知实体
 */
@Data
@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收用户ID */
    private Long userId;

    /** 发送者ID(系统通知为NULL或管理员ID) */
    private Long senderId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 通知类型: SYSTEM/WELCOME/ADMIN/COMMENT/REPLY */
    private String type;

    /** 关联文章ID（评论通知使用） */
    private Long articleId;

    /** 关联评论ID（评论通知使用） */
    private Long commentId;

    /** 是否已读: 0未读/1已读 */
    @TableField("is_read")
    private Integer isRead;

    private LocalDateTime createdAt;
}
