package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.ArticleRequest;
import com.vv.wikihow.dto.ArticleVersionResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.ArticleStep;
import com.vv.wikihow.entity.ArticleVersion;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.ArticleStepMapper;
import com.vv.wikihow.mapper.ArticleVersionMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.ArticleVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章版本服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleVersionServiceImpl implements ArticleVersionService {

    private final ArticleVersionMapper articleVersionMapper;
    private final ArticleMapper articleMapper;
    private final ArticleStepMapper articleStepMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void saveVersion(Article article, String stepsJson, Long userId, String changeDescription) {
        // 获取当前最大版本号
        Integer maxVersion = articleVersionMapper.getMaxVersionNumber(article.getId());
        int newVersionNumber = maxVersion + 1;

        ArticleVersion version = new ArticleVersion();
        version.setArticleId(article.getId());
        version.setVersionNumber(newVersionNumber);
        version.setTitle(article.getTitle());
        version.setSummary(article.getSummary());
        version.setContentSnapshot(stepsJson);
        version.setCoverImage(article.getCoverImage());
        version.setCategoryId(article.getCategoryId());
        version.setTags(article.getTags());
        version.setDifficulty(article.getDifficulty());
        version.setReferences(article.getReferences());
        version.setUserId(userId);
        version.setChangeDescription(changeDescription);

        articleVersionMapper.insert(version);
        log.info("保存文章版本成功, articleId={}, versionNumber={}", article.getId(), newVersionNumber);
    }

    @Override
    public PageResponse<ArticleVersionResponse> getVersions(Long articleId, Integer page, Integer size) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        Page<ArticleVersion> pageParam = new Page<>(page, size);
        Page<ArticleVersion> result = articleVersionMapper.selectPage(pageParam,
                new LambdaQueryWrapper<ArticleVersion>()
                        .eq(ArticleVersion::getArticleId, articleId)
                        .orderByDesc(ArticleVersion::getVersionNumber));

        List<ArticleVersionResponse> list = convertToResponse(result.getRecords());
        return PageResponse.of(list, result.getTotal(), page, size);
    }

    @Override
    public ArticleVersionResponse getVersionById(Long articleId, Long versionId) {
        ArticleVersion version = articleVersionMapper.selectById(versionId);
        if (version == null || !version.getArticleId().equals(articleId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "版本不存在");
        }

        User user = userMapper.selectById(version.getUserId());
        ArticleVersionResponse response = ArticleVersionResponse.fromEntity(version, user);

        // 解析步骤JSON
        if (version.getContentSnapshot() != null) {
            try {
                List<ArticleRequest.StepRequest> steps = objectMapper.readValue(
                        version.getContentSnapshot(),
                        new TypeReference<List<ArticleRequest.StepRequest>>() {});
                response.setSteps(steps);
            } catch (JsonProcessingException e) {
                log.error("解析版本步骤JSON失败", e);
            }
        }

        return response;
    }

    @Override
    @Transactional
    public void revertToVersion(Long articleId, Long versionId, Long userId, boolean isAdmin) {
        // 获取文章
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        // 权限检查：只有作者或管理员可以回滚
        if (!isAdmin && !article.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权回滚此文章");
        }

        // 获取目标版本
        ArticleVersion version = articleVersionMapper.selectById(versionId);
        if (version == null || !version.getArticleId().equals(articleId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "版本不存在");
        }

        // 先保存当前版本（回滚前的状态）
        String currentStepsJson = getCurrentStepsJson(articleId);
        saveVersion(article, currentStepsJson, userId, "回滚前自动保存");

        // 更新文章信息
        article.setTitle(version.getTitle());
        article.setSummary(version.getSummary());
        article.setCoverImage(version.getCoverImage());
        article.setCategoryId(version.getCategoryId());
        article.setTags(version.getTags());
        article.setDifficulty(version.getDifficulty());
        article.setReferences(version.getReferences());
        articleMapper.updateById(article);

        // 删除当前步骤
        articleStepMapper.delete(new LambdaQueryWrapper<ArticleStep>()
                .eq(ArticleStep::getArticleId, articleId));

        // 恢复版本中的步骤
        if (version.getContentSnapshot() != null) {
            try {
                List<ArticleRequest.StepRequest> steps = objectMapper.readValue(
                        version.getContentSnapshot(),
                        new TypeReference<List<ArticleRequest.StepRequest>>() {});
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
            } catch (JsonProcessingException e) {
                log.error("解析版本步骤JSON失败", e);
                throw new BusinessException("回滚失败：无法解析版本内容");
            }
        }

        // 保存回滚后的版本
        saveVersion(article, version.getContentSnapshot(), userId, 
                "回滚到版本 " + version.getVersionNumber());

        log.info("文章回滚成功, articleId={}, targetVersion={}", articleId, version.getVersionNumber());
    }

    @Override
    public Integer getLatestVersionNumber(Long articleId) {
        return articleVersionMapper.getMaxVersionNumber(articleId);
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

    /**
     * 转换为响应DTO列表
     */
    private List<ArticleVersionResponse> convertToResponse(List<ArticleVersion> versions) {
        if (versions.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量获取用户信息
        List<Long> userIds = versions.stream()
                .map(ArticleVersion::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return versions.stream()
                .map(v -> ArticleVersionResponse.fromEntity(v, userMap.get(v.getUserId())))
                .collect(Collectors.toList());
    }
}
