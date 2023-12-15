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
 * @author Song gh on 2023/12/15.
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
    @JsonAlias("id")
    private Integer id;

    /** 长途区号 */
    @NotBlank(message = "长途区号未填写")
    @ApiModelProperty("长途区号")
    @JsonAlias("area_code")
    private String areaCode;

    /** 区号对应的省/直辖市/自治区行政区域编码 */
    @NotNull(message = "区号对应的省/直辖市/自治区行政区域编码未填写")
    @PositiveOrZero(message = "区号对应的省/直辖市/自治区行政区域编码超出范围")
    @ApiModelProperty("区号对应的省/直辖市/自治区行政区域编码")
    @JsonAlias("province_code")
    private Integer provinceCode;

    /** 区号对应的省/直辖市/自治区全称 */
    @NotBlank(message = "区号对应的省/直辖市/自治区全称未填写")
    @ApiModelProperty("区号对应的省/直辖市/自治区全称")
    @JsonAlias("province_name")
    private String provinceName;

    /** 区号对应的市级行政区域编码(多个用,隔开) */
    @NotBlank(message = "区号对应的市级行政区域编码(多个用,隔开)未填写")
    @ApiModelProperty("区号对应的市级行政区域编码(多个用,隔开)")
    @JsonAlias("city_codes")
    private String cityCodes;

    /** 区号对应的行政区域编码(多个用,隔开) */
    @NotBlank(message = "区号对应的行政区域编码(多个用,隔开)未填写")
    @ApiModelProperty("区号对应的行政区域编码(多个用,隔开)")
    @JsonAlias("district_codes")
    private String districtCodes;

    /** 区号对应的行政区域全称(多个用,隔开) */
    @NotBlank(message = "区号对应的行政区域全称(多个用,隔开)未填写")
    @ApiModelProperty("区号对应的行政区域全称(多个用,隔开)")
    @JsonAlias("district_name")
    private String districtName;

    /** 区号对应的行政区域全称和描述 */
    @NotBlank(message = "区号对应的行政区域全称和描述未填写")
    @ApiModelProperty("区号对应的行政区域全称和描述")
    @JsonAlias("district_name_des")
    private String districtNameDes;
}

