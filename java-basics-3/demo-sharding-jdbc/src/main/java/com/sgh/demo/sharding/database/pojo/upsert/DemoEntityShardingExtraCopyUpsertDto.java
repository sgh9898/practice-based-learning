package com.sgh.demo.sharding.database.pojo.upsert;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * [新增/更新 DTO] 测试数据--分库分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Data
@Schema(description = "[新增&更新 DTO] 测试数据--分库分表")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityShardingExtraCopyUpsertDto {

    /** List(id) */
    @Schema(description = "List(id)")
    private List<Long> idList;

    /** id */
    @Schema(description = "id", notes = "新增数据时主键应为空")
    private Long id;

    /** 主表id */
    @NotNull(message = "主表id未填写")
    @PositiveOrZero(message = "主表id超出范围")
    @JsonAlias("demo_entity_id")
    @Schema(description = "主表id")
    private Long demoEntityId;

    /** 名称 */
    @NotBlank(message = "名称未填写")
    @JsonAlias("name")
    @Schema(description = "名称")
    private String name;

    /** 脱敏的名称 */
    @NotBlank(message = "脱敏的名称未填写")
    @JsonAlias("masked_name")
    @Schema(description = "脱敏的名称")
    private String maskedName;

    /** AES 加密的名称 */
    @NotBlank(message = "AES 加密的名称未填写")
    @JsonAlias("aes_name")
    @Schema(description = "AES 加密的名称")
    private String aesName;

    /** MD5 加密的名称 */
    @NotBlank(message = "MD5 加密的名称未填写")
    @JsonAlias("md5name")
    @Schema(description = "MD5 加密的名称")
    private String md5name;

    /** 明文的名称 */
    @NotBlank(message = "明文的名称未填写")
    @JsonAlias("plain_name")
    @Schema(description = "明文的名称")
    private String plainName;

    /** 数字 */
    @NotNull(message = "数字未填写")
    @PositiveOrZero(message = "数字超出范围")
    @JsonAlias("num")
    @Schema(description = "数字")
    private Integer num;

    /** 时间 */
    @NotNull(message = "时间未填写")
    @JsonAlias("date")
    @Schema(description = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /** 备注 */
    @NotBlank(message = "备注未填写")
    @JsonAlias("comment")
    @Schema(description = "备注")
    private String comment;
}
