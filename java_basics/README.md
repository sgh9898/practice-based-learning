# 通用基础模板

> * 包含开发中常用的各项功能, 可在此基础上进行定制化开发
> * 添加详细注释, 便于学习各项功能的基础应用
---

### 内容

1. Aspect Oriented Programming
    - [x] 利用 AOP 拦截记录日志
2. Elasticsearch [8.x]
3. Excel
    - [x] 根据 Excel 生成数据库建表语句
4. Exception
    - [x] Exception 异常类
    - [x] Exception Handler 异常处理
5. Kafka
    - [x] 消息推送/接收
6. Open Api [3.0]
    - [x] Swagger UI (Spring Doc)

---

### 手动启用/停用

> 以下功能需要本地启动对应服务, 为方便程序运行, 调整为需要手动启用

1. [Elasticsearch](src/main/java/com/demo/excluded/elasticsearch)
2. [Kafka Listener](src/main/java/com/demo/listener/DemoKafkaListener.java)

---

### 参考资料

#### Kafka

- Kafka: The Definitive Guide: Real-Time Data and Stream Processing at Scale

#### Redis

- Redis in Action