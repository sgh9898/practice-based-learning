package com.sgh.demo.general.neo4j;

import com.sgh.demo.common.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Neo4j 示例
 *
 * @author Song gh on 2023/8/15.
 */
@Slf4j
@RestController
@Tag(name = "Neo4j 示例")
@RequestMapping("/neo4j")
public class Neo4jPersonController {

    @Resource
    private Neo4jService neo4jService;

    @PostMapping("/createNode")
    @Operation(summary = "创建节点")
    public Object createNode(String name) {
        neo4jService.createNode(name);
        return ResultUtil.success();
    }

    @PostMapping("/buildUpRelationships")
    @Operation(summary = "建立节点关系")
    public Object buildUpRelationships(String name1, String name2, Integer type) {
        neo4jService.buildUpRelationships(name1, name2, type);
        return ResultUtil.success();
    }
}
