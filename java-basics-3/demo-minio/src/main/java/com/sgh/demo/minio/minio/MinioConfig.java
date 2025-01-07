package com.sgh.demo.minio.minio;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MinIO 文件管理配置
 * <pre>
 * 需要在配置文件中配置 minio.endpoint, minio.accessKey, minio.secretKey 等对应属性
 * </pre>
 *
 * @author Song gh
 * @version 2024/12/18
 */
@Data
@Profile("dev")
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

    /** 自定义文件访问路径 host(配合 nginx 使用, 不要以 "/" 结尾) */
    private String fileUrlHost;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}