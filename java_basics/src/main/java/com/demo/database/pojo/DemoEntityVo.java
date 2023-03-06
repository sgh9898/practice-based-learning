package com.demo.database.pojo;

import com.demo.database.entity.DemoEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 演示类
 *
 * @author Song gh on 2022/07/18.
 */
@Data
@Schema(description = "演示类")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoEntityVo {

    @Schema(name = "id")
    private Long id;

    @Schema(name = "名称")
    private String name;

    @Schema(name = "标签")
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

