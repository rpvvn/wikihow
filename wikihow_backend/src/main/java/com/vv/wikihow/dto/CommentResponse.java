package com.vv.wikihow.dto;

import com.vv.wikihow.entity.Comment;
import com.vv.wikihow.entity.User;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentResponse {
    private Long id;
    private Long articleId;
    private String content;
    private UserInfo user;
    private Long parentId;
    private LocalDateTime createdAt;
    
    /** 子评论（回复）列表 */
    private List<CommentResponse> replies = new ArrayList<>();

    @Data
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String avatar;
    }

    public static CommentResponse fromComment(Comment comment, User user) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setArticleId(comment.getArticleId());
        response.setContent(comment.getContent());
        response.setParentId(comment.getParentId());
        response.setCreatedAt(comment.getCreatedAt());
        
        UserInfo userInfo = new UserInfo();
        if (user != null) {
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatar(user.getAvatar());
        }
        response.setUser(userInfo);
        
        return response;
    }
}
