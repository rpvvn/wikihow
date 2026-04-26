package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.ArticleReviewRequest;
import com.vv.wikihow.dto.ArticleReviewResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.ArticleReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章审核控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleReviewController {

    private final ArticleReviewService articleReviewService;

    /**
     * 提交文章审核
     */
    @PostMapping("/articles/{id}/submit-review")
    public Result<Void> submitReview(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        articleReviewService.submitReview(id, userId);
        return Result.success("提交审核成功", null);
    }

    /**
     * 获取待审核文章列表（审核员/管理员）
     */
    @GetMapping("/admin/reviews")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<PageResponse<ArticleReviewResponse>> getPendingReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        PageResponse<ArticleReviewResponse> result = articleReviewService.getPendingReviews(page, size, status);
        return Result.success(result);
    }

    /**
     * 获取审核记录详情
     */
    @GetMapping("/admin/reviews/{id}")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<ArticleReviewResponse> getReviewById(@PathVariable Long id) {
        ArticleReviewResponse review = articleReviewService.getReviewById(id);
        return Result.success(review);
    }

    /**
     * 审核通过
     */
    @PutMapping("/admin/reviews/{id}/approve")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<Void> approveReview(@PathVariable Long id, @RequestBody(required = false) ArticleReviewRequest request) {
        Long reviewerId = UserContext.getCurrentUserId();
        String comment = request != null ? request.getComment() : null;
        articleReviewService.approveReview(id, reviewerId, comment);
        return Result.success("审核通过", null);
    }

    /**
     * 审核拒绝
     */
    @PutMapping("/admin/reviews/{id}/reject")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<Void> rejectReview(@PathVariable Long id, @RequestBody ArticleReviewRequest request) {
        Long reviewerId = UserContext.getCurrentUserId();
        articleReviewService.rejectReview(id, reviewerId, request.getComment());
        return Result.success("审核已拒绝", null);
    }

    /**
     * 获取文章的审核历史
     */
    @GetMapping("/articles/{id}/review-history")
    public Result<List<ArticleReviewResponse>> getArticleReviewHistory(@PathVariable Long id) {
        List<ArticleReviewResponse> history = articleReviewService.getArticleReviewHistory(id);
        return Result.success(history);
    }

    /**
     * 删除审核记录（仅管理员）
     */
    @DeleteMapping("/admin/reviews/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteReview(@PathVariable Long id) {
        articleReviewService.deleteReview(id);
        return Result.success("删除成功", null);
    }
}
