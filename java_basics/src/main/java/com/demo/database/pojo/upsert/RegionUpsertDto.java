package com.demo.database.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * [新增/更新 DTO] 区号
 *
 * @author Song gh on 2024/02/07.
 */
@Data
@ApiModel("[新增/更新 DTO] 区号")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegionUpsertDto {

    /** List(id) */
    @ApiModelProperty("List(id)")
    private List<Integer> idList;

    /** id */
    @ApiModelProperty(value = "id", notes = "新增数据时主键应为空")
    private Integer id;

    /** 长途区号 */
    @NotBlank(message = "长途区号未填写")
    @JsonAlias("area_code")
    @ApiModelProperty("长途区号")
    private String areaCode;

    /** 区号对应的省/直辖市/自治区行政区域编码 */
    @NotNull(message = "区号对应的省/直辖市/自治区行政区域编码未填写")
    @PositiveOrZero(message = "区号对应的省/直辖市/自治区行政区域编码超出范围")
    @JsonAlias("province_code")
    @ApiModelProperty("区号对应的省/直辖市/自治区行政区域编码")
    private Integer provinceCode;

    /** 区号对应的省/直辖市/自治区全称 */
    @NotBlank(message = "区号对应的省/直辖市/自治区全称未填写")
    @JsonAlias("province_name")
    @ApiModelProperty("区号对应的省/直辖市/自治区全称")
    private String provinceName;

    /** 区号对应的市级行政区域编码(多个用,隔开) */
    @NotBlank(message = "区号对应的市级行政区域编码(多个用,隔开)未填写")
    @JsonAlias("city_codes")
    @ApiModelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    private String cityCodes;

    /** 区号对应的行政区域编码(多个用,隔开) */
    @NotBlank(message = "区号对应的行政区域编码(多个用,隔开)未填写")
    @JsonAlias("district_codes")
    @ApiModelProperty("区号对应的行政区域编码(多个用,隔开)")
    private String districtCodes;

    /** 区号对应的行政区域全称(多个用,隔开) */
    @NotBlank(message = "区号对应的行政区域全称(多个用,隔开)未填写")
    @JsonAlias("district_name")
    @ApiModelProperty("区号对应的行政区域全称(多个用,隔开)")
    private String districtName;

    /** 区号对应的行政区域全称和描述 */
    @NotBlank(message = "区号对应的行政区域全称和描述未填写")
    @JsonAlias("district_name_des")
    @ApiModelProperty("区号对应的行政区域全称和描述")
    private String districtNameDes;
}

