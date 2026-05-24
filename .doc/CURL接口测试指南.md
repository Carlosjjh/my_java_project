# CURL 接口测试指南

本文档提供 Simple Mall v3.0 的 curl 测试示例。curl 是命令行 HTTP 客户端，用来直接调用后端 API，适合调试接口和复现问题。

启动项目：

```bash
mvn spring-boot:run
```

默认地址：

```text
http://localhost:8081
```

## 一、保存 Cookie

项目使用 session 登录。用 curl 测试时，需要用 cookie 文件保存登录状态。

本文档统一使用：

```bash
-c cookies.txt
-b cookies.txt
```

含义：

```text
-c cookies.txt  把服务端返回的 Cookie 保存到文件
-b cookies.txt  下次请求带上这个 Cookie
```

## 二、注册用户

```bash
curl -i -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d "{\"username\":\"Carlos\",\"account\":\"carlos001\",\"password\":\"123456\"}"
```

成功后会自动登录，并把 session Cookie 保存到 `cookies.txt`。

## 三、登录用户

```bash
curl -i -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d "{\"account\":\"carlos001\",\"password\":\"123456\"}"
```

## 四、查看当前登录用户

```bash
curl -i http://localhost:8081/api/auth/me \
  -b cookies.txt
```

## 五、保存收货信息

```bash
curl -i -X PUT http://localhost:8081/api/auth/profile \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d "{\"recipientName\":\"Carlos\",\"phone\":\"13800000000\",\"address\":\"Shanghai Pudong\"}"
```

如果没有带 Cookie，会返回：

```http
401 Unauthorized
```

## 六、查询商品

```bash
curl -i http://localhost:8081/api/products
```

查询单个商品：

```bash
curl -i http://localhost:8081/api/products/1
```

## 七、创建订单

创建订单需要先登录，并保存收货信息。

```bash
curl -i -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d "{\"items\":[{\"productId\":1,\"quantity\":2}]}"
```

## 八、查询当前用户订单

```bash
curl -i http://localhost:8081/api/orders \
  -b cookies.txt
```

## 九、退出登录

```bash
curl -i -X POST http://localhost:8081/api/auth/logout \
  -b cookies.txt
```

## 十、测试 Filter 是否生效

不带 Cookie 查询订单：

```bash
curl -i http://localhost:8081/api/orders
```

预期结果：

```http
HTTP/1.1 401
```

响应体：

```json
{
  "message": "请先登录"
}
```

这说明 `LoginRequiredFilter` 已经拦截了未登录请求。

## 十一、查看 Swagger

浏览器打开：

```text
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON：

```text
http://localhost:8081/api-docs
```

也可以用 curl 查看：

```bash
curl http://localhost:8081/api-docs
```

## 十二、Kafka 开关

`.env` 中默认：

```env
KAFKA_ENABLED=false
```

如果你本机已经启动 Kafka，可以改为：

```env
KAFKA_ENABLED=true
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_ORDER_CREATED_TOPIC=mall.order.created
```

然后重启项目。创建订单成功后，后端会向 Kafka topic 发送 `OrderCreatedEvent`。
