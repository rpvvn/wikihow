package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vv.wikihow.dto.OutdatedReportResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.OutdatedReport;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.OutdatedReportMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.NotificationService;
import com.vv.wikihow.service.OutdatedReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 过时内容举报服务实现
 */
@Service
@RequiredArgsConstructor
public class OutdatedReportServiceImpl implements OutdatedReportService {

    private final OutdatedReportMapper outdatedReportMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void reportOutdated(Long articleId, Long userId, String reason) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 检查是否已举报过
        if (hasReported(articleId, userId)) {
            throw new BusinessException("您已举报过该文章");
        }
        
        // 创建举报记录
        OutdatedReport report = new OutdatedReport();
        report.setArticleId(articleId);
        report.setUserId(userId);
        report.setReason(reason);
        report.setStatus(OutdatedReport.STATUS_PENDING);
        outdatedReportMapper.insert(report);
    }

    @Override
    @Transactional
    public void markOutdated(Long articleId, Long adminId, String reason) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 标记为过时
        article.setIsOutdated(1);
        article.setOutdatedReason(reason);
        articleMapper.updateById(article);
        
        // 发送通知给作者
        notificationService.sendOutdatedNotification(article.getUserId(), article.getTitle(), reason);
    }

    @Override
    @Transactional
    public void unmarkOutdated(Long articleId, Long adminId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        if (article.getIsOutdated() == null || article.getIsOutdated() == 0) {
            throw new BusinessException("该文章未被标记为过时");
        }
        
        // 取消过时标记
        article.setIsOutdated(0);
        article.setOutdatedReason(null);
        articleMapper.updateById(article);
    }

    @Override
    public PageResponse<OutdatedReportResponse> getReportList(Integer page, Integer size, Integer status) {
        Page<OutdatedReport> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<OutdatedReport> wrapper = new LambdaQueryWrapper<OutdatedReport>()
                .orderByDesc(OutdatedReport::getCreatedAt);
        
        if (status != null) {
            wrapper.eq(OutdatedReport::getStatus, status);
        }
        
        Page<OutdatedReport> result = outdatedReportMapper.selectPage(pageParam, wrapper);
        
        List<OutdatedReportResponse> list = result.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(list, result.getTotal(), page, size);
    }

    @Override
    @Transactional
    public void handleReport(Long reportId, Long handlerId, Integer status, String comment, String outdatedReason) {
        OutdatedReport report = outdatedReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        
        if (report.getStatus() != OutdatedReport.STATUS_PENDING) {
            throw new BusinessException("该举报已处理");
        }
        
        // 更新举报记录
        report.setHandlerId(handlerId);
        report.setStatus(status);
        report.setHandleComment(comment);
        report.setHandleTime(LocalDateTime.now());
        outdatedReportMapper.updateById(report);
        
        // 如果处理结果是标记过时，则更新文章
        if (status == OutdatedReport.STATUS_HANDLED && outdatedReason != null) {
            Article article = articleMapper.selectById(report.getArticleId());
            if (article != null) {
                article.setIsOutdated(1);
                article.setOutdatedReason(outdatedReason);
                articleMapper.updateById(article);
                
                // 发送通知给作者
                notificationService.sendOutdatedNotification(article.getUserId(), article.getTitle(), outdatedReason);
            }
        }
    }

    @Override
    public OutdatedReportResponse getReportById(Long reportId) {
        OutdatedReport report = outdatedReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        return convertToResponse(report);
    }

    @Override
    public boolean hasReported(Long articleId, Long userId) {
        Long count = outdatedReportMapper.selectCount(
                new LambdaQueryWrapper<OutdatedReport>()
                        .eq(OutdatedReport::getArticleId, articleId)
                        .eq(OutdatedReport::getUserId, userId));
        return count > 0;
    }

    @Override
    public void deleteReport(Long reportId) {
        OutdatedReport report = outdatedReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        outdatedReportMapper.deleteById(reportId);
    }
    
    /**
     * 转换为响应 DTO
     */
    private OutdatedReportResponse convertToResponse(OutdatedReport report) {
        OutdatedReportResponse response = new OutdatedReportResponse();
        response.setId(report.getId());
        response.setArticleId(report.getArticleId());
        response.setUserId(report.getUserId());
        response.setReason(report.getReason());
        response.setStatus(report.getStatus());
        response.setHandlerId(report.getHandlerId());
        response.setHandleComment(report.getHandleComment());
        response.setHandleTime(report.getHandleTime());
        response.setCreatedAt(report.getCreatedAt());
        
        // 设置状态文本
        switch (report.getStatus()) {
            case OutdatedReport.STATUS_PENDING:
                response.setStatusText("待处理");
                break;
            case OutdatedReport.STATUS_HANDLED:
                response.setStatusText("已处理");
                break;
            case OutdatedReport.STATUS_IGNORED:
                response.setStatusText("已忽略");
                break;
            default:
                response.setStatusText("未知");
        }
        
        // 获取文章信息
        Article article = articleMapper.selectById(report.getArticleId());
        if (article != null) {
            response.setArticleTitle(article.getTitle());
            response.setArticleSummary(article.getSummary());
            response.setArticleCoverImage(article.getCoverImage());
            response.setArticleIsOutdated(article.getIsOutdated() != null && article.getIsOutdated() == 1);
        }
        
        // 获取举报用户信息
        User user = userMapper.selectById(report.getUserId());
        if (user != null) {
            response.setUsername(user.getUsername());
            response.setNickname(user.getNickname());
            response.setUserAvatar(user.getAvatar());
        }
        
        // 获取处理人信息
        if (report.getHandlerId() != null) {
            User handler = userMapper.selectById(report.getHandlerId());
            if (handler != null) {
                response.setHandlerUsername(handler.getUsername());
                response.setHandlerNickname(handler.getNickname());
            }
        }
        
        return response;
    }
}
