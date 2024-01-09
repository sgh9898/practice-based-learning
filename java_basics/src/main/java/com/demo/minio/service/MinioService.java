package com.demo.minio.service;

import com.demo.minio.config.MinioConfig;
import io.minio.messages.Bucket;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * MinIO 文件管理功能
 * <br> bucket 命名规范: <a href="https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html">Amazon S3 命名规范</a>
 *
 * @author Song gh on 2023/12/26.
 */
public interface MinioService {

    /**
     * 判断 bucket 是否存在
     *
     * @param bucketName bucket 名称
     */
    boolean existBucket(String bucketName);

    /**
     * 如果 bucket 不存在, 则新建 bucket
     *
     * @param bucketName bucket 名称, 不传时使用默认名称 {@link MinioConfig#getDefaultBucketName()}
     * @return true: 成功创建, false: 已存在
     */
    boolean createBucketIfNotExist(String bucketName);

    /**
     * 配置 bucket 文件公开读取(允许通过 url 直接访问文件)
     *
     * @param bucketName     bucket 名称
     * @param fileNamePrefix 允许公开读取的文件名前缀, 结尾不要添加通配符(*), 为空时允许读取 bucket 全部文件
     */
    void allowsPublicReading(String bucketName, String fileNamePrefix);

    /** 删除 bucket */
    void removeBucket(String bucketName);

    /** 查询所有 bucket */
    List<Bucket> getAllBuckets();

    /**
     * 上传文件, 将文件名替换为 UUID
     *
     * @param file       文件
     * @param bucketName bucket 名称
     * @return 新文件名
     */
    String uploadFileWithUuidName(MultipartFile file, String bucketName);

    /**
     * 批量上传文件, 将文件名替换为 UUID
     *
     * @param files      文件
     * @param bucketName bucket 名称
     * @return 新文件名
     */
    List<String> batchUploadFileWithUuidName(MultipartFile[] files, String bucketName);

    /**
     * 下载文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    void downloadFile(HttpServletResponse response, String bucketName, String fileName);

    /**
     * 删除文件
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     */
    void deleteFile(String bucketName, String fileName);

    /**
     * 获取 minio 文件下载/预览地址
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     * @return minio 文件下载/预览地址
     */
    String getFileUrl(String bucketName, String fileName);

    /**
     * 获取 minio 文件下载/预览地址, 并将 url 更换为指定 host
     *
     * @param bucketName 文件所在 bucket 名称
     * @param fileName   文件名称
     * @return minio 文件下载/预览地址
     */
    String getCustomizedFileUrl(String bucketName, String fileName);
}
