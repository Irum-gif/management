# 订单管理系统
## 1.项目简介
    本项目是一个简单的订单管理系统，实现了商品的增删改查排序和订单的增删查排序功能。
    项目使用了Java语言和MySQL数据库，通过JDBC连接数据库进行操作。
## 2.整体框架
### 2.1对于数据库
    构建 商品表 product，订单表 orders，订单商品表 orders_product 三张表。
**创建商品表**
```
create table product
(
-- 商品编号
product_id    int primary key AUTO_INCREMENT,
-- 商品名
product_name  varchar(20),
-- 商品价格
product_price DECIMAL(10, 2) NOT NULL,
-- 库存数量
stock  int
);
```
**创建订单商品表**
```

create table orders_product
(
-- 订单编号
order_id    int  ,
-- 商品信息
product_id  int,
-- 下单数量
quantity int,
-- 绑定主键关系
primary key(order_id,product_id),
-- 确定外键关系
foreign key (product_id) REFERENCES product(product_id),
-- 级联删除订单和订单商品
foreign key (order_id) REFERENCES orders(order_id) on delete  cascade
);
```
**创建订单表**
```
create table orders
(
    -- 订单编号
    order_id    int primary key AUTO_INCREMENT,
    -- 总价
    total_price DECIMAL(10, 2)                   NOT NULL,
    -- 下单时间
    order_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
 );
```
### 2.2使用知识点
    Java语言，JDBC，MySQL数据库，Druid数据库连接池
## 3.功能介绍
    增加/修改/删除商品、查询单个商品、商品排序
    增加/删除订单、查询订单、订单排序
    合法性判断
### 3.1商品功能!
    ![img_1.png](img_1.png)!
### 3.2订单功能
    ![img_2.png](img_2.png)
### 3.3合法性判断
    ![img_8.png](img_8.png)
## 4.总结
    本项目是一个简单的订单管理系统，实现了商品的增删改查排序和订单的增删查排序功能。
    在本项目中，我能熟练地掌握设计并实现表的构建和表与表相互之间的联系，以及能否熟练运用Java操作MySQL数据库。
## 5.问题解决
### 5.1问题一：SQL注入
    解决方法：使用PreparedStatement，将SQL语句和参数分开，防止SQL注入。
### 5.2问题二：数据库内存泄露
    解决方法：使用Druid数据库连接池，连接池可以复用连接，减少数据库连接的创建和销毁，从而减少内存泄露的风险。
### 5.3问题三：事务处理
    解决方法：使用事务处理，保证数据的一致性和完整性。
### 5.4问题四：异常处理
    解决方法：使用try-catch语句，捕获异常并处理，防止程序崩溃。
### 5.5问题五：代码优化
    解决方法：使用Java语言中的集合类，如List、Set、Map等，提高代码的可读性和可维护性。
    
