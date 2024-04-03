package com.sgh.demo.minio.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * [新增/更新 DTO] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Data
@ApiModel("[新增&更新 DTO] Minio 文件管理")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoMinioUpsertDto {

    /** List(Id) */
    @ApiModelProperty("List(Id)")
    private List<Long> idList;

    /** Id */
    @ApiModelProperty(value = "Id", notes = "新增数据时主键应为空")
    private Long id;

    /** uuid 文件名 */
    @NotBlank(message = "uuid 文件名未填写")
    @JsonAlias("uuid_file_name")
    @ApiModelProperty("uuid 文件名")
    private String uuidFileName;

    /** 原文件名 */
    @NotBlank(message = "原文件名未填写")
    @JsonAlias("original_file_name")
    @ApiModelProperty("原文件名")
    private String originalFileName;

    /** 文件 url */
    @NotBlank(message = "文件 url未填写")
    @JsonAlias("file_url")
    @ApiModelProperty("文件 url")
    private String fileUrl;

    /** 文件所属 bucket 名称 */
    @NotBlank(message = "文件所属 bucket 名称未填写")
    @JsonAlias("bucket_name")
    @ApiModelProperty("文件所属 bucket 名称")
    private String bucketName;
}
