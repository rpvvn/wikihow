package com.vv.wikihow.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 过时内容举报响应 DTO
 */
@Data
public class OutdatedReportResponse {
    
    /**
     * 举报记录ID
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
     * 文章是否已标记过时
     */
    private Boolean articleIsOutdated;
    
    /**
     * 举报用户ID
     */
    private Long userId;
    
    /**
     * 举报用户名
     */
    private String username;
    
    /**
     * 举报用户昵称
     */
    private String nickname;
    
    /**
     * 举报用户头像
     */
    private String userAvatar;
    
    /**
     * 举报原因
     */
    private String reason;
    
    /**
     * 处理状态: 0待处理/1已处理/2已忽略
     */
    private Integer status;
    
    /**
     * 处理状态文本
     */
    private String statusText;
    
    /**
     * 处理人ID
     */
    private Long handlerId;
    
    /**
     * 处理人用户名
     */
    private String handlerUsername;
    
    /**
     * 处理人昵称
     */
    private String handlerNickname;
    
    /**
     * 处理备注
     */
    private String handleComment;
    
    /**
     * 处理时间
     */
    private LocalDateTime handleTime;
    
    /**
     * 举报时间
     */
    private LocalDateTime createdAt;
}
