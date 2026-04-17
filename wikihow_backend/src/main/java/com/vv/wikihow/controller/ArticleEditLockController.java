package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.EditLockResponse;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.ArticleEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文章编辑锁控制器
 * 用于编辑冲突调解
 */
@Slf4j
@RestController
@RequestMapping("/api/articles/{articleId}")
@RequiredArgsConstructor
public class ArticleEditLockController {

    private final ArticleEditLockService articleEditLockService;

    /**
     * 获取编辑锁
     * POST /api/articles/:id/lock
     */
    @PostMapping("/lock")
    public Result<EditLockResponse> acquireLock(@PathVariable Long articleId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        EditLockResponse response = articleEditLockService.acquireLock(articleId, userId);
        return Result.success(response);
    }

    /**
     * 释放编辑锁
     * DELETE /api/articles/:id/lock
     */
    @DeleteMapping("/lock")
    public Result<Void> releaseLock(@PathVariable Long articleId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        articleEditLockService.releaseLock(articleId, userId);
        return Result.success("编辑锁已释放", null);
    }

    /**
     * 检查锁状态
     * GET /api/articles/:id/lock-status
     */
    @GetMapping("/lock-status")
    public Result<EditLockResponse> getLockStatus(@PathVariable Long articleId) {
        EditLockResponse response = articleEditLockService.getLockStatus(articleId);
        return Result.success(response);
    }

    /**
     * 续期编辑锁
     * PUT /api/articles/:id/lock/renew
     */
    @PutMapping("/lock/renew")
    public Result<EditLockResponse> renewLock(@PathVariable Long articleId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        EditLockResponse response = articleEditLockService.renewLock(articleId, userId);
        return Result.success(response);
    }
}
