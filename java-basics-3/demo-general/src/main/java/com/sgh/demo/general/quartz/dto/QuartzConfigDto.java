package com.sgh.demo.general.quartz.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * [DTO] Quartz 定时任务
 *
 * @author Song gh on 2023/12/11.
 */
@Data
@Schema(description = "[DTO] Quartz 定时任务")
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuartzConfigDto {

    /** 任务名称 */
    @Schema(description = "任务名称")
    private String jobName;

    /** 任务所属组 */
    @Schema(description = "任务所属组")
    private String groupName;

    /** 任务执行类 */
    @Schema(description = "任务执行类")
    private String jobClass;

    /** 任务调度时间表达式 */
    @Schema(description = "任务调度时间表达式")
    private String cronExpression;

    /** 附加参数 */
    @Schema(description = "附加参数")
    private Map<String, Object> param;
}