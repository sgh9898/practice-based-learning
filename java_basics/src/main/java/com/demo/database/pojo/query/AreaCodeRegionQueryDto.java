package com.demo.database.pojo.query;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * [查询] 区号 dto
 *
 * @author Song gh on 2023/12/04.
 */
@Data
@ApiModel("[查询] 区号 dto")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaCodeRegionQueryDto {
    
    /** id */
    @ApiModelProperty("id")
    @JsonAlias("id")
    private Integer id;
    
    /** 长途区号 */
    @ApiModelProperty("长途区号")
    @JsonAlias("area_code")
    private String areaCode;
    
    /** 区号对应的省/直辖市/自治区行政区域编码 */
    @ApiModelProperty("区号对应的省/直辖市/自治区行政区域编码")
    @JsonAlias("province_code")
    private Integer provinceCode;
    
    /** 区号对应的省/直辖市/自治区全称 */
    @ApiModelProperty("区号对应的省/直辖市/自治区全称")
    @JsonAlias("province_name")
    private String provinceName;
    
    /** 区号对应的市级行政区域编码(多个用,隔开) */
    @ApiModelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    @JsonAlias("city_codes")
    private String cityCodes;
    
    /** 区号对应的行政区域编码(多个用,隔开) */
    @ApiModelProperty("区号对应的行政区域编码(多个用,隔开)")
    @JsonAlias("district_codes")
    private String districtCodes;
    
    /** 区号对应的行政区域全称(多个用,隔开) */
    @ApiModelProperty("区号对应的行政区域全称(多个用,隔开)")
    @JsonAlias("district_name")
    private String districtName;
    
    /** 区号对应的行政区域全称和描述 */
    @ApiModelProperty("区号对应的行政区域全称和描述")
    @JsonAlias("district_name_des")
    private String districtNameDes;

    /** 当前页码 */
    @ApiModelProperty("当前页码")
    private int page;

    /** 每页数据量 */
    @ApiModelProperty("每页数据量")
    private int size;
}

