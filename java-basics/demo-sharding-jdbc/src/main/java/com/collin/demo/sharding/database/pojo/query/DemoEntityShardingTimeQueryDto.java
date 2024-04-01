package com.collin.demo.sharding.database.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * [查询 DTO] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Data
@ApiModel("[查询 DTO] 测试数据--时间分表")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityShardingTimeQueryDto {

    /** id */
    @JsonAlias("id")
    @ApiModelProperty("id")
    private Long id;

    /** 主表id */
    @JsonAlias("demo_entity_id")
    @ApiModelProperty("主表id")
    private Long demoEntityId;

    /** 名称 */
    @JsonAlias("name")
    @ApiModelProperty("名称")
    private String name;

    /** 脱敏的名称 */
    @JsonAlias("masked_name")
    @ApiModelProperty("脱敏的名称")
    private String maskedName;

    /** AES 加密的名称 */
    @JsonAlias("aes_name")
    @ApiModelProperty("AES 加密的名称")
    private String aesName;

    /** MD5 加密的名称 */
    @JsonAlias("md5name")
    @ApiModelProperty("MD5 加密的名称")
    private String md5name;

    /** 明文的名称 */
    @JsonAlias("plain_name")
    @ApiModelProperty("明文的名称")
    private String plainName;

    /** 数字 */
    @JsonAlias("num")
    @ApiModelProperty("数字")
    private Integer num;

    /** 时间 */
    @JsonAlias("date")
    @ApiModelProperty("时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /** 备注 */
    @JsonAlias("comment")
    @ApiModelProperty("备注")
    private String comment;

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
