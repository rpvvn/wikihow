package com.vv.wikihow.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.ArticleStep;
import com.vv.wikihow.entity.Category;
import com.vv.wikihow.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 文章详情响应
 */
@Data
public class ArticleResponse {

    private Long id;
    private String title;
    private String summary;
    private String coverImage;
    private AuthorInfo author;
    private CategoryInfo category;
    private List<String> tags;
    private String difficulty;
    private List<StepInfo> steps;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Integer commentCount;
    private Boolean isLiked;
    private Boolean isFavorited;
    private Integer status;  // 文章状态：0草稿/1待审核/2已发布/3已拒绝/4已下架
    private Boolean isOutdated;  // 是否过时
    private String outdatedReason;  // 过时原因
    private List<ReferenceInfo> references;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class AuthorInfo {
        private Long id;
        private String nickname;
        private String avatar;

        public static AuthorInfo fromUser(User user) {
            AuthorInfo info = new AuthorInfo();
            info.setId(user.getId());
            info.setNickname(user.getNickname());
            info.setAvatar(user.getAvatar());
            return info;
        }
    }

    @Data
    public static class CategoryInfo {
        private Long id;
        private String name;

        public static CategoryInfo fromCategory(Category category) {
            CategoryInfo info = new CategoryInfo();
            info.setId(category.getId());
            info.setName(category.getName());
            return info;
        }
    }

    @Data
    public static class StepInfo {
        private Long id;
        private Integer order;
        private String title;
        private String content;
        private String image;

        public static StepInfo fromStep(ArticleStep step) {
            StepInfo info = new StepInfo();
            info.setId(step.getId());
            info.setOrder(step.getStepOrder());
            info.setTitle(step.getTitle());
            info.setContent(step.getContent());
            info.setImage(step.getImage());
            return info;
        }
    }

    @Data
    public static class ReferenceInfo {
        private String title;
        private String url;
        private String author;
        private String publishDate;
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ArticleResponse fromArticle(Article article, User author, Category category, List<ArticleStep> steps) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setSummary(article.getSummary());
        response.setCoverImage(article.getCoverImage());
        response.setAuthor(AuthorInfo.fromUser(author));
        response.setCategory(CategoryInfo.fromCategory(category));
        
        // 解析标签
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            response.setTags(Arrays.asList(article.getTags().split(",")));
        }
        
        response.setDifficulty(article.getDifficulty());
        response.setSteps(steps.stream().map(StepInfo::fromStep).toList());
        response.setViewCount(article.getViewCount());
        response.setLikeCount(article.getLikeCount());
        response.setFavoriteCount(article.getFavoriteCount());
        response.setCommentCount(article.getCommentCount());
        response.setStatus(article.getStatus());  // 设置文章状态
        response.setIsOutdated(article.getIsOutdated() != null && article.getIsOutdated() == 1);  // 设置过时标记
        response.setOutdatedReason(article.getOutdatedReason());  // 设置过时原因
        
        // 解析参考文献
        if (article.getReferences() != null && !article.getReferences().isEmpty()) {
            try {
                List<ReferenceInfo> refs = objectMapper.readValue(
                    article.getReferences(), 
                    new TypeReference<List<ReferenceInfo>>() {}
                );
                response.setReferences(refs);
            } catch (Exception e) {
                response.setReferences(Collections.emptyList());
            }
        } else {
            response.setReferences(Collections.emptyList());
        }
        
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());
        
        return response;
    }
}
