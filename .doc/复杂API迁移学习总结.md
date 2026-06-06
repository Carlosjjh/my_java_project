# 复杂 API 迁移学习总结

本文总结本项目中新建的复杂 API，以及如何把一条 API 从当前 repo 迁移到 `E:\project\sky_take_out2\sky-server`。

## 一、本次新增的 API

新增接口：

```http
GET /api/admin/order-insights
```

作用：统计订单运营数据，支持日期范围、最低订单金额、Top 商品数量等查询参数。

示例请求：

```http
GET http://localhost:8081/api/admin/order-insights?topLimit=5&minAmount=0
```

返回内容包括：

- `orderCount`：订单数量
- `totalSales`：总销售额
- `averageOrderValue`：客单价
- `topProducts`：销售额最高的商品列表
- `recentOrders`：最近订单摘要

这个 API 比普通 CRUD 更适合练迁移，因为它包含参数接收、校验、数据库查询、业务聚合、返回 VO、Swagger 文档和测试。

## 二、一条完整 API 包括什么

在 Spring Boot 企业项目里，一条完整 API 通常不只是一个 Controller 方法，而是一条完整的数据链路：

```text
前端请求
  -> Controller
  -> DTO 参数对象
  -> Service 业务逻辑
  -> Repository / Mapper 数据访问
  -> Entity 数据模型
  -> VO / Response 返回对象
  -> 全局异常处理
  -> 认证拦截器
  -> Swagger / OpenAPI 文档
  -> 测试用例
```

类比你的算法工程经验：

- `Controller` 像推理入口，负责接收输入和返回结果。
- `DTO` 像输入数据 schema，定义前端能传什么。
- `Service` 像核心算法逻辑，负责真正的业务计算。
- `Repository/Mapper` 像数据读取模块，负责从数据库取样本。
- `VO/Response` 像输出格式，决定前端看到什么结果。
- `Test` 像验证集，保证接口行为符合预期。

## 三、当前项目新增了哪些文件

Controller：

```text
src/main/java/carlos/jiang/web/AdminOrderInsightsController.java
```

Service：

```text
src/main/java/carlos/jiang/service/OrderInsightsService.java
```

Repository 增加查询方法：

```text
src/main/java/carlos/jiang/repository/ShopOrderRepository.java
```

DTO / Response：

```text
src/main/java/carlos/jiang/web/dto/OrderInsightsRequest.java
src/main/java/carlos/jiang/web/dto/OrderInsightsResponse.java
src/main/java/carlos/jiang/web/dto/TopProductResponse.java
src/main/java/carlos/jiang/web/dto/RecentOrderSummaryResponse.java
```

认证过滤器更新：

```text
src/main/java/carlos/jiang/web/filter/LoginRequiredFilter.java
```

Swagger YAML：

```text
src/main/resources/static/openapi.yaml
```

测试：

```text
src/test/java/carlos/jiang/web/AdminOrderInsightsControllerTest.java
```

## 四、当前项目中的数据流

请求进入：

```http
GET /api/admin/order-insights?from=2026-05-01&to=2026-05-07&minAmount=100&topLimit=5
```

Controller 接收参数：

```java
public OrderInsightsResponse analyze(@Valid @ModelAttribute OrderInsightsRequest request)
```

Service 处理业务：

1. 如果 `to` 为空，默认今天。
2. 如果 `from` 为空，默认最近 7 天。
3. 如果 `from > to`，抛出参数错误。
4. 查询 `[from, to + 1 day)` 范围内的订单。
5. 根据 `minAmount` 过滤订单。
6. 聚合总销售额、客单价、Top 商品和最近订单。

Repository 查询数据库：

```java
findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(fromTime, toExclusive)
```

最后返回 JSON 给前端。

## 五、迁移到 sky-server 的核心思想

当前项目是：

```text
package: carlos.jiang
技术: Spring Boot + JPA + Session
返回: 直接返回 Response 对象
```

目标项目 `sky-server` 是：

```text
package: com.sky.sky_server
技术: Spring Boot + MyBatis + JWT
返回: Result<T>
```

所以迁移不是复制粘贴，而是做三次转换：

```text
包名转换：carlos.jiang -> com.sky.sky_server
数据访问转换：JPA Repository -> MyBatis Mapper
认证/返回格式转换：Session + Response -> JWT + Result<VO>
```

## 六、迁移时建议创建的目标文件

Controller：

```text
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\controller\OrderInsightsController.java
```

DTO：

```text
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\dto\OrderInsightsQueryDTO.java
```

VO：

```text
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\vo\OrderInsightsVO.java
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\vo\TopProductVO.java
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\vo\RecentOrderSummaryVO.java
```

Service：

```text
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\service\OrderInsightsService.java
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\service\impl\OrderInsightsServiceImpl.java
```

Mapper：

```text
E:\project\sky_take_out2\sky-server\src\main\java\com\sky\sky_server\mapper\OrderInsightsMapper.java
```

## 七、迁移后的 Controller 应该长什么样

目标项目统一使用 `/admin/**` 路径，并返回 `Result<T>`：

```java
@RestController
@RequestMapping("/admin/order-insights")
public class OrderInsightsController {

    private final OrderInsightsService orderInsightsService;

    public OrderInsightsController(OrderInsightsService orderInsightsService) {
        this.orderInsightsService = orderInsightsService;
    }

    @GetMapping
    public Result<OrderInsightsVO> analyze(OrderInsightsQueryDTO queryDTO) {
        return Result.success(orderInsightsService.analyze(queryDTO));
    }
}
```

注意：不要迁移当前项目的 `LoginRequiredFilter`。  
`sky-server` 已经有 `JwtTokenAdminInterceptor`，所有 `/admin/**` 接口默认需要登录 token。

## 八、JPA 到 MyBatis 的迁移重点

当前项目 JPA 写法：

```java
List<ShopOrder> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
        LocalDateTime from,
        LocalDateTime toExclusive);
```

迁移到 `sky-server` 后，要改成 MyBatis SQL。

例如：

```java
@Mapper
public interface OrderInsightsMapper {

    @Select("""
            select id, number, amount, user_id, order_time
            from orders
            where order_time >= #{from}
              and order_time < #{toExclusive}
              and amount >= #{minAmount}
            order by order_time desc
            """)
    List<RecentOrderSummaryVO> listRecentOrders(LocalDateTime from,
                                                LocalDateTime toExclusive,
                                                BigDecimal minAmount);
}
```

实际字段名要以 `sky_take_out` 数据库为准。  
如果目标数据库还没有订单表，就需要先建表或等订单模块完成后再迁移这条 API。

## 九、如何测试源项目是否成功

源项目已经执行过：

```bash
mvn test
```

结果：

```text
BUILD SUCCESS
Tests run: 3, Failures: 0, Errors: 0
```

真实 HTTP 测试也已通过：

1. 注册用户
2. 补充收货信息
3. 创建订单
4. 请求 `/api/admin/order-insights`
5. 成功返回聚合统计数据

源项目访问地址：

```text
http://localhost:8081
```

Swagger 地址：

```text
http://localhost:8081/swagger-ui.html
```

## 十、如何测试迁移后的 sky-server

启动目标项目：

```bash
cd E:\project\sky_take_out2\sky-server
mvn spring-boot:run
```

先登录员工接口获取 token：

```http
POST http://localhost:8088/admin/employee/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

然后带 token 访问迁移后的接口：

```http
GET http://localhost:8088/admin/order-insights?topLimit=5&minAmount=0
token: 登录接口返回的 token
```

成功返回应类似：

```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "orderCount": 2,
    "totalSales": 357.00,
    "averageOrderValue": 178.50,
    "topProducts": [],
    "recentOrders": []
  }
}
```

如果没有带 token，预期返回 `401`。  
如果参数错误，例如 `from > to`，应该返回业务错误信息。

## 十一、迁移检查清单

迁移一条 API 时，可以按这个顺序检查：

- Controller 路径是否符合目标项目规范。
- DTO 是否能正确接收 query/body 参数。
- Service 是否保留了原业务逻辑。
- Repository 是否已经改成 Mapper SQL。
- Entity/VO 字段是否和数据库字段对应。
- 返回值是否包了一层 `Result<T>`。
- 认证机制是否使用目标项目已有 JWT。
- 异常是否走目标项目 `GlobalExceptionHandler`。
- Swagger 或接口文档是否同步更新。
- 是否写了成功和失败测试。
- 是否用真实 HTTP 请求验证过。

## 十二、最重要的迁移原则

迁移 API 的本质不是搬代码，而是搬业务能力。

你真正要保留的是：

```text
接口输入是什么
业务规则是什么
查哪些数据
如何聚合
返回什么结构
错误怎么处理
怎么验证成功
```

具体技术实现要服从目标项目。  
从当前项目迁移到 `sky-server` 时，最重要的一步就是把 JPA 查询翻译成 MyBatis Mapper SQL。
