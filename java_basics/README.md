# Java 常用方法

> * 包含 Java 开发中常用功能的基础应用方法
> * 包含部分模板或工具类, 可在日常开发中使用
---

### 目录

1. [Aspect Oriented Programming](src/main/java/com/demo/aop)
    - [x] 利用 AOP 拦截记录日志
    - [x] Filter
    - [x] Interceptor
2. [Async](src/main/java/com/demo/async)
3. [DataBase](src/main/java/com/demo/db)
4. [Elasticsearch](src/main/java/excluded/elasticsearch)
5. [EasyExcel](src/main/java/com/demo/excel)
    - [x] Excel [模板类](src/main/java/com/demo/excel/easyexcel/ExcelClassTemplate.java)
    - [x] [导入导出工具](src/main/java/com/demo/excel/easyexcel)
6. [Exception](src/main/java/com/demo/exception)
    - [x] 异常类
    - [x] 异常类处理
7. [Kafka](src/main/java/excluded/kafka) √
    1. 配置文件
    2. listener
    3. producer 工具类
8. [MinIO](src/main/java/com/demo/minio)
9. [Open Api](src/main/java/com/demo/swagger) (Swagger)
10. [Session](src/main/java/com/demo/session)

11. [WebMvc](src/main/java/com/demo/webmvc)
    - [x] [跨域配置](src/main/java/com/demo/webmvc/WebMvcConfig.java)
12. WebSocket
    - [x] [配置](src/main/java/com/demo/websocket/WebSocketConfig.java)
    - [x] 向前端推送消息

---

### [其他工具类](src/main/java/com/demo/util)

1. AES 加密/解密
2. Date 相关工具
3. RSA 加密/解密
4. Json 相关工具
5. Http(s) 相关工具

---

### 手动启用/停用

> 以下功能需要本地启动对应服务, 为方便程序运行, 调整为需要手动启用

1. [Elasticsearch](src/main/java/excluded/elasticsearch)
2. [Kafka Listener](src/main/java/com/demo/excel/listener/DemoKafkaListener.java)