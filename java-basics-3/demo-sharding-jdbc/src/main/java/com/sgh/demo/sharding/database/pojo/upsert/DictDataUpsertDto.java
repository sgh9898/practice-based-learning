package com.sgh.demo.sharding.database.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * [新增/更新 DTO] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Data
@Schema(description = "[新增&更新 DTO] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictDataUpsertDto {

    /** List(${column.comment}) */
    @Schema(description = "List(${column.comment})")
    private List<Long> idList;

    /** ${column.comment} */
    @Schema(description = "${column.comment}", notes = "新增数据时主键应为空")
    private Long id;

    /** 目录中文名 */
    @NotBlank(message = "目录中文名未填写")
    @JsonAlias("dict_name")
    @Schema(description = "目录中文名")
    private String dictName;

    /** 目录编码 */
    @NotBlank(message = "目录编码未填写")
    @JsonAlias("dict_code")
    @Schema(description = "目录编码")
    private String dictCode;

    /** 词条中文名 */
    @NotBlank(message = "词条中文名未填写")
    @JsonAlias("dict_data_name")
    @Schema(description = "词条中文名")
    private String dictDataName;

    /** 词条编码 */
    @NotBlank(message = "词条编码未填写")
    @JsonAlias("dict_data_code")
    @Schema(description = "词条编码")
    private String dictDataCode;
}
