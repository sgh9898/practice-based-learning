package com.sgh.demo.minio.service;

import com.sgh.demo.minio.db.entity.DemoMinio;
import com.sgh.demo.minio.pojo.query.DemoMinioQueryDto;
import com.sgh.demo.minio.pojo.upsert.DemoMinioUpsertDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * [功能] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
public interface DemoMinioService {

    /**
     * 新建 bucket (如果不存在)
     *
     * @param bucketName bucket 名称
     */
    void createBucketIfNotExist(String bucketName);

    /**
     * 上传文件, 将文件名替换为 UUID
     *
     * @param file 文件
     */
    DemoMinio uploadFileWithUuidName(MultipartFile file);

    /**
     * 下载文件
     *
     * @param fileId 文件id
     */
    void download(HttpServletResponse response, Long fileId);
    
    /** [删除] Minio 文件 */
    void delete(List<Long> idList);
    
    /** 查询 Minio 文件 */
    DemoMinio get(Long id);
    
    /** [列表] 查询 Minio 文件 */
    List<DemoMinio> getList();
    
    /** [分页] 查询 Minio 文件 */
    Page<DemoMinio> getPage(DemoMinioQueryDto dto);
}
