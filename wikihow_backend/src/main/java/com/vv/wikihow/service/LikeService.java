package com.vv.wikihow.service;

public interface LikeService {
    boolean toggleLike(Long articleId, Long userId);
    boolean isLiked(Long articleId, Long userId);
}
