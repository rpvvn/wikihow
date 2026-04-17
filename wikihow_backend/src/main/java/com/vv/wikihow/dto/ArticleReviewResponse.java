package com.vv.wikihow.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章审核记录响应 DTO
 */
@Data
public class ArticleReviewResponse {
    
    /**
     * 审核记录ID
     */
    private Long id;
    
    /**
     * 文章ID
     */
    private Long articleId;
    
    /**
     * 文章标题
     */
    private String articleTitle;
    
    /**
     * 文章摘要
     */
    private String articleSummary;
    
    /**
     * 文章封面图
     */
    private String articleCoverImage;
    
    /**
     * 作者ID
     */
    private Long authorId;
    
    /**
     * 作者用户名
     */
    private String authorUsername;
    
    /**
     * 作者昵称
     */
    private String authorNickname;
    
    /**
     * 作者头像
     */
    private String authorAvatar;
    
    /**
     * 审核员ID
     */
    private Long reviewerId;
    
    /**
     * 审核员用户名
     */
    private String reviewerUsername;
    
    /**
     * 审核员昵称
     */
    private String reviewerNickname;
    
    /**
     * 审核状态: 1待审核/2通过/3拒绝
     */
    private Integer status;
    
    /**
     * 审核状态文本
     */
    private String statusText;
    
    /**
     * 审核意见
     */
    private String comment;
    
    /**
     * 提交审核时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 分类名称
     */
    private String categoryName;
}
