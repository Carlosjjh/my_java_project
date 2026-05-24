# API 介绍文档

本文档以当前商城项目中的两个后端文件为例，解释 API 是什么、API 如何工作、API 通常包含哪些部分，以及 API 在本项目中长什么样。

示例文件：

```text
src/main/java/carlos/jiang/web/ProductController.java
src/main/java/carlos/jiang/web/OrderController.java
```

## 一、API 的全称

API 的全称是：

```text
Application Programming Interface
```

中文通常翻译为：

```text
应用程序编程接口
```

一句话理解：

```text
API 是一个程序暴露给另一个程序使用的功能入口。
```

在你的商城项目中，API 主要指：

```text
后端 Spring Boot 提供给前端 JavaScript 调用的 HTTP 接口。
```

## 二、API 是什么

API 不是单独的某一个字段，也不只是某一个 Java 方法。

更准确地说：

```text
API = 请求地址 + 请求方式 + 请求参数 + 响应结果 + 使用规则
```

比如：

```http
GET /api/products
```

这就是一个 API。它的含义是：

```text
前端可以用 GET 方法访问 /api/products，向后端获取商品列表。
```

再比如：

```http
POST /api/orders
```

这也是一个 API。它的含义是：

```text
前端可以用 POST 方法访问 /api/orders，向后端提交订单数据。
```

## 三、为什么需要 API

浏览器前端不能直接调用 Java 方法，也不应该直接访问数据库。

前端能做的是发 HTTP 请求：

```javascript
fetch("/api/products")
```

后端能做的是接收 HTTP 请求：

```java
@GetMapping
public List<Product> findAll() {
    return productRepository.findAll();
}
```

API 就是前端和后端之间的约定：

```text
前端：我访问哪个地址？
后端：我用哪个 Controller 接收？
前端：我要用 GET 还是 POST？
后端：我要返回什么 JSON？
前端：我要传哪些字段？
后端：我要如何解析这些字段？
```

## 四、本项目中的 API 总览

当前项目有四个主要 API：

```http
GET /api/products
GET /api/products/{id}
GET /api/orders
POST /api/orders
```

对应关系：

| API | Java 文件 | 作用 |
| --- | --- | --- |
| `GET /api/products` | `ProductController.java` | 查询所有商品 |
| `GET /api/products/{id}` | `ProductController.java` | 查询单个商品 |
| `GET /api/orders` | `OrderController.java` | 查询所有订单 |
| `POST /api/orders` | `OrderController.java` | 创建订单 |

## 五、ProductController 中的 API

文件位置：

```text
src/main/java/carlos/jiang/web/ProductController.java
```

代码核心：

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
```

`@RestController` 表示这个类是一个 REST API 控制器。方法返回的 Java 对象会被 Spring Boot 自动转换成 JSON。

`@RequestMapping("/api/products")` 表示这个 Controller 下面所有 API 的基础路径都是：

```http
/api/products
```

## 六、商品列表 API：GET /api/products

后端代码：

```java
@GetMapping
public List<Product> findAll() {
    return productRepository.findAll();
}
```

完整 API：

```http
GET /api/products
```

工作流程：

```text
1. 前端调用 fetch("/api/products")
2. 浏览器发送 GET http://localhost:8081/api/products
3. Spring Boot 找到 ProductController
4. Spring Boot 匹配 @GetMapping 方法
5. 后端执行 productRepository.findAll()
6. 后端查询 PRODUCT 表
7. 后端把商品列表转成 JSON 返回给前端
8. 前端渲染商品卡片
```

请求长这样：

```http
GET /api/products HTTP/1.1
Host: localhost:8081
```

响应长这样：

```json
[
  {
    "id": 1,
    "name": "经典白T恤",
    "description": "柔软纯棉，适合日常通勤和周末出游。",
    "price": 79.00,
    "imageUrl": "https://images.unsplash.com/...",
    "stock": 120
  }
]
```

返回字段：

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| `id` | number | 商品 ID |
| `name` | string | 商品名称 |
| `description` | string | 商品描述 |
| `price` | number | 商品价格 |
| `imageUrl` | string | 商品图片地址 |
| `stock` | number | 商品库存 |

## 七、单个商品 API：GET /api/products/{id}

后端代码：

```java
@GetMapping("/{id}")
public Product findById(@PathVariable Long id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + id));
}
```

完整 API 示例：

```http
GET /api/products/1
```

这里的 `{id}` 是路径参数。

例如：

```text
/api/products/1
/api/products/2
/api/products/3
```

都能匹配：

```java
@GetMapping("/{id}")
```

`@PathVariable Long id` 会把路径里的值取出来。

例如访问：

```http
GET /api/products/1
```

后端拿到：

```java
id = 1
```

请求字段：

| 字段 | 位置 | 类型 | 含义 |
| --- | --- | --- | --- |
| `id` | URL 路径 | number | 商品 ID |

## 八、OrderController 中的 API

文件位置：

```text
src/main/java/carlos/jiang/web/OrderController.java
```

代码核心：

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
```

这表示订单相关 API 的基础路径是：

```http
/api/orders
```

## 九、订单列表 API：GET /api/orders

后端代码：

```java
@GetMapping
public List<OrderResponse> findAll() {
    return orderService.findAllOrders().stream().map(OrderResponse::from).toList();
}
```

完整 API：

```http
GET /api/orders
```

工作流程：

```text
1. 前端调用 fetch("/api/orders")
2. 后端进入 OrderController.findAll()
3. 后端调用 orderService.findAllOrders()
4. OrderService 从数据库查询订单
5. 后端把订单转换成 OrderResponse
6. Spring Boot 把 OrderResponse 转成 JSON 返回前端
```

响应长这样：

```json
[
  {
    "id": 1,
    "customerName": "张三",
    "phone": "13800000000",
    "address": "上海市浦东新区",
    "totalAmount": 158.00,
    "createdAt": "2026-05-23T14:41:27",
    "items": [
      {
        "productId": 1,
        "productName": "经典白T恤",
        "unitPrice": 79.00,
        "quantity": 2,
        "lineTotal": 158.00
      }
    ]
  }
]
```

订单主字段：

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| `id` | number | 订单 ID |
| `customerName` | string | 收货人姓名 |
| `phone` | string | 手机号 |
| `address` | string | 收货地址 |
| `totalAmount` | number | 订单总金额 |
| `createdAt` | string | 创建时间 |
| `items` | array | 订单明细列表 |

订单明细字段：

| 字段 | 类型 | 含义 |
| --- | --- | --- |
| `productId` | number | 商品 ID |
| `productName` | string | 商品名称 |
| `unitPrice` | number | 商品单价 |
| `quantity` | number | 购买数量 |
| `lineTotal` | number | 当前商品小计 |

## 十、创建订单 API：POST /api/orders

后端代码：

```java
@PostMapping
public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
    return OrderResponse.from(orderService.createOrder(request));
}
```

完整 API：

```http
POST /api/orders
```

`@PostMapping` 表示这个方法处理 POST 请求。POST 通常用于创建新数据，例如提交订单。

`@RequestBody` 表示后端会从 HTTP 请求体中读取 JSON。

`@Valid` 表示提交的数据需要校验。

## 十一、创建订单 API 的请求结构

前端代码：

```javascript
fetch("/api/orders", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(payload)
})
```

对应 HTTP 请求：

```http
POST /api/orders HTTP/1.1
Host: localhost:8081
Content-Type: application/json
```

请求体：

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

请求字段：

| 字段 | 类型 | 是否必填 | 含义 |
| --- | --- | --- | --- |
| `customerName` | string | 是 | 收货人姓名 |
| `phone` | string | 是 | 手机号 |
| `address` | string | 是 | 收货地址 |
| `items` | array | 是 | 商品列表 |
| `items[].productId` | number | 是 | 商品 ID |
| `items[].quantity` | number | 是 | 购买数量 |

## 十二、创建订单 API 的完整工作流程

```text
1. 用户在前端页面点击“提交订单”
2. app.js 整理购物车和收货信息
3. app.js 使用 fetch("/api/orders") 发起 POST 请求
4. 请求体中带上订单 JSON
5. Spring Boot 根据 /api/orders 找到 OrderController
6. Spring Boot 根据 POST 找到 create() 方法
7. @RequestBody 把 JSON 转成 CreateOrderRequest
8. @Valid 校验请求数据
9. OrderController 调用 OrderService
10. OrderService 查询商品、扣减库存、创建订单
11. Repository 把订单写入 H2 数据库
12. 后端返回 OrderResponse
13. Spring Boot 把 OrderResponse 转成 JSON
14. 前端收到 JSON
15. 前端显示“订单创建成功”
```

## 十三、API 和 Java 方法的关系

在这个项目里：

```java
@GetMapping
public List<Product> findAll() {
    return productRepository.findAll();
}
```

这个 Java 方法是 API 的后端实现。

真正对外暴露给前端的是：

```http
GET /api/products
```

所以可以这样理解：

```text
API 是外部访问规则。
Java 方法是内部实现逻辑。
```

前端知道：

```text
GET /api/products
```

但前端不知道、也不需要知道：

```java
productRepository.findAll()
```

## 十四、API 和数据库的关系

API 本身不等于数据库。

API 是后端暴露出来的入口，数据库是后端内部使用的数据存储。

关系是：

```text
前端
  -> 调用 API
  -> Controller
  -> Service
  -> Repository
  -> 数据库
```

前端不能直接访问数据库，它只能通过 API 间接获取数据库数据。

## 十五、如何在浏览器中看到 API

启动项目：

```powershell
mvn spring-boot:run
```

浏览器访问：

```text
http://localhost:8081/api/products
```

你会看到商品 JSON。

访问：

```text
http://localhost:8081/api/orders
```

你会看到订单 JSON。

也可以打开开发者工具：

```text
F12 -> Network -> Fetch/XHR
```

刷新页面后，你会看到前端调用了：

```text
/api/products
/api/orders
```

这就是前端正在调用 API。

## 十六、总结

API 的核心作用是：

```text
让前端和后端通过统一规则交换数据。
```

在本项目中：

```text
ProductController 提供商品 API
OrderController 提供订单 API
app.js 使用 fetch() 调用这些 API
Repository 负责从数据库读取或保存数据
```

所以，本项目中的 API 不是凭空出现的，它们就是 Controller 中通过 `@GetMapping`、`@PostMapping`、`@RequestMapping` 暴露出来的 HTTP 接口。
