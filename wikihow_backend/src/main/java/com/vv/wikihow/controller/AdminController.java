package com.vv.wikihow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.ArticleListResponse;
import com.vv.wikihow.dto.DashboardStatsResponse;
import com.vv.wikihow.dto.PageResponse;
import com.vv.wikihow.entity.*;
import com.vv.wikihow.mapper.*;
import com.vv.wikihow.security.UserContext;
import com.vv.wikihow.service.ArticleService;
import com.vv.wikihow.service.NotificationService;
import com.vv.wikihow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员控制器
 * 所有接口需要管理员权限
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserMapper userMapper;
    private final UserService userService;
    private final ArticleService articleService;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final CommentMapper commentMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 创建用户（管理员）
     */
    @PostMapping("/users")
    public Result<Void> createUser(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        String role = body.getOrDefault("role", "USER");
        
        // 检查用户名是否已存在
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username)) > 0) {
            return Result.error("用户名已存在");
        }
        // 检查邮箱是否已存在
        if (userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, email)) > 0) {
            return Result.error("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(username);
        user.setRole(role);
        user.setStatus(1);
        userMapper.insert(user);
        
        // 发送欢迎通知
        notificationService.sendWelcomeNotification(user.getId());
        
        return Result.success("创建成功", null);
    }

    /**
     * 重置用户密码（管理员）
     */
    @PutMapping("/users/{id}/password")
    public Result<Void> resetUserPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (!StringUtils.hasText(newPassword) || newPassword.length() < 6) {
            return Result.error("密码长度至少6位");
        }
        
        User user = userService.getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return Result.success("密码重置成功", null);
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Long>> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("articleCount", articleMapper.selectCount(null));
        stats.put("categoryCount", categoryMapper.selectCount(null));
        stats.put("commentCount", commentMapper.selectCount(null));
        return Result.success(stats);
    }

    /**
     * 获取详细统计数据（数据概览页面）
     */
    @GetMapping("/dashboard-stats")
    public Result<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();
        
        // 基础统计
        stats.setUserCount(userMapper.selectCount(null));
        stats.setArticleCount(articleMapper.selectCount(null));
        stats.setCategoryCount(categoryMapper.selectCount(null));
        stats.setCommentCount(commentMapper.selectCount(null));
        
        // 文章状态统计
        stats.setDraftCount(articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getStatus, 0)));
        stats.setPendingCount(articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1)));
        stats.setPublishedCount(articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getStatus, 2)));
        stats.setRejectedCount(articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getStatus, 3)));
        stats.setOfflineCount(articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getStatus, 4)));
        stats.setOutdatedCount(articleMapper.selectCount(
            new LambdaQueryWrapper<Article>().eq(Article::getIsOutdated, 1)));
        
        // 文章状态分布（饼图数据）
        stats.setArticleStatusChart(List.of(
            new DashboardStatsResponse.PieChartData("草稿", stats.getDraftCount()),
            new DashboardStatsResponse.PieChartData("待审核", stats.getPendingCount()),
            new DashboardStatsResponse.PieChartData("已发布", stats.getPublishedCount()),
            new DashboardStatsResponse.PieChartData("已拒绝", stats.getRejectedCount()),
            new DashboardStatsResponse.PieChartData("已下架", stats.getOfflineCount())
        ));
        
        // 最近7天文章发布趋势
        List<DashboardStatsResponse.LineChartData> articleTrend = getArticleTrendData();
        stats.setArticleTrendChart(articleTrend);
        
        // 最近7天用户注册趋势
        List<DashboardStatsResponse.LineChartData> userTrend = getUserTrendData();
        stats.setUserTrendChart(userTrend);
        
        // 分类文章数量统计
        List<DashboardStatsResponse.BarChartData> categoryData = getCategoryStatsData();
        stats.setCategoryChart(categoryData);
        
        return Result.success(stats);
    }
    
    /**
     * 获取最近7天文章发布趋势数据
     */
    private List<DashboardStatsResponse.LineChartData> getArticleTrendData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        return LocalDate.now().minusDays(6).datesUntil(LocalDate.now().plusDays(1))
                .map(date -> {
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.atTime(23, 59, 59);
                    
                    Long count = articleMapper.selectCount(
                        new LambdaQueryWrapper<Article>()
                            .between(Article::getCreatedAt, startOfDay, endOfDay)
                    );
                    
                    return new DashboardStatsResponse.LineChartData(
                        date.format(formatter), count);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取最近7天用户注册趋势数据
     */
    private List<DashboardStatsResponse.LineChartData> getUserTrendData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        return LocalDate.now().minusDays(6).datesUntil(LocalDate.now().plusDays(1))
                .map(date -> {
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.atTime(23, 59, 59);
                    
                    Long count = userMapper.selectCount(
                        new LambdaQueryWrapper<User>()
                            .between(User::getCreatedAt, startOfDay, endOfDay)
                    );
                    
                    return new DashboardStatsResponse.LineChartData(
                        date.format(formatter), count);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 获取分类文章数量统计数据
     */
    private List<DashboardStatsResponse.BarChartData> getCategoryStatsData() {
        List<Category> categories = categoryMapper.selectList(
            new LambdaQueryWrapper<Category>()
                .isNull(Category::getParentId)
                .orderByAsc(Category::getSortOrder)
        );
        
        return categories.stream()
                .map(category -> {
                    Long count = articleMapper.selectCount(
                        new LambdaQueryWrapper<Article>()
                            .eq(Article::getCategoryId, category.getId())
                            .eq(Article::getStatus, 2) // 只统计已发布的文章
                    );
                    return new DashboardStatsResponse.BarChartData(category.getName(), count);
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/users")
    public Result<PageResponse<User>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status) {
        
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .like(StringUtils.hasText(keyword), User::getUsername, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), User::getEmail, keyword)
                .eq(StringUtils.hasText(role), User::getRole, role)
                .eq(status != null, User::getStatus, status)
                .orderByDesc(User::getCreatedAt);

        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        return Result.success(PageResponse.of(result.getRecords(), result.getTotal(), page, size));
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User user = userService.getUserById(id);
        if (body.containsKey("nickname")) user.setNickname(body.get("nickname"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("bio")) user.setBio(body.get("bio"));
        userMapper.updateById(user);
        return Result.success("更新成功", null);
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        User user = userService.getUserById(id);
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success("操作成功", null);
    }

    /**
     * 更新用户角色
     */
    @PutMapping("/users/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String role = body.get("role");
        User user = userService.getUserById(id);
        user.setRole(role);
        userMapper.updateById(user);
        return Result.success("权限设置成功", null);
    }

    /**
     * 获取文章列表（管理员）
     */
    @GetMapping("/articles")
    public Result<PageResponse<ArticleListResponse>> getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        
        Page<Article> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .like(StringUtils.hasText(keyword), Article::getTitle, keyword)
                .eq(categoryId != null, Article::getCategoryId, categoryId)
                .eq(status != null, Article::getStatus, status)
                .orderByDesc(Article::getCreatedAt);

        Page<Article> result = articleMapper.selectPage(pageParam, wrapper);
        
        // 转换为响应对象
        List<Long> userIds = result.getRecords().stream().map(Article::getUserId).distinct().toList();
        List<Long> categoryIds = result.getRecords().stream().map(Article::getCategoryId).distinct().toList();
        
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() : 
            userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Category> categoryMap = categoryIds.isEmpty() ? Map.of() :
            categoryMapper.selectBatchIds(categoryIds).stream().collect(Collectors.toMap(Category::getId, c -> c));
        
        List<ArticleListResponse> list = result.getRecords().stream()
                .map(a -> ArticleListResponse.fromArticle(a, userMap.get(a.getUserId()), categoryMap.get(a.getCategoryId())))
                .toList();

        return Result.success(PageResponse.of(list, result.getTotal(), page, size));
    }

    /**
     * 更新文章状态
     */
    @PutMapping("/articles/{id}/status")
    public Result<Void> updateArticleStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        String reason = (String) body.get("reason");
        
        Article article = articleMapper.selectById(id);
        if (article != null) {
            Integer oldStatus = article.getStatus();
            article.setStatus(status);
            articleMapper.updateById(article);
            
            // 如果是下架操作（状态变为4），发送通知给作者
            if (status == 4 && oldStatus != 4) {
                notificationService.sendArticleOfflineNotification(
                        article.getUserId(), 
                        article.getTitle(), 
                        reason);
            }
        }
        return Result.success("操作成功", null);
    }

    /**
     * 删除文章（管理员）
     */
    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        Long userId = UserContext.getCurrentUserId();
        articleService.deleteArticle(id, userId, true);
        return Result.success("删除成功", null);
    }

    /**
     * 获取评论列表
     */
    @GetMapping("/comments")
    public Result<PageResponse<Map<String, Object>>> getComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "15") Integer size,
            @RequestParam(required = false) String keyword) {
        
        Page<Comment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .like(StringUtils.hasText(keyword), Comment::getContent, keyword)
                .orderByDesc(Comment::getCreatedAt);

        Page<Comment> result = commentMapper.selectPage(pageParam, wrapper);
        
        // 获取用户和文章信息
        List<Long> userIds = result.getRecords().stream().map(Comment::getUserId).distinct().toList();
        List<Long> articleIds = result.getRecords().stream().map(Comment::getArticleId).distinct().toList();
        
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
            userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));
        Map<Long, Article> articleMap = articleIds.isEmpty() ? Map.of() :
            articleMapper.selectBatchIds(articleIds).stream().collect(Collectors.toMap(Article::getId, a -> a));
        
        List<Map<String, Object>> list = result.getRecords().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("content", c.getContent());
            map.put("articleId", c.getArticleId());
            map.put("articleTitle", articleMap.get(c.getArticleId()) != null ? articleMap.get(c.getArticleId()).getTitle() : null);
            map.put("createdAt", c.getCreatedAt());
            User user = userMap.get(c.getUserId());
            if (user != null) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("nickname", user.getNickname());
                userInfo.put("avatar", user.getAvatar());
                map.put("user", userInfo);
            }
            return map;
        }).toList();

        return Result.success(PageResponse.of(list, result.getTotal(), page, size));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        commentMapper.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取管理员发送的通知历史
     * 由于一条广播会给每个用户创建一条记录，这里通过在Java层去重来展示
     */
    @GetMapping("/notifications")
    public Result<PageResponse<Map<String, Object>>> getNotificationHistory(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // 先查询所有管理员通知，然后在Java层去重
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<Notification>()
                .eq(Notification::getType, "ADMIN")
                .isNotNull(Notification::getSenderId)
                .orderByDesc(Notification::getCreatedAt);

        List<Notification> allNotifications = notificationMapper.selectList(wrapper);
        
        // 按标题+内容+发送者+时间去重
        List<Notification> uniqueNotifications = allNotifications.stream()
                .collect(Collectors.toMap(
                        n -> n.getTitle() + "|" + n.getContent() + "|" + n.getSenderId() + "|" + n.getCreatedAt(),
                        n -> n,
                        (existing, replacement) -> existing
                ))
                .values()
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
        
        // 手动分页
        int total = uniqueNotifications.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Notification> pagedNotifications = start < total ? uniqueNotifications.subList(start, end) : List.of();
        
        // 获取发送者信息
        List<Long> senderIds = pagedNotifications.stream()
                .map(Notification::getSenderId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        
        Map<Long, User> senderMap = senderIds.isEmpty() ? Map.of() :
                userMapper.selectBatchIds(senderIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
        
        List<Map<String, Object>> list = pagedNotifications.stream()
                .map(n -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", n.getId());
                    map.put("title", n.getTitle());
                    map.put("content", n.getContent());
                    map.put("createdAt", n.getCreatedAt());
                    User sender = n.getSenderId() != null ? senderMap.get(n.getSenderId()) : null;
                    if (sender != null) {
                        map.put("senderNickname", sender.getNickname());
                        map.put("senderAvatar", sender.getAvatar());
                    }
                    return map;
                })
                .toList();

        return Result.success(PageResponse.of(list, (long) total, page, size));
    }
}
