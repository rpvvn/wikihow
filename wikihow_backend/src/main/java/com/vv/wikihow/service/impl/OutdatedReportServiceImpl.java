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
        report.setReportType(OutdatedReport.REPORT_TYPE_OUTDATED);  // 过时举报
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

    /**
     * 处理举报记录（包括过时举报和复核申请）
     * 
     * <p>该方法支持两种类型的处理：</p>
     * <ul>
     *   <li>过时举报处理：管理员可以选择标记文章为过时或删除文章</li>
     *   <li>复核申请处理：管理员审核作者的复核申请，决定是否移除过时标记</li>
     * </ul>
     * 
     * <p>处理流程：</p>
     * <ol>
     *   <li>验证举报记录是否存在且未处理</li>
     *   <li>根据举报类型（reportType）执行不同的处理逻辑</li>
     *   <li>更新文章状态（过时标记或下架状态）</li>
     *   <li>发送相应的通知给文章作者</li>
     *   <li>更新举报记录的处理状态</li>
     * </ol>
     * 
     * @param reportId 举报记录ID
     * @param handlerId 处理人ID（管理员或审核员）
     * @param status 处理状态：1-已处理，2-已忽略
     * @param handleType 处理类型（仅过时举报需要）：1-标记过时，2-删除文章
     * @param comment 处理备注，向作者说明处理原因
     * @param reason 过时原因（仅标记过时时需要）
     * @throws BusinessException 当举报记录不存在、已处理、文章不存在或参数无效时抛出
     */
    @Override
    @Transactional
    public void handleReport(Long reportId, Long handlerId, Integer status, Integer handleType, String comment, String reason) {
        // 验证举报记录是否存在
        OutdatedReport report = outdatedReportMapper.selectById(reportId);
        if (report == null) {
            throw new BusinessException("举报记录不存在");
        }
        
        // 检查举报记录是否已经被处理过
        if (report.getStatus() != OutdatedReport.STATUS_PENDING) {
            throw new BusinessException("该举报已处理");
        }
        
        // 更新举报记录的基本处理信息
        report.setHandlerId(handlerId);
        report.setStatus(status);
        report.setHandleComment(comment);
        report.setHandleTime(LocalDateTime.now());
        
        // 验证关联的文章是否存在
        Article article = articleMapper.selectById(report.getArticleId());
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 根据举报类型执行不同的处理逻辑
        // 情况1：复核申请处理（作者申请移除过时标记或恢复已下架文章）
        if (report.getReportType() != null && report.getReportType() == OutdatedReport.REPORT_TYPE_REVIEW) {
            if (status == OutdatedReport.STATUS_HANDLED) {
                // 记录文章原始状态，用于发送不同的通知
                boolean wasOffline = article.getStatus() != null && article.getStatus() == 4;
                boolean wasOutdated = article.getIsOutdated() != null && article.getIsOutdated() == 1;
                
                // 通过复核：移除文章的过时标记，如果是已下架状态则恢复为已发布
                article.setIsOutdated(0);
                article.setOutdatedReason(null);
                
                // 如果文章是已下架状态，恢复为已发布
                if (wasOffline) {
                    article.setStatus(2);  // 恢复为已发布
                }
                
                articleMapper.updateById(article);
                
                // 根据原始状态发送不同的通知
                if (wasOffline) {
                    // 文章从已下架恢复为已发布
                    notificationService.sendArticleRestoredNotification(article.getUserId(), article.getTitle(), comment);
                } else if (wasOutdated) {
                    // 文章过时标记被移除
                    notificationService.sendReviewApprovedNotification(article.getUserId(), article.getTitle(), comment);
                } else {
                    // 其他情况（理论上不应该发生，但保险起见）
                    notificationService.sendReviewApprovedNotification(article.getUserId(), article.getTitle(), comment);
                }
            } else if (status == OutdatedReport.STATUS_IGNORED) {
                // 拒绝复核：保持文章的过时标记和下架状态不变，仅发送拒绝通知
                notificationService.sendReviewRejectedNotification(article.getUserId(), article.getTitle(), comment);
            }
        } 
        // 情况2：过时举报处理（用户举报文章内容过时）
        else {
            // 只有处理状态为"已处理"时才需要执行具体操作
            if (status == OutdatedReport.STATUS_HANDLED) {
                // 验证处理类型参数是否提供
                if (handleType == null) {
                    throw new BusinessException("处理类型不能为空");
                }
                
                report.setHandleType(handleType);
                
                if (handleType == OutdatedReport.HANDLE_TYPE_MARK_OUTDATED) {
                    // 处理方式1：标记文章为过时
                    article.setIsOutdated(1);
                    article.setOutdatedReason(reason);
                    articleMapper.updateById(article);
                    
                    // 发送过时标记通知给作者
                    notificationService.sendOutdatedNotification(article.getUserId(), article.getTitle(), reason);
                    
                } else if (handleType == OutdatedReport.HANDLE_TYPE_DELETE_ARTICLE) {
                    // 处理方式2：删除文章（实际是下架，设置状态为4）
                    article.setStatus(4); // 4 = 已下架
                    articleMapper.updateById(article);
                    
                    // 发送文章删除通知给作者
                    notificationService.sendArticleDeletedNotification(article.getUserId(), article.getTitle(), reason);
                    
                } else {
                    throw new BusinessException("无效的处理类型");
                }
            }
            // 如果状态是"已忽略"，则不需要对文章做任何操作
        }
        
        // 保存举报记录的更新
        outdatedReportMapper.updateById(report);
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
                        .eq(OutdatedReport::getUserId, userId)
                        .eq(OutdatedReport::getReportType, OutdatedReport.REPORT_TYPE_OUTDATED)  // 只检查过时举报
                        .eq(OutdatedReport::getStatus, OutdatedReport.STATUS_PENDING));  // 只检查待处理的
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

    @Override
    @Transactional
    public void requestReview(Long articleId, Long userId, String reason) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        
        // 检查是否是文章作者
        if (!article.getUserId().equals(userId)) {
            throw new BusinessException("只有文章作者可以申请复核");
        }
        
        // 检查文章是否被标记为过时或已下架
        boolean isOutdated = article.getIsOutdated() != null && article.getIsOutdated() == 1;
        boolean isOffline = article.getStatus() != null && article.getStatus() == 4;
        
        if (!isOutdated && !isOffline) {
            throw new BusinessException("只有被标记为过时或已下架的文章才能申请复核");
        }
        
        // 检查是否已有待处理的复核申请
        if (hasPendingReview(articleId, userId)) {
            throw new BusinessException("您已提交过复核申请，请等待处理");
        }
        
        // 创建复核申请记录
        OutdatedReport report = new OutdatedReport();
        report.setArticleId(articleId);
        report.setUserId(userId);
        report.setReason(reason);
        report.setReportType(OutdatedReport.REPORT_TYPE_REVIEW);  // 复核申请
        report.setStatus(OutdatedReport.STATUS_PENDING);
        outdatedReportMapper.insert(report);
    }

    @Override
    public boolean hasPendingReview(Long articleId, Long userId) {
        Long count = outdatedReportMapper.selectCount(
                new LambdaQueryWrapper<OutdatedReport>()
                        .eq(OutdatedReport::getArticleId, articleId)
                        .eq(OutdatedReport::getUserId, userId)
                        .eq(OutdatedReport::getReportType, OutdatedReport.REPORT_TYPE_REVIEW)
                        .eq(OutdatedReport::getStatus, OutdatedReport.STATUS_PENDING));
        return count > 0;
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
        response.setReportType(report.getReportType());
        response.setStatus(report.getStatus());
        response.setHandlerId(report.getHandlerId());
        response.setHandleComment(report.getHandleComment());
        response.setHandleTime(report.getHandleTime());
        response.setCreatedAt(report.getCreatedAt());
        
        // 设置类型文本
        if (report.getReportType() != null) {
            switch (report.getReportType()) {
                case OutdatedReport.REPORT_TYPE_OUTDATED:
                    response.setReportTypeText("举报");
                    break;
                case OutdatedReport.REPORT_TYPE_REVIEW:
                    response.setReportTypeText("复核申请");
                    break;
                default:
                    response.setReportTypeText("未知");
            }
        } else {
            response.setReportTypeText("举报"); // 默认为过时举报
        }
        
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
