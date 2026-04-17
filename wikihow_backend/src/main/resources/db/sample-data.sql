-- WikiHow 知识库系统 - 示例数据
-- 执行前请确保已运行 schema.sql 创建表结构

USE `wikihow`;

-- ===================== 示例用户 =====================
-- 密码都是: password123
INSERT INTO `user` (`username`, `email`, `password`, `nickname`, `bio`, `role`, `status`) VALUES
('zhangsan', 'zhangsan@example.com', '$2a$10$NPkttIVEqPsYI1NtLo6va.qjJmSkS9KOESqDLholg/Vr4Xk3HAE3W', '张三', '热爱生活，喜欢分享烹饪技巧', 'USER', 1),
('lisi', 'lisi@example.com', '$2a$10$NPkttIVEqPsYI1NtLo6va.qjJmSkS9KOESqDLholg/Vr4Xk3HAE3W', '李四', '程序员一枚，专注技术分享', 'USER', 1),
('wangwu', 'wangwu@example.com', '$2a$10$NPkttIVEqPsYI1NtLo6va.qjJmSkS9KOESqDLholg/Vr4Xk3HAE3W', '王五', '手工达人，DIY爱好者', 'USER', 1),
('xiaoming', 'xiaoming@example.com', '$2a$10$NPkttIVEqPsYI1NtLo6va.qjJmSkS9KOESqDLholg/Vr4Xk3HAE3W', '小明', '健身教练，分享运动知识', 'USER', 1);

-- ===================== 示例文章 =====================

-- 文章1: 如何制作番茄炒蛋 (分类: 烹饪美食, ID=4)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`, `references`) VALUES
('如何制作番茄炒蛋', '番茄炒蛋是一道简单又美味的家常菜，本教程将教你如何做出色香味俱全的番茄炒蛋。', 2, 4, '烹饪,家常菜,快手菜', 'EASY', 1256, 89, 45, 1, '[{"title":"番茄的营养价值","url":"https://zh.wikipedia.org/wiki/%E7%95%AA%E8%8C%84","author":"维基百科"},{"title":"鸡蛋的烹饪技巧","url":"https://www.xiachufang.com/","author":"下厨房"}]');

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(1, 1, '准备食材', '准备2个鸡蛋、2个番茄、适量葱花、盐、糖、食用油。番茄选择熟透的，这样口感更好。'),
(1, 2, '处理番茄', '将番茄洗净，在顶部划十字刀，用开水烫30秒后去皮，切成小块备用。'),
(1, 3, '打散鸡蛋', '将鸡蛋打入碗中，加入少许盐，用筷子充分打散，直到蛋液均匀。'),
(1, 4, '炒鸡蛋', '锅中倒入适量油，油热后倒入蛋液，用筷子快速搅动，炒至八成熟后盛出备用。'),
(1, 5, '炒番茄', '锅中再加少许油，放入番茄块翻炒，加入少许盐和糖，炒至番茄出汁变软。'),
(1, 6, '混合出锅', '将炒好的鸡蛋倒回锅中，与番茄混合翻炒均匀，撒上葱花即可出锅。');

-- 文章2: 如何学习Python编程 (分类: 编程开发, ID=6)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`, `references`) VALUES
('如何学习Python编程', 'Python是一门简单易学的编程语言，本教程将带你从零开始学习Python编程。', 3, 6, '编程,Python,入门', 'MEDIUM', 2341, 156, 98, 1, '[{"title":"Python官方文档","url":"https://docs.python.org/zh-cn/3/","author":"Python Software Foundation"},{"title":"Python教程 - 菜鸟教程","url":"https://www.runoob.com/python3/python3-tutorial.html","author":"菜鸟教程"},{"title":"VS Code Python扩展","url":"https://marketplace.visualstudio.com/items?itemName=ms-python.python","author":"Microsoft"}]');

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(2, 1, '安装Python环境', '访问Python官网(python.org)下载最新版本，安装时勾选"Add Python to PATH"选项。'),
(2, 2, '选择代码编辑器', '推荐使用VS Code或PyCharm作为代码编辑器，它们都有很好的Python支持和代码提示功能。'),
(2, 3, '学习基础语法', '从变量、数据类型、运算符开始学习，理解print()函数的使用，尝试编写Hello World程序。'),
(2, 4, '掌握控制结构', '学习if条件语句、for和while循环，理解缩进在Python中的重要性。'),
(2, 5, '学习函数和模块', '了解如何定义和调用函数，学习导入和使用Python标准库模块。'),
(2, 6, '实践项目练习', '通过实际项目来巩固所学知识，比如制作一个简单的计算器或猜数字游戏。');

-- 文章3: 如何整理房间 (分类: 家居清洁, ID=5)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`, `references`) VALUES
('如何快速整理房间', '房间乱糟糟不知从何下手？本教程教你用科学的方法快速整理房间，让家焕然一新。', 2, 5, '整理,收纳,家居', 'EASY', 876, 67, 34, 1, '[{"title":"断舍离整理法","url":"https://book.douban.com/subject/24749465/","author":"山下英子"}]');

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(3, 1, '制定整理计划', '先观察房间整体情况，确定需要整理的区域优先级，准备好垃圾袋和收纳盒。'),
(3, 2, '清理垃圾', '首先把明显的垃圾清理掉，包括废纸、空瓶子、过期物品等，这能让房间立刻清爽很多。'),
(3, 3, '物品分类', '将物品分为：保留、丢弃、捐赠三类。对于一年内没用过的东西，考虑是否真的需要。'),
(3, 4, '归位整理', '把保留的物品放回它们应该在的位置，遵循"用完即归位"的原则。'),
(3, 5, '清洁打扫', '整理完毕后，擦拭桌面、吸尘或拖地，让房间彻底干净。');

-- 文章4: 如何制作手工贺卡 (分类: 手工制作, ID=8)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何制作精美手工贺卡', '亲手制作的贺卡更有心意，本教程教你制作一张精美的立体贺卡。', 4, 8, '手工,贺卡,DIY', 'MEDIUM', 654, 45, 28, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(4, 1, '准备材料', '准备彩色卡纸、剪刀、胶水、彩笔、装饰贴纸等材料。'),
(4, 2, '裁剪卡纸', '将卡纸对折作为贺卡主体，选择喜欢的颜色，尺寸可以根据需要调整。'),
(4, 3, '设计图案', '在草稿纸上先设计好想要的图案，可以是花朵、爱心、星星等。'),
(4, 4, '制作立体部分', '用另一张卡纸剪出立体图案，折叠后粘贴在贺卡内部，打开时会弹出。'),
(4, 5, '装饰美化', '用彩笔写上祝福语，贴上装饰贴纸，可以加上亮片或丝带增加美感。');

-- 文章5: 如何开始跑步锻炼 (分类: 运动健身, ID=9)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何科学开始跑步锻炼', '跑步是最简单有效的运动方式，本教程帮助初学者科学地开始跑步锻炼。', 5, 9, '跑步,健身,运动', 'EASY', 1532, 112, 76, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(5, 1, '选择合适装备', '购买一双专业跑鞋，选择透气舒适的运动服，准备运动手表或手机记录数据。'),
(5, 2, '制定跑步计划', '初学者建议从走跑结合开始，每周3次，每次20-30分钟，逐渐增加跑步时间。'),
(5, 3, '热身准备', '跑步前进行5-10分钟热身，包括慢走、关节活动、动态拉伸等。'),
(5, 4, '掌握正确姿势', '保持身体直立微微前倾，手臂自然摆动，脚掌中部着地，呼吸均匀有节奏。'),
(5, 5, '控制配速', '初学者不要追求速度，保持能正常说话的配速，心率控制在最大心率的60-70%。'),
(5, 6, '跑后拉伸', '跑步结束后进行静态拉伸，每个动作保持15-30秒，帮助肌肉恢复。');

-- 文章6: 如何使用Git版本控制 (分类: 编程开发, ID=6)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`, `references`) VALUES
('如何使用Git进行版本控制', 'Git是程序员必备技能，本教程教你掌握Git的基本使用方法。', 3, 6, 'Git,版本控制,开发工具', 'MEDIUM', 1876, 134, 89, 1, '[{"title":"Git官方文档","url":"https://git-scm.com/doc","author":"Git"},{"title":"Pro Git 中文版","url":"https://git-scm.com/book/zh/v2","author":"Scott Chacon"},{"title":"GitHub官方指南","url":"https://docs.github.com/zh","author":"GitHub"}]');

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(6, 1, '安装Git', '从git-scm.com下载安装Git，安装完成后在命令行输入git --version验证安装成功。'),
(6, 2, '配置用户信息', '使用git config --global user.name和git config --global user.email配置你的用户名和邮箱。'),
(6, 3, '初始化仓库', '在项目目录下执行git init初始化仓库，或使用git clone克隆远程仓库。'),
(6, 4, '基本工作流程', '使用git add添加文件到暂存区，git commit提交更改，git push推送到远程仓库。'),
(6, 5, '分支管理', '使用git branch创建分支，git checkout切换分支，git merge合并分支。'),
(6, 6, '解决冲突', '当合并出现冲突时，手动编辑冲突文件，解决后重新add和commit。');

-- 文章7: 如何煮一碗好吃的面条 (分类: 烹饪美食, ID=4)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何煮一碗好吃的面条', '看似简单的煮面条其实有很多技巧，本教程教你煮出劲道爽滑的面条。', 2, 4, '面条,烹饪,主食', 'EASY', 987, 78, 42, 1);


INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(7, 1, '选择面条', '根据口味选择面条类型，细面煮得快适合汤面，宽面更有嚼劲适合拌面。'),
(7, 2, '烧开水', '用大锅烧足量的水，水要多，面条才有足够空间翻滚，不会粘连。'),
(7, 3, '下面条', '水沸腾后下面条，用筷子轻轻拨散，防止面条粘在一起。'),
(7, 4, '控制火候', '保持中大火，让水持续沸腾但不溢出，期间可加少量冷水防止溢锅。'),
(7, 5, '判断熟度', '面条浮起变软即可，可以捞一根尝试，喜欢劲道的可以稍早出锅。'),
(7, 6, '过冷水', '如果做拌面，捞出后过冷水可以让面条更劲道，汤面则直接放入汤中。');

-- 文章8: 如何设计简洁的Logo (分类: 设计创作, ID=7)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何设计简洁的Logo', '好的Logo简洁而有辨识度，本教程分享Logo设计的基本原则和方法。', 3, 7, '设计,Logo,品牌', 'HARD', 1234, 98, 67, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(8, 1, '了解品牌', '深入了解品牌的定位、价值观、目标受众，这是设计的基础。'),
(8, 2, '收集灵感', '浏览优秀的Logo设计案例，分析它们的特点，但不要抄袭。'),
(8, 3, '草图构思', '用纸笔快速画出多个创意草图，不要急于使用电脑，先发散思维。'),
(8, 4, '选择字体', '如果Logo包含文字，选择与品牌调性匹配的字体，可以适当修改。'),
(8, 5, '确定配色', '选择1-3种主色调，考虑色彩心理学，确保在不同背景下都清晰可见。'),
(8, 6, '数字化制作', '使用Illustrator等矢量软件制作，确保Logo可以无损缩放。'),
(8, 7, '测试应用', '在不同场景测试Logo效果：名片、网站、产品包装等，确保通用性。');

-- 文章9: 如何养成早起习惯 (分类: 生活技能, ID=1)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何养成早起的好习惯', '早起让你拥有更多时间，本教程分享科学的早起方法，帮你告别赖床。', 5, 1, '早起,习惯,自律', 'MEDIUM', 2156, 167, 112, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(9, 1, '设定明确目标', '想清楚为什么要早起，是为了锻炼、学习还是享受安静时光，有目标才有动力。'),
(9, 2, '循序渐进', '不要一下子提前太多，每周提前15-30分钟，让身体逐渐适应。'),
(9, 3, '固定作息时间', '每天同一时间睡觉和起床，包括周末，建立稳定的生物钟。'),
(9, 4, '优化睡眠环境', '保持卧室黑暗、安静、凉爽，睡前1小时不看手机，提高睡眠质量。'),
(9, 5, '把闹钟放远', '把闹钟放在需要起身才能关掉的地方，强迫自己离开床。'),
(9, 6, '准备早起奖励', '为自己准备喜欢的早餐或活动，让早起变成期待而不是痛苦。');

-- 文章10: 如何拍出好看的照片 (分类: 兴趣爱好, ID=3)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何用手机拍出好看的照片', '不需要专业相机，掌握这些技巧，用手机也能拍出大片效果。', 4, 3, '摄影,手机,技巧', 'EASY', 1876, 145, 98, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(10, 1, '保持镜头清洁', '拍照前用软布擦拭镜头，指纹和灰尘会严重影响画质。'),
(10, 2, '利用自然光', '尽量在自然光下拍摄，早晨和傍晚的光线最柔和，避免正午强光。'),
(10, 3, '运用构图法则', '使用三分法构图，将主体放在画面的三分线交点上，画面更有美感。'),
(10, 4, '寻找有趣角度', '尝试不同的拍摄角度，俯拍、仰拍、平视各有特点，打破常规视角。'),
(10, 5, '注意背景', '简洁的背景能突出主体，拍摄前观察背景是否有杂乱元素。'),
(10, 6, '善用手机功能', '学会使用HDR、人像模式、专业模式等功能，根据场景选择合适的模式。');

-- ===================== 示例评论 =====================
INSERT INTO `comment` (`article_id`, `user_id`, `content`, `parent_id`) VALUES
(1, 3, '按照教程做了，味道很棒！番茄一定要去皮，口感确实更好。', NULL),
(1, 4, '新手第一次做就成功了，感谢分享！', NULL),
(1, 5, '我加了一点番茄酱，颜色更红更好看。', NULL),
(2, 2, '讲解很清晰，适合零基础的人学习。', NULL),
(2, 4, 'Python确实比其他语言简单，推荐新手学习。', NULL),
(2, 5, '请问有推荐的Python学习网站吗？', NULL),
(2, 3, '可以看看菜鸟教程或者B站的视频教程。', 6),
(5, 2, '跑了一个月，感觉身体好多了！', NULL),
(5, 3, '热身真的很重要，之前不热身膝盖疼了好久。', NULL),
(6, 4, 'Git是程序员必备技能，这个教程很实用。', NULL),
(9, 2, '坚持早起一个月了，效率提高很多！', NULL),
(9, 3, '把闹钟放远这招真的有用哈哈。', NULL),
(10, 5, '三分法构图确实让照片好看很多，学到了！', NULL);

-- ===================== 示例点赞 =====================
INSERT INTO `like_record` (`user_id`, `article_id`) VALUES
(2, 1), (3, 1), (4, 1), (5, 1),
(2, 2), (4, 2), (5, 2),
(3, 3), (4, 3),
(2, 4), (3, 4), (5, 4),
(2, 5), (3, 5), (4, 5),
(2, 6), (4, 6), (5, 6),
(3, 7), (4, 7), (5, 7),
(2, 8), (4, 8),
(2, 9), (3, 9), (4, 9), (5, 9),
(2, 10), (3, 10), (4, 10);

-- ===================== 示例收藏 =====================
INSERT INTO `favorite` (`user_id`, `article_id`) VALUES
(3, 1), (4, 1), (5, 1),
(2, 2), (4, 2), (5, 2),
(2, 3), (4, 3),
(2, 4), (3, 4),
(2, 5), (3, 5), (4, 5),
(4, 6), (5, 6),
(2, 7), (3, 7),
(2, 8), (5, 8),
(2, 9), (3, 9), (5, 9),
(2, 10), (3, 10), (5, 10);
