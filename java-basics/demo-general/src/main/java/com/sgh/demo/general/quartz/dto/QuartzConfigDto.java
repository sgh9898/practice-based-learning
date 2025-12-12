package com.sgh.demo.general.quartz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * [DTO] Quartz 任务调度
 *
 * @author Song gh on 2023/12/11.
 */
@Data
@ApiModel("[DTO] Quartz 任务调度")
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuartzConfigDto {

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务所属组 */
    @ApiModelProperty("任务所属组")
    private String groupName;

    /** 任务执行类 */
    @ApiModelProperty("任务执行类")
    private String jobClass;

    /** 任务调度时间表达式 */
    @ApiModelProperty("任务调度时间表达式")
    private String cronExpression;

    /** 附加参数 */
    @ApiModelProperty("附加参数")
    private Map<String, Object> param;
}