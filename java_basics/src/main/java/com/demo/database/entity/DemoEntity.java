package com.demo.database.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.demo.database.pojo.DemoEntityDto;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateEntity;
import com.demo.easyexcel.util.pojo.EasyExcelTemplateExcelVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.demo.easyexcel.pojo.DemoExcelVo;
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
    @ExcelProperty("测试")
    private String name;

    @ApiModelProperty("标签")
    private String tags;

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

    /**
     * [使用默认配置时无须 override] 在默认的 BeanUtils.copyProperties 之后, 手动定义 Entity 中部分参数
     *
     * @param excel 需要转换的 Excel 类, 必须 extends {@link EasyExcelTemplateExcelVo}
     */
    @Override
    public <T extends EasyExcelTemplateExcelVo> void setParamsAfterCopy(T excel) {
        this.name = "测试修改后的名字";
        System.out.println("修改后的" + ((DemoExcelVo) excel).getTags());
    }
}

