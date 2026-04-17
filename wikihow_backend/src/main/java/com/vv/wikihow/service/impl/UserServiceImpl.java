package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.LoginRequest;
import com.vv.wikihow.dto.LoginResponse;
import com.vv.wikihow.dto.RegisterRequest;
import com.vv.wikihow.dto.UserResponse;
import com.vv.wikihow.dto.UserStatsResponse;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.Favorite;
import com.vv.wikihow.entity.User;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.FavoriteMapper;
import com.vv.wikihow.mapper.UserMapper;
import com.vv.wikihow.security.JwtUtil;
import com.vv.wikihow.service.NotificationService;
import com.vv.wikihow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final FavoriteMapper favoriteMapper;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getUsername());
        user.setRole("USER");
        user.setStatus(1);

        userMapper.insert(user);
        
        // 发送欢迎通知
        notificationService.sendWelcomeNotification(user.getId());
        
        return UserResponse.fromEntity(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 支持用户名或邮箱登录
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, request.getUsername())
                .or()
                .eq(User::getEmail, request.getUsername()));

        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, UserResponse.fromEntity(user));
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    @Override
    public UserResponse getUserResponseById(Long id) {
        return UserResponse.fromEntity(getUserById(id));
    }

    @Override
    public UserResponse updateUser(Long id, User updateUser) {
        User user = getUserById(id);
        
        if (updateUser.getNickname() != null) {
            user.setNickname(updateUser.getNickname());
        }
        if (updateUser.getAvatar() != null) {
            user.setAvatar(updateUser.getAvatar());
        }
        if (updateUser.getBio() != null) {
            user.setBio(updateUser.getBio());
        }

        userMapper.updateById(user);
        return UserResponse.fromEntity(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    private boolean existsByUsername(String username) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)) > 0;
    }

    private boolean existsByEmail(String email) {
        return userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)) > 0;
    }
    
    @Override
    public UserStatsResponse getUserStats(Long userId) {
        UserStatsResponse stats = new UserStatsResponse();
        
        // 获取用户发布的所有文章
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getUserId, userId));
        
        // 文章总数
        stats.setArticleCount(articles.size());
        
        // 计算总点赞数、总收藏数、总浏览量
        int totalLikes = 0;
        int totalFavorites = 0;
        int totalViews = 0;
        for (Article article : articles) {
            totalLikes += article.getLikeCount() != null ? article.getLikeCount() : 0;
            totalFavorites += article.getFavoriteCount() != null ? article.getFavoriteCount() : 0;
            totalViews += article.getViewCount() != null ? article.getViewCount() : 0;
        }
        stats.setTotalLikes(totalLikes);
        stats.setTotalFavorites(totalFavorites);
        stats.setTotalViews(totalViews);
        
        // 用户收藏的文章数
        Long favoriteCount = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId));
        stats.setFavoriteCount(favoriteCount.intValue());
        
        return stats;
    }
}
