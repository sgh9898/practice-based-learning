package com.demo.db.pojo;

import com.demo.db.entity.DemoEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 演示类
 *
 * @author Song gh on 2022/07/18.
 */
@Data
@Api("演示类")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityVo {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty(name = "名称")
    private String name;

    @ApiModelProperty(name = "标签")
    private String tags;

    public DemoEntityVo(DemoEntity demoEntity) {
        // 实体类需要做 null 判断, 防止查询报错
        if (demoEntity != null) {
            this.id = demoEntity.getId();
            this.name = demoEntity.getName();
            this.tags = demoEntity.getTags();
        }
    }
}

