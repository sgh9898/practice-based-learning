package com.sgh.demo.minio.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * [新增/更新 DTO] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Data
@Schema(description = "[新增&更新 DTO] Minio 文件管理")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoMinioUpsertDto {

    /** List(Id) */
    @Schema(description = "List(Id)")
    private List<Long> idList;

    /** Id */
    @Schema(description = "Id")
    private Long id;

    /** uuid 文件名 */
    @NotBlank(message = "uuid 文件名未填写")
    @JsonAlias("uuid_file_name")
    @Schema(description = "uuid 文件名")
    private String uuidFileName;

    /** 原文件名 */
    @NotBlank(message = "原文件名未填写")
    @JsonAlias("original_file_name")
    @Schema(description = "原文件名")
    private String originalFileName;

    /** 文件 url */
    @NotBlank(message = "文件 url未填写")
    @JsonAlias("file_url")
    @Schema(description = "文件 url")
    private String fileUrl;

    /** 文件所属 bucket 名称 */
    @NotBlank(message = "文件所属 bucket 名称未填写")
    @JsonAlias("bucket_name")
    @Schema(description = "文件所属 bucket 名称")
    private String bucketName;
}
