package com.demo.minio;


import io.minio.*;
import io.minio.messages.Bucket;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MinIO 工具类
 *
 * @author Song gh on 2023/10/13.
 */
@Component
public class MinioUtil {

    /** 配置见 {@link MinioConfig} */
    @Resource
    private MinioClient minioClient;

    /** 判断 bucket 是否存在 */
    public boolean existBucket(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 如果 bucket 不存在, 则新建 bucket
     *
     * @param bucketName bucket 名称
     * @return true: 成功创建; false: 已存在
     */
    public boolean createBucketIfNotExist(String bucketName) {
        try {
            if (!existBucket(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 删除 bucket */
    public void removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** 获取全部 bucket */
    public List<Bucket> getAllBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文件上传, 将文件名替换为 UUID
     *
     * @param file       文件
     * @param bucketName 目标 bucket 名称
     * @return 新文件名
     */
    public String uploadWithUuidFileName(MultipartFile file, String bucketName) {
        // 获取文件原名
        String originalFileName = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFileName)) {
            throw new RuntimeException();
        }

        // 将文件名替换为 UUID
        int lastDotIdx = originalFileName.lastIndexOf(".");
        String newFileName;
        if (lastDotIdx >= 0) {
            newFileName = UUID.randomUUID() + originalFileName.substring(lastDotIdx);
        } else {
            newFileName = String.valueOf(UUID.randomUUID());
        }
        try {
            // 保存文件, 名称相同会覆盖
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType()).build());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return newFileName;
    }

    /**
     * 批量文件上传, 将文件名替换为 UUID
     *
     * @param files      文件
     * @param bucketName 目标 bucket 名称
     * @return 新文件名
     */
    public List<String> batchUploadWithUuidFileName(MultipartFile[] files, String bucketName) {
        List<String> newFileNames = new ArrayList<>(files.length);
        for (MultipartFile file : files) {
            newFileNames.add(uploadWithUuidFileName(file, bucketName));
        }
        return newFileNames;
    }

    /**
     * 下载文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    public void download(HttpServletResponse response, String bucketName, String fileName) {
        // 获取文件
        GetObjectResponse fileStream;
        try {
            fileStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            throw new RuntimeException("MinIO 获取文件报错: " + e.getMessage(), e);
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
            throw new RuntimeException(e);
        }
    }
}