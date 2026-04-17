-- WikiHow 知识库系统 - 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS `wikihow` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `wikihow`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用/1正常',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '分类封面图',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父分类ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 文章表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `user_id` BIGINT NOT NULL COMMENT '作者ID',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `difficulty` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '难度: EASY/MEDIUM/HARD',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览数',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0草稿/1已发布/2下架',
  `references` TEXT DEFAULT NULL COMMENT '参考文献(JSON格式)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';


-- 文章步骤表
CREATE TABLE IF NOT EXISTS `article_step` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `step_order` INT NOT NULL COMMENT '步骤序号',
  `title` VARCHAR(200) NOT NULL COMMENT '步骤标题',
  `content` TEXT NOT NULL COMMENT '步骤内容',
  `image` VARCHAR(255) DEFAULT NULL COMMENT '步骤图片',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章步骤表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID(回复)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- 点赞表
CREATE TABLE IF NOT EXISTS `like_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_article` (`user_id`, `article_id`),
  KEY `idx_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 系统通知表
CREATE TABLE IF NOT EXISTS `notification` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
  `sender_id` BIGINT DEFAULT NULL COMMENT '发送者ID(系统通知为NULL或管理员ID)',
  `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
  `content` VARCHAR(500) NOT NULL COMMENT '通知内容',
  `type` VARCHAR(20) NOT NULL DEFAULT 'SYSTEM' COMMENT '通知类型: SYSTEM/WELCOME/ADMIN',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读/1已读',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_created` (`created_at`),
  KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- ===================== 初始化数据 =====================

-- 默认分类（一级）
INSERT INTO `category` (`name`, `parent_id`, `sort_order`) VALUES
('生活技能', NULL, 1),
('技术教程', NULL, 2),
('兴趣爱好', NULL, 3);

-- 默认分类（二级）
INSERT INTO `category` (`name`, `parent_id`, `sort_order`) VALUES
('烹饪美食', 1, 1),
('家居清洁', 1, 2),
('编程开发', 2, 1),
('设计创作', 2, 2),
('手工制作', 3, 1),
('运动健身', 3, 2);

-- 默认管理员（密码: admin123）
-- BCrypt 哈希值由 BCryptPasswordEncoder 生成
INSERT INTO `user` (`username`, `email`, `password`, `nickname`, `role`) VALUES
('admin', 'admin@example.com', '$2a$10$NPkttIVEqPsYI1NtLo6va.qjJmSkS9KOESqDLholg/Vr4Xk3HAE3W', '管理员', 'ADMIN');
