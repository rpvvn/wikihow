package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.NotificationRequest;
import com.vv.wikihow.dto.NotificationResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取当前用户的通知列表
     */
    @GetMapping
    public Result<PageResponse<NotificationResponse>> getNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Boolean unreadOnly) {
        Long userId = UserContext.getCurrentUserId();
        PageResponse<NotificationResponse> notifications = notificationService.getUserNotifications(userId, page, size, unreadOnly);
        return Result.success(notifications);
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Long userId = UserContext.getCurrentUserId();
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 标记单个通知为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return Result.success("已标记为已读", null);
    }

    /**
     * 标记所有通知为已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = UserContext.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return Result.success("已全部标记为已读", null);
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        notificationService.deleteNotification(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * 管理员发送全站通知
     */
    @PostMapping("/broadcast")
    public Result<Void> broadcastNotification(@Valid @RequestBody NotificationRequest request) {
        Long senderId = UserContext.getCurrentUserId();
        notificationService.sendAdminNotification(senderId, request.getTitle(), request.getContent());
        return Result.success("通知发送成功", null);
    }
}
