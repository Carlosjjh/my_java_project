# Swagger YAML 学习指南

本文档围绕当前商城项目讲清楚三件事：

1. 项目里有没有 Swagger YAML 文件。
2. 新增的 `openapi.yaml` 每一段、每一行大概在做什么。
3. 拿到 Swagger 文件后如何 call API，以及如何把 `meta tag` 和 `Filter` 串起来控制用户行为。

---

## 1. 当前项目有没有 Swagger YAML 文件

原本没有手写的 Swagger YAML 文件。

当前项目使用的是 `springdoc-openapi`，它会在项目运行时根据 Controller 自动生成 OpenAPI 文档。

运行项目后可以访问：

```text
http://localhost:8081/swagger-ui.html
```

这是 Swagger UI 页面。

也可以访问：

```text
http://localhost:8081/api-docs
```

这是自动生成的 OpenAPI JSON 文档。

这次我新增了一个静态 YAML 文件：

```text
src/main/resources/static/openapi.yaml
```

项目启动后，也可以通过下面地址访问：

```text
http://localhost:8081/openapi.yaml
```

注意：

```text
/api-docs 是 Spring Boot 根据代码动态生成的。
/openapi.yaml 是我们手写并放在 static 目录下的静态文件。
```

---

## 2. Swagger、OpenAPI、YAML 的关系

严格来说：

```text
OpenAPI 是接口描述规范。
Swagger 是围绕 OpenAPI 的工具生态。
YAML 是一种文本格式。
```

它们之间的关系可以理解为：

```text
后端 API
  ↓
OpenAPI 规范描述这些 API
  ↓
用 YAML 或 JSON 写出来
  ↓
Swagger UI 读取这份描述文件
  ↓
生成可视化接口文档和测试页面
```

所以 `openapi.yaml` 的本质是：

```text
一份 API 说明书。
```

它告诉调用者：

- 这个服务叫什么
- 服务地址是什么
- 有哪些接口
- 每个接口用 GET 还是 POST
- 请求路径是什么
- 请求参数是什么
- 请求体 JSON 长什么样
- 返回结果 JSON 长什么样
- 是否需要登录 Cookie

---

## 3. YAML 基础语法

YAML 用缩进表示层级。

例如：

```yaml
info:
  title: Simple Mall API
  version: v3.0
```

含义是：

```text
info 下面有 title 和 version 两个字段。
```

列表用 `-` 表示：

```yaml
tags:
  - name: Products
  - name: Orders
```

含义是：

```text
tags 是一个数组，数组里有两个对象。
```

引用其他定义用 `$ref`：

```yaml
$ref: '#/components/schemas/Product'
```

含义是：

```text
这里不要重复写 Product 的结构，直接引用 components.schemas.Product。
```

---

## 4. openapi.yaml 每一段的作用

下面按文件顺序解释 `src/main/resources/static/openapi.yaml`。

---

## 5. 顶层版本信息

```yaml
openapi: 3.0.3
```

表示这份文件使用 OpenAPI 3.0.3 规范。

Swagger UI 会根据这个版本理解文件结构。

---

## 6. info：API 基础信息

```yaml
info:
```

表示下面开始描述这个 API 文档的基础信息。

```yaml
  title: Simple Mall API
```

接口文档标题。

```yaml
  description: Spring Boot mall API with MySQL, session login, filters, Swagger, and optional Kafka events.
```

接口文档描述。

```yaml
  version: v3.0
```

API 版本号。

```yaml
  contact:
```

联系人信息。

```yaml
    name: Carlosjjh
```

联系人名字。

```yaml
  license:
```

许可证信息。

```yaml
    name: MIT-style demo project
```

许可证名称。

---

## 7. servers：服务地址

```yaml
servers:
```

表示 API 服务器列表。

```yaml
  - url: http://localhost:8081
```

表示本地开发时，API 的根地址是 `http://localhost:8081`。

```yaml
    description: Local development server
```

对这个服务器地址的说明。

如果你以后部署到云服务器，可以增加：

```yaml
  - url: https://api.example.com
    description: Production server
```

---

## 8. tags：接口分组

```yaml
tags:
```

表示 API 分组列表。

```yaml
  - name: Authentication
```

定义一个叫 `Authentication` 的分组。

```yaml
    description: Registration, login, logout, and shipping profile APIs
```

这个分组用于注册、登录、退出、收货信息接口。

```yaml
  - name: Products
    description: Product browsing APIs
```

商品接口分组。

```yaml
  - name: Orders
    description: Authenticated order APIs
```

订单接口分组。

Swagger UI 会按这些分组展示接口。

---

## 9. paths：所有 API 路径

```yaml
paths:
```

表示下面开始定义所有 API。

每一个路径都对应一个后端接口地址。

例如：

```yaml
  /api/products:
```

对应后端：

```java
@RequestMapping("/api/products")
```

---

## 10. 注册接口

```yaml
  /api/auth/register:
```

定义注册接口路径。

```yaml
    post:
```

表示这个接口使用 HTTP POST 方法。

对应后端：

```java
@PostMapping("/register")
```

```yaml
      tags:
        - Authentication
```

表示这个接口属于 `Authentication` 分组。

```yaml
      summary: Register a new user and start a session
```

接口摘要，Swagger UI 会展示这句话。

```yaml
      operationId: register
```

接口操作 ID。这个值通常给代码生成工具使用，要求尽量唯一。

```yaml
      requestBody:
```

表示这个接口需要请求体。

```yaml
        required: true
```

表示请求体必填。

```yaml
        content:
          application/json:
```

表示请求体格式是 JSON。

```yaml
            schema:
              $ref: '#/components/schemas/RegisterRequest'
```

表示请求体结构引用 `RegisterRequest`。

```yaml
            example:
              username: Carlos
              account: carlos001
              password: "123456"
```

给 Swagger UI 展示一个示例 JSON。

实际请求体长这样：

```json
{
  "username": "Carlos",
  "account": "carlos001",
  "password": "123456"
}
```

```yaml
      responses:
```

表示接口响应。

```yaml
        "200":
```

HTTP 200，表示成功。

```yaml
          description: Registered user information
```

成功响应说明。

```yaml
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/UserResponse'
```

表示成功后返回 `UserResponse` 结构的 JSON。

```yaml
        "400":
          description: Invalid request body
```

HTTP 400，表示请求参数错误。

---

## 11. 登录接口

```yaml
  /api/auth/login:
    post:
```

定义登录接口，使用 POST。

```yaml
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/LoginRequest'
```

表示登录需要提交 `LoginRequest`。

登录请求示例：

```json
{
  "account": "carlos001",
  "password": "123456"
}
```

```yaml
          headers:
            Set-Cookie:
```

表示登录成功后，后端可能会返回 `Set-Cookie` 响应头。

Spring Boot 的 Session 登录会通过 Cookie 保存登录状态。

浏览器收到后会保存类似：

```text
JSESSIONID=xxxxxx
```

后续请求带上这个 Cookie，后端就知道你是谁。

---

## 12. 当前用户接口

```yaml
  /api/auth/me:
    get:
```

定义获取当前登录用户的接口。

```yaml
      security:
        - sessionCookie: []
```

表示这个接口和 Session Cookie 有关。

它对应：

```yaml
components:
  securitySchemes:
    sessionCookie:
      type: apiKey
      in: cookie
      name: JSESSIONID
```

意思是：

```text
客户端需要携带名为 JSESSIONID 的 Cookie。
```

---

## 13. 收货信息接口

```yaml
  /api/auth/profile:
    put:
```

定义更新收货信息接口，使用 PUT。

```yaml
      security:
        - sessionCookie: []
```

表示需要登录 Cookie。

```yaml
      requestBody:
        required: true
```

表示必须提交请求体。

```yaml
            schema:
              $ref: '#/components/schemas/ProfileRequest'
```

请求体结构是 `ProfileRequest`。

示例：

```json
{
  "recipientName": "Zhang San",
  "phone": "13800138000",
  "address": "Beijing Chaoyang District"
}
```

---

## 14. 退出登录接口

```yaml
  /api/auth/logout:
    post:
```

定义退出登录接口，使用 POST。

```yaml
      security:
        - sessionCookie: []
```

表示需要登录状态。

```yaml
              schema:
                $ref: '#/components/schemas/MessageResponse'
```

表示返回一个消息对象。

例如：

```json
{
  "message": "logout success"
}
```

---

## 15. 商品列表接口

```yaml
  /api/products:
    get:
```

定义商品列表接口，使用 GET。

```yaml
      tags:
        - Products
```

表示属于商品分组。

```yaml
      summary: List all products
```

接口摘要：查询所有商品。

```yaml
      responses:
        "200":
          description: Product list
```

成功后返回商品列表。

```yaml
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Product'
```

表示返回值是数组，数组里的每一项都是 `Product`。

---

## 16. 商品详情接口

```yaml
  /api/products/{id}:
    get:
```

定义商品详情接口。

`{id}` 是路径参数。

例如：

```text
/api/products/1
```

```yaml
      parameters:
        - name: id
          in: path
          required: true
```

表示有一个名为 `id` 的参数，它在路径里，并且必填。

```yaml
          schema:
            type: integer
            format: int64
            example: 1
```

表示 `id` 是整数，示例值是 `1`。

---

## 17. 订单接口

```yaml
  /api/orders:
```

订单接口路径。

```yaml
    get:
```

GET 表示查询当前用户订单。

```yaml
    post:
```

POST 表示创建订单。

订单接口里都有：

```yaml
      security:
        - sessionCookie: []
```

表示需要登录 Cookie。

也有：

```yaml
      parameters:
        - name: X-Client-Meta-Tag
          in: header
          required: false
```

这表示请求可以带一个 HTTP Header：

```http
X-Client-Meta-Tag: allow-orders
```

这不是浏览器自动带的，而是我们为了演示“从 meta tag 读取值，然后交给 Filter 判断”而设计的自定义请求头。

---

## 18. components：复用定义

```yaml
components:
```

表示下面开始定义可复用内容。

常见内容有：

- securitySchemes：认证方式
- schemas：JSON 数据结构
- parameters：可复用参数
- responses：可复用响应

---

## 19. securitySchemes：登录认证方式

```yaml
  securitySchemes:
```

定义认证方式。

```yaml
    sessionCookie:
```

定义一个认证方式，名字叫 `sessionCookie`。

```yaml
      type: apiKey
```

OpenAPI 里 Cookie 认证通常可以用 `apiKey` 类型描述。

```yaml
      in: cookie
```

表示认证信息放在 Cookie 里。

```yaml
      name: JSESSIONID
```

Cookie 名称是 `JSESSIONID`。

这和 Spring Boot Session 登录机制对应。

---

## 20. schemas：请求和响应 JSON 结构

```yaml
  schemas:
```

表示下面定义各种 JSON 对象结构。

例如：

```yaml
    RegisterRequest:
```

表示注册请求体。

```yaml
      type: object
```

表示它是 JSON 对象。

```yaml
      required:
        - username
        - account
        - password
```

表示这三个字段必填。

```yaml
      properties:
```

表示下面开始定义字段。

```yaml
        username:
          type: string
          example: Carlos
```

表示 `username` 是字符串，示例是 `Carlos`。

其他 Schema 也是类似规则。

---

## 21. 如何使用这个 openapi.yaml

### 21.1 直接在浏览器查看

启动项目：

```bash
mvn spring-boot:run
```

访问：

```text
http://localhost:8081/openapi.yaml
```

浏览器会显示 YAML 文件内容。

---

### 21.2 使用 Swagger UI 查看动态文档

访问：

```text
http://localhost:8081/swagger-ui.html
```

这是 `springdoc-openapi` 根据 Controller 自动生成的文档。

注意：

```text
默认 Swagger UI 展示的是 /api-docs 动态文档，不是 /openapi.yaml 静态文件。
```

---

### 21.3 在外部 Swagger Editor 中打开

可以访问：

```text
https://editor.swagger.io/
```

然后把 `openapi.yaml` 内容粘贴进去。

Swagger Editor 会检查语法，并生成可视化文档。

---

### 21.4 根据 YAML 写 curl 命令

例如 YAML 里写：

```yaml
  /api/products:
    get:
```

再结合：

```yaml
servers:
  - url: http://localhost:8081
```

你就可以拼出完整地址：

```text
http://localhost:8081/api/products
```

调用命令：

```bash
curl http://localhost:8081/api/products
```

再比如注册接口：

```yaml
  /api/auth/register:
    post:
```

请求体引用：

```yaml
$ref: '#/components/schemas/RegisterRequest'
```

所以 curl 可以写成：

```bash
curl -X POST http://localhost:8081/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"Carlos\",\"account\":\"carlos001\",\"password\":\"123456\"}"
```

---

## 22. 从 Swagger 文件 call 一条 API 的完整过程

假设你拿到 `openapi.yaml`，现在要调用创建订单接口。

### 第一步：看服务器地址

```yaml
servers:
  - url: http://localhost:8081
```

所以基础地址是：

```text
http://localhost:8081
```

### 第二步：看路径和方法

```yaml
/api/orders:
  post:
```

所以接口是：

```http
POST http://localhost:8081/api/orders
```

### 第三步：看是否需要登录

```yaml
security:
  - sessionCookie: []
```

说明需要登录 Cookie。

所以要先调用登录接口，并保存 Cookie：

```bash
curl -c cookie.txt -X POST http://localhost:8081/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"account\":\"carlos001\",\"password\":\"123456\"}"
```

### 第四步：看请求体结构

```yaml
$ref: '#/components/schemas/CreateOrderRequest'
```

找到：

```yaml
CreateOrderRequest:
  type: object
  required:
    - items
  properties:
    items:
      type: array
```

再找到：

```yaml
CartItemRequest:
  required:
    - productId
    - quantity
```

所以请求体应该长这样：

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

### 第五步：真正调用 API

```bash
curl -b cookie.txt -X POST http://localhost:8081/api/orders ^
  -H "Content-Type: application/json" ^
  -d "{\"items\":[{\"productId\":1,\"quantity\":2}]}"
```

这就是根据 Swagger 文件调用 API 的过程。

---

## 23. meta tag 和 Filter 之间的关键点

你提出的场景是：

```text
我拿到了一个 Swagger 文件。
我根据 Swagger 文件 call API。
这个过程涉及 meta_tag 和 filter。
我要根据某个 meta_tag 去 filter 用户的某个行为。
```

这里有一个非常重要的点：

```text
HTML meta tag 默认只存在于前端 HTML 页面里。
它不会自动发送到后端。
Filter 是后端代码，它不能直接读取浏览器 HTML 里的 meta tag。
```

所以必须增加一个桥梁：

```text
前端 JavaScript 读取 meta tag
  ↓
把 meta tag 的值放进 HTTP Header
  ↓
后端 Filter 读取 HTTP Header
  ↓
Filter 决定放行或拦截请求
```

完整流程：

```text
index.html 里写 meta tag
  ↓
app.js 读取 meta tag
  ↓
fetch 调用 API 时带上 X-Client-Meta-Tag 请求头
  ↓
Swagger YAML 描述这个请求头
  ↓
后端 Filter 读取 X-Client-Meta-Tag
  ↓
如果值符合规则，允许下单
  ↓
如果值不符合规则，返回 403
```

---

## 24. 代码演示：在 HTML 中增加 meta tag

在：

```text
src/main/resources/static/index.html
```

的 `<head>` 里增加：

```html
<meta name="mall-behavior-policy" content="allow-orders">
```

含义：

```text
这个页面声明当前允许订单行为。
```

如果改成：

```html
<meta name="mall-behavior-policy" content="block-orders">
```

就表示当前页面不允许订单行为。

---

## 25. 代码演示：前端读取 meta tag 并发送 Header

在前端 JavaScript 中读取：

```js
function getBehaviorPolicy() {
  const tag = document.querySelector('meta[name="mall-behavior-policy"]');
  return tag ? tag.content : '';
}
```

调用订单 API 时带上 Header：

```js
async function createOrder(items) {
  const response = await fetch('/api/orders', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Client-Meta-Tag': getBehaviorPolicy()
    },
    credentials: 'same-origin',
    body: JSON.stringify({ items })
  });

  if (!response.ok) {
    throw new Error('Create order failed');
  }

  return response.json();
}
```

重点是：

```js
'X-Client-Meta-Tag': getBehaviorPolicy()
```

这行代码把 HTML meta tag 的值变成了 HTTP 请求头。

---

## 26. 代码演示：Swagger YAML 描述这个 Header

在 `openapi.yaml` 中，订单接口已经写了：

```yaml
parameters:
  - name: X-Client-Meta-Tag
    in: header
    required: false
    description: Demo header copied from an HTML meta tag and checked by a filter
    schema:
      type: string
      example: allow-orders
```

含义：

```text
这个 API 可以接收一个名为 X-Client-Meta-Tag 的请求头。
这个请求头来自前端 HTML meta tag。
后端 Filter 可以读取它。
```

这样调用 API 的人看到 Swagger 文件后，就知道这个接口支持这个 Header。

---

## 27. 代码演示：后端 Filter 根据 Header 拦截行为

可以新增一个 Filter：

```java
package carlos.jiang.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(3)
public class MetaTagBehaviorFilter extends OncePerRequestFilter {
    private static final String META_TAG_HEADER = "X-Client-Meta-Tag";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!isOrderWriteRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String behaviorPolicy = request.getHeader(META_TAG_HEADER);
        if (!"allow-orders".equals(behaviorPolicy)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"message\":\"当前页面策略不允许提交订单\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOrderWriteRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
                && "/api/orders".equals(request.getRequestURI());
    }
}
```

这个 Filter 的逻辑是：

```text
如果不是 POST /api/orders，直接放行。
如果是 POST /api/orders，就读取 X-Client-Meta-Tag。
如果值是 allow-orders，放行。
否则返回 403。
```

---

## 28. 代码演示：用 curl 模拟 meta tag

curl 没有 HTML 页面，所以它没有真正的 meta tag。

但是可以直接模拟前端发送的 Header：

```bash
curl -b cookie.txt -X POST http://localhost:8081/api/orders ^
  -H "Content-Type: application/json" ^
  -H "X-Client-Meta-Tag: allow-orders" ^
  -d "{\"items\":[{\"productId\":1,\"quantity\":2}]}"
```

如果 Header 是：

```http
X-Client-Meta-Tag: allow-orders
```

Filter 放行。

如果 Header 是：

```http
X-Client-Meta-Tag: block-orders
```

Filter 返回：

```http
403 Forbidden
```

响应体类似：

```json
{
  "message": "当前页面策略不允许提交订单"
}
```

---

## 29. 这个设计是否安全

只靠 meta tag 控制用户行为，不安全。

原因是：

```text
meta tag 在前端页面里，用户可以通过浏览器开发者工具修改。
HTTP Header 也可以被 curl 或 Postman 伪造。
```

所以它适合做：

- 前端页面策略标记
- A/B 测试标记
- 客户端来源标记
- 简单行为分流
- 非核心安全逻辑

不适合做：

- 管理员权限判断
- 支付权限判断
- 订单归属判断
- 用户身份认证
- 核心风控

真正安全的判断应该依赖：

- 后端 Session
- 数据库里的用户权限
- 服务端配置
- JWT 或 Spring Security
- 不可由前端随意伪造的数据

---

## 30. 推荐的真实项目做法

如果你想让这个流程更正规，可以这样设计：

```text
meta tag 只负责告诉前端当前页面模式。
前端把页面模式作为 Header 发给后端。
Filter 可以记录这个 Header 或做非安全分流。
真正的权限判断仍然放在后端用户权限系统里。
```

例如：

```text
普通用户是否能下单：由后端根据登录用户判断。
页面是否处于活动页：可以由 meta tag + header 辅助判断。
```

---

## 31. 一句话总结

```text
Swagger YAML 是 API 说明书。
你根据它知道如何 call API。
meta tag 是前端页面信息。
Filter 是后端请求检查器。
meta tag 不能直接被 Filter 读取，必须由前端 JS 读取后放进 HTTP Header。
Swagger YAML 负责把这个 Header 也写进接口说明里。
```

