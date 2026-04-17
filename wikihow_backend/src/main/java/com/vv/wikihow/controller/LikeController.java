package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public Result<Map<String, Boolean>> toggleLike(@RequestBody Map<String, Long> body) {
        Long articleId = body.get("articleId");
        Long userId = UserContext.getCurrentUserId();
        boolean liked = likeService.toggleLike(articleId, userId);
        return Result.success(Map.of("liked", liked));
    }
}
