# 通用基础模板

> * 包含开发中常用的各项功能, 可在此基础上进行定制化开发
> * 添加详细注释, 便于学习各项功能的基础应用
---

### 功能

1. Aspect Oriented Programming
    - [x] [利用 AOP 拦截记录日志](src/main/java/com/demo/aop/LogAspect.java)
    - [x] [过滤器](src/main/java/com/demo/aop/DemoFilter.java)
    - [x] [拦截器](src/main/java/com/demo/aop/DemoInterceptor.java)
2. Async
    - [x] [异步执行任务](src/main/java/com/demo/service/AsyncService.java)
3. Elasticsearch [8.x]
4. Excel
    - [x] [根据 Excel 生成数据库建表语句](src/main/java/com/demo/service/ExcelService.java)
5. Exception
    - [x] [异常类](src/main/java/com/demo/exception)
    - [x] [异常处理](src/main/java/com/demo/handler)
6. Kafka
    - [x] [配置](src/main/java/com/demo/config/KafkaConfig.java)
    - [x] [消息推送/接收](src/main/java/com/demo/service/KafkaService.java)
7. Open Api [3.0]
    - [x] Swagger UI (Spring Doc)
8. WebMvc
    - [x] [跨域](src/main/java/com/demo/config/WebMvcConfig.java)
9. WebSocket
    - [x] [配置](src/main/java/com/demo/config/WebSocketConfig.java)
    - [x] [向前端推送消息](src/main/java/com/demo/config/WebSocketConfig.java)

---

### 工具类

1. [加密/解密]工具
2. Json 相关工具
3. Http(s) 相关工具

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