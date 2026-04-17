package com.vv.wikihow.dto;

import lombok.Data;

/**
 * 用户统计数据响应
 */
@Data
public class UserStatsResponse {
    
    /**
     * 文章总数
     */
    private Integer articleCount;
    
    /**
     * 获得的总点赞数
     */
    private Integer totalLikes;
    
    /**
     * 获得的总收藏数
     */
    private Integer totalFavorites;
    
    /**
     * 获得的总浏览量
     */
    private Integer totalViews;
    
    /**
     * 收藏的文章数
     */
    private Integer favoriteCount;
}
