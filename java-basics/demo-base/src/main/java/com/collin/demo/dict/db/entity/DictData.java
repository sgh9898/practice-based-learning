package com.collin.demo.dict.db.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * [实体类] 数据字典
 *
 * @author Song gh
 * @version 2024/03/12
 */
@Entity
@Getter
@Setter
@Table(name = "dict_data")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    private Long id;

    /** 目录中文名 */
    @JsonAlias("dict_name")
    private String dictName;

    /** 目录编码 */
    @JsonAlias("dict_code")
    private String dictCode;

    /** 词条中文名 */
    @JsonAlias("dict_data_name")
    private String dictDataName;

    /** 词条编码 */
    @JsonAlias("dict_data_code")
    private String dictDataCode;
}
