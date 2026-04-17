-- WikiHow 知识库系统 - 更多分类数据
-- 新增一级分类和二级分类

USE `wikihow`;

-- ===================== 新增一级分类 =====================

-- 一级分类：健康养生 (ID=10)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('健康养生', '关注身心健康，学习科学的养生方法。从饮食调理到心理健康，从中医养生到现代保健，帮助你建立健康的生活方式，远离亚健康状态！', 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=1200', NULL, 4);

-- 一级分类：金融理财 (ID=11)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('金融理财', '掌握理财知识，实现财务自由。从基础的储蓄规划到投资理财，从保险配置到税务筹划，让你的钱为你工作，实现财富增值！', 'https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=1200', NULL, 5);

-- 一级分类：职场发展 (ID=12)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('职场发展', '提升职场竞争力，规划职业生涯。从求职面试到职场沟通，从时间管理到领导力培养，助你在职场中脱颖而出，实现职业目标！', 'https://images.unsplash.com/photo-1521737711867-e3b97375f902?w=1200', NULL, 6);

-- 一级分类：教育学习 (ID=13)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('教育学习', '终身学习，不断进步。从学习方法到考试技巧，从语言学习到专业技能，掌握高效的学习策略，让学习变得更轻松有趣！', 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=1200', NULL, 7);

-- 一级分类：旅行出游 (ID=14)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('旅行出游', '探索世界，发现美好。从旅行攻略到签证办理，从行李打包到旅途安全，让每一次出行都成为难忘的回忆！', 'https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=1200', NULL, 8);

-- 一级分类：宠物养护 (ID=15)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('宠物养护', '科学养宠，用心陪伴。从宠物喂养到健康护理，从行为训练到日常互动，让你成为一个负责任的铲屎官！', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=1200', NULL, 9);

-- ===================== 新增二级分类 =====================

-- 健康养生的二级分类 (parent_id=10)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('饮食营养', '科学搭配饮食，均衡摄入营养。了解各类食物的营养价值，制定健康的饮食计划。', 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=800', 10, 1),
('睡眠改善', '提高睡眠质量，告别失眠困扰。学习科学的睡眠方法，让你每天精力充沛。', 'https://images.unsplash.com/photo-1541781774459-bb2af2f05b55?w=800', 10, 2),
('心理健康', '关注心理健康，学会情绪管理。缓解压力焦虑，保持积极乐观的心态。', 'https://images.unsplash.com/photo-1499209974431-9dddcece7f88?w=800', 10, 3),
('中医养生', '传承中医智慧，调理身体机能。学习穴位按摩、食疗养生等传统方法。', 'https://images.unsplash.com/photo-1512290923902-8a9f81dc236c?w=800', 10, 4);

-- 金融理财的二级分类 (parent_id=11)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('储蓄规划', '合理规划储蓄，积累财富基础。学习存钱技巧，建立应急资金。', 'https://images.unsplash.com/photo-1579621970563-ebec7560ff3e?w=800', 11, 1),
('股票投资', '了解股票市场，学习投资策略。从入门到进阶，掌握股票投资技巧。', 'https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=800', 11, 2),
('基金理财', '分散投资风险，稳健增值财富。了解各类基金产品，选择适合自己的投资组合。', 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800', 11, 3),
('保险配置', '科学配置保险，为生活保驾护航。了解各类保险产品，合理规划保障方案。', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85?w=800', 11, 4);

-- 职场发展的二级分类 (parent_id=12)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('求职面试', '掌握求职技巧，赢得心仪offer。从简历撰写到面试技巧，全方位提升求职竞争力。', 'https://images.unsplash.com/photo-1565688534245-05d6b5be184a?w=800', 12, 1),
('职场沟通', '提升沟通能力，建立良好人际关系。学习高效沟通技巧，化解职场矛盾。', 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=800', 12, 2),
('时间管理', '高效管理时间，提升工作效率。学习时间管理方法，告别拖延症。', 'https://images.unsplash.com/photo-1506784983877-45594efa4cbe?w=800', 12, 3),
('领导力', '培养领导能力，带领团队成功。学习管理技巧，成为优秀的领导者。', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c?w=800', 12, 4);

-- 教育学习的二级分类 (parent_id=13)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('学习方法', '掌握高效学习方法，事半功倍。从记忆技巧到思维导图，提升学习效率。', 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800', 13, 1),
('语言学习', '突破语言障碍，打开世界大门。从英语到小语种，掌握语言学习技巧。', 'https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=800', 13, 2),
('考试技巧', '科学备考，轻松应对各类考试。从复习规划到答题技巧，助你考试成功。', 'https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=800', 13, 3),
('亲子教育', '科学育儿，陪伴孩子成长。学习教育方法，建立良好的亲子关系。', 'https://images.unsplash.com/photo-1476703993599-0035a21b17a9?w=800', 13, 4);

-- 旅行出游的二级分类 (parent_id=14)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('国内旅行', '探索祖国大好河山，发现身边的美景。从热门景点到小众目的地，规划完美旅程。', 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800', 14, 1),
('出境游', '走出国门，体验异国风情。从签证办理到行程规划，让出境游更轻松。', 'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=800', 14, 2),
('自驾游', '自由驾驶，随心所欲。学习自驾游攻略，享受在路上的乐趣。', 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?w=800', 14, 3),
('户外探险', '挑战自我，探索未知。从徒步登山到露营野炊，体验户外运动的魅力。', 'https://images.unsplash.com/photo-1551632811-561732d1e306?w=800', 14, 4);

-- 宠物养护的二级分类 (parent_id=15)
INSERT INTO `category` (`name`, `description`, `cover_image`, `parent_id`, `sort_order`) VALUES
('狗狗养护', '科学养狗，让狗狗健康快乐。从喂养护理到训练技巧，成为合格的狗主人。', 'https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=800', 15, 1),
('猫咪养护', '了解猫咪习性，给猫咪最好的照顾。从日常护理到健康管理，做一个称职的猫奴。', 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=800', 15, 2),
('小宠养护', '照顾小型宠物，享受养宠乐趣。从仓鼠到兔子，学习各类小宠的养护知识。', 'https://images.unsplash.com/photo-1425082661705-1834bfd09dca?w=800', 15, 3),
('水族养护', '打造水下世界，养好观赏鱼。从鱼缸布置到水质管理，享受水族养殖的乐趣。', 'https://images.unsplash.com/photo-1522069169874-c58ec4b76be5?w=800', 15, 4);

-- ===================== 为新分类添加示例文章 =====================

-- 文章：如何改善睡眠质量 (分类: 睡眠改善)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何改善睡眠质量，告别失眠', '失眠困扰着很多人，本教程分享科学的睡眠改善方法，帮你获得高质量睡眠。', 2, 17, '睡眠,健康,失眠', 'EASY', 3456, 278, 198, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '建立规律作息', '每天固定时间睡觉和起床，包括周末，帮助身体建立稳定的生物钟。'),
(LAST_INSERT_ID(), 2, '优化睡眠环境', '保持卧室黑暗、安静、凉爽（18-22度），使用舒适的床品。'),
(LAST_INSERT_ID(), 3, '睡前放松', '睡前1小时避免使用电子设备，可以泡脚、听轻音乐或做冥想。'),
(LAST_INSERT_ID(), 4, '注意饮食', '避免睡前摄入咖啡因和酒精，晚餐不要吃太饱，睡前可喝杯温牛奶。'),
(LAST_INSERT_ID(), 5, '适度运动', '白天进行适度运动有助于睡眠，但避免睡前3小时内剧烈运动。');

-- 文章：如何制作个人理财计划 (分类: 储蓄规划)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何制定个人理财计划', '理财从规划开始，本教程教你制定适合自己的理财计划，实现财务目标。', 3, 20, '理财,储蓄,规划', 'MEDIUM', 2876, 234, 167, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '梳理财务状况', '统计每月收入和支出，了解钱都花在了哪里，找出可以节省的地方。'),
(LAST_INSERT_ID(), 2, '设定理财目标', '明确短期、中期、长期的财务目标，如应急基金、买房、退休等。'),
(LAST_INSERT_ID(), 3, '制定预算', '根据收入制定月度预算，遵循50/30/20法则：50%必要支出，30%个人消费，20%储蓄投资。'),
(LAST_INSERT_ID(), 4, '建立应急基金', '优先存够3-6个月生活费作为应急基金，放在随时可取的账户中。'),
(LAST_INSERT_ID(), 5, '开始投资', '在保证应急基金后，根据风险承受能力选择合适的投资方式。'),
(LAST_INSERT_ID(), 6, '定期复盘', '每月检查预算执行情况，每季度评估投资收益，及时调整计划。');

-- 文章：如何写一份优秀的简历 (分类: 求职面试)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何写一份让HR眼前一亮的简历', '简历是求职的敲门砖，本教程教你写出专业、有吸引力的简历。', 3, 24, '简历,求职,面试', 'MEDIUM', 4567, 356, 278, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '选择合适的模板', '选择简洁专业的模板，避免花哨设计，确保内容清晰易读。'),
(LAST_INSERT_ID(), 2, '撰写个人信息', '包含姓名、联系方式、求职意向，照片要正式得体。'),
(LAST_INSERT_ID(), 3, '突出工作经历', '用STAR法则描述工作成果：情境(Situation)、任务(Task)、行动(Action)、结果(Result)。'),
(LAST_INSERT_ID(), 4, '量化成果', '用数据说话，如"提升销售额30%"、"管理10人团队"等。'),
(LAST_INSERT_ID(), 5, '匹配岗位要求', '根据目标岗位调整简历内容，突出相关经验和技能。'),
(LAST_INSERT_ID(), 6, '检查润色', '仔细检查错别字和格式，请他人帮忙审阅，确保无误。');

-- 文章：如何高效学习英语 (分类: 语言学习)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何高效学习英语，快速提升水平', '英语学习有方法，本教程分享高效的英语学习策略，帮你突破语言障碍。', 4, 29, '英语,学习,语言', 'MEDIUM', 5678, 456, 345, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '明确学习目标', '确定学习英语的目的：考试、工作、旅行等，选择对应的学习方向。'),
(LAST_INSERT_ID(), 2, '打好词汇基础', '每天背诵20-30个单词，使用间隔重复法复习，结合语境记忆。'),
(LAST_INSERT_ID(), 3, '多听多说', '每天听英语播客或看美剧，模仿发音，大胆开口说。'),
(LAST_INSERT_ID(), 4, '阅读英文材料', '从简单的文章开始，逐渐增加难度，培养英语思维。'),
(LAST_INSERT_ID(), 5, '写作练习', '每天写英语日记或作文，记录生活，锻炼表达能力。'),
(LAST_INSERT_ID(), 6, '创造语言环境', '加入英语角、找语伴练习，将英语融入日常生活。');

-- 文章：如何规划一次完美的旅行 (分类: 国内旅行)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何规划一次完美的国内旅行', '旅行需要好好规划，本教程教你从零开始规划一次难忘的旅程。', 5, 32, '旅行,攻略,规划', 'EASY', 3456, 267, 189, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '确定目的地和时间', '根据假期长度、预算和兴趣选择目的地，了解当地最佳旅游季节。'),
(LAST_INSERT_ID(), 2, '预订交通和住宿', '提前预订机票或火车票，选择位置便利、评价好的酒店或民宿。'),
(LAST_INSERT_ID(), 3, '制定行程计划', '列出想去的景点，合理安排每天的行程，留出弹性时间。'),
(LAST_INSERT_ID(), 4, '准备必需物品', '根据目的地气候准备衣物，带好证件、充电器、常用药品等。'),
(LAST_INSERT_ID(), 5, '了解当地文化', '提前了解目的地的风俗习惯、特色美食和注意事项。'),
(LAST_INSERT_ID(), 6, '做好预算管理', '估算各项开支，准备适量现金和移动支付，记录旅途花费。');

-- 文章：如何训练狗狗基本指令 (分类: 狗狗养护)
INSERT INTO `article` (`title`, `summary`, `user_id`, `category_id`, `tags`, `difficulty`, `view_count`, `like_count`, `favorite_count`, `status`) VALUES
('如何训练狗狗学会基本指令', '训练狗狗需要耐心和方法，本教程教你训练狗狗坐下、握手等基本指令。', 4, 36, '狗狗,训练,宠物', 'MEDIUM', 2345, 189, 134, 1);

INSERT INTO `article_step` (`article_id`, `step_order`, `title`, `content`) VALUES
(LAST_INSERT_ID(), 1, '准备训练工具', '准备狗狗喜欢的零食作为奖励，选择安静无干扰的训练环境。'),
(LAST_INSERT_ID(), 2, '训练"坐下"', '手持零食在狗狗鼻子上方，慢慢向后移动，狗狗自然会坐下，立即给予奖励。'),
(LAST_INSERT_ID(), 3, '训练"握手"', '让狗狗坐下后，轻轻抬起它的前爪，说"握手"，然后给予奖励。'),
(LAST_INSERT_ID(), 4, '训练"趴下"', '从坐姿开始，将零食从鼻子前方向下移动到地面，狗狗会跟着趴下。'),
(LAST_INSERT_ID(), 5, '保持耐心', '每次训练5-10分钟，每天多次练习，保持积极的态度。'),
(LAST_INSERT_ID(), 6, '及时奖励', '狗狗做对后立即给予奖励和表扬，强化正确行为。');
