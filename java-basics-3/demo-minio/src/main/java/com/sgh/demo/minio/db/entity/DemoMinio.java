package com.sgh.demo.minio.db.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * [实体类] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Entity
@Getter
@Setter
@Table(name = "demo_minio")
@Schema(description = "[实体类] Minio 文件管理")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoMinio {

    /** Id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    @Schema(description = "Id")
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

    /** 是否删除 */
    @JsonIgnore
    @JsonAlias("is_deleted")
    @Schema(description = "是否删除", hidden = true)
    private Boolean isDeleted;

    /** 创建时间 */
    @JsonIgnore
    @JsonAlias("create_time")
    @Schema(description = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonIgnore
    @JsonAlias("update_time")
    @Schema(description = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** [构造] */
    public DemoMinio() {
        init();
    }

    /** [构造] */
    public DemoMinio(String uuidFileName, String originalFileName, String fileUrl, String bucketName) {
        init();
        this.uuidFileName = uuidFileName;
        this.originalFileName = originalFileName;
        this.fileUrl = fileUrl;
        this.bucketName = bucketName;
    }

    /** 初始化 */
    public void init() {
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }
}
