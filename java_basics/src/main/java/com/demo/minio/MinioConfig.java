package com.demo.minio;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置
 *
 * @author Song gh on 2023/12/26.
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