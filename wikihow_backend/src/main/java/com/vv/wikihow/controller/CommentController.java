package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.CommentResponse;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public Result<Void> createComment(@RequestBody Map<String, Object> body) {
        Long articleId = Long.valueOf(body.get("articleId").toString());
        String content = body.get("content").toString();
        Long userId = UserContext.getCurrentUserId();
        
        // 检查是否是回复评论
        Object parentIdObj = body.get("parentId");
        if (parentIdObj != null) {
            Long parentId = Long.valueOf(parentIdObj.toString());
            commentService.createReply(articleId, userId, content, parentId);
            return Result.success("回复成功", null);
        }
        
        commentService.createComment(articleId, userId, content);
        return Result.success("评论成功", null);
    }

    @DeleteMapping("/api/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        commentService.deleteComment(id, userId);
        return Result.success("删除成功", null);
    }
}
