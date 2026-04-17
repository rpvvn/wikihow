package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.CommentResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.Comment;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.CommentMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.CommentService;
import com.vv.wikihow.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final NotificationService notificationService;

    @Override
    public List<CommentResponse> getCommentsByArticleId(Long articleId) {
        // 获取该文章的所有评论
        List<Comment> allComments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getArticleId, articleId)
                        .orderByAsc(Comment::getCreatedAt));

        if (allComments.isEmpty()) return List.of();

        // 获取所有用户信息
        List<Long> userIds = allComments.stream().map(Comment::getUserId).distinct().toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 转换为 Response 对象
        List<CommentResponse> allResponses = allComments.stream()
                .map(c -> CommentResponse.fromComment(c, userMap.get(c.getUserId())))
                .toList();

        // 构建树形结构
        return buildCommentTree(allResponses);
    }

    /**
     * 构建评论树形结构
     */
    private List<CommentResponse> buildCommentTree(List<CommentResponse> allComments) {
        // 按 ID 建立索引
        Map<Long, CommentResponse> commentMap = allComments.stream()
                .collect(Collectors.toMap(CommentResponse::getId, c -> c));

        List<CommentResponse> rootComments = new ArrayList<>();

        for (CommentResponse comment : allComments) {
            if (comment.getParentId() == null) {
                // 顶级评论
                rootComments.add(comment);
            } else {
                // 子评论，添加到父评论的 replies 列表
                CommentResponse parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    parent.getReplies().add(comment);
                } else {
                    // 父评论不存在，作为顶级评论处理
                    rootComments.add(comment);
                }
            }
        }

        // 按时间倒序排列顶级评论（最新的在前）
        rootComments.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return rootComments;
    }

    @Override
    @Transactional
    public void createComment(Long articleId, Long userId, String content) {
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(null);
        commentMapper.insert(comment);

        // 更新文章评论数
        Article article = articleMapper.selectById(articleId);
        if (article != null) {
            article.setCommentCount(article.getCommentCount() + 1);
            articleMapper.updateById(article);
            
            // 发送评论通知给文章作者（如果评论者不是作者本人）
            if (!article.getUserId().equals(userId)) {
                notificationService.sendCommentNotification(
                        article.getUserId(),
                        userId,
                        article.getTitle(),
                        articleId,
                        comment.getId(),
                        content
                );
            }
        }
    }

    @Override
    @Transactional
    public void createReply(Long articleId, Long userId, String content, Long parentId) {
        // 验证父评论存在
        Comment parentComment = commentMapper.selectById(parentId);
        if (parentComment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "父评论不存在");
        }
        
        // 验证父评论属于同一文章
        if (!parentComment.getArticleId().equals(articleId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "评论不属于该文章");
        }

        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId);
        commentMapper.insert(comment);

        // 更新文章评论数
        Article article = articleMapper.selectById(articleId);
        if (article != null) {
            article.setCommentCount(article.getCommentCount() + 1);
            articleMapper.updateById(article);
        }

        // 发送回复通知给被回复的评论作者（如果回复者不是评论作者本人）
        if (!parentComment.getUserId().equals(userId)) {
            String articleTitle = article != null ? article.getTitle() : "未知文章";
            notificationService.sendReplyNotification(
                    parentComment.getUserId(),
                    userId,
                    articleTitle,
                    articleId,
                    comment.getId(),
                    content
            );
        }
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除此评论");
        }

        // 统计要删除的评论数（包括子评论）
        int deleteCount = countCommentsToDelete(commentId);

        // 删除评论及其所有子评论
        deleteCommentAndReplies(commentId);

        // 更新文章评论数
        Article article = articleMapper.selectById(comment.getArticleId());
        if (article != null && article.getCommentCount() >= deleteCount) {
            article.setCommentCount(article.getCommentCount() - deleteCount);
            articleMapper.updateById(article);
        }
    }

    /**
     * 统计要删除的评论数（包括子评论）
     */
    private int countCommentsToDelete(Long commentId) {
        int count = 1; // 当前评论
        List<Comment> replies = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, commentId));
        for (Comment reply : replies) {
            count += countCommentsToDelete(reply.getId());
        }
        return count;
    }

    /**
     * 递归删除评论及其所有子评论
     */
    private void deleteCommentAndReplies(Long commentId) {
        // 先删除子评论
        List<Comment> replies = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getParentId, commentId));
        for (Comment reply : replies) {
            deleteCommentAndReplies(reply.getId());
        }
        // 再删除当前评论
        commentMapper.deleteById(commentId);
    }
}
