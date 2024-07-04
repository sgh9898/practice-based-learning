package com.sgh.demo.sharding.database.pojo.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sgh.demo.sharding.database.db.entity.DictData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * [展示 VO] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Data
@Schema(description = "[展示 VO] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictDataVo {

    /** ${column.comment} */
    @JsonAlias("id")
    @JsonSerialize(using = ToStringSerializer.class)
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

    /** [构造] */
    public DictDataVo() {
    }

    /** [构造] 根据实体类创建 */
    public DictDataVo(DictData entity) {
        this.id = entity.getId();
        this.dictName = entity.getDictName();
        this.dictCode = entity.getDictCode();
        this.dictDataName = entity.getDictDataName();
        this.dictDataCode = entity.getDictDataCode();
    }
}
