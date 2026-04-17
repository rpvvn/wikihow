package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.ChangePasswordRequest;
import com.vv.wikihow.dto.UserResponse;
import com.vv.wikihow.dto.UserStatsResponse;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public Result<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserResponseById(id);
        return Result.success(user);
    }

    /**
     * 更新用户信息（需要登录，只能更新自己）
     */
    @PutMapping("/{id}")
    public Result<UserResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        Long currentUserId = UserContext.getCurrentUserId();
        if (!id.equals(currentUserId)) {
            return Result.error("无权修改他人信息");
        }
        UserResponse updated = userService.updateUser(id, user);
        return Result.success("更新成功", updated);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser() {
        Long userId = UserContext.getCurrentUserId();
        UserResponse user = userService.getUserResponseById(userId);
        return Result.success(user);
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/{id}/stats")
    public Result<UserStatsResponse> getUserStats(@PathVariable Long id) {
        UserStatsResponse stats = userService.getUserStats(id);
        return Result.success(stats);
    }

    /**
     * 修改密码（需要登录，只能修改自己的密码）
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        Long currentUserId = UserContext.getCurrentUserId();
        userService.changePassword(currentUserId, request.getOldPassword(), request.getNewPassword());
        return Result.success("密码修改成功", null);
    }
}
