-- 为通知表添加文章ID和评论ID字段，支持评论通知跳转功能
ALTER TABLE `notification` 
ADD COLUMN `article_id` BIGINT DEFAULT NULL COMMENT '关联文章ID' AFTER `type`,
ADD COLUMN `comment_id` BIGINT DEFAULT NULL COMMENT '关联评论ID' AFTER `article_id`;

-- 添加索引
ALTER TABLE `notification` ADD INDEX `idx_article` (`article_id`);
