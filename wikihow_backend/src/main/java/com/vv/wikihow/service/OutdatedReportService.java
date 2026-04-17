package com.vv.wikihow.service;

import com.vv.wikihow.dto.OutdatedReportResponse;
import com.vv.wikihow.dto.PageResponse;

/**
 * 过时内容举报服务接口
 */
public interface OutdatedReportService {

    /**
     * 用户举报过时内容
     * @param articleId 文章ID
     * @param userId 举报用户ID
     * @param reason 举报原因
     */
    void reportOutdated(Long articleId, Long userId, String reason);

    /**
     * 管理员标记文章为过时
     * @param articleId 文章ID
     * @param adminId 管理员ID
     * @param reason 过时原因
     */
    void markOutdated(Long articleId, Long adminId, String reason);

    /**
     * 管理员取消过时标记
     * @param articleId 文章ID
     * @param adminId 管理员ID
     */
    void unmarkOutdated(Long articleId, Long adminId);

    /**
     * 获取过时举报列表
     * @param page 页码
     * @param size 每页数量
     * @param status 处理状态（可选）
     * @return 分页结果
     */
    PageResponse<OutdatedReportResponse> getReportList(Integer page, Integer size, Integer status);

    /**
     * 处理举报
     * @param reportId 举报记录ID
     * @param handlerId 处理人ID
     * @param status 处理结果: 1已处理/2已忽略
     * @param comment 处理备注
     * @param outdatedReason 过时原因（当标记过时时需要）
     */
    void handleReport(Long reportId, Long handlerId, Integer status, String comment, String outdatedReason);

    /**
     * 获取举报详情
     * @param reportId 举报记录ID
     * @return 举报详情
     */
    OutdatedReportResponse getReportById(Long reportId);

    /**
     * 检查用户是否已举报过该文章
     * @param articleId 文章ID
     * @param userId 用户ID
     * @return 是否已举报
     */
    boolean hasReported(Long articleId, Long userId);

    /**
     * 删除举报记录
     * @param reportId 举报记录ID
     */
    void deleteReport(Long reportId);
}
