# KAFKA、CURL、FILTER、META_TAG、Swagger 介绍

这份文档用于解释商城项目 v3.0 中提到或使用到的几个概念：

- Kafka
- curl
- Filter
- meta tag
- Swagger / OpenAPI

它们不是同一类东西。简单来说：

| 名称 | 属于哪一类 | 主要作用 |
| --- | --- | --- |
| Kafka | 消息队列 / 事件流平台 | 让系统之间异步传递消息 |
| curl | 命令行 HTTP 工具 | 在命令行里测试 API |
| Filter | 后端请求过滤器 | 在请求进入 Controller 前做统一处理 |
| meta tag | HTML 元信息标签 | 给浏览器、搜索引擎、社交平台看的网页信息 |
| Swagger | API 文档和调试工具 | 自动生成后端接口文档，并能在线测试接口 |

---

## 1. Kafka 是什么

Kafka 全称一般直接叫 Apache Kafka。

它是一个高性能的消息队列和事件流平台。

你可以把 Kafka 理解成一个“消息中转站”：

1. 一个系统产生消息。
2. 把消息发送到 Kafka。
3. 另一个系统从 Kafka 里读取消息。
4. 两个系统不需要直接互相等待。

在传统写法里，下单成功后，后端可能会立刻做很多事情：

- 保存订单
- 扣库存
- 发短信
- 发邮件
- 通知仓库
- 写日志
- 生成统计数据

如果这些事情全部在一次请求里同步完成，用户点击“提交订单”后就会等很久。

使用 Kafka 后，可以先完成最重要的事情：保存订单。然后把“订单已创建”这个事件发给 Kafka。其他系统或者模块后续慢慢消费这个事件。

---

## 2. Kafka 如何工作

Kafka 里有几个核心概念：

| 概念 | 含义 |
| --- | --- |
| Producer | 生产者，负责发送消息 |
| Consumer | 消费者，负责读取消息 |
| Topic | 主题，消息分类 |
| Message | 消息内容 |
| Broker | Kafka 服务器节点 |
| Consumer Group | 消费者组，用来管理多个消费者如何分工 |

一个典型流程是：

```text
用户下单
  ↓
Spring Boot 后端保存订单
  ↓
后端作为 Producer 发送消息到 Kafka Topic
  ↓
Kafka 保存消息
  ↓
Consumer 从 Topic 中读取消息
  ↓
执行后续业务，例如发通知、做统计、同步库存
```

---

## 3. Kafka 在当前项目中的作用

当前商城项目中，Kafka 用来处理“订单创建事件”。

当用户提交订单后：

1. 后端先把订单保存到 MySQL。
2. 保存成功后，创建一个 `OrderCreatedEvent` 事件对象。
3. 如果 Kafka 开启，就把事件发送到 Kafka。
4. 如果 Kafka 没有开启，就使用日志方式模拟发送事件。

这样做的好处是：

- 下单接口不会强依赖 Kafka。
- 你本地没有启动 Kafka 时，项目也能正常运行。
- 以后可以很自然地扩展订单通知、库存同步、数据统计等功能。

当前项目里 Kafka 默认是关闭的：

```properties
KAFKA_ENABLED=false
```

如果你本地已经启动了 Kafka，可以在 `.env` 里改成：

```properties
KAFKA_ENABLED=true
```

并配置 Kafka 地址：

```properties
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_ORDER_CREATED_TOPIC=mall.order.created
```

---

## 4. Kafka 消息长什么样

当前项目的订单创建事件可以理解成类似下面这样的 JSON：

```json
{
  "orderId": 1,
  "username": "carlos",
  "customerName": "张三",
  "phone": "13800138000",
  "address": "北京市朝阳区",
  "totalAmount": 199.00,
  "createdAt": "2026-05-23T22:30:00"
}
```

它表达的意思是：

```text
有一个订单已经创建成功了，订单编号是 1，用户是 carlos，订单金额是 199.00。
```

Kafka 不负责理解业务含义，它只负责可靠地保存和传递这条消息。

---

## 5. curl 是什么

curl 是一个命令行工具。

它的作用是发送 HTTP 请求。

平时我们访问网页，是通过浏览器发送请求：

```text
浏览器 → 后端服务器
```

使用 curl 时，是通过命令行发送请求：

```text
命令行 curl → 后端服务器
```

它常用于测试后端 API。

比如前端还没写好时，后端开发者可以用 curl 测试接口是否正常。

---

## 6. curl 和 API 的关系

API 是后端提供的接口。

curl 是调用 API 的工具。

比如当前项目有一个商品列表接口：

```http
GET /api/products
```

用浏览器访问可以这样：

```text
http://localhost:8081/api/products
```

用 curl 访问可以这样：

```bash
curl http://localhost:8081/api/products
```

如果接口返回 JSON，命令行里就会看到类似：

```json
[
  {
    "id": 1,
    "name": "无线鼠标",
    "price": 59.90,
    "stock": 100
  }
]
```

---

## 7. curl 常见写法

### 7.1 GET 请求

GET 通常用于查询数据。

```bash
curl http://localhost:8081/api/products
```

含义：

```text
请求后端返回商品列表。
```

---

### 7.2 POST 请求

POST 通常用于新增数据。

例如注册用户：

```bash
curl -X POST http://localhost:8081/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"carlos\",\"account\":\"carlos001\",\"password\":\"123456\"}"
```

其中：

| 参数 | 含义 |
| --- | --- |
| `-X POST` | 指定请求方法是 POST |
| `-H` | 设置请求头 |
| `Content-Type: application/json` | 告诉后端，请求体是 JSON |
| `-d` | 设置请求体数据 |

---

### 7.3 带 Cookie 的请求

登录系统通常需要保存登录状态。

在浏览器里，Cookie 会自动保存。

在 curl 里，需要手动保存和携带 Cookie。

登录并保存 Cookie：

```bash
curl -c cookie.txt -X POST http://localhost:8081/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"account\":\"carlos001\",\"password\":\"123456\"}"
```

携带 Cookie 查询订单：

```bash
curl -b cookie.txt http://localhost:8081/api/orders
```

含义：

```text
先登录，保存登录凭证；之后再带着登录凭证访问需要登录的接口。
```

---

## 8. Filter 是什么

Filter 是 Java Web / Spring Boot 后端里的过滤器。

它的作用是在请求进入 Controller 之前，先进行统一处理。

你可以把它理解为后端接口前面的一道“检查关”。

请求流程大概是：

```text
浏览器 / curl
  ↓
Filter
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
Database
```

如果 Filter 判断请求不符合要求，可以直接拦截，不让它继续进入 Controller。

---

## 9. Filter 可以做什么

Filter 常见用途包括：

| 用途 | 说明 |
| --- | --- |
| 登录校验 | 判断用户是否已登录 |
| 权限控制 | 判断用户是否有权限访问某个接口 |
| 请求日志 | 记录请求地址、方法、耗时、状态码 |
| 跨域处理 | 处理前后端分离时的跨域请求 |
| 字符编码 | 统一设置请求和响应编码 |
| 安全检查 | 拦截非法请求 |

当前项目 v3.0 中使用了两个 Filter：

| Filter | 作用 |
| --- | --- |
| `LoginRequiredFilter` | 拦截需要登录的接口 |
| `RequestLoggingFilter` | 记录请求日志 |

---

## 10. 登录校验 Filter 如何工作

以订单接口为例：

```http
GET /api/orders
```

订单属于用户个人数据，所以应该要求登录。

流程如下：

```text
前端请求 /api/orders
  ↓
LoginRequiredFilter 检查 Session 里是否有登录用户
  ↓
如果没有登录，直接返回 401
  ↓
如果已经登录，放行请求
  ↓
OrderController 继续处理订单查询
```

未登录时，后端会返回类似：

```json
{
  "message": "请先登录"
}
```

HTTP 状态码是：

```http
401 Unauthorized
```

这说明请求没有通过登录验证。

---

## 11. Filter 和 Controller 的区别

| 对比项 | Filter | Controller |
| --- | --- | --- |
| 执行时机 | Controller 之前 | Filter 之后 |
| 主要职责 | 通用检查、统一处理 | 具体业务接口 |
| 是否针对单个业务 | 通常不是 | 通常是 |
| 例子 | 登录校验、请求日志 | 查询商品、提交订单 |

简单理解：

```text
Filter 管“能不能进来”
Controller 管“进来以后做什么”
```

---

## 12. meta tag 是什么

meta tag 是 HTML 里的元信息标签。

它写在 HTML 文件的 `<head>` 标签里面。

meta tag 通常不会直接显示在页面上，但是浏览器、搜索引擎、社交平台会读取它。

例如：

```html
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="一个基于 Spring Boot 的简单商城项目">
```

这些信息告诉浏览器和搜索引擎：

- 当前网页使用什么字符编码
- 手机端如何缩放页面
- 当前网页的描述是什么

---

## 13. 常见 meta tag

### 13.1 字符编码

```html
<meta charset="UTF-8">
```

作用：

```text
告诉浏览器使用 UTF-8 编码解析网页。
```

如果没有正确设置编码，中文可能会乱码。

---

### 13.2 移动端适配

```html
<meta name="viewport" content="width=device-width, initial-scale=1.0">
```

作用：

```text
让网页在手机端按照设备宽度正确显示。
```

这是移动端页面非常常见的配置。

---

### 13.3 页面描述

```html
<meta name="description" content="一个基于 Spring Boot、MySQL、Kafka 的简单商城项目">
```

作用：

```text
告诉搜索引擎这个网页大概是做什么的。
```

---

### 13.4 主题色

```html
<meta name="theme-color" content="#0f766e">
```

作用：

```text
在部分浏览器或移动端环境中影响地址栏、状态栏等 UI 颜色。
```

---

### 13.5 Open Graph 分享信息

```html
<meta property="og:title" content="Simple Mall">
<meta property="og:description" content="一个简单商城项目">
```

作用：

```text
当网页链接分享到社交平台时，平台可能会读取这些信息作为标题和描述。
```

---

## 14. meta tag 在当前项目中的作用

当前项目的前端页面是：

```text
src/main/resources/static/index.html
```

在这个文件中加入 meta tag 后，页面会更规范：

- 中文编码更稳定
- 手机端显示更正常
- 搜索引擎更容易理解页面内容
- 浏览器主题色更统一
- 分享链接时有更明确的标题和描述

meta tag 不负责业务逻辑。

它不会影响登录、下单、数据库保存这些功能。

它主要影响的是网页基础信息和展示体验。

---

## 15. Swagger 是什么

Swagger 是一个 API 文档和接口调试工具。

现在 Java Spring Boot 项目里常用的是 OpenAPI 规范加 Swagger UI 页面。

你可以把 Swagger 理解成：

```text
后端 API 的自动说明书 + 在线测试页面
```

以前写 API 文档，可能需要手动维护一个 Markdown 或 Word 文件。

问题是：

- 代码改了，文档忘记改
- 接口参数变了，文档还是旧的
- 前端不知道接口到底怎么调用

Swagger 可以根据后端 Controller 自动生成接口文档。

---

## 16. Swagger 在当前项目中怎么访问

启动项目后，访问：

```text
http://localhost:8081/swagger-ui.html
```

可以看到可视化 API 页面。

访问：

```text
http://localhost:8081/api-docs
```

可以看到 OpenAPI JSON 文档。

Swagger UI 是给人看的网页。

OpenAPI JSON 是给程序或工具读取的接口描述文件。

---

## 17. Swagger 页面里有什么

Swagger 页面通常会展示：

| 内容 | 说明 |
| --- | --- |
| API 分组 | 例如商品接口、订单接口、登录接口 |
| 请求方法 | GET、POST、PUT、DELETE |
| 请求路径 | 例如 `/api/products` |
| 请求参数 | 例如商品 ID、登录账号、密码 |
| 请求体 | POST / PUT 时提交的 JSON |
| 响应结果 | 接口返回的数据结构 |
| Try it out | 在线测试按钮 |

例如商品列表接口会显示类似：

```http
GET /api/products
```

订单创建接口可能显示：

```http
POST /api/orders
```

并告诉你请求体需要传哪些字段。

---

## 18. Swagger 和 curl 的区别

Swagger 和 curl 都可以测试 API，但使用方式不同。

| 对比项 | Swagger | curl |
| --- | --- | --- |
| 使用方式 | 浏览器网页 | 命令行 |
| 是否可视化 | 是 | 否 |
| 适合人群 | 新手、前端、测试人员 | 后端、运维、自动化脚本 |
| 是否能生成文档 | 能 | 不能 |
| 是否适合自动化 | 一般 | 很适合 |

简单理解：

```text
Swagger 更像接口说明书。
curl 更像接口测试命令。
```

---

## 19. Swagger 和 Controller 的关系

当前项目的接口主要写在 Controller 里：

```text
src/main/java/carlos/jiang/web/AuthController.java
src/main/java/carlos/jiang/web/ProductController.java
src/main/java/carlos/jiang/web/OrderController.java
```

Swagger 会扫描这些 Controller。

例如 Controller 里有：

```java
@GetMapping("/api/products")
public List<Product> listProducts() {
    return productRepository.findAll();
}
```

Swagger 就能识别出：

```http
GET /api/products
```

如果代码里写了 `@Operation` 注解，Swagger 页面还会显示更友好的接口说明。

例如：

```java
@Operation(summary = "查询商品列表")
```

这会让 Swagger 文档更容易阅读。

---

## 20. 这几个东西之间的关系

在当前商城项目中，它们各自的位置大概是：

```text
浏览器页面
  ├─ meta tag：告诉浏览器和搜索引擎页面基础信息
  └─ JavaScript：调用后端 API

命令行
  └─ curl：手动调用和测试后端 API

Spring Boot 后端
  ├─ Filter：请求进入 Controller 前先检查
  ├─ Controller：定义 API
  ├─ Swagger：根据 Controller 生成 API 文档
  ├─ Service：处理业务逻辑
  ├─ Repository：访问数据库
  └─ Kafka：发送或消费业务事件

MySQL 数据库
  └─ 保存用户、商品、订单等数据
```

完整下单流程可以理解为：

```text
用户登录
  ↓
前端保存登录状态
  ↓
用户点击提交订单
  ↓
请求先经过 Filter
  ↓
Filter 判断已登录，放行
  ↓
OrderController 接收请求
  ↓
OrderService 保存订单到 MySQL
  ↓
OrderService 发布订单创建事件
  ↓
Kafka 接收事件
  ↓
后续消费者可以处理通知、统计、库存等任务
```

---

## 21. 为什么 v3.0 要引入这些东西

第一版项目重点是：

```text
能跑起来，有商品、有订单、有数据库。
```

第二版项目重点是：

```text
加入 MySQL、用户注册、登录、收货信息。
```

第三版项目开始变得更正规：

| 技术 | 带来的提升 |
| --- | --- |
| Kafka | 让订单后的扩展逻辑更灵活 |
| curl | 方便脱离前端测试后端接口 |
| Filter | 统一处理登录校验和请求日志 |
| meta tag | 让前端 HTML 更规范 |
| Swagger | 让 API 文档自动化、可测试 |

这些不是为了让项目变复杂，而是为了让项目更接近真实开发中的结构。

---

## 22. 学习顺序建议

建议按下面顺序理解：

1. 先学 curl：知道怎么手动调用 API。
2. 再学 Swagger：知道怎么查看和测试 API 文档。
3. 再学 Filter：理解请求进入 Controller 前发生了什么。
4. 再学 meta tag：理解 HTML 页面的基础元信息。
5. 最后学 Kafka：理解异步消息和系统解耦。

其中最重要的是：

```text
API 是后端提供的入口。
curl 和 Swagger 是测试 API 的工具。
Filter 是 API 前面的检查层。
Kafka 是业务完成后继续传递事件的消息系统。
meta tag 是前端页面的基础描述信息。
```

