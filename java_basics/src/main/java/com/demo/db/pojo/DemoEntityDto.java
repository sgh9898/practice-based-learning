package com.demo.db.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;

/**
 * [新增/更新] 演示类
 * <br> 新增数据时主键应为空
 *
 * @author Song gh on 2023/02/28.
 */
@Data
@ApiModel("演示类 DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityDto {

    @NotBlank(message = "id未填写")
    @PositiveOrZero(message = "id超出范围")
    private Long id;

    @NotBlank(message = "名称未填写")
    @ApiModelProperty("名称")
    private String name;

    @NotBlank(message = "标签未填写")
    @ApiModelProperty("标签")
    private String tags;

    @JsonIgnore
    @ApiModelProperty(value = "删除", hidden = true)
    private Boolean isDeleted;

    @JsonIgnore
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;

    @JsonIgnore
    @ApiModelProperty(value = "更新时间", hidden = true)
    private Date updateTime;
}
