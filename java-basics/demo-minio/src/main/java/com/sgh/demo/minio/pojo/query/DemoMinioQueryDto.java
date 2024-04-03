package com.sgh.demo.minio.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * [查询 DTO] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Data
@ApiModel("[查询 DTO] Minio 文件管理")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoMinioQueryDto {

    /** Id */
    @JsonAlias("id")
    @ApiModelProperty("Id")
    private Long id;

    /** 文件名 */
    @JsonAlias("file_name")
    @ApiModelProperty("文件名")
    private String fileName;

    /** 文件所属 bucket 名称 */
    @JsonAlias("bucket_name")
    @ApiModelProperty("文件所属 bucket 名称")
    private String bucketName;

    /** 当前页码, 从 1 开始 */
    @ApiModelProperty("当前页码")
    private int page;

    /** 每页数据量 */
    @ApiModelProperty("每页数据量")
    private int size;
    
    /** 校验分页参数 */
    public void checkPageable() {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
    }
}
