package com.demo.db.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String name;

    @Schema(name = "标签")
    private String tags;

    @Schema(name = "1 已删除, 0 未删除")
    private Boolean isDeleted;

    @Schema(name = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @Schema(name = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @Schema(name = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date dateOnly;

    @Schema(name = "时间")
    @JsonFormat(pattern = "HH:mm", timezone = "GMT+8")
    private Date timeOnly;

    public void init() {
        this.name = "测试";
        this.tags = "标签1,标签2,标签3";
        this.isDeleted = Boolean.FALSE;
        this.createTime = new Date();
        this.updateTime = this.createTime;
    }
}

