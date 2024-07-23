package com.sgh.demo.general.neo4j;

import com.sgh.demo.common.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Neo4j 示例
 *
 * @author Song gh on 2023/8/15.
 */
@Slf4j
@RestController
@Api(tags = "Neo4j 示例")
@RequestMapping("/neo4j")
public class Neo4jPersonController {

    @Resource
    private Neo4jService neo4jService;

    @PostMapping("/createNode")
    @ApiOperation("创建节点")
    public Object createNode(String name) {
        neo4jService.createNode(name);
        return ResultUtil.success();
    }

    @PostMapping("/buildUpRelationships")
    @ApiOperation("建立节点关系")
    public Object buildUpRelationships(String name1, String name2, Integer type) {
        neo4jService.buildUpRelationships(name1, name2, type);
        return ResultUtil.success();
    }
}
