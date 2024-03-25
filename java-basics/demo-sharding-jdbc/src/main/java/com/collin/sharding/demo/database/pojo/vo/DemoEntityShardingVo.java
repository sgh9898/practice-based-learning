package com.collin.sharding.demo.database.pojo.vo;

import com.collin.sharding.demo.database.db.entity.DemoEntitySharding;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * [展示 VO] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Data
@ApiModel("[展示 VO] 测试数据--分表")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityShardingVo {

    /** id */
    @JsonAlias("id")
    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
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

    /** [构造] */
    public DemoEntityShardingVo() {
    }

    /** [构造] 根据实体类创建 */
    public DemoEntityShardingVo(DemoEntitySharding entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.num = entity.getNum();
        this.date = entity.getDate();
        this.comment = entity.getComment();
    }
}
