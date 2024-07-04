package com.sgh.demo.minio.pojo.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sgh.demo.minio.db.entity.DemoMinio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * [展示 VO] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Data
@Schema(description = "[展示 VO] Minio 文件管理")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoMinioVo {

    /** Id */
    @JsonAlias("id")
    @Schema(description = "Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** uuid 文件名 */
    @JsonAlias("uuid_file_name")
    @Schema(description = "uuid 文件名")
    private String uuidFileName;

    /** 原文件名 */
    @JsonAlias("original_file_name")
    @Schema(description = "原文件名")
    private String originalFileName;

    /** 文件 url */
    @JsonAlias("file_url")
    @Schema(description = "文件 url")
    private String fileUrl;

    /** 文件所属 bucket 名称 */
    @JsonAlias("bucket_name")
    @Schema(description = "文件所属 bucket 名称")
    private String bucketName;

    /** [构造] */
    public DemoMinioVo() {
    }

    /** [构造] 根据实体类创建 */
    public DemoMinioVo(DemoMinio entity) {
        this.id = entity.getId();
        this.uuidFileName = entity.getUuidFileName();
        this.originalFileName = entity.getOriginalFileName();
        this.fileUrl = entity.getFileUrl();
        this.bucketName = entity.getBucketName();
    }
}
