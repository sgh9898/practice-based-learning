package com.demo.minio;


import com.demo.exception.BaseException;
import io.minio.*;
import io.minio.messages.Bucket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
 * 2. 创建 bucket : {@link #createBucketIfNotExist}
 * 3. 允许通过 url 直接访问 bucket 之中的文件: {@link #allowsPublicReading} </pre>
 * @author Song gh on 2023/12/26.
 */
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

    @Resource
    private MinioConfig minioConfig;
    @Resource
    private MinioClient minioClient;

    /** 判断 bucket 是否存在 */
    public boolean existBucket(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new BaseException("访问 bucket 失败, 名称: " + bucketName, e);
        }
    }

    /**
     * 如果 bucket 不存在, 则新建 bucket
     *
     * @param bucketName bucket 名称, 不传时使用默认名称 {@link MinioConfig#getDefaultBucketName()}
     */
    public void createBucketIfNotExist(String bucketName) {
        if (!existBucket(bucketName)) {
            try {
                // 新建 bucket, 未提供名称时使用默认名称
                if (StringUtils.isBlank(bucketName)) {
                    bucketName = minioConfig.getDefaultBucketName();
                }
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } catch (Exception e) {
                throw new BaseException("创建 bucket 失败, 名称: " + bucketName, e);
            }
        }
    }

    /**
     * 配置 bucket 文件公开读取(允许通过 url 直接访问文件)
     *
     * @param bucketName     bucket 名称
     * @param fileNamePrefix 允许公开读取的文件名前缀, 结尾不要添加通配符(*), 为空时允许读取 bucket 全部文件
     */
    public void allowsPublicReading(String bucketName, String fileNamePrefix) {
        if (StringUtils.isBlank(fileNamePrefix)) {
            fileNamePrefix = "";
        }
        String publicReadingConfig = String.format(PUBLIC_READING_TEMPLATE, bucketName, fileNamePrefix);
        try {
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(publicReadingConfig).build());
        } catch (Exception e) {
            throw new BaseException("配置 bucket 文件公开读取失败, 名称: " + bucketName, e);
        }
    }

    /** 删除 bucket */
    public void removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new BaseException("删除 bucket 失败, 名称: " + bucketName, e);
        }
    }

    /** 查询所有 bucket */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new BaseException("查询所有 bucket 失败", e);
        }
    }

    /**
     * 上传文件, 将文件名替换为 UUID
     *
     * @param file       文件
     * @param bucketName bucket 名称, 不存在时会自动创建
     * @return 新文件名
     */
    public String uploadFileWithUuidName(MultipartFile file, String bucketName) {
        // 不存在则创建 bucket
        createBucketIfNotExist(bucketName);

        // 将文件名替换为 UUID
        String originalFileName = file.getOriginalFilename();
        String newFileName;
        if (StringUtils.isBlank(originalFileName)) {
            throw new BaseException("文件名不得为空");
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
            throw new BaseException("保存文件失败, 文件名: " + originalFileName, e);
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
    public List<String> batchUploadFileWithUuidName(MultipartFile[] files, String bucketName) {
        List<String> newFileNames = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            newFileNames.add(uploadFileWithUuidName(file, bucketName));
        }
        return newFileNames;
    }

    /**
     * 下载文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    public void downloadFile(HttpServletResponse response, String bucketName, String fileName) {
        if (!existBucket(bucketName)) {
            throw new BaseException("bucket 不存在: " + bucketName);
        }

        // 获取文件
        InputStream fileStream;
        try {
            fileStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            throw new BaseException("获取文件失败, bucket 名称: " + bucketName + "; 文件名: " + fileName, e);
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
            throw new BaseException("下载文件失败, 文件名: " + fileName, e);
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    public void deleteFile(String bucketName, String fileName) {
        if (!existBucket(bucketName)) {
            throw new BaseException("bucket 不存在: " + bucketName);
        }
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            throw new BaseException("删除文件失败, bucket 名称: " + bucketName + "; 文件名: " + fileName, e);
        }
    }

    /** 获取 minio 文件下载/预览地址 */
    public String getFileUrl(String bucketName, String fileName) {
        try {
            return minioClient.getObjectUrl(bucketName, fileName);
        } catch (Exception e) {
            throw new BaseException("获取文件路径失败, bucket 名称: " + bucketName + "; 文件名: " + fileName, e);
        }
    }

    /** 获取 minio 文件下载/预览地址, 并将 url 更换为指定 host */
    public String getCustomizedFileUrl(String bucketName, String fileName) {
        try {
            URL originalUrl = new URL(minioClient.getObjectUrl(bucketName, fileName));
            return minioConfig.getFileUrlHost() + originalUrl.getPath();
        } catch (Exception e) {
            throw new BaseException("获取文件路径失败, bucket 名称: " + bucketName + "; 文件名: " + fileName, e);
        }
    }

    /** 关闭文件流 */
    private static void closeInputStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new BaseException("文件流关闭失败", e);
            }
        }
    }
}