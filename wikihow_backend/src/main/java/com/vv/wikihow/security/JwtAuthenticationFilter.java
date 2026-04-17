package com.vv.wikihow.security;

import com.vv.wikihow.common.UserRole;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.mapper.UserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JWT 认证过滤器
 * 解析 Token 并设置 Spring Security 上下文
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            
            if (user != null && user.getStatus() == 1) {
                // 设置 UserContext（保持兼容）
                UserContext.setCurrentUserId(userId);
                
                // 构建权限列表
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                
                if (UserRole.REVIEWER.equals(user.getRole())) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_REVIEWER"));
                }
                if (UserRole.ADMIN.equals(user.getRole())) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_REVIEWER"));
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                
                // 创建认证对象并设置到 SecurityContext.
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 清理上下文.
            UserContext.clear();
            SecurityContextHolder.clearContext();
        }
    }
// TEST git
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
