package com.sgh.demo.sharding.database.pojo.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * [展示 VO] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Data
@Schema(description = "[展示 VO] 测试数据--时间分表")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityShardingTimeVo {

    /** id */
    @JsonAlias("id")
    @Schema(description = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** 主表id */
    @JsonAlias("demo_entity_id")
    @Schema(description = "主表id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long demoEntityId;

    /** 名称 */
    @JsonAlias("name")
    @Schema(description = "名称")
    private String name;

    /** 脱敏的名称 */
    @JsonAlias("masked_name")
    @Schema(description = "脱敏的名称")
    private String maskedName;

    /** AES 加密的名称 */
    @JsonAlias("aes_name")
    @Schema(description = "AES 加密的名称")
    private String aesName;

    /** MD5 加密的名称 */
    @JsonAlias("md5name")
    @Schema(description = "MD5 加密的名称")
    private String md5name;

    /** 明文的名称 */
    @JsonAlias("plain_name")
    @Schema(description = "明文的名称")
    private String plainName;

    /** 数字 */
    @JsonAlias("num")
    @Schema(description = "数字")
    private Integer num;

    /** 时间 */
    @JsonAlias("date")
    @Schema(description = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date date;

    /** 备注 */
    @JsonAlias("comment")
    @Schema(description = "备注")
    private String comment;

    /** [构造] */
    public DemoEntityShardingTimeVo() {
    }

    /** [构造] 根据实体类创建 */
    public DemoEntityShardingTimeVo(DemoEntityShardingTime entity) {
        this.id = entity.getId();
        this.demoEntityId = entity.getDemoEntityId();
        this.name = entity.getName();
        this.maskedName = entity.getMaskedName();
        this.aesName = entity.getAesName();
        this.md5name = entity.getMd5name();
        this.plainName = entity.getPlainName();
        this.num = entity.getNum();
        this.date = entity.getDate();
        this.comment = entity.getComment();
    }
}
