package com.vv.wikihow.dto;

import com.vv.wikihow.entity.ArticleEditLock;
import com.vv.wikihow.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 编辑锁状态响应
 */
@Data
public class EditLockResponse {

    /**
     * 是否已锁定
     */
    private Boolean locked;

    /**
     * 是否是当前用户持有锁
     */
    private Boolean isOwner;

    /**
     * 锁定用户ID
     */
    private Long lockedByUserId;

    /**
     * 锁定用户名
     */
    private String lockedByUsername;

    /**
     * 锁定用户昵称
     */
    private String lockedByNickname;

    /**
     * 锁定用户头像
     */
    private String lockedByAvatar;

    /**
     * 锁定时间
     */
    private LocalDateTime lockedAt;

    /**
     * 过期时间
     */
    private LocalDateTime expiresAt;

    /**
     * 创建未锁定状态响应
     */
    public static EditLockResponse unlocked() {
        EditLockResponse response = new EditLockResponse();
        response.setLocked(false);
        response.setIsOwner(false);
        return response;
    }

    /**
     * 从实体创建响应
     */
    public static EditLockResponse fromEntity(ArticleEditLock lock, User lockOwner, Long currentUserId) {
        EditLockResponse response = new EditLockResponse();
        response.setLocked(true);
        response.setIsOwner(lock.getUserId().equals(currentUserId));
        response.setLockedByUserId(lock.getUserId());
        response.setLockedAt(lock.getLockedAt());
        response.setExpiresAt(lock.getExpiresAt());

        if (lockOwner != null) {
            response.setLockedByUsername(lockOwner.getUsername());
            response.setLockedByNickname(lockOwner.getNickname());
            response.setLockedByAvatar(lockOwner.getAvatar());
        }

        return response;
    }
}
