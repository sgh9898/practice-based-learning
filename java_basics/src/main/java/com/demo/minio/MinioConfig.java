package com.demo.minio;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 文件管理配置
 * <pre>
 * 需要在配置文件中配置对应属性, 示例如下:
 *
 * # MinIO 文件管理配置
 * minio:
 *   endpoint: http://localhost:9000                       # 服务器地址
 *   accessKey: lGVSfahkzSI8W0GmH3ur                       # 访问密钥, 在 minio 网页管理界面配置
 *   secretKey: 7L6KUYuOSKToU7DlCIicGO1tUfH62xvM8wHzhlAO   # 访问密钥的密码, 在 minio 网页管理界面配置
 *   bucketName: test_bucket                               # 默认 bucket 名称
 *   fileUrlHost: http://newhost:9000/new-minio/           # 手动指定的 MinIO 文件访问前缀, 用于 nginx 配置后} </pre>
 *
 * @author Song gh
 * @version 2024/2/20
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /** minio 服务器地址 */
    private String endpoint;

    /** 用户名, 在 minio 管理页面生成 */
    private String accessKey;

    /** 密码, 在 minio 管理页面生成 */
    private String secretKey;

    /** 默认 bucket 名称 */
    private String defaultBucketName;

    /** 自定义文件访问路径 host(配合 nginx 使用) */
    private String fileUrlHost;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}