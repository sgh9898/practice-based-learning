package com.sgh.demo.general.database.pojo.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sgh.demo.general.database.db.entity.DemoEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * [展示 VO] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Data
@Schema(description = "[展示 VO] 测试数据")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityVo {

    /** id */
    @JsonAlias("id")
    @Schema(description = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 名称 */
    @JsonAlias("name")
    @Schema(description = "名称")
    private String name;

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

    /** [构造] */
    public DemoEntityVo() {
    }

    /** [构造] 根据实体类创建 */
    public DemoEntityVo(DemoEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.num = entity.getNum();
        this.date = entity.getDate();
        this.comment = entity.getComment();
    }
}
