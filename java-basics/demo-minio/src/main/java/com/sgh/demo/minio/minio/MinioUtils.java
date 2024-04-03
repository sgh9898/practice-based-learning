package com.sgh.demo.minio.minio;

import io.minio.*;
import io.minio.messages.Bucket;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MinIO 文件管理功能
 * <pre>
 * 1. 判断 bucket 是否存在: {@link #existBucket}
 * 2. 创建 bucket : {@link #createBucket}
 * 3. 允许通过 url 直接访问 bucket 之中的文件: {@link #allowsPublicReading} </pre>
 *
 * @author Song gh
 * @version 2024/4/3
 */
@Slf4j
@Component
public class MinioUtils {

    /** bucket 文件公开读取配置(允许通过 url 访问) */
    private static final String PUBLIC_READING_TEMPLATE = "{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Principal\": {\n" +
            "                \"AWS\": [\n" +
            "                    \"*\"\n" +
            "                ]\n" +
            "            },\n" +
            "            \"Action\": [\n" +
            "                \"s3:GetObject\"\n" +
            "            ],\n" +
            "            \"Resource\": [\n" +
            "                \"arn:aws:s3:::%s/%s*\"\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    @Setter(AccessLevel.PRIVATE)
    private static MinioConfig minioConfig;

    @Setter(AccessLevel.PRIVATE)
    private static MinioClient minioClient;

    /** [构造] */
    protected MinioUtils(@Autowired MinioConfig minioConfig, @Autowired MinioClient minioClient) {
        setMinioConfig(minioConfig);
        setMinioClient(minioClient);
    }

    /** 判断 bucket 是否存在 */
    public static boolean existBucket(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("访问 bucket 失败, bucket 名称: {}; 报错原因: {}", bucketName, e.getMessage(), e);
            throw new UnsupportedOperationException("访问 bucket 失败, bucket 名称: " + bucketName);
        }
    }

    /**
     * 新建 bucket (如果不存在)
     * <pre> 如果有通过 url 访问文件的需求, 需要调用 {@link #allowsPublicReading} </pre>
     *
     * @param bucketName bucket 名称, 不传时使用默认名称 {@link MinioConfig#getDefaultBucketName()}
     * @return true: 成功创建; false: 已存在
     */
    public static boolean createBucket(String bucketName) {
        if (existBucket(bucketName)) {
            return false;
        } else {
            try {
                // 新建 bucket, 未提供名称时使用默认名称
                if (StringUtils.isBlank(bucketName)) {
                    bucketName = minioConfig.getDefaultBucketName();
                }
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } catch (Exception e) {
                log.error("创建 bucket 失败, bucket 名称: {}; 报错原因: {}", bucketName, e.getMessage(), e);
                throw new UnsupportedOperationException("创建 bucket 失败, bucket 名称: " + bucketName);
            }
            return true;
        }
    }

    /**
     * 配置 bucket 文件公开读取(允许通过 url 直接访问文件)
     *
     * @param bucketName     bucket 名称
     * @param fileNamePrefix 允许公开读取的文件名前缀, 结尾不要添加通配符(*), null 则允许读取 bucket 全部文件
     */
    public static void allowsPublicReading(String bucketName, String fileNamePrefix) {
        if (StringUtils.isBlank(fileNamePrefix)) {
            fileNamePrefix = "";
        }
        String publicReadingConfig = String.format(PUBLIC_READING_TEMPLATE, bucketName, fileNamePrefix);
        try {
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(publicReadingConfig).build());
        } catch (Exception e) {
            log.error("配置 bucket 文件公开读取失败, bucket 名称: {}; 报错原因: {}", bucketName, e.getMessage(), e);
            throw new UnsupportedOperationException("配置 bucket 文件公开读取失败, bucket 名称: " + bucketName);
        }
    }

    /** 删除 bucket */
    public static void removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("删除 bucket 失败, bucket 名称: {}; 报错原因: {}", bucketName, e.getMessage(), e);
            throw new UnsupportedOperationException("删除 bucket 失败, bucket 名称: " + bucketName);
        }
    }

    /** 查询所有 bucket */
    public static List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            log.error("查询全部 bucket 失败, 报错原因: {}", e.getMessage(), e);
            throw new UnsupportedOperationException("查询全部 bucket 失败");
        }
    }

    /**
     * 上传文件, 将文件名替换为 UUID
     *
     * @param bucketName bucket 名称, 不存在时会自动创建
     * @param file       文件
     * @return 保存后的 UUID 文件名
     */
    public static String uploadFileWithUuidName(String bucketName, MultipartFile file) {
        // 不存在则创建 bucket
        createBucket(bucketName);

        // 将文件名替换为 UUID
        String originalFileName = file.getOriginalFilename();
        String newFileName;
        if (StringUtils.isBlank(originalFileName)) {
            throw new IllegalArgumentException("文件名不得为空");
        }
        int lastDotIdx = originalFileName.lastIndexOf(".");
        if (lastDotIdx >= 0) {
            newFileName = UUID.randomUUID() + originalFileName.substring(lastDotIdx);
        } else {
            newFileName = String.valueOf(UUID.randomUUID());
        }

        // 保存文件
        InputStream inputStream = null;
        try {
            // 保存文件, 名称相同会覆盖
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            log.error("保存文件失败, bucket 名称: {}; 文件名: {}; 报错原因: {}", bucketName, originalFileName, e.getMessage(), e);
            throw new UnsupportedOperationException("保存文件失败, bucket 名称: " + bucketName + "; 文件名: " + originalFileName);
        } finally {
            closeInputStream(inputStream);
        }
        return newFileName;
    }

    /**
     * 批量上传文件, 将文件名替换为 UUID
     *
     * @param files      文件
     * @param bucketName bucket 名称
     * @return 新文件名
     */
    public static List<String> batchUploadFileWithUuidName(String bucketName, MultipartFile[] files) {
        List<String> newFileNames = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            newFileNames.add(uploadFileWithUuidName(bucketName, file));
        }
        return newFileNames;
    }

    /**
     * 下载文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    public static void downloadFile(HttpServletResponse response, String bucketName, String fileName) {
        // 获取文件
        InputStream fileStream;
        try {
            fileStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            log.error("获取文件失败, bucket 名称: {}; 文件名: {}; 报错原因: {}", bucketName, fileName, e.getMessage(), e);
            throw new UnsupportedOperationException("获取文件失败, bucket 名称: " + bucketName + "; 文件名: " + fileName);
        }

        // http response 配置
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
        response.setCharacterEncoding("utf-8");

        // 下载文件
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
            fileStream.close();
            outputStream.close();
        } catch (IOException e) {
            log.error("下载文件失败, bucket 名称: {}; 文件名: {}; 报错原因: {}", bucketName, fileName, e.getMessage(), e);
            throw new UnsupportedOperationException("下载文件失败, bucket 名称: " + bucketName + "; 文件名: " + fileName);
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    public static void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            log.error("删除文件失败, bucket 名称: {}; 文件名: {}; 报错原因: {}", bucketName, fileName, e.getMessage(), e);
            throw new UnsupportedOperationException("删除文件失败, bucket 名称: " + bucketName + "; 文件名: " + fileName);
        }
    }

    /** 获取 minio 文件下载/预览地址 */
    public static String getFileUrl(String bucketName, String fileName) {
        try {
            return minioClient.getObjectUrl(bucketName, fileName);
        } catch (Exception e) {
            log.error("获取文件路径失败, bucket 名称: {}; 文件名: {}; 报错原因: {}", bucketName, fileName, e.getMessage(), e);
            throw new UnsupportedOperationException("获取文件路径失败, bucket 名称: " + bucketName + "; 文件名: " + fileName);
        }
    }

    /**
     * 获取 minio 文件下载/预览地址, 指定 host (用于 nginx 代理后访问文件)
     *
     * @see MinioConfig#getFileUrlHost() Host 配置
     */
    public static String getHostFileUrl(String bucketName, String fileName) {
        try {
            URL originalUrl = new URL(minioClient.getObjectUrl(bucketName, fileName));
            return minioConfig.getFileUrlHost() + originalUrl.getPath();
        } catch (Exception e) {
            log.error("获取指定 host 文件路径失败, bucket 名称: {}; 文件名: {}; 报错原因: {}", bucketName, fileName, e.getMessage(), e);
            throw new UnsupportedOperationException("获取指定 host 文件路径失败, bucket 名称: " + bucketName + "; 文件名: " + fileName);
        }
    }

// ------------------------------ Private 方法 ------------------------------

    /** 关闭文件流 */
    private static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("关闭文件流失败; 报错原因: {}", e.getMessage(), e);
                throw new UnsupportedOperationException("关闭文件流失败");
            }
        }
    }
}