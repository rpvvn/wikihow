package com.vv.wikihow.service;

import com.vv.wikihow.dto.LoginRequest;
import com.vv.wikihow.dto.LoginResponse;
import com.vv.wikihow.dto.RegisterRequest;
import com.vv.wikihow.dto.UserResponse;
import com.vv.wikihow.dto.UserStatsResponse;
import com.vv.wikihow.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 获取用户信息响应
     */
    UserResponse getUserResponseById(Long id);

    /**
     * 更新用户信息
     */
    UserResponse updateUser(Long id, User user);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 获取用户统计数据
     */
    UserStatsResponse getUserStats(Long userId);
}
