package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.*;
import com.vv.wikihow.entity.*;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.*;
import com.vv.wikihow.service.ArticleService;
import com.vv.wikihow.service.ArticleVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final ArticleStepMapper articleStepMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final LikeMapper likeMapper;
    private final FavoriteMapper favoriteMapper;
    private final ArticleVersionService articleVersionService;
    private final ArticleReviewMapper articleReviewMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageResponse<ArticleListResponse> getArticles(Integer page, Integer size, Long categoryId, String keyword) {
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .in(Article::getStatus, 1, 2) // 显示已发布的文章（兼容旧状态1和新状态2）
                .like(keyword != null && !keyword.isEmpty(), Article::getTitle, keyword)
                .orderByDesc(Article::getCreatedAt);

        // 如果指定了分类ID，需要查询该分类及其所有子分类的文章
        if (categoryId != null) {
            List<Long> categoryIds = getCategoryAndChildrenIds(categoryId);
            wrapper.in(Article::getCategoryId, categoryIds);
        }

        Page<Article> result = articleMapper.selectPage(pageParam, wrapper);
        List<ArticleListResponse> list = convertToListResponse(result.getRecords());
        
        return PageResponse.of(list, result.getTotal(), page, size);
    }
    
    /**
     * 获取指定分类及其所有子分类的ID列表
     */
    private List<Long> getCategoryAndChildrenIds(Long categoryId) {
        List<Long> ids = new ArrayList<>();
        ids.add(categoryId);
        
        // 查询所有子分类
        List<Category> children = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().eq(Category::getParentId, categoryId));
        for (Category child : children) {
            ids.add(child.getId());
        }
        
        return ids;
    }

    @Override
    public ArticleResponse getArticleById(Long id, Long currentUserId) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        // 只有已发布的文章可以公开查看（兼容旧状态1和新状态2），或者是作者本人可以查看自己的文章
        boolean isPublished = article.getStatus() == 1 || article.getStatus() == 2;
        if (!isPublished && (currentUserId == null || !article.getUserId().equals(currentUserId))) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        User author = userMapper.selectById(article.getUserId());
        Category category = categoryMapper.selectById(article.getCategoryId());
        List<ArticleStep> steps = articleStepMapper.selectList(
                new LambdaQueryWrapper<ArticleStep>()
                        .eq(ArticleStep::getArticleId, id)
                        .orderByAsc(ArticleStep::getStepOrder));

        ArticleResponse response = ArticleResponse.fromArticle(article, author, category, steps);
        
        // 设置当前用户的点赞和收藏状态
        if (currentUserId != null) {
            response.setIsLiked(isLiked(id, currentUserId));
            response.setIsFavorited(isFavorited(id, currentUserId));
        } else {
            response.setIsLiked(false);
            response.setIsFavorited(false);
        }

        return response;
    }

    @Override
    @Transactional
    public Long createArticle(ArticleRequest request, Long userId) {
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setUserId(userId);
        article.setCategoryId(request.getCategoryId());
        article.setTags(request.getTags() != null ? String.join(",", request.getTags()) : null);
        article.setDifficulty(request.getDifficulty());
        // 新文章默认为草稿状态，需要提交审核后才能发布
        article.setStatus(0);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setFavoriteCount(0);
        article.setCommentCount(0);
        
        // 保存参考文献（JSON格式）
        if (request.getReferences() != null && !request.getReferences().isEmpty()) {
            try {
                article.setReferences(objectMapper.writeValueAsString(request.getReferences()));
            } catch (Exception e) {
                // 忽略序列化错误
            }
        }

        articleMapper.insert(article);

        // 保存步骤
        if (request.getSteps() != null) {
            saveSteps(article.getId(), request.getSteps());
        }

        return article.getId();
    }

    @Override
    @Transactional
    public void updateArticle(Long id, ArticleRequest request, Long userId, boolean isAdmin) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        // 管理员或文章作者可以修改
        if (!isAdmin && !article.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权修改此文章");
        }

        // 在更新前保存当前版本
        try {
            String currentStepsJson = getCurrentStepsJson(id);
            articleVersionService.saveVersion(article, currentStepsJson, userId, "更新前自动保存");
        } catch (Exception e) {
            log.warn("保存文章版本失败: {}", e.getMessage());
        }

        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setCategoryId(request.getCategoryId());
        article.setTags(request.getTags() != null ? String.join(",", request.getTags()) : null);
        article.setDifficulty(request.getDifficulty());
        
        // 更新参考文献
        if (request.getReferences() != null && !request.getReferences().isEmpty()) {
            try {
                article.setReferences(objectMapper.writeValueAsString(request.getReferences()));
            } catch (Exception e) {
                // 忽略序列化错误
            }
        } else {
            article.setReferences(null);
        }

        articleMapper.updateById(article);

        // 更新步骤：先删除旧的，再插入新的
        articleStepMapper.delete(new LambdaQueryWrapper<ArticleStep>()
                .eq(ArticleStep::getArticleId, id));
        if (request.getSteps() != null) {
            saveSteps(id, request.getSteps());
        }
    }

    /**
     * 获取当前文章步骤的JSON字符串
     */
    private String getCurrentStepsJson(Long articleId) {
        List<ArticleStep> steps = articleStepMapper.selectList(
                new LambdaQueryWrapper<ArticleStep>()
                        .eq(ArticleStep::getArticleId, articleId)
                        .orderByAsc(ArticleStep::getStepOrder));

        List<ArticleRequest.StepRequest> stepRequests = steps.stream()
                .map(step -> {
                    ArticleRequest.StepRequest req = new ArticleRequest.StepRequest();
                    req.setOrder(step.getStepOrder());
                    req.setTitle(step.getTitle());
                    req.setContent(step.getContent());
                    req.setImage(step.getImage());
                    return req;
                })
                .collect(Collectors.toList());

        try {
            return objectMapper.writeValueAsString(stepRequests);
        } catch (JsonProcessingException e) {
            log.error("序列化步骤JSON失败", e);
            return "[]";
        }
    }

    @Override
    @Transactional
    public void deleteArticle(Long id, Long userId, boolean isAdmin) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        if (!isAdmin && !article.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此文章");
        }

        // 删除文章
        articleMapper.deleteById(id);
        // 删除文章步骤
        articleStepMapper.delete(new LambdaQueryWrapper<ArticleStep>()
                .eq(ArticleStep::getArticleId, id));
        // 删除相关的审核记录
        articleReviewMapper.delete(new LambdaQueryWrapper<ArticleReview>()
                .eq(ArticleReview::getArticleId, id));
        
        log.info("文章已删除, articleId={}, 同时删除了相关审核记录", id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public PageResponse<ArticleListResponse> getUserArticles(Long userId, Integer page, Integer size) {
        Page<Article> pageParam = new Page<>(page, size);
        Page<Article> result = articleMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getUserId, userId)
                        .orderByDesc(Article::getCreatedAt));

        List<ArticleListResponse> list = convertToListResponse(result.getRecords());
        return PageResponse.of(list, result.getTotal(), page, size);
    }

    private void saveSteps(Long articleId, List<ArticleRequest.StepRequest> steps) {
        for (int i = 0; i < steps.size(); i++) {
            ArticleRequest.StepRequest stepReq = steps.get(i);
            ArticleStep step = new ArticleStep();
            step.setArticleId(articleId);
            step.setStepOrder(stepReq.getOrder() != null ? stepReq.getOrder() : i + 1);
            step.setTitle(stepReq.getTitle());
            step.setContent(stepReq.getContent());
            step.setImage(stepReq.getImage());
            articleStepMapper.insert(step);
        }
    }

    private List<ArticleListResponse> convertToListResponse(List<Article> articles) {
        if (articles.isEmpty()) return new ArrayList<>();

        List<Long> userIds = articles.stream().map(Article::getUserId).distinct().toList();
        List<Long> categoryIds = articles.stream().map(Article::getCategoryId).distinct().toList();

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Category> categoryMap = categoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        return articles.stream()
                .map(a -> ArticleListResponse.fromArticle(a, userMap.get(a.getUserId()), categoryMap.get(a.getCategoryId())))
                .toList();
    }

    private boolean isLiked(Long articleId, Long userId) {
        return likeMapper.selectCount(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getArticleId, articleId)
                .eq(LikeRecord::getUserId, userId)) > 0;
    }

    private boolean isFavorited(Long articleId, Long userId) {
        return favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getArticleId, articleId)
                .eq(Favorite::getUserId, userId)) > 0;
    }

    @Override
    public List<ArticleListResponse> getRandomArticles(Long excludeArticleId, Integer count) {
        // 获取所有已发布的文章ID（排除当前文章）- 兼容旧状态1和新状态2
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .in(Article::getStatus, 1, 2)
                .ne(excludeArticleId != null, Article::getId, excludeArticleId)
                .select(Article::getId);
        
        List<Article> allArticles = articleMapper.selectList(wrapper);
        
        if (allArticles.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 随机选择指定数量的文章
        java.util.Collections.shuffle(allArticles);
        List<Long> randomIds = allArticles.stream()
                .limit(count)
                .map(Article::getId)
                .toList();
        
        // 获取完整的文章信息
        List<Article> randomArticles = articleMapper.selectBatchIds(randomIds);
        return convertToListResponse(randomArticles);
    }
}
