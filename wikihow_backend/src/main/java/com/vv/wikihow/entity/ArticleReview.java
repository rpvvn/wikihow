package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章审核记录实体
 */
@Data
@TableName("article_review")
public class ArticleReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 审核员ID
     */
    private Long reviewerId;

    /**
     * 审核状态: 1待审核/2通过/3拒绝
     */
    private Integer status;

    /**
     * 审核意见
     */
    private String comment;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    // 审核状态常量
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_APPROVED = 2;
    public static final int STATUS_REJECTED = 3;
}
