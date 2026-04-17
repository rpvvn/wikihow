package com.vv.wikihow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.LoginRequest;
import com.vv.wikihow.dto.LoginResponse;
import com.vv.wikihow.dto.RegisterRequest;
import com.vv.wikihow.dto.UserResponse;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;

    /**
     * 调试密码验证（生产环境请删除）
     */
    @GetMapping("/debug-password")
    public Result<Map<String, Object>> debugPassword(@RequestParam String username, @RequestParam String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Map<String, Object> result = new HashMap<>();
        
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        
        if (user == null) {
            result.put("userFound", false);
            return Result.success(result);
        }
        
        result.put("userFound", true);
        result.put("dbPassword", user.getPassword());
        result.put("dbPasswordLength", user.getPassword().length());
        result.put("inputPassword", password);
        result.put("newEncodedPassword", encoder.encode(password));
        result.put("matchResult", encoder.matches(password, user.getPassword()));
        
        return Result.success(result);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse user = userService.register(request);
        return Result.success("注册成功", user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }
}
