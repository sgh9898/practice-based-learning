package com.collin.demo.sharding.database.db.entity;

import com.collin.demo.sharding.database.pojo.upsert.DemoEntityShardingTimeUpsertDto;
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
 * [实体类] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Entity
@Getter
@Setter
@Table(name = "demo_entity_sharding_time")
@ApiModel("[实体类] 测试数据--时间分表")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityShardingTime {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    @ApiModelProperty("id")
    private Long id;

    /** 主表id */
    @JsonAlias("demo_entity_id")
    @ApiModelProperty("主表id")
    private Long demoEntityId;

    /** 名称 */
    @JsonAlias("name")
    @ApiModelProperty("名称")
    private String name;

    /** 脱敏的名称 */
    @JsonAlias("masked_name")
    @ApiModelProperty("脱敏的名称")
    private String maskedName;

    /** AES 加密的名称 */
    @JsonAlias("aes_name")
    @ApiModelProperty("AES 加密的名称")
    private String aesName;

    /** MD5 加密的名称 */
    @JsonAlias("md5name")
    @ApiModelProperty("MD5 加密的名称")
    private String md5name;

    /** 明文的名称 */
    @JsonAlias("plain_name")
    @ApiModelProperty("明文的名称")
    private String plainName;

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
    public DemoEntityShardingTime() {
        init();
    }

    /** [构造] 根据 dto 创建 */
    public DemoEntityShardingTime(DemoEntityShardingTimeUpsertDto dto) {
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
    public void update(DemoEntityShardingTimeUpsertDto dto) {
        if (dto.getDemoEntityId() != null) {
            this.demoEntityId = dto.getDemoEntityId();
        }
        if (dto.getName() != null) {
            this.name = dto.getName();
        }
        if (dto.getMaskedName() != null) {
            this.maskedName = dto.getMaskedName();
        }
        if (dto.getAesName() != null) {
            this.aesName = dto.getAesName();
        }
        if (dto.getMd5name() != null) {
            this.md5name = dto.getMd5name();
        }
        if (dto.getPlainName() != null) {
            this.plainName = dto.getPlainName();
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
