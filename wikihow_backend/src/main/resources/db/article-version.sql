-- WikiHow 知识库系统 - 文章版本管理表
-- 用于存储文章的历史版本，支持版本回滚功能

USE `wikihow`;

-- 文章版本表
CREATE TABLE IF NOT EXISTS `article_version` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `version_number` INT NOT NULL COMMENT '版本号',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content_snapshot` TEXT NOT NULL COMMENT '内容快照(JSON格式存储步骤)',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签',
  `difficulty` VARCHAR(20) DEFAULT NULL COMMENT '难度',
  `references` TEXT DEFAULT NULL COMMENT '参考文献(JSON格式)',
  `user_id` BIGINT NOT NULL COMMENT '修改人ID',
  `change_description` VARCHAR(500) DEFAULT NULL COMMENT '修改说明',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_article_version` (`article_id`, `version_number`),
  KEY `idx_user` (`user_id`),
  KEY `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章版本表';

-- ===================== 示例版本数据 =====================

-- 文章1: 如何制作番茄炒蛋 - 版本历史
INSERT INTO `article_version` (`article_id`, `version_number`, `title`, `summary`, `content_snapshot`, `cover_image`, `category_id`, `tags`, `difficulty`, `references`, `user_id`, `change_description`, `created_at`) VALUES
(1, 1, '如何制作番茄炒蛋', '番茄炒蛋是一道简单的家常菜。', '[{"order":1,"title":"准备食材","content":"准备2个鸡蛋、2个番茄。","image":null},{"order":2,"title":"处理番茄","content":"将番茄洗净切块。","image":null},{"order":3,"title":"打散鸡蛋","content":"将鸡蛋打入碗中打散。","image":null},{"order":4,"title":"炒制出锅","content":"先炒蛋再炒番茄，混合出锅。","image":null}]', NULL, 4, '烹饪,家常菜', 'EASY', NULL, 2, '初始版本', '2024-01-15 10:00:00'),
(1, 2, '如何制作番茄炒蛋', '番茄炒蛋是一道简单又美味的家常菜，本教程将教你如何做出色香味俱全的番茄炒蛋。', '[{"order":1,"title":"准备食材","content":"准备2个鸡蛋、2个番茄、适量葱花、盐、糖、食用油。","image":null},{"order":2,"title":"处理番茄","content":"将番茄洗净，在顶部划十字刀，用开水烫30秒后去皮，切成小块备用。","image":null},{"order":3,"title":"打散鸡蛋","content":"将鸡蛋打入碗中，加入少许盐，用筷子充分打散。","image":null},{"order":4,"title":"炒鸡蛋","content":"锅中倒入适量油，油热后倒入蛋液，炒至八成熟后盛出备用。","image":null},{"order":5,"title":"炒番茄","content":"锅中再加少许油，放入番茄块翻炒，加入少许盐和糖。","image":null},{"order":6,"title":"混合出锅","content":"将炒好的鸡蛋倒回锅中，与番茄混合翻炒均匀，撒上葱花即可出锅。","image":null}]', NULL, 4, '烹饪,家常菜,快手菜', 'EASY', NULL, 2, '完善步骤说明，增加调料细节', '2024-02-20 14:30:00'),
(1, 3, '如何制作番茄炒蛋', '番茄炒蛋是一道简单又美味的家常菜，本教程将教你如何做出色香味俱全的番茄炒蛋。', '[{"order":1,"title":"准备食材","content":"准备2个鸡蛋、2个番茄、适量葱花、盐、糖、食用油。番茄选择熟透的，这样口感更好。","image":null},{"order":2,"title":"处理番茄","content":"将番茄洗净，在顶部划十字刀，用开水烫30秒后去皮，切成小块备用。","image":null},{"order":3,"title":"打散鸡蛋","content":"将鸡蛋打入碗中，加入少许盐，用筷子充分打散，直到蛋液均匀。","image":null},{"order":4,"title":"炒鸡蛋","content":"锅中倒入适量油，油热后倒入蛋液，用筷子快速搅动，炒至八成熟后盛出备用。","image":null},{"order":5,"title":"炒番茄","content":"锅中再加少许油，放入番茄块翻炒，加入少许盐和糖，炒至番茄出汁变软。","image":null},{"order":6,"title":"混合出锅","content":"将炒好的鸡蛋倒回锅中，与番茄混合翻炒均匀，撒上葱花即可出锅。","image":null}]', NULL, 4, '烹饪,家常菜,快手菜', 'EASY', '[{"title":"番茄的营养价值","url":"https://zh.wikipedia.org/wiki/%E7%95%AA%E8%8C%84","author":"维基百科"},{"title":"鸡蛋的烹饪技巧","url":"https://www.xiachufang.com/","author":"下厨房"}]', 2, '添加参考文献，优化食材选择建议', '2024-03-10 09:15:00');

-- 文章2: 如何学习Python编程 - 版本历史
INSERT INTO `article_version` (`article_id`, `version_number`, `title`, `summary`, `content_snapshot`, `cover_image`, `category_id`, `tags`, `difficulty`, `references`, `user_id`, `change_description`, `created_at`) VALUES
(2, 1, 'Python编程入门', 'Python是一门简单的编程语言。', '[{"order":1,"title":"安装Python","content":"下载并安装Python。","image":null},{"order":2,"title":"学习基础","content":"学习变量和数据类型。","image":null},{"order":3,"title":"练习项目","content":"做一些简单的项目练习。","image":null}]', NULL, 6, '编程,Python', 'MEDIUM', NULL, 3, '初始版本', '2024-01-20 11:00:00'),
(2, 2, '如何学习Python编程', 'Python是一门简单易学的编程语言，本教程将带你从零开始学习Python编程。', '[{"order":1,"title":"安装Python环境","content":"访问Python官网下载最新版本，安装时勾选Add Python to PATH选项。","image":null},{"order":2,"title":"选择代码编辑器","content":"推荐使用VS Code或PyCharm作为代码编辑器。","image":null},{"order":3,"title":"学习基础语法","content":"从变量、数据类型、运算符开始学习。","image":null},{"order":4,"title":"掌握控制结构","content":"学习if条件语句、for和while循环。","image":null},{"order":5,"title":"学习函数和模块","content":"了解如何定义和调用函数。","image":null},{"order":6,"title":"实践项目练习","content":"通过实际项目来巩固所学知识。","image":null}]', NULL, 6, '编程,Python,入门', 'MEDIUM', NULL, 3, '重构内容结构，增加详细步骤', '2024-02-25 16:45:00'),
(2, 3, '如何学习Python编程', 'Python是一门简单易学的编程语言，本教程将带你从零开始学习Python编程。', '[{"order":1,"title":"安装Python环境","content":"访问Python官网(python.org)下载最新版本，安装时勾选\"Add Python to PATH\"选项。","image":null},{"order":2,"title":"选择代码编辑器","content":"推荐使用VS Code或PyCharm作为代码编辑器，它们都有很好的Python支持和代码提示功能。","image":null},{"order":3,"title":"学习基础语法","content":"从变量、数据类型、运算符开始学习，理解print()函数的使用，尝试编写Hello World程序。","image":null},{"order":4,"title":"掌握控制结构","content":"学习if条件语句、for和while循环，理解缩进在Python中的重要性。","image":null},{"order":5,"title":"学习函数和模块","content":"了解如何定义和调用函数，学习导入和使用Python标准库模块。","image":null},{"order":6,"title":"实践项目练习","content":"通过实际项目来巩固所学知识，比如制作一个简单的计算器或猜数字游戏。","image":null}]', NULL, 6, '编程,Python,入门', 'MEDIUM', '[{"title":"Python官方文档","url":"https://docs.python.org/zh-cn/3/","author":"Python Software Foundation"},{"title":"Python教程 - 菜鸟教程","url":"https://www.runoob.com/python3/python3-tutorial.html","author":"菜鸟教程"},{"title":"VS Code Python扩展","url":"https://marketplace.visualstudio.com/items?itemName=ms-python.python","author":"Microsoft"}]', 3, '添加参考文献，完善每个步骤的详细说明', '2024-03-15 10:30:00');

-- 文章5: 如何开始跑步锻炼 - 版本历史
INSERT INTO `article_version` (`article_id`, `version_number`, `title`, `summary`, `content_snapshot`, `cover_image`, `category_id`, `tags`, `difficulty`, `references`, `user_id`, `change_description`, `created_at`) VALUES
(5, 1, '跑步入门指南', '跑步是简单的运动方式。', '[{"order":1,"title":"准备装备","content":"买一双跑鞋。","image":null},{"order":2,"title":"开始跑步","content":"从慢跑开始。","image":null},{"order":3,"title":"坚持锻炼","content":"每周跑3次。","image":null}]', NULL, 9, '跑步,健身', 'EASY', NULL, 5, '初始版本', '2024-02-01 08:00:00'),
(5, 2, '如何科学开始跑步锻炼', '跑步是最简单有效的运动方式，本教程帮助初学者科学地开始跑步锻炼。', '[{"order":1,"title":"选择合适装备","content":"购买一双专业跑鞋，选择透气舒适的运动服，准备运动手表或手机记录数据。","image":null},{"order":2,"title":"制定跑步计划","content":"初学者建议从走跑结合开始，每周3次，每次20-30分钟，逐渐增加跑步时间。","image":null},{"order":3,"title":"热身准备","content":"跑步前进行5-10分钟热身，包括慢走、关节活动、动态拉伸等。","image":null},{"order":4,"title":"掌握正确姿势","content":"保持身体直立微微前倾，手臂自然摆动，脚掌中部着地，呼吸均匀有节奏。","image":null},{"order":5,"title":"控制配速","content":"初学者不要追求速度，保持能正常说话的配速，心率控制在最大心率的60-70%。","image":null},{"order":6,"title":"跑后拉伸","content":"跑步结束后进行静态拉伸，每个动作保持15-30秒，帮助肌肉恢复。","image":null}]', NULL, 9, '跑步,健身,运动', 'EASY', NULL, 5, '全面重写内容，增加科学训练方法', '2024-03-05 07:30:00');

-- 文章6: 如何使用Git版本控制 - 版本历史
INSERT INTO `article_version` (`article_id`, `version_number`, `title`, `summary`, `content_snapshot`, `cover_image`, `category_id`, `tags`, `difficulty`, `references`, `user_id`, `change_description`, `created_at`) VALUES
(6, 1, 'Git入门', 'Git是版本控制工具。', '[{"order":1,"title":"安装Git","content":"下载安装Git。","image":null},{"order":2,"title":"基本命令","content":"学习git add和git commit。","image":null}]', NULL, 6, 'Git,版本控制', 'MEDIUM', NULL, 3, '初始版本', '2024-01-25 13:00:00'),
(6, 2, '如何使用Git进行版本控制', 'Git是程序员必备技能，本教程教你掌握Git的基本使用方法。', '[{"order":1,"title":"安装Git","content":"从git-scm.com下载安装Git，安装完成后在命令行输入git --version验证安装成功。","image":null},{"order":2,"title":"配置用户信息","content":"使用git config --global user.name和git config --global user.email配置你的用户名和邮箱。","image":null},{"order":3,"title":"初始化仓库","content":"在项目目录下执行git init初始化仓库，或使用git clone克隆远程仓库。","image":null},{"order":4,"title":"基本工作流程","content":"使用git add添加文件到暂存区，git commit提交更改，git push推送到远程仓库。","image":null},{"order":5,"title":"分支管理","content":"使用git branch创建分支，git checkout切换分支，git merge合并分支。","image":null},{"order":6,"title":"解决冲突","content":"当合并出现冲突时，手动编辑冲突文件，解决后重新add和commit。","image":null}]', NULL, 6, 'Git,版本控制,开发工具', 'MEDIUM', '[{"title":"Git官方文档","url":"https://git-scm.com/doc","author":"Git"},{"title":"Pro Git 中文版","url":"https://git-scm.com/book/zh/v2","author":"Scott Chacon"},{"title":"GitHub官方指南","url":"https://docs.github.com/zh","author":"GitHub"}]', 3, '完善教程内容，添加参考文献', '2024-03-01 15:20:00');

-- 文章9: 如何养成早起习惯 - 版本历史
INSERT INTO `article_version` (`article_id`, `version_number`, `title`, `summary`, `content_snapshot`, `cover_image`, `category_id`, `tags`, `difficulty`, `references`, `user_id`, `change_description`, `created_at`) VALUES
(9, 1, '早起的方法', '早起让你更有效率。', '[{"order":1,"title":"设定闹钟","content":"设定一个固定的起床时间。","image":null},{"order":2,"title":"早睡早起","content":"晚上早点睡觉。","image":null}]', NULL, 1, '早起,习惯', 'MEDIUM', NULL, 5, '初始版本', '2024-02-10 06:00:00'),
(9, 2, '如何养成早起的好习惯', '早起让你拥有更多时间，本教程分享科学的早起方法，帮你告别赖床。', '[{"order":1,"title":"设定明确目标","content":"想清楚为什么要早起，是为了锻炼、学习还是享受安静时光，有目标才有动力。","image":null},{"order":2,"title":"循序渐进","content":"不要一下子提前太多，每周提前15-30分钟，让身体逐渐适应。","image":null},{"order":3,"title":"固定作息时间","content":"每天同一时间睡觉和起床，包括周末，建立稳定的生物钟。","image":null},{"order":4,"title":"优化睡眠环境","content":"保持卧室黑暗、安静、凉爽，睡前1小时不看手机，提高睡眠质量。","image":null},{"order":5,"title":"把闹钟放远","content":"把闹钟放在需要起身才能关掉的地方，强迫自己离开床。","image":null},{"order":6,"title":"准备早起奖励","content":"为自己准备喜欢的早餐或活动，让早起变成期待而不是痛苦。","image":null}]', NULL, 1, '早起,习惯,自律', 'MEDIUM', NULL, 5, '重新编写内容，增加实用技巧', '2024-03-08 05:45:00');
