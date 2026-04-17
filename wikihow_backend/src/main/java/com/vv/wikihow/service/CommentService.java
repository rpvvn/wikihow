package com.vv.wikihow.service;

import com.vv.wikihow.dto.CommentResponse;
import java.util.List;

public interface CommentService {
    /**
     * 获取文章评论（树形结构）
     */
    List<CommentResponse> getCommentsByArticleId(Long articleId);
    
    /**
     * 创建评论
     */
    void createComment(Long articleId, Long userId, String content);
    
    /**
     * 创建回复评论
     * @param articleId 文章ID
     * @param userId 用户ID
     * @param content 评论内容
     * @param parentId 父评论ID
     */
    void createReply(Long articleId, Long userId, String content, Long parentId);
    
    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);
}
