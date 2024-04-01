package com.collin.demo.sharding.database.pojo.vo;

import com.collin.demo.sharding.database.db.entity.DictData;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * [展示 VO] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Data
@ApiModel("[展示 VO] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictDataVo {

    /** ${column.comment} */
    @JsonAlias("id")
    @JsonSerialize(using = ToStringSerializer.class)
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
