package com.vv.wikihow.dto;

import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.Category;
import com.vv.wikihow.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章列表项响应
 */
@Data
public class ArticleListResponse {

    private Long id;
    private String title;
    private String summary;
    private String coverImage;
    private ArticleResponse.AuthorInfo author;
    private ArticleResponse.CategoryInfo category;
    private String difficulty;
    private Integer viewCount;
    private Integer likeCount;
    private Integer status;  // 文章状态: 0草稿/1待审核/2已发布/3已拒绝/4已下架
    private Integer isOutdated;  // 是否过时: 0否/1是
    private String outdatedReason;  // 过时原因
    private LocalDateTime createdAt;

    public static ArticleListResponse fromArticle(Article article, User author, Category category) {
        ArticleListResponse response = new ArticleListResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setSummary(article.getSummary());
        response.setCoverImage(article.getCoverImage());
        response.setAuthor(ArticleResponse.AuthorInfo.fromUser(author));
        response.setCategory(ArticleResponse.CategoryInfo.fromCategory(category));
        response.setDifficulty(article.getDifficulty());
        response.setViewCount(article.getViewCount());
        response.setLikeCount(article.getLikeCount());
        response.setStatus(article.getStatus());
        response.setIsOutdated(article.getIsOutdated());
        response.setOutdatedReason(article.getOutdatedReason());
        response.setCreatedAt(article.getCreatedAt());
        return response;
    }
}
