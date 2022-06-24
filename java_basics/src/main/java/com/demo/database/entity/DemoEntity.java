package com.demo.database.entity;

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

    @Schema(name = "创建时间")
    private Date createTime;

    @Schema(name = "更新时间")
    private Date updateTime;
}

