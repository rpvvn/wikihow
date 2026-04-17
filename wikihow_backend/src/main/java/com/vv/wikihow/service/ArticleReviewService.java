package com.vv.wikihow.service;

import com.vv.wikihow.dto.ArticleReviewResponse;
import com.vv.wikihow.dto.PageResponse;

/**
 * 文章审核服务接口
 */
public interface ArticleReviewService {

    /**
     * 提交文章审核
     * @param articleId 文章ID
     * @param userId 提交用户ID
     */
    void submitReview(Long articleId, Long userId);

    /**
     * 审核通过
     * @param reviewId 审核记录ID
     * @param reviewerId 审核员ID
     * @param comment 审核意见
     */
    void approveReview(Long reviewId, Long reviewerId, String comment);

    /**
     * 审核拒绝
     * @param reviewId 审核记录ID
     * @param reviewerId 审核员ID
     * @param comment 拒绝原因（必填）
     */
    void rejectReview(Long reviewId, Long reviewerId, String comment);

    /**
     * 获取待审核文章列表
     * @param page 页码
     * @param size 每页数量
     * @param status 审核状态（可选）
     * @return 分页结果
     */
    PageResponse<ArticleReviewResponse> getPendingReviews(Integer page, Integer size, Integer status);

    /**
     * 获取文章的审核历史
     * @param articleId 文章ID
     * @return 审核历史列表
     */
    java.util.List<ArticleReviewResponse> getArticleReviewHistory(Long articleId);

    /**
     * 获取审核记录详情
     * @param reviewId 审核记录ID
     * @return 审核记录详情
     */
    ArticleReviewResponse getReviewById(Long reviewId);

    /**
     * 删除审核记录
     * @param reviewId 审核记录ID
     */
    void deleteReview(Long reviewId);
}
