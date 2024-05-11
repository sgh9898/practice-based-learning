package com.sgh.demo.minio.service.impl;

import com.sgh.demo.common.util.DateUtils;
import com.sgh.demo.minio.db.entity.DemoMinio;
import com.sgh.demo.minio.db.repository.DemoMinioRepository;
import com.sgh.demo.minio.minio.MinioUtils;
import com.sgh.demo.minio.pojo.query.DemoMinioQueryDto;
import com.sgh.demo.minio.service.DemoMinioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * [功能] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Service
public class DemoMinioServiceImpl implements DemoMinioService {

    /** bucket 前缀 */
    private static final String BUCKET_PREFIX = "demo-minio-default-bucket-";

    /** bucket 时间后缀格式(如: yyyyMM), 用于自动分组 */
    private static final String BUCKET_PREFIX_DATE_PATTERN = "yyyyMM";

    /** 文件大小上限: MB, ≤ 0 则不生效 */
    private static final Integer FILE_MAX_SIZE_MB = -1;

    @Resource
    private DemoMinioRepository demoMinioRepository;

    /**
     * 新建 bucket (如果不存在)
     *
     * @param bucketName bucket 名称
     */
    @Override
    public void createBucketIfNotExist(String bucketName) {
        MinioUtils.createBucket(bucketName);
        // 允许通过 url 读取文件
        MinioUtils.allowsPublicReading(bucketName, null);
    }

    /**
     * 上传文件, 将文件名替换为 UUID (防止特殊字符及重复名)
     *
     * @param file 文件
     */
    @Override
    public DemoMinio uploadFileWithUuidName(MultipartFile file) {
        if (FILE_MAX_SIZE_MB > 0 && file.getSize() > FILE_MAX_SIZE_MB * 1024 * 1024) {
            throw new UnsupportedOperationException("上传文件过大, 最大限制: " + FILE_MAX_SIZE_MB + " MB");
        }

        // 根据时间分配 bucket (名称 = 前缀 + 年月), 不存在则创建
        String bucketName = BUCKET_PREFIX + DateUtils.toStr(new Date(), BUCKET_PREFIX_DATE_PATTERN);
        createBucketIfNotExist(bucketName);

        // 文件存入 minio
        String originalFileName = file.getOriginalFilename();
        String uuidFileName = MinioUtils.uploadFileWithUuidName(bucketName, file);

        // 获取文件 url
        String fileUrl = MinioUtils.getHostFileUrl(bucketName, uuidFileName);

        // 记录文件信息
        DemoMinio demoMinio = new DemoMinio(uuidFileName, originalFileName, fileUrl, bucketName);
        demoMinioRepository.save(demoMinio);
        return demoMinio;
    }

    /**
     * 下载文件
     *
     * @param fileId 文件id
     */
    @Override
    public void download(HttpServletResponse response, Long fileId) {
        DemoMinio demoMinio = demoMinioRepository.findFirstByIdAndIsDeletedIsFalse(fileId);
        MinioUtils.downloadFile(response, demoMinio.getBucketName(), demoMinio.getUuidFileName());
    }

    /** [删除] Minio 文件 */
    @Override
    @Transactional
    public void delete(List<Long> idList) {
        List<DemoMinio> demoMinioList = demoMinioRepository.findAllByIsDeletedIsFalseAndIdIn(idList);
        for (DemoMinio demoMinio : demoMinioList) {
            MinioUtils.deleteFile(demoMinio.getBucketName(), demoMinio.getUuidFileName());
            demoMinio.setIsDeleted(true);
        }
        demoMinioRepository.saveAll(demoMinioList);
    }
    
    /** 查询 Minio 文件 */
    @Override
    public DemoMinio get(Long id){
        return demoMinioRepository.findFirstByIdAndIsDeletedIsFalse(id);
    }
    
    /** [列表] 查询 Minio 文件 */
    @Override
    public List<DemoMinio> getList(){
        return demoMinioRepository.findAllByIsDeletedIsFalse();
    }
    
    /** [分页] 查询 Minio 文件 */
    @Override
    public Page<DemoMinio> getPage(DemoMinioQueryDto dto){
        dto.checkPageable();
        return demoMinioRepository.findAllByIsDeletedIsFalse(PageRequest.of(dto.getPage() - 1, dto.getSize()));
    }
}
