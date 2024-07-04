package com.sgh.demo.sharding.database.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * [查询 DTO] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Data
@Schema(description = "[查询 DTO] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictDataQueryDto {

    /** ${column.comment} */
    @JsonAlias("id")
    private Long id;

    /** 目录中文名 */
    @JsonAlias("dict_name")
    @Schema(description = "目录中文名")
    private String dictName;

    /** 目录编码 */
    @JsonAlias("dict_code")
    @Schema(description = "目录编码")
    private String dictCode;

    /** 词条中文名 */
    @JsonAlias("dict_data_name")
    @Schema(description = "词条中文名")
    private String dictDataName;

    /** 词条编码 */
    @JsonAlias("dict_data_code")
    @Schema(description = "词条编码")
    private String dictDataCode;

    /** 当前页码, 从 1 开始 */
    @Schema(description = "当前页码")
    private int page;

    /** 每页数据量 */
    @Schema(description = "每页数据量")
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
