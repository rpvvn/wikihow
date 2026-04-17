package com.vv.wikihow.service;

import com.vv.wikihow.dto.*;

/**
 * 文章服务接口
 */
public interface ArticleService {

    /**
     * 分页查询文章列表
     */
    PageResponse<ArticleListResponse> getArticles(Integer page, Integer size, Long categoryId, String keyword);

    /**
     * 获取文章详情
     */
    ArticleResponse getArticleById(Long id, Long currentUserId);

    /**
     * 创建文章
     */
    Long createArticle(ArticleRequest request, Long userId);

    /**
     * 更新文章（管理员或文章作者可修改）
     */
    void updateArticle(Long id, ArticleRequest request, Long userId, boolean isAdmin);

    /**
     * 删除文章
     */
    void deleteArticle(Long id, Long userId, boolean isAdmin);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 获取用户的文章列表
     */
    PageResponse<ArticleListResponse> getUserArticles(Long userId, Integer page, Integer size);

    /**
     * 获取随机推荐文章（排除指定文章）
     */
    java.util.List<ArticleListResponse> getRandomArticles(Long excludeArticleId, Integer count);
}
