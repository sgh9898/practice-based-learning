package com.sgh.demo.sharding.database.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * [新增/更新 DTO] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Data
@ApiModel("[新增&更新 DTO] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictDataUpsertDto {

    /** List(${column.comment}) */
    @ApiModelProperty("List(${column.comment})")
    private List<Long> idList;

    /** ${column.comment} */
    @ApiModelProperty(value = "${column.comment}", notes = "新增数据时主键应为空")
    private Long id;

    /** 目录中文名 */
    @NotBlank(message = "目录中文名未填写")
    @JsonAlias("dict_name")
    @ApiModelProperty("目录中文名")
    private String dictName;

    /** 目录编码 */
    @NotBlank(message = "目录编码未填写")
    @JsonAlias("dict_code")
    @ApiModelProperty("目录编码")
    private String dictCode;

    /** 词条中文名 */
    @NotBlank(message = "词条中文名未填写")
    @JsonAlias("dict_data_name")
    @ApiModelProperty("词条中文名")
    private String dictDataName;

    /** 词条编码 */
    @NotBlank(message = "词条编码未填写")
    @JsonAlias("dict_data_code")
    @ApiModelProperty("词条编码")
    private String dictDataCode;
}
