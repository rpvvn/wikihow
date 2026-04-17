package com.vv.wikihow.service;

import com.vv.wikihow.dto.ArticleListResponse;
import com.vv.wikihow.dto.PageResponse;

public interface FavoriteService {
    boolean toggleFavorite(Long articleId, Long userId);
    boolean isFavorited(Long articleId, Long userId);
    PageResponse<ArticleListResponse> getUserFavorites(Long userId, Integer page, Integer size);
}
