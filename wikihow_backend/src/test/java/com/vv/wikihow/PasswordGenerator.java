package com.vv.wikihow;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具 - 用于生成 BCrypt 哈希值
 * 运行此类的 main 方法即可获取密码的哈希值
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "admin123";
        String encoded = encoder.encode(password);
        
        System.out.println("原始密码: " + password);
        System.out.println("BCrypt哈希: " + encoded);
        System.out.println();
        System.out.println("SQL更新语句:");
        System.out.println("UPDATE user SET password = '" + encoded + "' WHERE username = 'admin';");
    }
}
