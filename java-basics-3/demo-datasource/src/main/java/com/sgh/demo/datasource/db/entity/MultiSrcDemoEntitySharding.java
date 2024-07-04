package com.sgh.demo.datasource.db.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * [实体类] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/05/15
 */
@Entity
@Getter
@Setter
@Table(name = "demo_entity_sharding")
@Schema(description = "[实体类] 测试数据--分表")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MultiSrcDemoEntitySharding {

    /** id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonAlias("id")
    @Schema(description = "id")
    private Long id;

    /** 名称 */
    @JsonAlias("name")
    @Schema(description = "名称")
    private String name;

    /** 脱敏的名称 */
    @JsonAlias("masked_name")
    @Schema(description = "脱敏的名称")
    private String maskedName;

    /** AES 加密的名称 */
    @JsonAlias("aes_name")
    @Schema(description = "AES 加密的名称")
    private String aesName;

    /** MD5 加密的名称 */
    @JsonAlias("md5name")
    @Schema(description = "MD5 加密的名称")
    private String md5name;

    /** 明文的名称 */
    @JsonAlias("plain_name")
    @Schema(description = "明文的名称")
    private String plainName;

    /** 数字 */
    @JsonAlias("num")
    @Schema(description = "数字")
    private Integer num;

    /** 时间 */
    @JsonAlias("date")
    @Schema(description = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /** 备注 */
    @JsonAlias("comment")
    @Schema(description = "备注")
    private String comment;

    /** 逻辑删除: 1-删除, 0-未删除 */
    @JsonIgnore
    @JsonAlias("is_deleted")
    @Schema(description = "逻辑删除: 1-删除, 0-未删除", hidden = true)
    private Boolean isDeleted;

    /** 创建时间 */
    @JsonIgnore
    @JsonAlias("create_time")
    @Schema(description = "创建时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @JsonIgnore
    @JsonAlias("update_time")
    @Schema(description = "更新时间", hidden = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
