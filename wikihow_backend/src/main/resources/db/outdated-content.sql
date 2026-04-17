-- 过时内容标记功能 - 数据库修改脚本
-- Requirements: 设计要求 - 过时内容标记

USE `wikihow`;

-- 1. 在 article 表添加过时标记相关字段
ALTER TABLE `article` 
ADD COLUMN `is_outdated` TINYINT NOT NULL DEFAULT 0 COMMENT '是否过时: 0否/1是' AFTER `status`,
ADD COLUMN `outdated_reason` VARCHAR(500) DEFAULT NULL COMMENT '过时原因' AFTER `is_outdated`;

-- 2. 创建过时举报表
CREATE TABLE IF NOT EXISTS `outdated_report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '举报用户ID',
  `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '处理状态: 0待处理/1已处理/2已忽略',
  `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
  `handle_comment` VARCHAR(500) DEFAULT NULL COMMENT '处理备注',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='过时内容举报表';
