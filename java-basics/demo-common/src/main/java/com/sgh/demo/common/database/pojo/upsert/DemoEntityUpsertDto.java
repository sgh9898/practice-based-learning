package com.sgh.demo.common.database.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;
import java.util.List;

/**
 * [新增/更新 DTO] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Data
@ApiModel("[新增/更新 DTO] 测试数据")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityUpsertDto {

    /** List(id) */
    @ApiModelProperty("List(id)")
    private List<Long> idList;

    /** id */
    @ApiModelProperty(value = "id", notes = "新增数据时主键应为空")
    private Long id;

    /** 名称 */
    @NotBlank(message = "名称未填写")
    @JsonAlias("name")
    @ApiModelProperty("名称")
    private String name;

    /** 数字 */
    @NotNull(message = "数字未填写")
    @PositiveOrZero(message = "数字超出范围")
    @JsonAlias("num")
    @ApiModelProperty("数字")
    private Integer num;

    /** 时间 */
    @NotNull(message = "时间未填写")
    @JsonAlias("date")
    @ApiModelProperty("时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /** 备注 */
    @NotBlank(message = "备注未填写")
    @JsonAlias("comment")
    @ApiModelProperty("备注")
    private String comment;
}
