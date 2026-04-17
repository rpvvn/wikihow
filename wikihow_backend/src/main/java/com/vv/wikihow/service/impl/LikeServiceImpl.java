package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.LikeRecord;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.LikeMapper;
import com.vv.wikihow.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final ArticleMapper articleMapper;

    @Override
    @Transactional
    public boolean toggleLike(Long articleId, Long userId) {
        LikeRecord existing = likeMapper.selectOne(
                new LambdaQueryWrapper<LikeRecord>()
                        .eq(LikeRecord::getArticleId, articleId)
                        .eq(LikeRecord::getUserId, userId));

        Article article = articleMapper.selectById(articleId);
        
        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            if (article != null && article.getLikeCount() > 0) {
                article.setLikeCount(article.getLikeCount() - 1);
                articleMapper.updateById(article);
            }
            return false;
        } else {
            LikeRecord like = new LikeRecord();
            like.setArticleId(articleId);
            like.setUserId(userId);
            likeMapper.insert(like);
            if (article != null) {
                article.setLikeCount(article.getLikeCount() + 1);
                articleMapper.updateById(article);
            }
            return true;
        }
    }

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        return likeMapper.selectCount(
                new LambdaQueryWrapper<LikeRecord>()
                        .eq(LikeRecord::getArticleId, articleId)
                        .eq(LikeRecord::getUserId, userId)) > 0;
    }
}
