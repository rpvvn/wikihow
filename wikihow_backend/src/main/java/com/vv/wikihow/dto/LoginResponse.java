package com.vv.wikihow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UserResponse user;
}
