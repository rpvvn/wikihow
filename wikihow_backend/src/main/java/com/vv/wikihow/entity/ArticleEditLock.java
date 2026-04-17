package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章编辑锁实体
 * 用于编辑冲突调解，防止多人同时编辑同一篇文章
 */
@Data
@TableName("article_edit_lock")
public class ArticleEditLock {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 锁定用户ID
     */
    private Long userId;

    /**
     * 锁定时间
     */
    private LocalDateTime lockedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;
}
