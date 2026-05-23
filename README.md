# Simple Mall

这是一个基于 Maven quickstart 改造出来的最小可运行商城项目。第一版目标是跑通完整链路：浏览商品、加入购物车、填写收货信息、提交订单、查看最近订单。

## 技术栈

- 后端：Spring Boot 3.3.5、Spring Web、Spring Data JPA
- 前端：HTML、CSS、原生 JavaScript
- 数据库：H2 内存数据库
- 构建工具：Maven
- Java 版本：Java 21

## 已实现功能

- 商品列表接口：查询所有商品、查询单个商品
- 订单接口：提交订单、查询订单列表
- 库存扣减：下单时校验库存并扣减商品库存
- 初始化数据：应用启动后自动写入 6 个示例商品
- 前端页面：商品展示、购物车数量调整、订单提交、最近订单查看
- H2 控制台：可通过浏览器查看内存数据库

## 启动方式

在项目根目录执行：

```powershell
mvn spring-boot:run
```

启动成功后访问：

```text
http://localhost:8081
```

H2 数据库控制台：

```text
http://localhost:8081/h2-console
```

连接信息：

```text
JDBC URL: jdbc:h2:mem:mall
User Name: sa
Password: 留空
```

## 接口说明

### 查询商品

```http
GET /api/products
```

### 查询单个商品

```http
GET /api/products/{id}
```

### 创建订单

```http
POST /api/orders
Content-Type: application/json
```

请求示例：

```json
{
  "customerName": "张三",
  "phone": "13800000000",
  "address": "上海市浦东新区",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ]
}
```

### 查询订单

```http
GET /api/orders
```

## 文件作用

```text
pom.xml
```

Maven 项目配置文件，定义 Spring Boot 父工程、依赖、Java 版本和启动插件。

```text
.gitignore
```

Git 忽略配置，排除 Maven 构建产物、本地日志和 IDE 临时目录。

```text
src/main/java/carlos/jiang/App.java
```

Spring Boot 应用入口，负责启动后端服务。

```text
src/main/java/carlos/jiang/DataInitializer.java
```

启动时初始化示例商品数据。

```text
src/main/java/carlos/jiang/model/Product.java
```

商品实体，对应数据库中的商品表。

```text
src/main/java/carlos/jiang/model/ShopOrder.java
```

订单实体，保存收货信息、订单总金额、创建时间和订单明细。

```text
src/main/java/carlos/jiang/model/OrderItem.java
```

订单明细实体，保存下单时的商品名称、单价、数量和小计。

```text
src/main/java/carlos/jiang/repository/ProductRepository.java
```

商品数据访问接口，继承 Spring Data JPA 的 `JpaRepository`。

```text
src/main/java/carlos/jiang/repository/ShopOrderRepository.java
```

订单数据访问接口，继承 Spring Data JPA 的 `JpaRepository`。

```text
src/main/java/carlos/jiang/service/OrderService.java
```

订单业务逻辑，负责合并购物车商品、校验商品是否存在、扣减库存并保存订单。

```text
src/main/java/carlos/jiang/web/ProductController.java
```

商品 REST 接口控制器。

```text
src/main/java/carlos/jiang/web/OrderController.java
```

订单 REST 接口控制器。

```text
src/main/java/carlos/jiang/web/ApiExceptionHandler.java
```

统一处理接口异常，返回前端可读的错误信息。

```text
src/main/java/carlos/jiang/web/dto/*.java
```

接口请求和响应对象，隔离前端 JSON 数据与数据库实体。

```text
src/main/resources/application.properties
```

Spring Boot 配置文件，配置应用名、H2 数据库、JPA 建表策略和 H2 控制台。

```text
src/main/resources/static/index.html
```

商城首页 HTML。

```text
src/main/resources/static/styles.css
```

商城页面样式。

```text
src/main/resources/static/app.js
```

前端交互逻辑，负责请求接口、渲染商品、维护购物车和提交订单。

```text
src/test/java/carlos/jiang/AppTest.java
```

Spring Boot 上下文加载测试，确认应用基础配置可以正常启动。

## 后续可以扩展

- 使用 MySQL 替换 H2
- 增加用户登录和权限
- 增加后台商品管理
- 增加订单状态流转
- 增加分页、搜索和商品分类
