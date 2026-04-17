package com.vv.wikihow.service;

import com.vv.wikihow.dto.EditLockResponse;

/**
 * 文章编辑锁服务接口
 * 用于编辑冲突调解，防止多人同时编辑同一篇文章
 */
public interface ArticleEditLockService {

    /**
     * 获取编辑锁
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 锁状态响应
     */
    EditLockResponse acquireLock(Long articleId, Long userId);

    /**
     * 释放编辑锁
     * @param articleId 文章ID
     * @param userId 用户ID
     */
    void releaseLock(Long articleId, Long userId);

    /**
     * 检查锁状态
     * @param articleId 文章ID
     * @return 锁状态响应
     */
    EditLockResponse getLockStatus(Long articleId);

    /**
     * 续期编辑锁
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 锁状态响应
     */
    EditLockResponse renewLock(Long articleId, Long userId);

    /**
     * 清理过期锁
     * @return 清理的锁数量
     */
    int cleanExpiredLocks();
}
