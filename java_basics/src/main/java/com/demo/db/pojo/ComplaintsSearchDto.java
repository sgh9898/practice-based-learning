package com.demo.db.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 投诉信息查询 DTO
 *
 * @author Song gh on 2023/10/12.
 */
@Data
@ApiModel("投诉信息查询 DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplaintsSearchDto {

    /** 短信验证码 */
    @ApiModelProperty("短信验证码")
    private String smcode;

    /** 当前页码 */
    @ApiModelProperty("当前页码")
    private int page;

    /** 每页数据量 */
    @ApiModelProperty("每页数据量")
    private int size;

    /** 批次号(timestamp) */
    @ApiModelProperty("批次号(timestamp)")
    private String batchId;

    /** 投诉人号码 */
    @ApiModelProperty("投诉人号码")
    private String submitterPhone;

    /** 被投诉号码 */
    @ApiModelProperty("被投诉号码")
    private String reportedPhone;

    /** id */
    @ApiModelProperty("id")
    private String linkId;

    /** 开始时间 */
    @ApiModelProperty("开始时间, yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String startDay;

    /** 结束时间 */
    @ApiModelProperty("结束时间, yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String endDay;

    /** 数据有效性筛选 */
    @ApiModelProperty("数据有效性筛选: 0-不筛选, 1-仅通过校验的数据, 2-仅未通过校验的数据")
    private Integer validType;
}