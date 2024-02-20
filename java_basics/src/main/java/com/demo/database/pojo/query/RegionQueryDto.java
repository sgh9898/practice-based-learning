package com.demo.database.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * [查询 DTO] 区号
 *
 * @author Song gh on 2024/02/07.
 */
@Data
@ApiModel("[查询 DTO] 区号")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionQueryDto {

    /** id */
    @JsonAlias("id")
    @ApiModelProperty("id")
    private Integer id;

    /** 长途区号 */
    @JsonAlias("area_code")
    @ApiModelProperty("长途区号")
    private String areaCode;

    /** 区号对应的省/直辖市/自治区行政区域编码 */
    @JsonAlias("province_code")
    @ApiModelProperty("区号对应的省/直辖市/自治区行政区域编码")
    private Integer provinceCode;

    /** 区号对应的省/直辖市/自治区全称 */
    @JsonAlias("province_name")
    @ApiModelProperty("区号对应的省/直辖市/自治区全称")
    private String provinceName;

    /** 区号对应的市级行政区域编码(多个用,隔开) */
    @JsonAlias("city_codes")
    @ApiModelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    private String cityCodes;

    /** 区号对应的行政区域编码(多个用,隔开) */
    @JsonAlias("district_codes")
    @ApiModelProperty("区号对应的行政区域编码(多个用,隔开)")
    private String districtCodes;

    /** 区号对应的行政区域全称(多个用,隔开) */
    @JsonAlias("district_name")
    @ApiModelProperty("区号对应的行政区域全称(多个用,隔开)")
    private String districtName;

    /** 区号对应的行政区域全称和描述 */
    @JsonAlias("district_name_des")
    @ApiModelProperty("区号对应的行政区域全称和描述")
    private String districtNameDes;

    /** 当前页码 */
    @ApiModelProperty("当前页码")
    private int page;

    /** 每页数据量 */
    @ApiModelProperty("每页数据量")
    private int size;
}

