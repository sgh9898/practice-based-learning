package com.sgh.demo.sharding.database.db.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sgh.demo.sharding.database.pojo.upsert.DictDataUpsertDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * [实体类] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Entity
@Getter
@Setter
@Table(name = "dict_data")
@Schema(description = "[实体类] 数据字典")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    /** [构造] */
    public DictData() {
        init();
    }

    /** [构造] 根据 dto 创建 */
    public DictData(DictDataUpsertDto dto) {
        init();
        update(dto);
    }

    /** 初始化 */
    public void init() {
    }

    /** 根据 dto 更新 */
    public void update(DictDataUpsertDto dto) {
        if (dto.getDictName() != null) {
            this.dictName = dto.getDictName();
        }
        if (dto.getDictCode() != null) {
            this.dictCode = dto.getDictCode();
        }
        if (dto.getDictDataName() != null) {
            this.dictDataName = dto.getDictDataName();
        }
        if (dto.getDictDataCode() != null) {
            this.dictDataCode = dto.getDictDataCode();
        }
    }
}
