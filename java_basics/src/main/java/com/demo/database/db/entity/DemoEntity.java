package com.demo.database.db.entity;

import com.demo.database.pojo.excel.ExcelDemoEntity;
import com.demo.database.pojo.upsert.DemoEntityUpsertDto;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * [实体类] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Entity
@Getter
@Setter
@Table(name = "demo_entity")
@ApiModel("[实体类] 测试数据")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntity {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    @ApiModelProperty("id")
    private Long id;

    /** 名称 */
    @JsonAlias("name")
    @ApiModelProperty("名称")
    private String name;

    /** 数字 */
    @JsonAlias("num")
    @ApiModelProperty("数字")
    private Integer num;

    /** 时间 */
    @JsonAlias("date")
    @ApiModelProperty("时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /** 备注 */
    @JsonAlias("comment")
    @ApiModelProperty("备注")
    private String comment;

    /** 逻辑删除: 1-删除, 0-未删除 */
    @JsonIgnore
    @JsonAlias("is_deleted")
    @ApiModelProperty(value = "逻辑删除: 1-删除, 0-未删除", hidden = true)
    private Boolean isDeleted;

    /** 创建时间 */
    @JsonIgnore
    @JsonAlias("create_time")
    @ApiModelProperty(value = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonIgnore
    @JsonAlias("update_time")
    @ApiModelProperty(value = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /** [构造] */
    public DemoEntity() {
        init();
    }

    /** [构造] 根据 dto 创建 */
    public DemoEntity(DemoEntityUpsertDto dto) {
        init();
        update(dto);
    }

    /** [构造] 根据 excel 创建 */
    public DemoEntity(ExcelDemoEntity excel) {
        init();
        this.name = excel.getName();
        this.num = excel.getNum();
        this.date = excel.getDate();
        this.comment = excel.getComment();
    }

    /** 初始化 */
    public void init() {
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }

    /** 根据 dto 更新 */
    public void update(DemoEntityUpsertDto dto) {
        if (dto.getName() != null) {
            this.name = dto.getName();
        }
        if (dto.getNum() != null) {
            this.num = dto.getNum();
        }
        if (dto.getDate() != null) {
            this.date = dto.getDate();
        }
        if (dto.getComment() != null) {
            this.comment = dto.getComment();
        }
        // 默认配置
        this.updateTime = new Date();
    }
}
