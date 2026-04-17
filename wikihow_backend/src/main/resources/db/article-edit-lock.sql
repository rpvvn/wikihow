-- 文章编辑锁表 - 用于编辑冲突调解
-- 当用户开始编辑文章时获取锁，防止多人同时编辑同一篇文章

CREATE TABLE IF NOT EXISTS `article_edit_lock` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '锁定用户ID',
  `locked_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '锁定时间',
  `expires_at` DATETIME NOT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article` (`article_id`) COMMENT '每篇文章只能有一个锁',
  KEY `idx_user` (`user_id`),
  KEY `idx_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章编辑锁表';
