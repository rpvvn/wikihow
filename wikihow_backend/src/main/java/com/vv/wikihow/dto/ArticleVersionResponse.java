package com.vv.wikihow.dto;

import com.vv.wikihow.entity.ArticleVersion;
import com.vv.wikihow.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章版本响应DTO
 */
@Data
public class ArticleVersionResponse {

    private Long id;

    private Long articleId;

    private Integer versionNumber;

    private String title;

    private String summary;

    private String coverImage;

    private Long categoryId;

    private String categoryName;

    private String tags;

    private String difficulty;

    private String references;

    /**
     * 步骤列表（从contentSnapshot解析）
     */
    private List<ArticleRequest.StepRequest> steps;

    /**
     * 修改人ID
     */
    private Long userId;

    /**
     * 修改人用户名
     */
    private String username;

    /**
     * 修改人头像
     */
    private String userAvatar;

    /**
     * 修改说明
     */
    private String changeDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 从实体转换为响应DTO
     */
    public static ArticleVersionResponse fromEntity(ArticleVersion version, User user) {
        ArticleVersionResponse response = new ArticleVersionResponse();
        response.setId(version.getId());
        response.setArticleId(version.getArticleId());
        response.setVersionNumber(version.getVersionNumber());
        response.setTitle(version.getTitle());
        response.setSummary(version.getSummary());
        response.setCoverImage(version.getCoverImage());
        response.setCategoryId(version.getCategoryId());
        response.setTags(version.getTags());
        response.setDifficulty(version.getDifficulty());
        response.setReferences(version.getReferences());
        response.setUserId(version.getUserId());
        response.setChangeDescription(version.getChangeDescription());
        response.setCreatedAt(version.getCreatedAt());

        if (user != null) {
            response.setUsername(user.getUsername());
            response.setUserAvatar(user.getAvatar());
        }

        return response;
    }
}
