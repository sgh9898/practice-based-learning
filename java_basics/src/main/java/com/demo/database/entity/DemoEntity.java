package com.demo.database.entity;

import com.demo.database.pojo.DemoEntityDto;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.demo.database.pojo.ExcelDemoExcelVo;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "演示类")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntity extends EasyExcelTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("标签")
    private String tags;

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

    /** 根据 Excel 构建 */
    public DemoEntity(ExcelDemoExcelVo excel) {
        this.name = excel.getName();
        this.tags = excel.getTags();
        // 默认配置
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }

    /**
     * 将 Excel 类转换为 Entity 类
     *
     * @param excel 需要转换的 Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     * @return 转换后的 Entity 类, 必须 extends 本类
     */
    @Override
    public <T extends EasyExcelTemplateEntity, U extends EasyExcelTemplateExcelVo> T convertExcel(U excel) {
        return null;
    }
}

