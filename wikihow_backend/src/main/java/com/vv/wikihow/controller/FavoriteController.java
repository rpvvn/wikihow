package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.ArticleListResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/api/favorites")
    public Result<Map<String, Boolean>> toggleFavorite(@RequestBody Map<String, Long> body) {
        Long articleId = body.get("articleId");
        Long userId = UserContext.getCurrentUserId();
        boolean favorited = favoriteService.toggleFavorite(articleId, userId);
        return Result.success(Map.of("favorited", favorited));
    }

    @GetMapping("/api/users/{userId}/favorites")
    public Result<PageResponse<ArticleListResponse>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResponse<ArticleListResponse> result = favoriteService.getUserFavorites(userId, page, size);
        return Result.success(result);
    }
}
