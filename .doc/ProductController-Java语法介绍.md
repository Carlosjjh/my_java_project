# ProductController Java 语法介绍

本文档以当前项目中的 `ProductController.java` 为例，介绍这个文件里出现的 Java 基础语法和 Spring Boot 常用注解。

示例文件：

```text
src/main/java/carlos/jiang/web/ProductController.java
```

## 一、完整代码结构

`ProductController.java` 的整体结构大致是：

```java
package carlos.jiang.web;

import carlos.jiang.model.Product;
import carlos.jiang.repository.ProductRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + id));
    }
}
```

整体可以分成五部分：

```text
包声明
导入类
类上的注解
类定义
类中的字段、构造方法、普通方法
```

## 二、package：包声明

```java
package carlos.jiang.web;
```

`package` 表示当前 Java 文件属于哪个包。

在 Java 中，包用于组织代码，避免类名冲突。

这个包名和项目文件夹路径是对应的：

```text
src/main/java/carlos/jiang/web/ProductController.java
```

对应包名：

```text
carlos.jiang.web
```

可以简单理解为：

```text
package 是 Java 代码的“目录名”。
```

## 三、import：导入类

```java
import carlos.jiang.model.Product;
import carlos.jiang.repository.ProductRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
```

`import` 表示导入其他地方定义好的类。

当前文件中用到了这些类：

| 导入内容 | 作用 |
| --- | --- |
| `Product` | 商品实体类 |
| `ProductRepository` | 商品数据库访问接口 |
| `List` | Java 列表类型 |
| `GetMapping` | Spring Boot 的 GET 接口注解 |
| `PathVariable` | 从 URL 路径中取参数 |
| `RequestMapping` | 设置接口基础路径 |
| `RestController` | 声明 REST API 控制器 |

如果不写 `import`，就需要写完整类名，例如：

```java
java.util.List<Product>
```

写了 `import java.util.List;` 后，就可以直接写：

```java
List<Product>
```

## 四、注解：以 @ 开头的语法

Java 中以 `@` 开头的是注解，英文叫 annotation。

这个文件里有：

```java
@RestController
@RequestMapping("/api/products")
@GetMapping
@GetMapping("/{id}")
@PathVariable
```

注解的作用是给类、方法或参数添加额外说明。

Spring Boot 会读取这些注解，然后决定这个类和这些方法如何工作。

## 五、@RestController

```java
@RestController
```

它表示：

```text
这个类是一个 REST API 控制器。
```

也就是说，这个类中的方法可以接收浏览器或前端发来的 HTTP 请求。

当方法返回 Java 对象时，Spring Boot 会自动把 Java 对象转换成 JSON。

例如 Java 对象：

```java
Product
```

可能会返回成 JSON：

```json
{
  "id": 1,
  "name": "经典白T恤",
  "price": 79.00,
  "stock": 120
}
```

## 六、@RequestMapping

```java
@RequestMapping("/api/products")
```

它表示这个 Controller 里的 API 地址都以 `/api/products` 开头。

例如：

```java
@GetMapping
```

配合类上的：

```java
@RequestMapping("/api/products")
```

最终 API 地址就是：

```http
GET /api/products
```

再例如：

```java
@GetMapping("/{id}")
```

最终 API 地址就是：

```http
GET /api/products/{id}
```

## 七、class：定义类

```java
public class ProductController {
```

这行代码定义了一个 Java 类。

拆开看：

```text
public
```

表示这个类是公开的，其他包里的代码也可以访问它。

```text
class
```

表示声明一个类。

```text
ProductController
```

是类名。

Java 类名一般使用大驼峰命名法：

```text
ProductController
OrderController
ProductRepository
```

大驼峰的特点是每个单词首字母大写。

## 八、大括号：代码块

```java
public class ProductController {
    ...
}
```

Java 使用 `{}` 表示代码块。

类的大括号里面可以放：

```text
字段
构造方法
普通方法
内部类
```

当前文件中，类里面有：

```text
productRepository 字段
ProductController 构造方法
findAll 方法
findById 方法
```

## 九、字段：类中的变量

```java
private final ProductRepository productRepository;
```

这是一个字段，也可以叫成员变量。

拆开看：

| 部分 | 含义 |
| --- | --- |
| `private` | 私有，只能在当前类内部访问 |
| `final` | 赋值后不能再改成另一个对象 |
| `ProductRepository` | 变量类型 |
| `productRepository` | 变量名 |

这行代码的意思是：

```text
ProductController 中保存了一个 ProductRepository，用来访问商品数据。
```

## 十、private：私有访问修饰符

```java
private
```

表示只能在当前类内部访问。

比如：

```java
private final ProductRepository productRepository;
```

这个字段只能在 `ProductController` 里面使用，外部不能直接访问。

这样做的好处是：

```text
保护内部数据，不让外部代码随便修改。
```

## 十一、final：不可重新赋值

```java
final
```

表示这个变量一旦赋值，就不能再指向另一个对象。

例如：

```java
private final ProductRepository productRepository;
```

它会在构造方法里赋值：

```java
this.productRepository = productRepository;
```

赋值完成后，不能再写：

```java
this.productRepository = otherRepository;
```

这样可以让代码更稳定。

## 十二、构造方法

```java
public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
}
```

这是构造方法。

构造方法的特点：

```text
1. 方法名和类名完全一样
2. 没有返回值类型
3. 创建对象时自动执行
```

普通方法会写返回值类型，例如：

```java
public List<Product> findAll()
```

但构造方法不会写返回值类型：

```java
public ProductController(...)
```

这个构造方法的作用是：

```text
创建 ProductController 时，把 ProductRepository 传进来并保存起来。
```

在 Spring Boot 中，这叫依赖注入。

## 十三、this 关键字

```java
this.productRepository = productRepository;
```

`this` 表示当前对象。

这句代码中，左右两边名字一样，但含义不同：

```java
this.productRepository
```

表示当前类里的字段。

```java
productRepository
```

表示构造方法传进来的参数。

所以这句代码的意思是：

```text
把传进来的 productRepository 参数，赋值给当前对象的 productRepository 字段。
```

## 十四、方法定义

```java
public List<Product> findAll() {
    return productRepository.findAll();
}
```

这是一个方法。

方法声明拆开看：

| 部分 | 含义 |
| --- | --- |
| `public` | 公开方法 |
| `List<Product>` | 返回值类型 |
| `findAll` | 方法名 |
| `()` | 参数列表 |
| `{}` | 方法体 |

也就是：

```text
定义一个公开方法，方法名叫 findAll，不接收参数，返回商品列表。
```

## 十五、List<Product>：泛型

```java
List<Product>
```

`List` 表示列表。

`Product` 表示商品。

`List<Product>` 的意思是：

```text
一个列表，列表里的每一项都是 Product 类型。
```

这叫泛型。

泛型可以让 Java 知道列表中存放的具体数据类型。

例如：

```java
List<Product>
```

表示商品列表。

```java
List<OrderResponse>
```

表示订单响应列表。

## 十六、return：返回结果

```java
return productRepository.findAll();
```

`return` 表示返回方法执行结果。

这个方法声明中写了：

```java
public List<Product> findAll()
```

说明它必须返回一个：

```java
List<Product>
```

而：

```java
productRepository.findAll()
```

正好会查询并返回商品列表。

## 十七、方法调用

```java
productRepository.findAll()
```

这是方法调用。

格式是：

```text
对象.方法名()
```

这里：

```text
productRepository
```

是对象。

```text
findAll()
```

是方法。

意思是：

```text
调用 productRepository 对象的 findAll 方法。
```

## 十八、@GetMapping

```java
@GetMapping
public List<Product> findAll() {
    return productRepository.findAll();
}
```

`@GetMapping` 表示这个方法处理 HTTP GET 请求。

它对应的 API 是：

```http
GET /api/products
```

GET 请求通常用于查询数据。

这个方法的作用是：

```text
查询所有商品。
```

## 十九、带路径参数的 @GetMapping

```java
@GetMapping("/{id}")
public Product findById(@PathVariable Long id) {
    ...
}
```

`@GetMapping("/{id}")` 表示路径中带一个参数。

完整路径是：

```http
GET /api/products/{id}
```

例如：

```http
GET /api/products/1
```

这里的 `1` 就是 `id`。

## 二十、@PathVariable

```java
public Product findById(@PathVariable Long id)
```

`@PathVariable` 表示从 URL 路径中取值。

例如访问：

```http
GET /api/products/1
```

后端会把 `1` 放进：

```java
Long id
```

于是方法内部就可以使用：

```java
id
```

## 二十一、Long 类型

```java
Long id
```

`Long` 是 Java 中的整数类型之一。

它可以保存比较大的整数。

这里用它表示商品 ID。

例如：

```java
id = 1
id = 2
id = 100
```

## 二十二、链式调用

```java
return productRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("商品不存在：" + id));
```

这是链式调用。

可以理解为：

```text
先执行 productRepository.findById(id)
再对结果执行 orElseThrow(...)
```

写成更直观的含义是：

```text
根据 id 查询商品。
如果查到了，就返回商品。
如果查不到，就抛出异常。
```

## 二十三、Lambda 表达式

```java
() -> new IllegalArgumentException("商品不存在：" + id)
```

这是 Java 的 Lambda 表达式。

这里可以简单理解为：

```text
当需要抛出异常时，就执行箭头右边的代码，创建一个异常对象。
```

`->` 左边是参数。

```java
()
```

表示没有参数。

`->` 右边是要执行的代码：

```java
new IllegalArgumentException("商品不存在：" + id)
```

## 二十四、new：创建对象

```java
new IllegalArgumentException("商品不存在：" + id)
```

`new` 表示创建一个对象。

这里创建的是异常对象：

```java
IllegalArgumentException
```

它表示：

```text
传入的参数不合法。
```

在这里的含义是：

```text
前端请求的商品 ID 不存在。
```

## 二十五、字符串拼接

```java
"商品不存在：" + id
```

Java 使用 `+` 拼接字符串。

如果：

```java
id = 99
```

那么拼接结果是：

```text
商品不存在：99
```

## 二十六、分号

Java 大多数语句结尾都要写分号：

```java
package carlos.jiang.web;
import java.util.List;
private final ProductRepository productRepository;
return productRepository.findAll();
```

但是类、方法、if、for 这种代码块后面通常不写分号：

```java
public class ProductController {
}
```

```java
public List<Product> findAll() {
}
```

## 二十七、这个文件的运行含义

`ProductController.java` 最终提供了两个商品 API：

```http
GET /api/products
GET /api/products/{id}
```

它的工作流程是：

```text
浏览器或前端 app.js 发请求
  -> Spring Boot 找到 ProductController
  -> 根据请求路径找到对应方法
  -> 方法调用 ProductRepository 查询数据库
  -> 查询结果返回给前端
```

## 二十八、总结

这个文件里包含了很多常见 Java 语法：

| 语法 | 示例 | 含义 |
| --- | --- | --- |
| 包声明 | `package carlos.jiang.web;` | 当前类所属包 |
| 导入 | `import java.util.List;` | 引入其他类 |
| 注解 | `@RestController` | 给类或方法添加说明 |
| 类 | `public class ProductController` | 定义一个类 |
| 字段 | `private final ProductRepository productRepository;` | 类中的变量 |
| 构造方法 | `public ProductController(...)` | 创建对象时执行 |
| this | `this.productRepository` | 当前对象 |
| 方法 | `public List<Product> findAll()` | 定义可执行逻辑 |
| 泛型 | `List<Product>` | 指定列表元素类型 |
| 返回 | `return ...;` | 返回方法结果 |
| 路径参数 | `@PathVariable Long id` | 从 URL 中取参数 |
| Lambda | `() -> ...` | 简洁函数写法 |
| new | `new IllegalArgumentException(...)` | 创建对象 |

一句话总结：

```text
ProductController 是一个 Java 类，也是一个 Spring Boot API 控制器；它通过注解暴露商品接口，通过方法处理请求，通过 Repository 查询数据库，然后把结果返回给前端。
```
