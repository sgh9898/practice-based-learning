# Sharding 分库分表

<!-- Song gh on 2023/12/15 -->

> Sharding 分库分表, 附带数据库字段加密等功能
---

### 特性

1. 自动分库分表
2. 自动数据库字段加密
3. 可能会导致部分复杂 sql 查询失效, 需要避免 sql 多层嵌套

---

### 结构



---

### Maven 依赖

```
        <!-- Sharding 分库分表 -->
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-jdbc-core</artifactId>
            <version>5.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-transaction-xa-core</artifactId>
            <version>5.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-transaction-core</artifactId>
            <version>5.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.shardingsphere</groupId>
            <artifactId>shardingsphere-cluster-mode-repository-zookeeper</artifactId>
            <version>5.3.2</version>
        </dependency>
        <dependency>
            <groupId>com.atomikos</groupId>
            <artifactId>transactions-jta</artifactId>
            <version>5.0.9</version>
        </dependency>
```

### Spring 配置

```  
spring:
   datasource:
       driver-class-name: org.apache.shardingsphere.driver.ShardingSphereDriver
       url: jdbc:shardingsphere:classpath:sharding-dev.yaml
```

---

### 使用步骤

