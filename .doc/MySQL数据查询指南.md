# MySQL 数据查询指南

本文档说明如何在 MySQL 中查看当前商城项目的用户信息、收货信息、订单信息、订单明细和商品库存。

当前项目 v2.0 使用 MySQL 作为主数据库。默认数据库名是：

```text
simple_mall
```

## 一、连接 MySQL

如果你使用 MySQL 命令行，可以执行：

```bash
mysql -u root -p
```

输入密码后，选择项目数据库：

```sql
USE simple_mall;
```

如果你的 `.env` 中配置了其他数据库名，请以 `.env` 中的 `DB_URL` 为准。

当前 `.env` 示例：

```env
DB_URL=jdbc:mysql://localhost:3306/simple_mall?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8
DB_USERNAME=root
DB_PASSWORD=
```

## 二、查看当前有哪些表

```sql
SHOW TABLES;
```

当前项目常用表：

```text
app_user
product
shop_order
order_item
```

含义：

| 表名 | 作用 |
| --- | --- |
| `app_user` | 用户信息、账号、密码哈希、收货信息 |
| `product` | 商品信息、价格、库存、图片地址 |
| `shop_order` | 订单主表 |
| `order_item` | 订单明细表 |

## 三、查看表结构

查看用户表结构：

```sql
DESC app_user;
```

查看订单表结构：

```sql
DESC shop_order;
```

查看订单明细表结构：

```sql
DESC order_item;
```

查看商品表结构：

```sql
DESC product;
```

## 四、查询用户信息

查询所有用户：

```sql
SELECT
  id,
  username,
  account,
  recipient_name,
  phone,
  address,
  created_at
FROM app_user
ORDER BY id DESC;
```

说明：

| 字段 | 含义 |
| --- | --- |
| `id` | 用户 ID |
| `username` | 用户名 |
| `account` | 登录账号 |
| `recipient_name` | 收货人 |
| `phone` | 手机号 |
| `address` | 收货地址 |
| `created_at` | 注册时间 |

## 五、查询用户密码字段

用户密码字段是：

```text
password_hash
```

查询示例：

```sql
SELECT
  id,
  account,
  password_hash
FROM app_user;
```

注意：

```text
password_hash 不是明文密码。
```

项目使用 BCrypt 保存密码哈希，所以数据库中看到的内容通常类似：

```text
$2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

这表示密码已经被加密哈希处理。你不能通过这个字段直接还原用户原始密码，这是正常且安全的做法。

## 六、按账号查询某个用户

比如查询账号为 `zhangsan` 的用户：

```sql
SELECT
  id,
  username,
  account,
  recipient_name,
  phone,
  address,
  created_at
FROM app_user
WHERE account = 'zhangsan';
```

## 七、查询商品信息

查询所有商品：

```sql
SELECT
  id,
  name,
  description,
  price,
  stock,
  image_url
FROM product
ORDER BY id;
```

只看商品库存：

```sql
SELECT
  id,
  name,
  stock
FROM product
ORDER BY id;
```

## 八、查询所有订单

查询订单主表：

```sql
SELECT
  id,
  user_id,
  customer_name,
  phone,
  address,
  total_amount,
  created_at
FROM shop_order
ORDER BY created_at DESC;
```

说明：

| 字段 | 含义 |
| --- | --- |
| `id` | 订单 ID |
| `user_id` | 下单用户 ID |
| `customer_name` | 下单时使用的收货人 |
| `phone` | 下单时使用的手机号 |
| `address` | 下单时使用的收货地址 |
| `total_amount` | 订单总金额 |
| `created_at` | 下单时间 |

注意：

```text
订单里保存的是下单时的收货信息快照。
```

也就是说，用户后来修改了收货地址，历史订单中的地址不会跟着变化。

## 九、查询订单明细

查询所有订单明细：

```sql
SELECT
  id,
  order_id,
  product_id,
  product_name,
  unit_price,
  quantity,
  line_total
FROM order_item
ORDER BY order_id DESC, id;
```

说明：

| 字段 | 含义 |
| --- | --- |
| `id` | 订单明细 ID |
| `order_id` | 所属订单 ID |
| `product_id` | 商品 ID |
| `product_name` | 下单时的商品名称 |
| `unit_price` | 下单时的商品单价 |
| `quantity` | 购买数量 |
| `line_total` | 当前明细小计 |

## 十、联合查询订单、用户和订单明细

这是最常用的订单查看 SQL：

```sql
SELECT
  o.id AS order_id,
  u.account,
  u.username,
  o.customer_name,
  o.phone,
  o.address,
  o.total_amount,
  o.created_at,
  i.product_id,
  i.product_name,
  i.unit_price,
  i.quantity,
  i.line_total
FROM shop_order o
JOIN app_user u ON u.id = o.user_id
JOIN order_item i ON i.order_id = o.id
ORDER BY o.created_at DESC, i.id;
```

这个查询可以看到：

```text
哪个用户下了哪个订单
订单用了什么收货信息
订单里买了哪些商品
每个商品买了几件
订单总金额和明细金额
```

## 十一、查询某个用户的订单

按账号查询，例如查询 `zhangsan` 的所有订单：

```sql
SELECT
  o.id AS order_id,
  u.account,
  u.username,
  o.customer_name,
  o.phone,
  o.address,
  o.total_amount,
  o.created_at
FROM shop_order o
JOIN app_user u ON u.id = o.user_id
WHERE u.account = '123456'
ORDER BY o.created_at DESC;
```

查询某个用户订单及明细：

```sql
SELECT
  o.id AS order_id,
  u.account,
  u.username,
  i.product_name,
  i.unit_price,
  i.quantity,
  i.line_total,
  o.total_amount,
  o.created_at
FROM shop_order o
JOIN app_user u ON u.id = o.user_id
JOIN order_item i ON i.order_id = o.id
WHERE u.account = '123456'
ORDER BY o.created_at DESC, i.id;
```

## 十二、查询某个订单详情

比如查看订单 ID 为 `1` 的订单：

```sql
SELECT
  o.id AS order_id,
  u.account,
  u.username,
  o.customer_name,
  o.phone,
  o.address,
  o.total_amount,
  o.created_at,
  i.product_name,
  i.unit_price,
  i.quantity,
  i.line_total
FROM shop_order o
JOIN app_user u ON u.id = o.user_id
JOIN order_item i ON i.order_id = o.id
WHERE o.id = 1;
```

## 十三、统计每个用户的订单数量和消费金额

```sql
SELECT
  u.id AS user_id,
  u.account,
  u.username,
  COUNT(o.id) AS order_count,
  COALESCE(SUM(o.total_amount), 0) AS total_spent
FROM app_user u
LEFT JOIN shop_order o ON o.user_id = u.id
GROUP BY u.id, u.account, u.username
ORDER BY total_spent DESC;
```

## 十四、统计商品销量

```sql
SELECT
  product_id,
  product_name,
  SUM(quantity) AS total_quantity,
  SUM(line_total) AS total_amount
FROM order_item
GROUP BY product_id, product_name
ORDER BY total_quantity DESC;
```

## 十五、常见问题

### 1. 查不到用户或订单

先确认项目连接的是不是同一个数据库：

```sql
SELECT DATABASE();
```

再确认 `.env` 中的数据库名：

```env
DB_URL=jdbc:mysql://localhost:3306/simple_mall...
```

### 2. 表不存在

先启动一次项目：

```bash
mvn spring-boot:run
```

项目启动后，JPA 会根据实体类自动创建或更新表结构。

### 3. 商品表为空

项目启动时会执行：

```text
src/main/java/carlos/jiang/DataInitializer.java
```

如果 `product` 表为空，它会自动插入 6 个示例商品。

### 4. 为什么看不到明文密码

这是正常的。项目不会保存明文密码，只保存 BCrypt 哈希：

```text
app_user.password_hash
```

登录时，后端会用 BCrypt 校验用户输入的密码是否匹配数据库中的哈希。

## 十六、建议的查看顺序

如果你刚刚注册、保存收货信息、提交订单，可以按这个顺序查看：

```sql
SELECT * FROM app_user;
SELECT * FROM shop_order;
SELECT * FROM order_item;
SELECT * FROM product;
```

然后使用联合查询查看完整订单：

```sql
SELECT
  o.id AS order_id,
  u.account,
  u.username,
  o.customer_name,
  o.phone,
  o.address,
  o.total_amount,
  o.created_at,
  i.product_name,
  i.unit_price,
  i.quantity,
  i.line_total
FROM shop_order o
JOIN app_user u ON u.id = o.user_id
JOIN order_item i ON i.order_id = o.id
ORDER BY o.created_at DESC, i.id;
```
