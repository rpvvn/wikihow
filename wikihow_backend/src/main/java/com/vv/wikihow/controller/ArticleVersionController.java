package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.ArticleVersionResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.ArticleVersionService;
import com.vv.wikihow.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 文章版本控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/articles/{articleId}/versions")
@RequiredArgsConstructor
public class ArticleVersionController {

    private final ArticleVersionService articleVersionService;
    private final UserService userService;
    private final ArticleMapper articleMapper;

    /**
     * 检查用户是否有权限访问版本历史（管理员或文章作者）
     */
    private void checkVersionAccessPermission(Long articleId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        
        User user = userService.getUserById(userId);
        boolean isAdmin = "ADMIN".equals(user.getRole());
        boolean isAuthor = article.getUserId().equals(userId);
        
        if (!isAdmin && !isAuthor) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员或文章作者可以查看版本历史");
        }
    }

    /**
     * 获取文章版本历史（仅管理员或文章作者）
     */
    @GetMapping
    public Result<PageResponse<ArticleVersionResponse>> getVersions(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        checkVersionAccessPermission(articleId);
        PageResponse<ArticleVersionResponse> result = articleVersionService.getVersions(articleId, page, size);
        return Result.success(result);
    }

    /**
     * 获取指定版本详情（仅管理员或文章作者）
     */
    @GetMapping("/{versionId}")
    public Result<ArticleVersionResponse> getVersionById(
            @PathVariable Long articleId,
            @PathVariable Long versionId) {
        checkVersionAccessPermission(articleId);
        ArticleVersionResponse version = articleVersionService.getVersionById(articleId, versionId);
        return Result.success(version);
    }

    /**
     * 回滚到指定版本（仅作者或管理员）
     */
    @PostMapping("/{versionId}/revert")
    public Result<Void> revertToVersion(
            @PathVariable Long articleId,
            @PathVariable Long versionId) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        
        User user = userService.getUserById(userId);
        boolean isAdmin = "ADMIN".equals(user.getRole());
        boolean isAuthor = article.getUserId().equals(userId);
        
        if (!isAdmin && !isAuthor) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员或文章作者可以回滚版本");
        }
        
        articleVersionService.revertToVersion(articleId, versionId, userId, isAdmin);
        return Result.success("回滚成功", null);
    }
}
