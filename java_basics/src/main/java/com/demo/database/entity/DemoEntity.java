package com.demo.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 演示类
 *
 * @author Song gh on 2022/04/14.
 */
@Entity
@Getter
@Setter
@Table(name = "demo_entity")
@Schema(description = "演示类")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "id")
    private Long id;

    @Schema(name = "名称")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private String name;

    @Schema(name = "1 已删除, 0 未删除")
    private Boolean isDeleted;

    @Schema(name = "创建时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(name = "更新时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Schema(name = "时间")
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    private String timeOnly;
}

