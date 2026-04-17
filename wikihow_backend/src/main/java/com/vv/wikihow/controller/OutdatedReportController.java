package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.*;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.OutdatedReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 过时内容举报控制器
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OutdatedReportController {

    private final OutdatedReportService outdatedReportService;

    /**
     * 用户举报过时内容
     */
    @PostMapping("/articles/{id}/report-outdated")
    public Result<Void> reportOutdated(@PathVariable Long id, 
                                        @Valid @RequestBody OutdatedReportRequest request) {
        Long userId = UserContext.getCurrentUserId();
        outdatedReportService.reportOutdated(id, userId, request.getReason());
        return Result.success("举报成功，我们会尽快处理", null);
    }

    /**
     * 检查用户是否已举报过该文章
     */
    @GetMapping("/articles/{id}/report-status")
    public Result<Boolean> checkReportStatus(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        boolean hasReported = outdatedReportService.hasReported(id, userId);
        return Result.success(hasReported);
    }

    /**
     * 管理员标记文章为过时
     */
    @PutMapping("/admin/articles/{id}/mark-outdated")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> markOutdated(@PathVariable Long id, 
                                      @Valid @RequestBody MarkOutdatedRequest request) {
        Long adminId = UserContext.getCurrentUserId();
        outdatedReportService.markOutdated(id, adminId, request.getReason());
        return Result.success("已标记为过时", null);
    }

    /**
     * 管理员取消过时标记
     */
    @PutMapping("/admin/articles/{id}/unmark-outdated")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> unmarkOutdated(@PathVariable Long id) {
        Long adminId = UserContext.getCurrentUserId();
        outdatedReportService.unmarkOutdated(id, adminId);
        return Result.success("已取消过时标记", null);
    }

    /**
     * 获取过时举报列表
     */
    @GetMapping("/admin/outdated-reports")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<PageResponse<OutdatedReportResponse>> getReportList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        PageResponse<OutdatedReportResponse> result = outdatedReportService.getReportList(page, size, status);
        return Result.success(result);
    }

    /**
     * 获取举报详情
     */
    @GetMapping("/admin/outdated-reports/{id}")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<OutdatedReportResponse> getReportById(@PathVariable Long id) {
        OutdatedReportResponse report = outdatedReportService.getReportById(id);
        return Result.success(report);
    }

    /**
     * 处理举报
     */
    @PutMapping("/admin/outdated-reports/{id}/handle")
    @PreAuthorize("hasRole('REVIEWER')")
    public Result<Void> handleReport(@PathVariable Long id, 
                                      @Valid @RequestBody HandleReportRequest request) {
        Long handlerId = UserContext.getCurrentUserId();
        outdatedReportService.handleReport(id, handlerId, request.getStatus(), 
                request.getComment(), request.getOutdatedReason());
        return Result.success("处理成功", null);
    }

    /**
     * 删除举报记录（仅管理员）
     */
    @DeleteMapping("/admin/outdated-reports/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteReport(@PathVariable Long id) {
        outdatedReportService.deleteReport(id);
        return Result.success("删除成功", null);
    }
}
