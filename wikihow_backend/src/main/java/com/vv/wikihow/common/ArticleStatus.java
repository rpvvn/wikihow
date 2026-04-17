package com.vv.wikihow.common;

/**
 * 文章状态常量
 */
public class ArticleStatus {
    
    /**
     * 草稿
     */
    public static final int DRAFT = 0;
    
    /**
     * 待审核
     */
    public static final int PENDING_REVIEW = 1;
    
    /**
     * 已发布
     */
    public static final int PUBLISHED = 2;
    
    /**
     * 已拒绝
     */
    public static final int REJECTED = 3;
    
    /**
     * 已下架
     */
    public static final int OFFLINE = 4;
    
    /**
     * 获取状态描述
     */
    public static String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case DRAFT: return "草稿";
            case PENDING_REVIEW: return "待审核";
            case PUBLISHED: return "已发布";
            case REJECTED: return "已拒绝";
            case OFFLINE: return "已下架";
            default: return "未知";
        }
    }
}
