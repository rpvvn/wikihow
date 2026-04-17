package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.*;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.ArticleService;
import com.vv.wikihow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 文章控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    /**
     * 获取文章列表
     */
    @GetMapping
    public Result<PageResponse<ArticleListResponse>> getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        PageResponse<ArticleListResponse> result = articleService.getArticles(page, size, categoryId, keyword);
        return Result.success(result);
    }

    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public Result<ArticleResponse> getArticleById(@PathVariable Long id) {
        Long currentUserId = UserContext.getCurrentUserId();
        ArticleResponse article = articleService.getArticleById(id, currentUserId);
        // 增加浏览量
        articleService.incrementViewCount(id);
        return Result.success(article);
    }

    /**
     * 创建文章
     */
    @PostMapping
    public Result<Map<String, Long>> createArticle(@Valid @RequestBody ArticleRequest request) {
        Long userId = UserContext.getCurrentUserId();
        log.info("创建文章, userId={}, request={}", userId, request);
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        Long articleId = articleService.createArticle(request, userId);
        return Result.success("创建成功", Map.of("id", articleId));
    }

    /**
     * 更新文章（管理员或文章作者可修改）
     */
    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequest request) {
        Long userId = UserContext.getCurrentUserId();
        User user = userService.getUserById(userId);
        boolean isAdmin = "ADMIN".equals(user.getRole());
        articleService.updateArticle(id, request, userId, isAdmin);
        return Result.success("更新成功", null);
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        User user = userService.getUserById(userId);
        boolean isAdmin = "ADMIN".equals(user.getRole());
        articleService.deleteArticle(id, userId, isAdmin);
        return Result.success("删除成功", null);
    }

    /**
     * 获取用户的文章列表
     */
    @GetMapping("/user/{userId}")
    public Result<PageResponse<ArticleListResponse>> getUserArticles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResponse<ArticleListResponse> result = articleService.getUserArticles(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取随机推荐文章
     */
    @GetMapping("/random")
    public Result<java.util.List<ArticleListResponse>> getRandomArticles(
            @RequestParam(required = false) Long excludeId,
            @RequestParam(defaultValue = "4") Integer count) {
        java.util.List<ArticleListResponse> result = articleService.getRandomArticles(excludeId, count);
        return Result.success(result);
    }
}
