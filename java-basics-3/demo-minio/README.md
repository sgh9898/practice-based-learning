# MinIO 文件管理

<!-- Song gh on 2024/1/3 -->

> 分布式对象存储
---

### 特性

1. 支持单机/集群配置
2. 应用简便: 自带管理页面
3. 扩展性强: 支持动态控制

---

### 结构

1. [Config](config) -- 默认配置, 一般不需要更改
2. [Service](service) -- 功能类, 一般不需要更改

---

### Maven 依赖

```
        <!-- MinIO 文件管理 -->
        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>7.1.4</version>
            <exclusions>
                <exclusion>
                    <groupId>com.squareup.okhttp3</groupId>
                    <artifactId>okhttp</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```

---

### Spring 配置

```
# MinIO 文件管理
minio:
  endpoint: http://localhost:9000                       # 服务器地址
  accessKey: lGVSfahkzSI8W0GmH3ur                       # 访问密钥, 在服务器网页管理界面配置
  secretKey: 7L6KUYuOSKToU7DlCIicGO1tUfH62xvM8wHzhlAO   # 访问密钥的密码, 在服务器网页管理界面配置
  defaultBucketName: test_bucket                               # 默认 bucket 名称
  fileUrlHost: http://172.16.30.35:9000/customized-minio/   # 手动指定的 MinIO 文件访问前缀, 用于 nginx 配置后
  
# MinIO 文件管理(可选配置, 控制接口上传文件大小)
spring:
  servlet:
    multipart:
      max-file-size: 5MB    # 上传的文件大小
      max-request-size: 5MB # 接口请求的大小, 一般与文件大小保持一致

```

### 使用步骤

1. 本地安装并启动 MinIO 服务器
2. 完成 Spring 配置, 其中 accessKey 与 secretKey 需要在管理界面生成或指定