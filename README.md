# Simple Mall v2.0

这是一个最小可运行的商城项目。v2.0 版本在 v1.0 的商品浏览、购物车、下单功能基础上，新增了 MySQL 数据库、用户注册登录、登录状态校验和个人收货信息维护。

## 技术栈

- 后端：Spring Boot 3.3.5、Spring Web、Spring Data JPA、Bean Validation
- 密码加密：Spring Security Crypto、BCrypt
- 前端：HTML、CSS、原生 JavaScript
- 数据库：MySQL
- 测试数据库：H2，仅用于 `mvn test`
- 构建工具：Maven
- Java 版本：Java 21

## 已实现功能

- 商品列表：查询所有商品、查询单个商品
- 用户系统：注册、登录、查询当前登录用户、退出登录
- 密码安全：数据库只保存 BCrypt 哈希，不保存明文密码
- 收货信息：登录后维护收货人、手机号、收货地址
- 订单系统：登录后提交订单，订单自动使用用户保存的收货信息
- 订单隔离：用户只能查看自己的订单
- 库存扣减：下单时校验库存并扣减商品库存
- 初始化数据：应用启动后自动写入 6 个示例商品

## 相关文档

- [.doc/前后端数据库交互说明.md](.doc/前后端数据库交互说明.md)
- [.doc/API介绍文档.md](.doc/API介绍文档.md)
- [.doc/ProductController-Java语法介绍.md](.doc/ProductController-Java语法介绍.md)
- [.doc/MySQL数据查询指南.md](.doc/MySQL数据查询指南.md)

注意：当前 `.gitignore` 中包含 `.doc/`，所以 `.doc` 目录默认不会被 Git 提交。

## MySQL 准备

先在本地 MySQL 中创建数据库：

```sql
CREATE DATABASE simple_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

项目默认连接配置：

```text
URL: jdbc:mysql://localhost:3306/simple_mall
Username: root
Password: 空
```

如果你的 MySQL 用户名或密码不同，可以用环境变量覆盖。

PowerShell 示例：

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/simple_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="你的MySQL密码"
mvn spring-boot:run
```

Git Bash 示例：

```bash
export DB_URL="jdbc:mysql://localhost:3306/simple_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
export DB_USERNAME="root"
export DB_PASSWORD="你的MySQL密码"
mvn spring-boot:run
```

## 启动方式

在项目根目录执行：

```powershell
mvn spring-boot:run
```

启动成功后访问：

```text
http://localhost:8081
```

## 接口说明

### 用户注册

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "username": "张三",
  "account": "zhangsan",
  "password": "123456"
}
```

### 用户登录

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "account": "zhangsan",
  "password": "123456"
}
```

### 查询当前登录用户

```http
GET /api/auth/me
```

### 保存收货信息

```http
PUT /api/auth/profile
Content-Type: application/json
```

```json
{
  "recipientName": "张三",
  "phone": "13800000000",
  "address": "上海市浦东新区"
}
```

### 退出登录

```http
POST /api/auth/logout
```

### 查询商品

```http
GET /api/products
```

### 查询单个商品

```http
GET /api/products/{id}
```

### 创建订单

需要先登录，并保存收货信息。

```http
POST /api/orders
Content-Type: application/json
```

```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

### 查询当前用户订单

```http
GET /api/orders
```

## 主要文件作用

```text
pom.xml
```

Maven 项目配置文件，定义 Spring Boot、JPA、MySQL、密码加密、测试依赖和启动插件。

```text
src/main/resources/application.properties
```

主环境配置，连接 MySQL，并使用 `spring.jpa.hibernate.ddl-auto=update` 自动维护表结构。

```text
src/test/resources/application.properties
```

测试环境配置，使用 H2 内存数据库，方便执行 `mvn test`。

```text
src/main/java/carlos/jiang/App.java
```

Spring Boot 应用入口。

```text
src/main/java/carlos/jiang/DataInitializer.java
```

应用启动时初始化示例商品。

```text
src/main/java/carlos/jiang/model/AppUser.java
```

用户实体，保存用户名、账号、密码哈希、收货人、手机号、地址。

```text
src/main/java/carlos/jiang/model/Product.java
```

商品实体。

```text
src/main/java/carlos/jiang/model/ShopOrder.java
```

订单实体，和登录用户关联。

```text
src/main/java/carlos/jiang/model/OrderItem.java
```

订单明细实体。

```text
src/main/java/carlos/jiang/repository/*.java
```

数据库访问接口，继承 Spring Data JPA 的 `JpaRepository`。

```text
src/main/java/carlos/jiang/service/AuthService.java
```

注册、登录、当前用户、退出登录、收货信息维护逻辑。

```text
src/main/java/carlos/jiang/service/OrderService.java
```

订单业务逻辑，负责校验收货信息、扣减库存、保存订单。

```text
src/main/java/carlos/jiang/web/*.java
```

REST API 控制器和统一异常处理。

```text
src/main/resources/static/index.html
src/main/resources/static/styles.css
src/main/resources/static/app.js
```

前端页面、样式和交互逻辑。

## 测试

执行：

```powershell
mvn test
```

测试时会使用 `src/test/resources/application.properties` 中配置的 H2 内存数据库，不需要连接你的本地 MySQL。
