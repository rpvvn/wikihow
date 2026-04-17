# WikiHow 项目开发规范

本项目是一个知识库系统，采用前后端分离架构。

## 技术栈

### 后端
- Java 21
- Spring Boot 3.x
- MyBatis-Plus 3.5.x
- MySQL
- JWT 认证

### 前端
- Vue 3 (Composition API)
- Pinia 状态管理
- Vue Router
- Element Plus UI
- Axios

---

## 后端开发规范

### Jakarta EE 命名空间

Spring Boot 3.x 使用 Jakarta EE，**必须使用 `jakarta.*` 而非 `javax.*`**：

```java
// ✅ 正确
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.servlet.http.HttpServletRequest;

// ❌ 错误
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
```

### 项目结构

```
com.vv.wikihow/
├── common/          # 通用类：Result, ResultCode, 枚举
├── config/          # 配置类：CorsConfig, WebMvcConfig
├── controller/      # 控制器：REST API 接口
├── dto/             # 数据传输对象：Request/Response
├── entity/          # 实体类：数据库表映射
├── exception/       # 异常处理：BusinessException, GlobalExceptionHandler
├── mapper/          # MyBatis-Plus Mapper 接口
├── security/        # 安全相关：JWT, UserContext
├── service/         # 服务接口
│   └── impl/        # 服务实现
└── util/            # 工具类
```

### 统一响应格式

所有 API 返回 `Result<T>` 对象：

```java
// 成功
return Result.success(data);
return Result.success("操作成功", data);

// 失败
return Result.error("错误信息");
return Result.error(ResultCode.NOT_FOUND);
```

### Controller 规范

```java
@Slf4j
@RestController
@RequestMapping("/api/xxx")
@RequiredArgsConstructor
public class XxxController {

    private final XxxService xxxService;

    @GetMapping
    public Result<PageResponse<XxxResponse>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(xxxService.list(page, size));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody XxxRequest request) {
        Long userId = UserContext.getCurrentUserId();
        xxxService.create(request, userId);
        return Result.success("创建成功", null);
    }
}
```

### Entity 规范

```java
@Data
@TableName("table_name")
public class Xxx {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fieldName;  // 驼峰命名，自动映射下划线
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### DTO 规范

- Request 类：用于接收请求参数，使用 `@NotBlank`, `@NotNull` 等校验注解
- Response 类：用于返回数据，提供 `fromEntity()` 静态方法转换

```java
@Data
public class XxxRequest {
    @NotBlank(message = "名称不能为空")
    private String name;
}

@Data
public class XxxResponse {
    private Long id;
    private String name;
    
    public static XxxResponse fromEntity(Xxx entity) {
        XxxResponse response = new XxxResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
}
```

### Service 规范

- 接口定义在 `service/` 目录
- 实现类在 `service/impl/` 目录，使用 `@Service` 注解
- 使用 `@RequiredArgsConstructor` 注入依赖
- 事务操作使用 `@Transactional`

```java
public interface XxxService {
    PageResponse<XxxResponse> list(Integer page, Integer size);
    void create(XxxRequest request, Long userId);
}

@Service
@RequiredArgsConstructor
public class XxxServiceImpl implements XxxService {
    private final XxxMapper xxxMapper;
    
    @Override
    @Transactional
    public void create(XxxRequest request, Long userId) {
        // 实现逻辑
    }
}
```

### 异常处理

使用 `BusinessException` 抛出业务异常：

```java
throw new BusinessException("错误信息");
throw new BusinessException(ResultCode.NOT_FOUND, "资源不存在");
```

---

## 前端开发规范

### 项目结构

```
src/
├── api/             # API 请求模块
├── assets/          # 静态资源
├── components/      # 公共组件
├── router/          # 路由配置
├── stores/          # Pinia 状态管理
└── views/           # 页面组件
    └── admin/       # 管理后台页面
```

### API 模块规范

每个功能模块一个文件，导出函数式 API：

```javascript
import request from './index'

export function getList(params) {
  return request.get('/xxx', { params })
}

export function create(data) {
  return request.post('/xxx', data)
}

export function update(id, data) {
  return request.put(`/xxx/${id}`, data)
}

export function remove(id) {
  return request.delete(`/xxx/${id}`)
}
```

### Vue 组件规范

使用 Composition API + `<script setup>` 语法：

```vue
<template>
  <div class="page-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const list = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getList()
    list.value = res.data || []
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-name {
  /* 样式 */
}
</style>
```

### Pinia Store 规范

使用 Composition API 风格：

```javascript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useXxxStore = defineStore('xxx', () => {
  const data = ref(null)
  
  const computed = computed(() => /* ... */)
  
  function setData(value) {
    data.value = value
  }
  
  return { data, computed, setData }
}, {
  persist: true  // 需要持久化时添加
})
```

### 命名规范

- 组件文件：PascalCase（如 `ArticleCard.vue`）
- 页面文件：PascalCase（如 `HomeView.vue`）
- API 文件：camelCase（如 `article.js`）
- CSS 类名：kebab-case（如 `.article-card`）

---

## 数据库规范

- 表名：小写下划线（如 `article_step`）
- 字段名：小写下划线（如 `created_at`）
- 主键：`id BIGINT AUTO_INCREMENT`
- 时间字段：`created_at`, `updated_at` 使用 `DATETIME`
- SQL 文件放在 `src/main/resources/db/` 目录
