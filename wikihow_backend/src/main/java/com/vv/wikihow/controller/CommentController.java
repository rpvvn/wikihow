package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.CommentRequest;
import com.vv.wikihow.dto.CommentResponse;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/api/articles/{articleId}/comments")
    public Result<List<CommentResponse>> getComments(@PathVariable Long articleId) {
        List<CommentResponse> comments = commentService.getCommentsByArticleId(articleId);
        return Result.success(comments);
    }

    @PostMapping("/api/comments")
    public Result<Void> createComment(@Valid @RequestBody CommentRequest request) {
        Long userId = UserContext.getCurrentUserId();
        
        // 检查是否是回复评论
        if (request.getParentId() != null) {
            commentService.createReply(request.getArticleId(), userId, request.getContent(), request.getParentId());
            return Result.success("回复成功", null);
        }
        
        commentService.createComment(request.getArticleId(), userId, request.getContent());
        return Result.success("评论成功", null);
    }

    @DeleteMapping("/api/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        commentService.deleteComment(id, userId);
        return Result.success("删除成功", null);
    }
}
