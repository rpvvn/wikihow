-- WikiHow 知识库系统 - 内容审核功能数据库更新脚本

USE `wikihow`;

-- 1. 更新 user 表的 role 字段注释，支持 REVIEWER 角色
-- 角色: USER(普通用户) / REVIEWER(审核员) / ADMIN(管理员)
ALTER TABLE `user` MODIFY COLUMN `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/REVIEWER/ADMIN';

-- 2. 更新 article 表的 status 字段含义
-- 状态: 0草稿 / 1待审核 / 2已发布 / 3已拒绝 / 4已下架
ALTER TABLE `article` MODIFY COLUMN `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0草稿/1待审核/2已发布/3已拒绝/4已下架';

-- 3. 将现有已发布的文章（旧状态1）更新为新的已发布状态（2）
UPDATE `article` SET `status` = 2 WHERE `status` = 1;

-- 4. 创建文章审核记录表
CREATE TABLE IF NOT EXISTS `article_review` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `reviewer_id` BIGINT DEFAULT NULL COMMENT '审核员ID',
  `status` TINYINT NOT NULL COMMENT '审核状态: 1待审核/2通过/3拒绝',
  `comment` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_reviewer` (`reviewer_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章审核记录表';

-- 5. 添加一个测试审核员账号（密码: reviewer123）
-- 如果需要可以取消注释执行
-- INSERT INTO `user` (`username`, `email`, `password`, `nickname`, `role`) VALUES
-- ('reviewer', 'reviewer@example.com', '$2a$10$NPkttIVEqPsYI1NtLo6va.qjJmSkS9KOESqDLholg/Vr4Xk3HAE3W', '审核员', 'REVIEWER');
