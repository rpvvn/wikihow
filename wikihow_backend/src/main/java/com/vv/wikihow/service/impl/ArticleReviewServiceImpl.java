package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vv.wikihow.common.ArticleStatus;
import com.vv.wikihow.dto.ArticleReviewResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.ArticleReview;
import com.vv.wikihow.entity.Category;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.ArticleReviewMapper;
import com.vv.wikihow.mapper.CategoryMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.ArticleReviewService;
import com.vv.wikihow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章审核服务实现
 */
@Service
@RequiredArgsConstructor
public class ArticleReviewServiceImpl implements ArticleReviewService {

    private final ArticleReviewMapper articleReviewMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void submitReview(Long articleId, Long userId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 检查是否为文章作者
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException("只能提交自己的文章审核");
        }
        
        // 检查文章状态（只有草稿或已拒绝的文章可以提交审核）
        if (article.getStatus() != ArticleStatus.DRAFT && article.getStatus() != ArticleStatus.REJECTED) {
            throw new BusinessException("当前文章状态不允许提交审核");
        }
        
        // 更新文章状态为待审核
        article.setStatus(ArticleStatus.PENDING_REVIEW);
        articleMapper.updateById(article);
        
        // 创建审核记录
        ArticleReview review = new ArticleReview();
        review.setArticleId(articleId);
        review.setStatus(ArticleReview.STATUS_PENDING);
        articleReviewMapper.insert(review);
    }

    @Override
    @Transactional
    public void approveReview(Long reviewId, Long reviewerId, String comment) {
        ArticleReview review = articleReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }
        
        if (review.getStatus() != ArticleReview.STATUS_PENDING) {
            throw new BusinessException("该审核已处理");
        }
        
        // 更新审核记录
        review.setReviewerId(reviewerId);
        review.setStatus(ArticleReview.STATUS_APPROVED);
        review.setComment(comment);
        articleReviewMapper.updateById(review);
        
        // 更新文章状态为已发布
        Article article = articleMapper.selectById(review.getArticleId());
        if (article != null) {
            article.setStatus(ArticleStatus.PUBLISHED);
            articleMapper.updateById(article);
            
            // 发送通知给作者
            notificationService.sendReviewResultNotification(
                    article.getUserId(), 
                    article.getTitle(), 
                    true, 
                    comment);
        }
    }

    @Override
    @Transactional
    public void rejectReview(Long reviewId, Long reviewerId, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new BusinessException("拒绝原因不能为空");
        }
        
        ArticleReview review = articleReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }
        
        if (review.getStatus() != ArticleReview.STATUS_PENDING) {
            throw new BusinessException("该审核已处理");
        }
        
        // 更新审核记录
        review.setReviewerId(reviewerId);
        review.setStatus(ArticleReview.STATUS_REJECTED);
        review.setComment(comment);
        articleReviewMapper.updateById(review);
        
        // 更新文章状态为已拒绝
        Article article = articleMapper.selectById(review.getArticleId());
        if (article != null) {
            article.setStatus(ArticleStatus.REJECTED);
            articleMapper.updateById(article);
            
            // 发送通知给作者
            notificationService.sendReviewResultNotification(
                    article.getUserId(), 
                    article.getTitle(), 
                    false, 
                    comment);
        }
    }

    @Override
    public PageResponse<ArticleReviewResponse> getPendingReviews(Integer page, Integer size, Integer status) {
        Page<ArticleReview> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<ArticleReview> wrapper = new LambdaQueryWrapper<ArticleReview>()
                .orderByDesc(ArticleReview::getCreatedAt);
        
        // 如果指定了状态，按状态筛选；否则默认显示待审核的
        if (status != null) {
            wrapper.eq(ArticleReview::getStatus, status);
        }
        
        Page<ArticleReview> result = articleReviewMapper.selectPage(pageParam, wrapper);
        
        List<ArticleReviewResponse> list = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(list, result.getTotal(), page, size);
    }

    @Override
    public List<ArticleReviewResponse> getArticleReviewHistory(Long articleId) {
        List<ArticleReview> reviews = articleReviewMapper.selectList(
                new LambdaQueryWrapper<ArticleReview>()
                        .eq(ArticleReview::getArticleId, articleId)
                        .orderByDesc(ArticleReview::getCreatedAt));
        
        return reviews.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ArticleReviewResponse getReviewById(Long reviewId) {
        ArticleReview review = articleReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }
        return convertToResponse(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        ArticleReview review = articleReviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException("审核记录不存在");
        }
        articleReviewMapper.deleteById(reviewId);
    }
    
    /**
     * 转换为响应 DTO
     */
    private ArticleReviewResponse convertToResponse(ArticleReview review) {
        ArticleReviewResponse response = new ArticleReviewResponse();
        response.setId(review.getId());
        response.setArticleId(review.getArticleId());
        response.setReviewerId(review.getReviewerId());
        response.setStatus(review.getStatus());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());
        
        // 设置状态文本
        switch (review.getStatus()) {
            case ArticleReview.STATUS_PENDING:
                response.setStatusText("待审核");
                break;
            case ArticleReview.STATUS_APPROVED:
                response.setStatusText("已通过");
                break;
            case ArticleReview.STATUS_REJECTED:
                response.setStatusText("已拒绝");
                break;
            default:
                response.setStatusText("未知");
        }
        
        // 获取文章信息
        Article article = articleMapper.selectById(review.getArticleId());
        if (article != null) {
            response.setArticleTitle(article.getTitle());
            response.setArticleSummary(article.getSummary());
            response.setArticleCoverImage(article.getCoverImage());
            response.setAuthorId(article.getUserId());
            
            // 获取作者信息
            User author = userMapper.selectById(article.getUserId());
            if (author != null) {
                response.setAuthorUsername(author.getUsername());
                response.setAuthorNickname(author.getNickname());
                response.setAuthorAvatar(author.getAvatar());
            }
            
            // 获取分类信息
            Category category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                response.setCategoryName(category.getName());
            }
        }
        
        // 获取审核员信息
        if (review.getReviewerId() != null) {
            User reviewer = userMapper.selectById(review.getReviewerId());
            if (reviewer != null) {
                response.setReviewerUsername(reviewer.getUsername());
                response.setReviewerNickname(reviewer.getNickname());
            }
        }
        
        return response;
    }
}
