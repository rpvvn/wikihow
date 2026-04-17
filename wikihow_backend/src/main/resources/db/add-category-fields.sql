-- 为 category 表添加 description 和 cover_image 字段
-- 请在 MySQL 中执行此脚本

USE `wikihow`;

-- 添加 description 字段
ALTER TABLE `category` ADD COLUMN `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述' AFTER `name`;

-- 添加 cover_image 字段
ALTER TABLE `category` ADD COLUMN `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '分类封面图' AFTER `description`;

-- 如果字段已存在会报错 "Duplicate column name"，可以忽略

-- 验证字段是否添加成功
DESCRIBE `category`;
