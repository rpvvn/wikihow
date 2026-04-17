package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vv.wikihow.dto.ArticleListResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.*;
import com.vv.wikihow.mapper.*;
import com.vv.wikihow.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public boolean toggleFavorite(Long articleId, Long userId) {
        Favorite existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getArticleId, articleId)
                        .eq(Favorite::getUserId, userId));

        Article article = articleMapper.selectById(articleId);

        if (existing != null) {
            favoriteMapper.deleteById(existing.getId());
            if (article != null && article.getFavoriteCount() > 0) {
                article.setFavoriteCount(article.getFavoriteCount() - 1);
                articleMapper.updateById(article);
            }
            return false;
        } else {
            Favorite favorite = new Favorite();
            favorite.setArticleId(articleId);
            favorite.setUserId(userId);
            favoriteMapper.insert(favorite);
            if (article != null) {
                article.setFavoriteCount(article.getFavoriteCount() + 1);
                articleMapper.updateById(article);
            }
            return true;
        }
    }

    @Override
    public boolean isFavorited(Long articleId, Long userId) {
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getArticleId, articleId)
                        .eq(Favorite::getUserId, userId)) > 0;
    }

    @Override
    public PageResponse<ArticleListResponse> getUserFavorites(Long userId, Integer page, Integer size) {
        Page<Favorite> pageParam = new Page<>(page, size);
        Page<Favorite> result = favoriteMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreatedAt));

        if (result.getRecords().isEmpty()) {
            return PageResponse.of(new ArrayList<>(), 0L, page, size);
        }

        List<Long> articleIds = result.getRecords().stream().map(Favorite::getArticleId).toList();
        List<Article> articles = articleMapper.selectBatchIds(articleIds);

        List<Long> userIds = articles.stream().map(Article::getUserId).distinct().toList();
        List<Long> categoryIds = articles.stream().map(Article::getCategoryId).distinct().toList();

        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Category> categoryMap = categoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        List<ArticleListResponse> list = articles.stream()
                .map(a -> ArticleListResponse.fromArticle(a, userMap.get(a.getUserId()), categoryMap.get(a.getCategoryId())))
                .toList();

        return PageResponse.of(list, result.getTotal(), page, size);
    }
}
