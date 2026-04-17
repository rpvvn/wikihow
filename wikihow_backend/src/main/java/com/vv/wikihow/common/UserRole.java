package com.vv.wikihow.common;

/**
 * 用户角色常量
 */
public class UserRole {
    
    /**
     * 普通用户
     */
    public static final String USER = "USER";
    
    /**
     * 审核员
     */
    public static final String REVIEWER = "REVIEWER";
    
    /**
     * 管理员
     */
    public static final String ADMIN = "ADMIN";
    
    /**
     * 判断是否为审核员或管理员
     */
    public static boolean canReview(String role) {
        return REVIEWER.equals(role) || ADMIN.equals(role);
    }
    
    /**
     * 判断是否为管理员
     */
    public static boolean isAdmin(String role) {
        return ADMIN.equals(role);
    }
}
