# 校园论坛系统 (Campus Forum)

一个基于前后端分离架构的现代化校园论坛系统模板，提供用户认证、邮件通知、流量控制等功能，可以用来简化后续其他项目的开发。

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
- **工具库**: FastJSON2、Lombok、JWT

### 前端

- **框架**: Vue 3
- **构建工具**: Vite 4.4.6
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4.2.4
- **HTTP 客户端**: Axios 1.4.0

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
│   │   │   │   ├── service/         # 业务逻辑
│   │   │   │   ├── utils/           # 工具类
│   │   │   │   └── BackendApplication.java  # 应用入口
│   │   │   └── resources/           # 配置文件
│   │   └── test/                    # 测试代码
│   └── pom.xml                      # Maven依赖配置
├── frontend/                # 前端应用
│   ├── src/
│   │   ├── net/             # 网络请求
│   │   ├── router/          # 路由配置
│   │   ├── views/           # 页面组件
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 入口文件
│   ├── index.html           # HTML模板
│   ├── package.json         # 依赖配置
│   └── vite.config.js       # Vite配置
├── database.sql             # 数据库脚本
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

## 核心功能

### 用户认证（登录注册模板）

- **注册**: 邮箱注册，发送验证码验证，支持用户名/密码/邮箱格式验证
- **登录**: 邮箱/用户名登录，JWT 令牌生成，支持记住我功能
- **忘记密码**: 邮箱验证重置密码，完整的密码重置流程
- **令牌管理**: JWT 令牌验证和刷新，安全的身份认证机制

### 系统安全

- **CORS 支持**: 跨域资源共享配置，支持前后端分离架构
- **流量控制**: 请求频率限制，防止恶意请求
- **请求日志**: 记录所有 HTTP 请求，便于调试和监控
- **异常处理**: 统一的错误处理机制，友好的错误提示

### 消息服务

- **邮件发送**: 通过 RabbitMQ 异步发送邮件，支持验证码和通知邮件
- **队列监听**: 邮件发送队列处理，确保邮件可靠送达

## 登录注册功能模板

该项目提供了一个完整的登录注册功能模板，可直接用于快速构建具有认证功能的 Web 应用。

### 功能特点

#### 前端特点

- **现代化 UI 设计**: 使用 Element Plus 组件库，提供美观的用户界面
- **响应式布局**: 支持 PC 和移动设备，自适应不同屏幕尺寸
- **表单验证**: 实时表单验证，提供友好的错误提示
- **验证码倒计时**: 邮箱验证码发送倒计时功能
- **密码强度验证**: 密码长度和格式验证
- **登录状态管理**: 基于 JWT 的登录状态管理

#### 后端特点

- **RESTful API 设计**: 规范的 API 接口设计，便于前端调用
- **JWT 认证**: 基于 JWT 的无状态认证机制
- **邮箱验证**: 支持注册和密码重置的邮箱验证
- **安全密码存储**: 密码加密存储，确保用户数据安全
- **流量控制**: 防止恶意请求和暴力破解
- **Swagger 文档**: 自动生成 API 文档，便于接口调试

## API 接口

### 用户认证接口

- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/reset` - 发送密码重置邮件
- `POST /api/auth/confirm-reset` - 确认密码重置

## 配置说明

### 后端配置

- **开发环境**: `application-dev.yml`
- **生产环境**: `application-prod.yml`

主要配置项：

- `spring.datasource` - 数据库连接配置
- `spring.rabbitmq` - RabbitMQ 配置
- `spring.security.jwt` - JWT 配置
- `spring.web.flow` - 流量控制配置

### 前端配置

- `axios.defaults.baseURL` - 后端 API 地址配置
- `vite.config.js` - Vite 构建配置

## 联系方式

如有问题或建议，请通过以下方式联系：

- Email: [2142609137@qq.com](mailto:2142609137@qq.com)
