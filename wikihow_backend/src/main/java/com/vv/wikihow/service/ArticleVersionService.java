package com.vv.wikihow.service;

import com.vv.wikihow.dto.ArticleVersionResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.Article;

import java.util.List;

/**
 * 文章版本服务接口
 */
public interface ArticleVersionService {

    /**
     * 保存文章版本
     * @param article 文章实体
     * @param stepsJson 步骤JSON字符串
     * @param userId 操作用户ID
     * @param changeDescription 修改说明
     */
    void saveVersion(Article article, String stepsJson, Long userId, String changeDescription);

    /**
     * 获取文章版本列表
     * @param articleId 文章ID
     * @param page 页码
     * @param size 每页数量
     * @return 版本列表
     */
    PageResponse<ArticleVersionResponse> getVersions(Long articleId, Integer page, Integer size);

    /**
     * 获取指定版本详情
     * @param articleId 文章ID
     * @param versionId 版本ID
     * @return 版本详情
     */
    ArticleVersionResponse getVersionById(Long articleId, Long versionId);

    /**
     * 回滚到指定版本
     * @param articleId 文章ID
     * @param versionId 版本ID
     * @param userId 操作用户ID
     * @param isAdmin 是否管理员
     */
    void revertToVersion(Long articleId, Long versionId, Long userId, boolean isAdmin);

    /**
     * 获取文章的最新版本号
     * @param articleId 文章ID
     * @return 最新版本号
     */
    Integer getLatestVersionNumber(Long articleId);
}
