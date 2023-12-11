# Quartz 定时任务

<!-- Song gh on 2023/12/11 -->

> Quartz 定时任务模板, 多用于 schedule 无法满足项目需求时
---

### 特性

1. 支持 Mysql 持久化存储
2. 支持集群配置: 支持多实例
3. 扩展性强: 支持动态控制

---

### 结构

1. [Config](config) -- 默认配置, 一般不需要更改
2. [Schedule](schedule) -- 实际执行的定时任务
    * [QuartzInit](schedule/QuartzInit.java) -- 定时任务自动初始化, 一般需要将定时任务在此激活
3. [Service](service) -- 功能类, 一般不需要更改
4. [Sql](sql) -- Mysql 建表语句, 需要在数据库手动执行
5. [Properties](quartz.properties) -- 自定义配置, 需要放在项目 Resource 目录下

---

### Maven 依赖

```
        <!-- Quartz 定时任务 -->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.3.2</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
        
        <!--Druid 数据连接池-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.2.16</version>
        </dependency>
```

---

### 使用步骤

1. 配置 [Properties](quartz.properties), 置于项目 Resource 目录下
2. 执行 [Sql](sql)
3. 完成定时任务, 置于 [Schedule](schedule) 目录下
4. 在 [QuartzInit](schedule/QuartzInit.java) 中激活指定的定时任务