# Simple Mall v3.0

Simple Mall 是一个 Spring Boot 商城示例项目。v3.0 在 v2.0 的 MySQL、注册登录、收货信息、订单功能基础上，继续加入了 Swagger/OpenAPI、Servlet Filter、可选 Kafka 订单事件、curl 测试文档和更完整的 HTML meta tags。

## 技术栈

- 后端：Spring Boot 3.3.5、Spring Web、Spring Data JPA、Bean Validation
- 数据库：MySQL
- 密码加密：Spring Security Crypto、BCrypt
- API 文档：springdoc-openapi / Swagger UI
- 消息队列：Spring Kafka，可通过 `.env` 开关启用
- 前端：HTML、CSS、原生 JavaScript
- 测试数据库：H2，仅用于 `mvn test`
- 构建工具：Maven
- Java 版本：Java 21

## v3.0 新增能力

- Swagger UI：访问 `/swagger-ui.html` 查看和调试 API
- OpenAPI JSON：访问 `/api-docs`
- `LoginRequiredFilter`：统一拦截需要登录的 API
- `RequestLoggingFilter`：统一记录请求方法、路径、状态码和耗时
- Kafka 订单事件：下单成功后发布 `OrderCreatedEvent`
- Kafka 可选开关：本地未启动 Kafka 时默认使用日志事件，不影响下单
- curl 文档：提供注册、登录、保存收货信息、下单、查询订单等命令
- meta tags：补充 description、theme-color、robots、Open Graph 标签

## 相关文档

- [.doc/前后端数据库交互说明.md](.doc/前后端数据库交互说明.md)
- [.doc/API介绍文档.md](.doc/API介绍文档.md)
- [.doc/ProductController-Java语法介绍.md](.doc/ProductController-Java语法介绍.md)
- [.doc/MySQL数据查询指南.md](.doc/MySQL数据查询指南.md)
- [.doc/CURL接口测试指南.md](.doc/CURL接口测试指南.md)

注意：当前 `.gitignore` 中包含 `.doc/`，所以 `.doc` 目录默认不会被 Git 提交。

## MySQL 准备

先创建数据库：

```sql
CREATE DATABASE simple_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

项目根目录 `.env` 保存本地环境变量：

```env
DB_URL=jdbc:mysql://localhost:3306/simple_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8
DB_USERNAME=root
DB_PASSWORD=123456
KAFKA_ENABLED=false
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_CONSUMER_GROUP=simple-mall
KAFKA_ORDER_CREATED_TOPIC=mall.order.created
```

`.env` 已加入 `.gitignore`，不会上传到 GitHub。

## 启动

```powershell
mvn spring-boot:run
```

页面地址：

```text
http://localhost:8081
```

Swagger UI：

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON：

```text
http://localhost:8081/api-docs
```

## Kafka 使用

默认 `.env` 中：

```env
KAFKA_ENABLED=false
```

这表示 Kafka 功能不连接真实 Kafka 服务，下单后只记录一条订单事件日志，系统仍然稳定运行。

如果你本地已经启动 Kafka，可以改成：

```env
KAFKA_ENABLED=true
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_ORDER_CREATED_TOPIC=mall.order.created
```

然后重启项目。创建订单后，后端会发布 `OrderCreatedEvent` 到 Kafka topic。

## 主要接口

### 注册

```http
POST /api/auth/register
```

### 登录

```http
POST /api/auth/login
```

### 当前用户

```http
GET /api/auth/me
```

### 保存收货信息

```http
PUT /api/auth/profile
```

### 商品列表

```http
GET /api/products
```

### 创建订单

```http
POST /api/orders
```

### 当前用户订单

```http
GET /api/orders
```

## Filter 说明

`LoginRequiredFilter` 保护这些接口：

```text
/api/orders/**
PUT /api/auth/profile
POST /api/auth/logout
```

未登录访问会返回：

```http
401 Unauthorized
```

`RequestLoggingFilter` 会输出类似日志：

```text
POST /api/orders -> 200 (35 ms)
```

## 测试

```powershell
mvn test
```

测试会使用：

```text
src/test/resources/application.properties
```

因此测试环境使用 H2，不需要连接本地 MySQL 或 Kafka。
