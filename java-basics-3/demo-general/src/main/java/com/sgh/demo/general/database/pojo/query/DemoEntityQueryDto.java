package com.sgh.demo.general.database.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * [查询 DTO] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Data
@Schema(description = "[查询 DTO] 测试数据")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityQueryDto {

    /** id */
    @JsonAlias("id")
    @Schema(description = "id")
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

    /** 当前页码, 从 1 开始 */
    @Schema(description = "当前页码")
    private int page;

    /** 每页数据量 */
    @Schema(description = "每页数据量")
    private int size;

    /** 校验分页参数 */
    public void checkPageable() {
        if (page < 0) {
            page = 0;
        }
        if (size <= 0) {
            size = 10;
        }
    }
}
