package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.EditLockResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.ArticleEditLock;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleEditLockMapper;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.ArticleEditLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 文章编辑锁服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleEditLockServiceImpl implements ArticleEditLockService {

    private final ArticleEditLockMapper articleEditLockMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;

    /**
     * 锁有效期（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    @Transactional
    public EditLockResponse acquireLock(Long articleId, Long userId) {
        // 验证文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }

        // 先清理过期锁
        cleanExpiredLockForArticle(articleId);

        // 检查是否已有锁
        ArticleEditLock existingLock = getActiveLock(articleId);

        if (existingLock != null) {
            // 如果是自己的锁，续期并返回
            if (existingLock.getUserId().equals(userId)) {
                return renewLock(articleId, userId);
            }

            // 被他人锁定
            User lockOwner = userMapper.selectById(existingLock.getUserId());
            return EditLockResponse.fromEntity(existingLock, lockOwner, userId);
        }

        // 创建新锁
        LocalDateTime now = LocalDateTime.now();
        ArticleEditLock newLock = new ArticleEditLock();
        newLock.setArticleId(articleId);
        newLock.setUserId(userId);
        newLock.setLockedAt(now);
        newLock.setExpiresAt(now.plusMinutes(LOCK_DURATION_MINUTES));

        articleEditLockMapper.insert(newLock);
        log.info("获取编辑锁成功, articleId={}, userId={}", articleId, userId);

        User currentUser = userMapper.selectById(userId);
        return EditLockResponse.fromEntity(newLock, currentUser, userId);
    }

    @Override
    @Transactional
    public void releaseLock(Long articleId, Long userId) {
        ArticleEditLock lock = getActiveLock(articleId);

        if (lock == null) {
            // 锁不存在或已过期，无需释放
            return;
        }

        // 只有锁的持有者可以释放锁
        if (!lock.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权释放此编辑锁");
        }

        articleEditLockMapper.deleteById(lock.getId());
        log.info("释放编辑锁成功, articleId={}, userId={}", articleId, userId);
    }

    @Override
    public EditLockResponse getLockStatus(Long articleId) {
        // 先清理过期锁
        cleanExpiredLockForArticle(articleId);

        ArticleEditLock lock = getActiveLock(articleId);

        if (lock == null) {
            return EditLockResponse.unlocked();
        }

        User lockOwner = userMapper.selectById(lock.getUserId());
        // 获取状态时不知道当前用户，isOwner 设为 false
        EditLockResponse response = EditLockResponse.fromEntity(lock, lockOwner, null);
        response.setIsOwner(false);
        return response;
    }

    @Override
    @Transactional
    public EditLockResponse renewLock(Long articleId, Long userId) {
        ArticleEditLock lock = getActiveLock(articleId);

        if (lock == null) {
            // 锁不存在，尝试获取新锁
            return acquireLock(articleId, userId);
        }

        // 只有锁的持有者可以续期
        if (!lock.getUserId().equals(userId)) {
            User lockOwner = userMapper.selectById(lock.getUserId());
            return EditLockResponse.fromEntity(lock, lockOwner, userId);
        }

        // 续期
        LocalDateTime now = LocalDateTime.now();
        lock.setExpiresAt(now.plusMinutes(LOCK_DURATION_MINUTES));
        articleEditLockMapper.updateById(lock);
        log.info("续期编辑锁成功, articleId={}, userId={}", articleId, userId);

        User currentUser = userMapper.selectById(userId);
        return EditLockResponse.fromEntity(lock, currentUser, userId);
    }

    @Override
    @Transactional
    public int cleanExpiredLocks() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = articleEditLockMapper.delete(
                new LambdaQueryWrapper<ArticleEditLock>()
                        .lt(ArticleEditLock::getExpiresAt, now));
        if (deleted > 0) {
            log.info("清理过期编辑锁, 数量={}", deleted);
        }
        return deleted;
    }

    /**
     * 获取文章的有效锁
     */
    private ArticleEditLock getActiveLock(Long articleId) {
        LocalDateTime now = LocalDateTime.now();
        return articleEditLockMapper.selectOne(
                new LambdaQueryWrapper<ArticleEditLock>()
                        .eq(ArticleEditLock::getArticleId, articleId)
                        .gt(ArticleEditLock::getExpiresAt, now));
    }

    /**
     * 清理指定文章的过期锁
     */
    private void cleanExpiredLockForArticle(Long articleId) {
        LocalDateTime now = LocalDateTime.now();
        articleEditLockMapper.delete(
                new LambdaQueryWrapper<ArticleEditLock>()
                        .eq(ArticleEditLock::getArticleId, articleId)
                        .lt(ArticleEditLock::getExpiresAt, now));
    }
}
