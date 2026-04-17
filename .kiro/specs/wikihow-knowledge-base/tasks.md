# WikiHow 知识库系统 - 实现任务清单

> 基于需求文档、系统设计文档和数据库设计文档创建

---

## 阶段一：项目基础搭建

- [ ] 1. 后端项目初始化与配置
  - [x] 1.1 配置 pom.xml 依赖（MyBatis-Plus、JWT、Lombok、BCrypt）



    - 添加 mybatis-plus-boot-starter
    - 添加 jjwt 依赖
    - 添加 spring-boot-starter-validation
    - _Requirements: 技术栈要求_
  - [-] 1.2 配置 application.yml

    - 数据库连接配置
    - MyBatis-Plus 配置
    - JWT 密钥和过期时间配置
    - 文件上传路径配置
    - _Requirements: 系统设计文档 6、7_
  - [ ] 1.2.1 测试数据库连接是否正常
    - 启动项目验证控制台无报错
    - _Requirements: 基础配置验证_
  - [ ] 1.2.2 启动项目验证基础配置无报错
    - 确认 Swagger 可访问（如已配置）
    - _Requirements: 基础配置验证_



  - [ ] 1.3 创建统一响应类 Result 和 ResultCode
    - 实现统一响应格式 {code, message, data}


    - 定义状态码枚举（200/400/401/403/404/500）
    - _Requirements: 系统设计文档 4_
  - [x] 1.4 配置跨域 CorsConfig


    - 允许前端开发服务器跨域访问
    - _Requirements: 前后端分离架构_



- [x] 2. 数据库表创建与实体类


  - [ ] 2.1 执行数据库建表 SQL
    - 创建 user、category、article、article_step、comment、like_record、favorite 表
    - 插入初始分类数据和管理员账号
    - _Requirements: 数据库设计文档 3、4_


  - [ ] 2.2 创建实体类 Entity
    - User、Article、ArticleStep、Category、Comment、LikeRecord、Favorite


    - 使用 MyBatis-Plus 注解 @TableName、@TableId、@TableField
    - _Requirements: 数据库设计文档 3_
  - [x] 2.3 创建 Mapper 接口


    - UserMapper、ArticleMapper、ArticleStepMapper、CategoryMapper、CommentMapper、LikeMapper、FavoriteMapper
    - 继承 BaseMapper<T>
    - _Requirements: 系统设计文档 2_


- [ ] 3. 前端项目配置
  - [ ] 3.1 安装依赖并配置 Element Plus
    - 安装 element-plus、axios
    - 配置按需引入
    - _Requirements: 技术栈要求_
  - [x] 3.2 配置 axios 请求封装

    - 创建 api/index.js 统一请求实例

    - 配置请求拦截器（添加 Token）
    - 配置响应拦截器（统一错误处理）

    - _Requirements: 系统设计文档 6_
  - [ ] 3.3 配置路由 router/index.js
    - 配置前台页面路由（首页、文章详情、登录、注册等）
    - 配置后台管理路由
    - 配置路由守卫
    - _Requirements: 需求文档 4_


  - [ ] 3.4 配置 Pinia 状态管理
    - 创建 user store（用户登录状态、Token 管理）
    - 创建 app store（全局状态）


    - _Requirements: 系统设计文档 3_
  - [ ] 3.4.1 安装 pinia-plugin-persistedstate
    - 执行 `npm i pinia-plugin-persistedstate`
    - _Requirements: 状态持久化_


  - [x] 3.4.2 配置 user store 持久化存储


    - 在 main.js 添加 `pinia.use(piniaPluginPersistedstate)`
    - 在 store 中配置 `persist: true`
    - _Requirements: 状态持久化_


- [ ] 4. Checkpoint - 确保基础配置正确
  - 确保所有配置正确，项目可以正常启动



---

## 阶段二：用户认证模块



- [x] 5. 后端认证功能


  - [ ] 5.1 实现 JWT 工具类 JwtUtil
    - generateToken(userId, username) 生成 Token
    - parseToken(token) 解析 Token
    - isExpired(token) 判断是否过期


    - _Requirements: 系统设计文档 6_


  - [ ] 5.2 实现 JWT 拦截器 JwtInterceptor
    - 从 Header 获取 Authorization
    - 验证 Token 有效性


    - 将用户信息存入 ThreadLocal
    - _Requirements: 系统设计文档 6_
  - [x] 5.2.1 创建 WebMvcConfig 配置类



    - 注册 JwtInterceptor 并指定拦截路径 `/api/**`
    - 放行 `/api/auth/**` 认证接口
    - _Requirements: 系统设计文档 6_
  - [ ] 5.3 创建 DTO 类
    - LoginRequest（username, password）
    - RegisterRequest（username, email, password）
    - UserResponse（用户信息响应）
    - _Requirements: 系统设计文档 5.1_
  - [ ] 5.3.1 在 DTO 添加参数校验注解
    - `@NotBlank(message = "邮箱不能为空")`
    - `@Length(min=6, message="密码至少6位")`


    - _Requirements: 参数校验_
  - [ ] 5.4 实现 UserService
    - register() 注册（密码 BCrypt 加密）
    - login() 登录（验证密码、生成 Token）
    - getUserById() 获取用户信息


    - updateUser() 更新用户资料
    - _Requirements: 需求文档 3.1_
  - [ ] 5.5 实现 AuthController
    - POST /api/auth/register 注册
    - POST /api/auth/login 登录
    - _Requirements: 需求文档 5_


  - [x] 5.6 实现 UserController


    - GET /api/users/:id 获取用户信息
    - PUT /api/users/:id 更新用户信息
    - _Requirements: 需求文档 5_

- [ ] 6. 前端认证功能
  - [ ] 6.1 创建认证 API（api/auth.js）
    - login()、register()、getUserInfo()
    - _Requirements: 系统设计文档 3_

  - [ ] 6.2 实现登录页面 LoginView.vue
    - 登录表单（用户名/邮箱 + 密码）
    - 表单验证
    - 登录成功后存储 Token 并跳转

    - _Requirements: 需求文档 4_
  - [ ] 6.3 实现注册页面 RegisterView.vue
    - 注册表单（用户名 + 邮箱 + 密码 + 确认密码）


    - 表单验证
    - 注册成功后跳转登录
    - _Requirements: 需求文档 4_
  - [ ] 6.4 实现页头组件 AppHeader.vue
    - 显示 Logo、导航菜单
    - 未登录显示登录/注册按钮

    - 已登录显示用户头像和下拉菜单
    - _Requirements: 系统设计文档 3_

- [x] 7. Checkpoint - 确保认证功能正常


  - 确保所有测试通过，用户可以正常注册登录

---

## 阶段三：分类模块

- [x] 8. 后端分类功能


  - [ ] 8.1 实现 CategoryService
    - getCategories() 获取所有分类（树形结构）
    - getCategoryById() 获取单个分类

    - createCategory() 创建分类（管理员）

    - updateCategory() 更新分类（管理员）

    - deleteCategory() 删除分类（管理员）
    - _Requirements: 需求文档 3.3_
  - [ ] 8.2 实现 CategoryController
    - GET /api/categories 获取分类列表


    - POST /api/categories 创建分类
    - PUT /api/categories/:id 更新分类


    - DELETE /api/categories/:id 删除分类
    - _Requirements: 需求文档 5_



- [ ] 9. 前端分类功能
  - [ ] 9.1 创建分类 API（api/category.js）
    - getCategories()、createCategory()、updateCategory()、deleteCategory()

    - _Requirements: 系统设计文档 3_

  - [x] 9.2 实现分类页面 CategoryView.vue

    - 显示该分类下的文章列表

    - 显示子分类导航
    - _Requirements: 需求文档 4_


---

## 阶段四：文章模块（核心功能）

- [x] 10. 后端文章功能

  - [ ] 10.1 创建文章相关 DTO
    - ArticleRequest（创建/更新文章请求）
    - ArticleResponse（文章详情响应，包含步骤）
    - ArticleListResponse（文章列表项）
    - _Requirements: 系统设计文档 5.2、5.3、5.4_
  - [ ] 10.1.1 在 Article 表和实体添加 status 字段
    - `status TINYINT DEFAULT 1`（1 已发布 / 0 草稿 / 2 下架）
    - 本期仅预留字段，不展开草稿箱逻辑
    - _Requirements: 需求文档 3.2_
  - [ ] 10.2 实现 ArticleService
    - getArticles() 分页查询文章列表（支持分类筛选、排序）
    - getArticleById() 获取文章详情（包含步骤）
    - createArticle() 创建文章（含步骤）
    - updateArticle() 更新文章（仅作者）
    - deleteArticle() 删除文章（作者或管理员）
    - incrementViewCount() 增加浏览量

    - _Requirements: 需求文档 3.2_
  - [ ] 10.3 实现 ArticleStepService
    - saveSteps() 保存文章步骤
    - getStepsByArticleId() 获取文章步骤

    - deleteStepsByArticleId() 删除文章步骤
    - _Requirements: 需求文档 3.2_
  - [ ] 10.4 实现 ArticleController
    - GET /api/articles 文章列表（分页、筛选）

    - GET /api/articles/:id 文章详情
    - POST /api/articles 创建文章
    - PUT /api/articles/:id 更新文章

    - DELETE /api/articles/:id 删除文章
    - _Requirements: 需求文档 5_


- [ ] 11. 后端文件上传功能
  - [ ] 11.1 实现 FileUtil 工具类
    - 生成唯一文件名

    - 验证文件类型（仅图片）
    - 验证文件大小（最大 5MB）
    - _Requirements: 系统设计文档 7_
  - [ ] 11.2 实现 FileController
    - POST /api/files/upload 文件上传

    - 返回文件访问 URL
    - _Requirements: 系统设计文档 7_

  - [ ] 11.3 配置静态资源访问
    - 配置 /uploads/ 路径映射
    - _Requirements: 系统设计文档 7_


- [ ] 12. 前端文章功能
  - [ ] 12.1 创建文章 API（api/article.js）
    - getArticles()、getArticleById()、createArticle()、updateArticle()、deleteArticle()
    - uploadFile()
    - _Requirements: 系统设计文档 3_
  - [ ] 12.2 实现文章卡片组件 ArticleCard.vue
    - 显示封面图、标题、摘要、作者、分类、难度
    - 显示浏览量、点赞数
    - 点击跳转详情页
    - _Requirements: 系统设计文档 3_
  - [ ] 12.3 实现首页 HomeView.vue
    - 文章列表展示（使用 ArticleCard）
    - 分类筛选
    - 分页加载
    - _Requirements: 需求文档 4_

  - [ ] 12.4 实现步骤展示组件 ArticleStep.vue
    - 步骤序号、标题、内容、图片
    - WikiHow 风格的步骤式布局
    - _Requirements: 系统设计文档 3_
  - [ ] 12.5 实现文章详情页 ArticleDetail.vue
    - 文章标题、摘要、作者信息

    - 步骤列表展示
    - 互动按钮（点赞、收藏）
    - 评论区
    - _Requirements: 需求文档 4_
  - [x] 12.6 实现步骤编辑器组件 StepEditor.vue

    - 添加/删除步骤
    - 编辑步骤标题、内容
    - 上传步骤图片
    - 拖拽排序步骤
    - _Requirements: 系统设计文档 3_
  - [ ] 12.7 实现文章编辑页 ArticleEditor.vue
    - 文章基本信息表单（标题、摘要、分类、难度、标签）
    - 封面图上传
    - 步骤编辑器集成
    - 保存/发布功能
    - _Requirements: 需求文档 4_

- [x] 13. Checkpoint - 确保文章功能正常

  - 确保所有测试通过，文章 CRUD 功能正常

---

## 阶段五：互动模块

- [ ] 14. 后端互动功能
  - [ ] 14.1 实现 CommentService
    - getCommentsByArticleId() 获取文章评论
    - createComment() 发表评论
    - deleteComment() 删除评论（仅作者）

    - _Requirements: 需求文档 3.5_
  - [ ] 14.2 实现 CommentController
    - GET /api/articles/:id/comments 获取评论列表
    - POST /api/comments 发表评论
    - DELETE /api/comments/:id 删除评论

    - _Requirements: 需求文档 5_
  - [ ] 14.3 实现 LikeService
    - toggleLike() 点赞/取消点赞
    - isLiked() 检查是否已点赞
    - _Requirements: 需求文档 3.5_

  - [ ] 14.4 实现 LikeController
    - POST /api/likes 点赞/取消
    - _Requirements: 需求文档 5_
  - [ ] 14.5 实现 FavoriteService
    - toggleFavorite() 收藏/取消收藏
    - isFavorited() 检查是否已收藏
    - getUserFavorites() 获取用户收藏列表
    - _Requirements: 需求文档 3.5_
  - [ ] 14.6 实现 FavoriteController
    - POST /api/favorites 收藏/取消
    - GET /api/users/:id/favorites 获取收藏列表
    - _Requirements: 需求文档 5_


- [ ] 15. 前端互动功能
  - [ ] 15.1 创建互动 API（api/interaction.js）
    - toggleLike()、toggleFavorite()、getComments()、createComment()、deleteComment()


    - _Requirements: 系统设计文档 3_
  - [ ] 15.2 实现评论列表组件 CommentList.vue
    - 评论列表展示
    - 发表评论表单
    - 删除自己的评论
    - _Requirements: 系统设计文档 3_
  - [ ] 15.3 在文章详情页集成互动功能
    - 点赞按钮（显示状态和数量）
    - 收藏按钮（显示状态和数量）
    - 评论组件集成
    - _Requirements: 需求文档 3.5_

- [ ] 16. Checkpoint - 确保互动功能正常
  - 确保所有测试通过，点赞、收藏、评论功能正常

---

## 阶段六：搜索与个人中心

- [ ] 17. 搜索功能
  - [ ] 17.1 后端实现搜索接口
    - GET /api/articles?keyword=xxx 支持关键词搜索
    - 按标题模糊匹配
    - _Requirements: 需求文档 3.4_
  - [ ] 17.2 前端实现搜索页面 SearchView.vue
    - 搜索框
    - 搜索结果列表
    - 分类筛选、排序选项
    - _Requirements: 需求文档 4_

- [ ] 18. 个人中心
  - [ ] 18.1 实现个人中心页面 ProfileView.vue
    - 显示/编辑个人资料（头像、昵称、简介）
    - 我的文章列表
    - 我的收藏列表
    - 修改密码
    - _Requirements: 需求文档 4_
  - [ ] 18.2 实现用户主页 UserView.vue
    - 显示用户公开信息
    - 显示用户发布的文章
    - _Requirements: 需求文档 4_

---

## 阶段七：后台管理

- [ ] 19. 后端管理功能
  - [ ] 19.1 实现管理员权限校验
    - 创建 AdminInterceptor 拦截器
    - 校验用户角色为 ADMIN
    - _Requirements: 需求文档 2.3_
  - [ ] 19.2 实现用户管理接口
    - GET /api/admin/users 用户列表
    - PUT /api/admin/users/:id/status 禁用/启用用户
    - _Requirements: 需求文档 3.6_
  - [ ] 19.3 实现文章管理接口
    - GET /api/admin/articles 所有文章列表
    - DELETE /api/admin/articles/:id 删除文章
    - PUT /api/admin/articles/:id/status 下架/上架文章
    - _Requirements: 需求文档 3.6_

- [ ] 20. 前端管理功能
  - [ ] 20.1 实现用户管理页面 admin/UserManage.vue
    - 用户列表表格
    - 搜索、分页
    - 禁用/启用操作
    - _Requirements: 需求文档 4_
  - [ ] 20.2 实现文章管理页面 admin/ArticleManage.vue
    - 文章列表表格
    - 搜索、筛选、分页
    - 删除、下架操作
    - _Requirements: 需求文档 4_
  - [ ] 20.3 实现分类管理页面 admin/CategoryManage.vue
    - 分类树形展示
    - 添加、编辑、删除分类
    - _Requirements: 需求文档 4_
  - [ ] 20.4 实现首页头部栏搜索功能页面
    - 分类树形展示
    - 添加、编辑、删除分类
    - _Requirements: 需求文档 4_
- [ ] 21. Checkpoint - 确保后台管理功能正常
  - 确保所有测试通过，管理功能正常

---

## 阶段八：全局异常处理与优化

- [ ] 22. 异常处理
  - [ ] 22.1 创建自定义异常类 BusinessException
    - 包含错误码和错误信息
    - _Requirements: 系统设计文档 2_
  - [ ] 22.2 实现全局异常处理器 GlobalExceptionHandler
    - 处理 BusinessException
    - 处理参数校验异常
    - 处理未知异常
    - _Requirements: 系统设计文档 2_
  - [ ] 22.2.1 添加参数校验异常处理
    - `@ExceptionHandler(MethodArgumentNotValidException.class)`
    - 返回 `Result.error("参数校验失败：" + 拼接字段错误提示)`
    - _Requirements: 参数校验_

- [ ] 23. 前端优化
  - [ ] 23.1 实现页脚组件 AppFooter.vue
    - 版权信息、链接
    - _Requirements: 系统设计文档 3_
  - [x] 23.2 实现 404 页面
    - 友好的 404 提示
    - 返回首页链接
    - _Requirements: 用户体验_
  - [ ] 23.3 添加加载状态和空状态
    - 列表加载中状态
    - 列表为空提示
    - _Requirements: 用户体验_

- [ ] 24. Final Checkpoint - 全面测试
  - 确保所有功能正常工作
  - 前后端联调测试


## 阶段九：功能细化与完善

- [ ] 25. 功能完善与优化

  - [x] 25.1 写文章时增加参考文献
    - 在文章评论功能模块上方添加独立的参考文献显示块
    - 用户可在编写文章时选择是否添加参考文献

  - [x] 25.2 完善个人中心
    - 对自我描述排版进行专业排版布局优化，要精美的个人中心
    - 用户可以上传自己的头像
    - 用户可以在个人中心清晰看见的文章数据
    - 用户可以有权限对自己发布的文章，删除以及修改

  - [x] 25.3 用户修改密码
    - 增加修改密码在用户个人中心中
    - 布局友好精美
    - 优化掉用户头像点击下拉栏中‘我的文章’和‘我的收藏’，改用个人中心来代替

  - [x] 25.4 你也许感兴趣
    - 在参考文献显示上面增加一个‘你也许感兴趣’显示快
    - 其中显示随机的文章，以四方格子形式展示，点击后可跳转

  - [x] 25.5 文章分享功能
    - 为文章添加分享按钮
    - 实现用户可以分享文章的功能
    - 点击分享按钮后自动复制本文章链接，并弹出精美提示窗与提示语。

  - [x] 25.6 系统通知功能
    - 在用户登陆后增加系统通知按钮，放置于头部栏合适的位置中
    - 所有新用户在注册成功登录后都会收到欢迎语系统通知
    - 管理员可以发布通知，全部用户都将收到系统通知
    - 通知的信息可以按日期折叠显示，默认显示未读通知。按最近从上往下排列
    - 系统通知显示发送人的一些信息，如头像、用户名、发送时间等；并对其进行精美的排版布局设计

  - [x] 25.7 1 分类浏览列表
    - 将首页的分类按钮进行重构，做成一个从下往下的纵向排列表
    - 每条都带有与之对应的彩色小图标
    - 标题就叫：按类别浏览。下方紧跟这一条条的一级分类，不展示二级分类。
    - 将其放在首页文章右方空白区域，并对其布局排版进行友好排版布局优化。


---

## 阶段十：设计要求核心功能补充

> 根据设计要求文档，补充以下核心功能（词条版本管理、内容审核、过时内容标记、编辑冲突调解）

- [x] 26. 词条版本管理功能
  - [x] 26.1 创建版本管理数据库表


    - 创建 `article_version` 表存储文章历史版本
    - 字段包含：id, article_id, version_number, title, summary, content_snapshot(JSON存储步骤), user_id, change_description, created_at
    - _Requirements: 设计要求 - 现有词条编辑完善与版本管理_
  - [x] 26.2 后端版本管理实体和Mapper


    - 创建 ArticleVersion 实体类
    - 创建 ArticleVersionMapper 接口
    - _Requirements: 设计要求 - 版本管理_
  - [x] 26.3 后端版本管理服务实现


    - 实现 ArticleVersionService（保存版本、获取版本列表、获取指定版本、回滚版本）
    - 在文章更新时自动创建新版本记录
    - _Requirements: 设计要求 - 版本管理_
  - [x] 26.4 后端版本管理接口


    - GET /api/articles/:id/versions 获取文章版本历史
    - GET /api/articles/:id/versions/:versionId 获取指定版本详情
    - POST /api/articles/:id/revert/:versionId 回滚到指定版本（仅作者）
    - _Requirements: 设计要求 - 版本管理_
  - [x] 26.5 前端版本管理API

    - 创建 api/version.js 版本管理接口
    - _Requirements: 设计要求 - 版本管理_
  - [x] 26.6 前端版本历史页面

    - 在文章详情页添加"版本历史"入口按钮
    - 创建 ArticleVersions.vue 版本历史页面
    - 实现版本列表展示（版本号、修改时间、修改人、修改说明）
    - 支持查看历史版本内容
    - 支持回滚到指定版本
    - _Requirements: 设计要求 - 版本管理_

- [x] 27. 内容审核功能 ✅ 已完成

  - [x] 27.1 扩展用户角色支持审核员
    - 在 User 实体的 role 字段添加 REVIEWER 角色支持
    - 修改 article 表 status 字段含义：0草稿/1待审核/2已发布/3已拒绝/4已下架
    - _Requirements: 设计要求 - 四大角色功能边界_

  - [x] 27.2 创建审核相关数据库表
    - 创建 `article_review` 表（id, article_id, reviewer_id, status, comment, created_at）
    - _Requirements: 设计要求 - 词条审核_

  - [x] 27.3 后端审核实体和Mapper
    - 创建 ArticleReview 实体类
    - 创建 ArticleReviewMapper 接口
    - _Requirements: 设计要求 - 词条审核_

  - [x] 27.4 后端审核服务实现
    - 实现 ArticleReviewService（提交审核、审核通过、审核拒绝、获取待审核列表）
    - 文章状态流转：草稿 -> 待审核 -> 已发布/已拒绝
    - _Requirements: 设计要求 - 词条审核_

  - [x] 27.5 后端审核接口
    - POST /api/articles/:id/submit-review 提交审核
    - GET /api/admin/reviews 获取待审核文章列表（审核员/管理员）
    - PUT /api/admin/reviews/:id/approve 审核通过
    - PUT /api/admin/reviews/:id/reject 审核拒绝（需填写拒绝原因）
    - _Requirements: 设计要求 - 词条审核_

  - [x] 27.6 前端审核管理API
    - 创建 api/review.js 审核管理接口
    - _Requirements: 设计要求 - 词条审核_

  - [x] 27.7 前端审核管理页面
    - 创建 admin/ReviewManage.vue 审核管理页面
    - 显示待审核文章列表
    - 支持预览文章内容、通过/拒绝操作
    - 支持填写审核意见
    - _Requirements: 设计要求 - 词条审核_

  - [x] 27.8 前端文章状态展示优化
    - 在文章编辑页显示当前审核状态
    - 在个人中心显示文章审核状态（待审核/已通过/已拒绝）
    - 审核结果通过系统通知告知用户
    - 兼容旧状态1=已发布，新状态2=已发布
    - _Requirements: 设计要求 - 词条审核_

- [x] 28. 过时内容标记功能





  - [x] 28.1 数据库表修改


    - 在 article 表添加 is_outdated 字段（TINYINT DEFAULT 0）
    - 在 article 表添加 outdated_reason 字段（VARCHAR(500)）
    - 创建 `outdated_report` 表（id, article_id, user_id, reason, status, handler_id, handle_time, created_at）
    - _Requirements: 设计要求 - 过时内容标记_
  - [x] 28.2 后端过时标记实体和Mapper


    - 创建 OutdatedReport 实体类
    - 创建 OutdatedReportMapper 接口
    - _Requirements: 设计要求 - 过时内容标记_
  - [x] 28.3 后端过时标记服务


    - 实现 OutdatedReportService（举报过时、处理举报、获取举报列表）
    - 实现管理员标记/取消过时状态
    - _Requirements: 设计要求 - 过时内容标记_
  - [x] 28.4 后端过时标记接口


    - POST /api/articles/:id/report-outdated 用户举报过时内容
    - PUT /api/admin/articles/:id/mark-outdated 管理员标记过时
    - PUT /api/admin/articles/:id/unmark-outdated 取消过时标记
    - GET /api/admin/outdated-reports 获取过时举报列表
    - PUT /api/admin/outdated-reports/:id/handle 处理举报
    - _Requirements: 设计要求 - 过时内容标记_
  - [x] 28.5 前端过时标记API


    - 创建 api/outdated.js 过时标记接口
    - _Requirements: 设计要求 - 过时内容标记_
  - [x] 28.6 前端过时标记功能


    - 在文章详情页添加"标记为过时"按钮（登录用户可见）
    - 过时文章显示醒目的过时提示横幅（黄色警告样式）
    - 创建 admin/OutdatedManage.vue 过时举报管理页面
    - _Requirements: 设计要求 - 过时内容标记_

- [x] 29. 编辑冲突调解功能





  - [x] 29.1 创建编辑锁定数据库表


    - 创建 `article_edit_lock` 表（id, article_id, user_id, locked_at, expires_at）
    - _Requirements: 设计要求 - 编辑冲突调解_
  - [x] 29.2 后端编辑锁实体和Mapper


    - 创建 ArticleEditLock 实体类
    - 创建 ArticleEditLockMapper 接口
    - _Requirements: 设计要求 - 编辑冲突调解_

  - [x] 29.3 后端编辑冲突服务

    - 实现 ArticleEditLockService（获取锁、释放锁、检查锁状态、清理过期锁）
    - 编辑锁有效期30分钟，可续期
    - _Requirements: 设计要求 - 编辑冲突调解_

  - [x] 29.4 后端编辑冲突接口

    - POST /api/articles/:id/lock 获取编辑锁
    - DELETE /api/articles/:id/lock 释放编辑锁
    - GET /api/articles/:id/lock-status 检查锁状态
    - PUT /api/articles/:id/lock/renew 续期编辑锁
    - _Requirements: 设计要求 - 编辑冲突调解_

  - [x] 29.5 前端编辑锁API

    - 创建 api/editLock.js 编辑锁接口
    - _Requirements: 设计要求 - 编辑冲突调解_

  - [x] 29.6 前端编辑冲突处理

    - 进入编辑页面时检查并获取编辑锁
    - 如果文章被他人锁定，显示提示信息（谁在编辑、预计解锁时间）
    - 定时刷新锁状态（每5分钟续期一次）
    - 离开编辑页面时释放锁
    - _Requirements: 设计要求 - 编辑冲突调解_

- [x] 30. 用户角色权限完善
  - [x] 30.1 后端角色权限拦截器
    - 创建 RoleInterceptor 拦截器，区分 USER/REVIEWER/ADMIN 权限
    - 配置不同角色可访问的接口路径
    - _Requirements: 设计要求 - 四大角色功能边界_
  - [x] 30.2 管理员分配角色功能
    - 在用户管理页面添加角色修改功能
    - PUT /api/admin/users/:id/role 修改用户角色
    - 支持将普通用户提升为审核员或降级
    - _Requirements: 设计要求 - 用户权限配置_
  - [x] 30.3 前端角色权限控制
    - 根据用户角色显示/隐藏管理菜单
    - 审核员可访问审核管理、过时举报管理
    - 管理员可访问所有管理功能
    - _Requirements: 设计要求 - 四大角色功能边界_

- [x] 31. Checkpoint - 确保设计要求核心功能完整

  - 确保版本管理功能正常
  - 确保审核功能正常
  - 确保过时标记功能正常
  - 确保编辑冲突调解功能正常
  - 确保角色权限控制正常

---

## 阶段十一：可选增强功能

- [x] 32. 离线查看功能（可选）






  - [x] 32.1 实现 PWA 基础配置

    - 安装 vite-plugin-pwa 插件
    - 配置 Service Worker
    - 配置 manifest.json
    - _Requirements: 设计要求 - 词条收藏及离线查看_

  - [x] 32.2 实现文章离线缓存

    - 收藏的文章自动缓存到 IndexedDB
    - 离线时可查看已缓存的文章
    - _Requirements: 设计要求 - 离线查看_

- [-] 33. 评论回复功能增强（可选）



  - [x] 33.1 实现评论回复功能


    - 支持对评论进行回复（使用 parent_id 字段）
    - 评论显示为树形/嵌套结构
    - _Requirements: 设计要求 - 用户疑问回复_

  - [x] 33.2 评论通知





    - 在系统通知新增另外一个通知分类：用户通知
    - 当用户的文章被评论时发送用户通知
    - 当用户的评论被回复时发送用户通知
    - _Requirements: 设计要求 - 用户疑问回复_


- [x] 34. 项目总结 - 项目完成报告
  - 对项目进行详细总结报告
  - 对项目结构进行详细的展开说明，包含交互流程。
  - 对项目的所有核心功能进行详细说明讲述
  - 对项目的所有非核心功能进行详细说明讲述

- [ ] 35. Final Checkpoint - 项目完整性验证
  - 验证所有设计要求功能已实现
  - 进行功能完整性测试
  - 进行兼容性测试
  - 进行性能压力测试
