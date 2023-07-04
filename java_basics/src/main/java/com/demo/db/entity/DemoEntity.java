package com.demo.db.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.demo.db.pojo.DemoEntityDto;
import com.demo.excel.easyexcel.EasyExcelClassTemplate;
import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

/**
 * 演示类
 *
 * @author Song gh on 2022/04/14.
 */
@Slf4j
@Entity
@Getter
@Setter
@Table(name = "demo_entity")
@ApiModel(description = "演示类")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntity extends EasyExcelClassTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    @ExcelProperty("测试名称")
    private String name;

    @ApiModelProperty("标签")
    private String tags;

    @ExcelProperty("时间")
    @DateTimeFormat("yyyy-MM-dd")
    @ApiModelProperty("时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dateTime;

    @JsonIgnore
    @ApiModelProperty(value = "删除", hidden = true)
    private Boolean isDeleted;

    @JsonIgnore
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;

    @JsonIgnore
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateTime;

    /** 默认构建 */
    public DemoEntity() {
        // 默认配置
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }

    /** 根据 Dto 构建 */
    public DemoEntity(DemoEntityDto dto) {
        update(dto);
        // 默认配置
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }

    /** 根据 Dto 更新 */
    public void update(DemoEntityDto dto) {
        this.name = dto.getName();
        this.tags = dto.getTags();
        // 默认配置
        this.updateTime = new Date();
    }
}

