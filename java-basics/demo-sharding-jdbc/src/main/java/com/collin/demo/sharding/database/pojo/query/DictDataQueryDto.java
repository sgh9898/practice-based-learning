package com.collin.demo.sharding.database.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * [查询 DTO] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Data
@ApiModel("[查询 DTO] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictDataQueryDto {

    /** ${column.comment} */
    @JsonAlias("id")
    private Long id;

    /** 目录中文名 */
    @JsonAlias("dict_name")
    @ApiModelProperty("目录中文名")
    private String dictName;

    /** 目录编码 */
    @JsonAlias("dict_code")
    @ApiModelProperty("目录编码")
    private String dictCode;

    /** 词条中文名 */
    @JsonAlias("dict_data_name")
    @ApiModelProperty("词条中文名")
    private String dictDataName;

    /** 词条编码 */
    @JsonAlias("dict_data_code")
    @ApiModelProperty("词条编码")
    private String dictDataCode;

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
