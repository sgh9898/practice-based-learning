# 通用基础模板

> * 包含开发中常用的各项功能, 可在此基础上进行定制化开发
> * 添加详细注释, 便于学习各项功能的基础应用
---

### 功能

1. [Aspect Oriented Programming](src/main/java/com/demo/aop)
    - [x] LogAspect: 利用 AOP 拦截记录日志
    - [x] Filter: 过滤器
    - [x] Interceptor: 拦截器
2. [Async](src/main/java/com/demo/async)
    - [x] 异步执行任务
3. [DataBase](src/main/java/com/demo/database)
4. [Elasticsearch](src/main/java/excluded/elasticsearch) [8.x]
5. [EasyExcel](src/main/java/com/demo/easyexcel)
    - [x] Annotation + Handler + Util: 自定义注解与导出工具类
    - [x] Listener: 导入工具
6. [Exception](src/main/java/com/demo/exception)
    - [x] Exception: 异常类
    - [x] Handler: 异常类处理
7. [Kafka](src/main/java/excluded/kafka)
    - [x] Config: 配置文件
    - [x] Listener: 消息接收
    - [x] 消息推送
8. Open Api [3.0]
    - [x] Swagger UI (Spring Doc)
9. [WebMvc](src/main/java/com/demo/webmvc)
    - [x] Config: 跨域配置
10. WebSocket
     - [x] [配置](src/main/java/com/demo/config/WebSocketConfig.java)
     - [x] [向前端推送消息](src/main/java/com/demo/config/WebSocketConfig.java)

---

### 工具类

1. [加密/解密] 工具
2. Json 相关工具
3. Http(s) 相关工具

---

### 手动启用/停用

> 以下功能需要本地启动对应服务, 为方便程序运行, 调整为需要手动启用

1. [Elasticsearch](src/main/java/excluded/elasticsearch)
2. [Kafka Listener](src/main/java/com/demo/easyexcel/listener/DemoKafkaListener.java)

---

### 参考资料

#### Kafka

- Kafka: The Definitive Guide: Real-Time Data and Stream Processing at Scale

#### Redis

- Redis in Action