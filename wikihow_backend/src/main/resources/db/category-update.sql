-- WikiHow 知识库系统 - 分类数据更新脚本
-- 为分类表添加 description 和 cover_image 字段

USE `wikihow`;

-- 添加新字段（MySQL 兼容写法）
-- 如果字段已存在会报错，可以忽略或先检查
ALTER TABLE `category` ADD COLUMN `description` VARCHAR(500) DEFAULT NULL COMMENT '分类描述' AFTER `name`;
ALTER TABLE `category` ADD COLUMN `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '分类封面图' AFTER `description`;

-- 更新一级分类数据
UPDATE `category` SET 
  `description` = '掌握实用的生活技能，让日常生活更加便捷高效。从烹饪美食到家居清洁，从理财规划到人际交往，这里有你需要的一切生活智慧。无论你想学习如何做一道美味的菜肴，还是想了解如何更好地管理时间，生活技能分类都能帮助你成为更好的自己！',
  `cover_image` = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=1200'
WHERE `id` = 1;

UPDATE `category` SET 
  `description` = '探索技术的无限可能，从编程开发到设计创作，从数据分析到人工智能。无论你是初学者还是资深开发者，这里都有适合你的教程。学习最新的技术趋势，掌握实用的开发技巧，让技术成为你实现梦想的工具！',
  `cover_image` = 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=1200'
WHERE `id` = 2;

UPDATE `category` SET 
  `description` = '发现生活中的乐趣，培养属于自己的兴趣爱好。从手工制作到运动健身，从摄影绘画到音乐舞蹈，找到让你快乐的事情。兴趣爱好不仅能丰富你的生活，还能帮助你结交志同道合的朋友，让生活更加精彩！',
  `cover_image` = 'https://images.unsplash.com/photo-1513364776144-60967b0f800f?w=1200'
WHERE `id` = 3;

-- 更新二级分类数据
UPDATE `category` SET 
  `description` = '学习各种美食的制作方法，从家常菜到特色小吃，从中餐到西餐，让你成为厨房达人。掌握烹饪技巧，享受美食带来的幸福感！',
  `cover_image` = 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800'
WHERE `id` = 4;

UPDATE `category` SET 
  `description` = '打造整洁舒适的居住环境，学习高效的清洁方法和收纳技巧。让你的家焕然一新，享受干净整洁带来的好心情！',
  `cover_image` = 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800'
WHERE `id` = 5;

UPDATE `category` SET 
  `description` = '从零开始学习编程，掌握Python、Java、JavaScript等热门编程语言。无论是Web开发、移动应用还是数据分析，这里都有你需要的教程！',
  `cover_image` = 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=800'
WHERE `id` = 6;

UPDATE `category` SET 
  `description` = '释放你的创意潜能，学习平面设计、UI设计、视频剪辑等技能。用设计表达想法，让创意变成现实！',
  `cover_image` = 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800'
WHERE `id` = 7;

UPDATE `category` SET 
  `description` = '动手创造属于自己的作品，从纸艺到编织，从木工到陶艺。享受手工制作的乐趣，感受创造的成就感！',
  `cover_image` = 'https://images.unsplash.com/photo-1452860606245-08befc0ff44b?w=800'
WHERE `id` = 8;

UPDATE `category` SET 
  `description` = '科学健身，健康生活。学习正确的运动方法，制定适合自己的健身计划。无论是减脂增肌还是提升体能，这里都有专业指导！',
  `cover_image` = 'https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=800'
WHERE `id` = 9;

-- 插入更多示例文章

-- 文章11: 如何做红烧肉 (分类: 烹饪美食, ID=4)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何做出软糯入味的红烧肉', '红烧肉是中华美食的经典代表，本教程教你做出肥而不腻、入口即化的红烧肉。', 2, 4, '红烧肉,家常菜,肉类', 'MEDIUM', 2345, 189, 134, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '选择五花肉', '选择肥瘦相间的五花肉，肥肉和瘦肉比例约为3:7最佳，切成3厘米见方的块。'),
(LAST_INSERT_ID(), 2, '焯水去腥', '冷水下锅，加入姜片和料酒，煮沸后撇去浮沫，捞出洗净备用。'),
(LAST_INSERT_ID(), 3, '炒糖色', '锅中放少许油，加入冰糖小火慢炒至焦糖色，这是红烧肉上色的关键。'),
(LAST_INSERT_ID(), 4, '翻炒上色', '放入五花肉快速翻炒，让每块肉都均匀裹上糖色。'),
(LAST_INSERT_ID(), 5, '加调料炖煮', '加入生抽、老抽、料酒、八角、桂皮，倒入开水没过肉，大火烧开后转小火炖1.5小时。'),
(LAST_INSERT_ID(), 6, '收汁出锅', '大火收汁至汤汁浓稠，撒上葱花即可出锅。');

-- 文章12: 如何学习JavaScript (分类: 编程开发, ID=6)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('JavaScript入门到精通指南', 'JavaScript是Web开发必备语言，本教程带你系统学习JS，成为前端开发高手。', 3, 6, 'JavaScript,前端,Web开发', 'MEDIUM', 3456, 267, 198, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '了解JavaScript', 'JavaScript是一种脚本语言，主要用于网页交互，也可用于服务端开发(Node.js)。'),
(LAST_INSERT_ID(), 2, '学习基础语法', '掌握变量声明(var/let/const)、数据类型、运算符、条件语句和循环。'),
(LAST_INSERT_ID(), 3, '理解函数', '学习函数定义、参数传递、返回值、箭头函数和闭包等概念。'),
(LAST_INSERT_ID(), 4, '掌握DOM操作', '学习如何通过JavaScript操作HTML元素，实现页面动态效果。'),
(LAST_INSERT_ID(), 5, '学习异步编程', '理解回调函数、Promise、async/await，处理异步操作。'),
(LAST_INSERT_ID(), 6, '实战项目练习', '通过制作待办事项、轮播图等小项目巩固所学知识。');

-- 文章13: 如何清洁厨房 (分类: 家居清洁, ID=5)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('厨房深度清洁全攻略', '厨房是油烟重灾区，本教程教你如何高效清洁厨房各个角落，还你一个干净整洁的烹饪空间。', 2, 5, '厨房,清洁,去油污', 'MEDIUM', 1567, 123, 87, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '准备清洁工具', '准备小苏打、白醋、洗洁精、百洁布、喷壶、橡胶手套等清洁用品。'),
(LAST_INSERT_ID(), 2, '清洁油烟机', '拆下油网浸泡在热水+洗洁精中，用小苏打糊涂抹机身，静置后擦拭。'),
(LAST_INSERT_ID(), 3, '清洁灶台', '用小苏打+白醋喷洒灶台，静置5分钟后用百洁布擦拭，顽固污渍可用刮刀。'),
(LAST_INSERT_ID(), 4, '清洁水槽', '撒上小苏打，用柠檬片擦拭，既能去污又能除味，最后用清水冲洗。'),
(LAST_INSERT_ID(), 5, '清洁墙面瓷砖', '用热水+洗洁精喷洒，从上往下擦拭，缝隙处用旧牙刷清理。'),
(LAST_INSERT_ID(), 6, '整理收纳', '清洁完毕后整理厨具，常用物品放在顺手位置，保持台面整洁。');

-- 文章14: 如何制作手工皂 (分类: 手工制作, ID=8)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('天然手工皂制作教程', '自己动手制作天然手工皂，无添加更健康，还可以根据喜好添加精油和花瓣。', 4, 8, '手工皂,DIY,护肤', 'HARD', 987, 76, 54, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '准备材料', '准备皂基、精油、干花、模具、量杯、搅拌棒、温度计等材料。'),
(LAST_INSERT_ID(), 2, '切割皂基', '将皂基切成小块，方便融化，大小约2厘米见方。'),
(LAST_INSERT_ID(), 3, '隔水加热', '将皂基放入容器，隔水加热至完全融化，温度控制在60-70度。'),
(LAST_INSERT_ID(), 4, '添加配料', '待温度降至50度左右，加入精油和色素，搅拌均匀。'),
(LAST_INSERT_ID(), 5, '入模定型', '将皂液倒入模具，可在表面撒上干花装饰，静置冷却。'),
(LAST_INSERT_ID(), 6, '脱模保存', '完全凝固后脱模，放置通风处晾干1-2周后即可使用。');

-- 文章15: 如何做瑜伽入门 (分类: 运动健身, ID=9)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('瑜伽入门：从零开始的练习指南', '瑜伽能帮助放松身心、提升柔韧性，本教程教你从零开始学习瑜伽基础动作。', 5, 9, '瑜伽,健身,放松', 'EASY', 2134, 178, 145, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '准备瑜伽垫', '选择厚度适中、防滑的瑜伽垫，穿着舒适透气的运动服。'),
(LAST_INSERT_ID(), 2, '学习呼吸法', '瑜伽呼吸是基础，练习腹式呼吸，吸气时腹部鼓起，呼气时收缩。'),
(LAST_INSERT_ID(), 3, '山式站立', '双脚并拢站立，重心均匀分布，脊柱延伸，肩膀放松，这是很多体式的起点。'),
(LAST_INSERT_ID(), 4, '下犬式', '双手双脚撑地，臀部向上推，身体呈倒V字形，拉伸背部和腿部。'),
(LAST_INSERT_ID(), 5, '婴儿式', '跪坐后身体前倾，额头贴地，双臂前伸或放身侧，是很好的休息体式。'),
(LAST_INSERT_ID(), 6, '坚持练习', '每天练习15-30分钟，循序渐进，不要强迫身体做超出能力的动作。');

-- 文章16: 如何使用Photoshop (分类: 设计创作, ID=7)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('Photoshop新手入门教程', 'Photoshop是最强大的图像处理软件，本教程带你快速上手PS基础操作。', 3, 7, 'Photoshop,设计,修图', 'MEDIUM', 2876, 234, 167, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '认识界面', '了解PS的工作区布局：菜单栏、工具栏、选项栏、面板区和画布区。'),
(LAST_INSERT_ID(), 2, '学习图层', '图层是PS的核心概念，理解图层的叠加、混合模式和不透明度。'),
(LAST_INSERT_ID(), 3, '掌握选区工具', '学习矩形选框、套索、魔棒等选区工具，精确选择编辑区域。'),
(LAST_INSERT_ID(), 4, '使用画笔工具', '了解画笔的大小、硬度、不透明度设置，练习基础绑画和涂抹。'),
(LAST_INSERT_ID(), 5, '学习调色', '掌握色阶、曲线、色相/饱和度等调色工具，优化图片色彩。'),
(LAST_INSERT_ID(), 6, '导出保存', '了解不同格式的区别(PSD/JPG/PNG)，根据用途选择合适的导出设置。');

-- 文章17: 如何做蛋糕 (分类: 烹饪美食, ID=4)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('零失败戚风蛋糕制作教程', '戚风蛋糕口感轻盈细腻，是烘焙入门的经典款，本教程教你做出完美的戚风蛋糕。', 2, 4, '蛋糕,烘焙,甜点', 'MEDIUM', 1876, 156, 123, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '准备材料', '鸡蛋5个、低筋面粉85g、玉米油40g、牛奶40g、细砂糖60g、柠檬汁几滴。'),
(LAST_INSERT_ID(), 2, '制作蛋黄糊', '蛋黄加20g糖搅拌，依次加入油和牛奶，筛入面粉，Z字形搅拌至无颗粒。'),
(LAST_INSERT_ID(), 3, '打发蛋白', '蛋白加柠檬汁，分三次加入40g糖，打发至硬性发泡（提起有小尖角）。'),
(LAST_INSERT_ID(), 4, '混合面糊', '取1/3蛋白到蛋黄糊中翻拌均匀，再倒回蛋白盆中，翻拌至无白色纹路。'),
(LAST_INSERT_ID(), 5, '烘烤', '倒入模具，轻震排气，放入预热好的烤箱，150度烤60分钟。'),
(LAST_INSERT_ID(), 6, '脱模', '出炉后立即倒扣，完全冷却后脱模，切块享用。');

-- 文章18: 如何学习数据结构 (分类: 编程开发, ID=6)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('数据结构入门：程序员必备基础', '数据结构是编程的基石，本教程带你理解常用数据结构的原理和应用场景。', 3, 6, '数据结构,算法,编程基础', 'HARD', 2345, 198, 156, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '理解数组', '数组是最基础的数据结构，元素连续存储，支持随机访问，时间复杂度O(1)。'),
(LAST_INSERT_ID(), 2, '学习链表', '链表由节点组成，每个节点包含数据和指针，插入删除效率高，但不支持随机访问。'),
(LAST_INSERT_ID(), 3, '掌握栈和队列', '栈是后进先出(LIFO)，队列是先进先出(FIFO)，理解它们的应用场景。'),
(LAST_INSERT_ID(), 4, '理解树结构', '学习二叉树、二叉搜索树的概念，理解遍历方式：前序、中序、后序。'),
(LAST_INSERT_ID(), 5, '学习哈希表', '哈希表通过哈希函数实现O(1)的查找，理解哈希冲突的解决方法。'),
(LAST_INSERT_ID(), 6, '实践练习', '在LeetCode等平台练习相关题目，加深对数据结构的理解。');
