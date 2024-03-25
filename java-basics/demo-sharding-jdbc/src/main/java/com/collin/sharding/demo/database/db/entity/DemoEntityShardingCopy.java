package com.collin.sharding.demo.database.db.entity;

import com.collin.sharding.demo.database.pojo.upsert.DemoEntityShardingCopyUpsertDto;
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
 * [实体类] 测试数据--分表--copy
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Entity
@Getter
@Setter
@Table(name = "demo_entity_sharding_copy")
@ApiModel("[实体类] 测试数据--分表--copy")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityShardingCopy {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    @ApiModelProperty("id")
    private Long id;

    /** 主表id */
    @JsonAlias("demo_entity_id")
    @ApiModelProperty("主表id")
    @Column(updatable = false)  // 用于分片的字段不允许被更新
    private Long demoEntityId;

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
    public DemoEntityShardingCopy() {
        init();
    }

    /** [构造] 根据 dto 创建 */
    public DemoEntityShardingCopy(DemoEntityShardingCopyUpsertDto dto) {
        init();
        update(dto);
    }

    /** 初始化 */
    public void init() {
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }

    /** 根据 dto 更新 */
    public void update(DemoEntityShardingCopyUpsertDto dto) {
        if (dto.getDemoEntityId() != null) {
            this.demoEntityId = dto.getDemoEntityId();
        }
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
