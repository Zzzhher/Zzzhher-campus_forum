# 校园论坛系统 (Campus Forum)

一个基于前后端分离架构的现代化校园论坛系统，提供用户认证、论坛管理、AI内容审核、邮件通知、流量控制等功能，适用于高校校园社区交流。

## 技术栈

### 后端

- **框架**: Spring Boot 3.1.2
- **语言**: Java 17
- **安全**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0.34
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **API 文档**: Swagger 2.1.0
- **搜索**: Elasticsearch
- **对象存储**: MinIO
- **工具库**: FastJSON2、Lombok、JWT

### 前端

- **框架**: Vue 3
- **构建工具**: Vite 4.4.6
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4.2.4
- **HTTP 客户端**: Axios 1.4.0
- **富文本编辑器**: Quill

## 项目结构

```
campus_forum/
├── backend/                 # 后端应用
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── filter/          # 过滤器
│   │   │   │   ├── listener/        # 监听器
│   │   │   │   ├── mapper/          # Mapper接口
│   │   │   │   ├── repository/      # 数据访问层
│   │   │   │   ├── service/         # 业务逻辑
│   │   │   │   ├── utils/           # 工具类
│   │   │   │   └── BackendApplication.java  # 应用入口
│   │   │   └── resources/           # 配置文件
│   │   └── test/                    # 测试代码
│   ├── pom.xml                      # Maven依赖配置
│   └── prohibited.json              # 敏感词配置
├── frontend/                # 前端应用
│   ├── src/
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # 组件
│   │   ├── net/             # 网络请求
│   │   ├── router/          # 路由配置
│   │   ├── store/           # 状态管理
│   │   ├── views/           # 页面组件
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   ├── public/              # 公共资源
│   ├── index.html           # HTML模板
│   ├── package.json         # 依赖配置
│   └── vite.config.js       # Vite配置
├── sql/                     # 数据库脚本
│   └── campus_forum.sql
├── prohibited.json          # 敏感词配置
└── README.md                # 项目说明文档
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0+
- Redis 5.0+
- RabbitMQ 3.8+
- Elasticsearch 7.17+
- MinIO (可选，用于文件存储)

### 启动步骤

1. **配置数据库**
   - 创建数据库：`CREATE DATABASE campus_forum CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   - 执行SQL脚本：`mysql -u root -p campus_forum < sql/campus_forum.sql`

2. **配置后端**
   - 修改 `backend/src/main/resources/application.yml` 中的数据库连接信息
   - 配置 Redis、RabbitMQ、Elasticsearch 等服务地址

3. **启动后端**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. **配置前端**
   - 修改 `frontend/src/net/index.js` 中的 API 地址

5. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## 核心功能

### 用户认证

- **注册**: 邮箱注册，发送验证码验证，支持用户名/密码/邮箱格式验证
- **登录**: 邮箱/用户名登录，JWT 令牌生成，支持记住我功能
- **忘记密码**: 邮箱验证重置密码，完整的密码重置流程
- **令牌管理**: JWT 令牌验证和刷新，安全的身份认证机制

### 论坛功能

- **话题管理**: 创建、编辑、删除话题
- **评论系统**: 支持话题评论，回复功能
- **互动功能**: 点赞、收藏、关注
- **分类管理**: 话题分类创建和管理
- **搜索功能**: 基于 Elasticsearch 的全文搜索

### 系统安全

- **CORS 支持**: 跨域资源共享配置，支持前后端分离架构
- **流量控制**: 请求频率限制，防止恶意请求
- **请求日志**: 记录所有 HTTP 请求，便于调试和监控
- **异常处理**: 统一的错误处理机制，友好的错误提示
- **AI内容审核**: 集成校园论坛AI审核模型，实现内容自动审核

### 消息服务

- **邮件发送**: 通过 RabbitMQ 异步发送邮件，支持验证码和通知邮件
- **队列监听**: 邮件发送队列处理，确保邮件可靠送达
- **通知系统**: 站内通知，包括评论、点赞、关注等

### 管理员功能

- **用户管理**: 查看、编辑、禁用用户
- **内容管理**: 审核、删除违规内容
- **统计分析**: 论坛活跃度、用户增长等统计
- **系统配置**: 敏感词管理、审核策略配置

## API 接口

### 用户认证接口

- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/reset` - 发送密码重置邮件
- `POST /api/auth/confirm-reset` - 确认密码重置

### 论坛接口

- `GET /api/forum/topics` - 获取话题列表
- `GET /api/forum/topic/{id}` - 获取话题详情
- `POST /api/forum/topic` - 创建话题
- `PUT /api/forum/topic/{id}` - 更新话题
- `DELETE /api/forum/topic/{id}` - 删除话题
- `POST /api/forum/comment` - 添加评论
- `GET /api/forum/comments/{topicId}` - 获取评论列表

### 用户接口

- `GET /api/account/info` - 获取用户信息
- `PUT /api/account/details` - 更新用户详情
- `PUT /api/account/privacy` - 更新隐私设置
- `PUT /api/account/password` - 修改密码

### 通知接口

- `GET /api/notification` - 获取通知列表
- `PUT /api/notification/{id}/read` - 标记通知为已读

### 管理员接口

- `GET /api/admin/users` - 获取用户列表
- `PUT /api/admin/user/{id}/status` - 修改用户状态
- `GET /api/admin/topics` - 获取话题列表
- `DELETE /api/admin/topic/{id}` - 删除话题
- `GET /api/admin/comments` - 获取评论列表
- `DELETE /api/admin/comment/{id}` - 删除评论

## 配置说明

### 后端配置

- **主要配置文件**: `backend/src/main/resources/application.yml`

主要配置项：

- `spring.datasource` - 数据库连接配置
- `spring.rabbitmq` - RabbitMQ 配置
- `spring.redis` - Redis 配置
- `spring.elasticsearch` - Elasticsearch 配置
- `minio` - MinIO 配置
- `spring.security.jwt` - JWT 配置
- `spring.web.flow` - 流量控制配置

### 前端配置

- `frontend/src/net/index.js` - API 地址配置
- `frontend/vite.config.js` - Vite 构建配置

## 与AI审核模型集成

本项目集成了 `campus_forum_model` 作为内容审核服务，实现对用户发布内容的自动审核。

### 集成方式

1. 启动 AI 审核服务（详见 `campus_forum_model` 项目说明）
2. 在 `backend/src/main/resources/application.yml` 中配置 AI 审核服务地址
3. 系统会自动将用户发布的内容发送到 AI 审核服务进行检测

## 联系方式

如有问题或建议，请通过以下方式联系：

- Email: [2142609137@qq.com](mailto:2142609137@qq.com)
