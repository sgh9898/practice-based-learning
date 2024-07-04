package com.sgh.demo.sharding.database.controller;

import com.sgh.demo.common.util.ApiResp;
import com.sgh.demo.sharding.database.db.entity.DemoEntitySharding;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * [接口] 测试数据--分表
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Slf4j
@Tag(name = "测试数据--分表")
@RestController
@RequestMapping("/demoEntitySharding")
public class DemoEntityShardingController {

    @Resource
    private DemoEntityShardingService demoEntityShardingService;

    /** [新增/更新] 测试数据--分表 */
    @PostMapping("/upsert")
    @Operation(summary = "[新增&更新] 测试数据--分表")
    public ApiResp upsert(@RequestBody DemoEntityShardingUpsertDto dto) {
        demoEntityShardingService.upsert(dto);
        return ApiResp.success();
    }

    /** [删除] 测试数据--分表 */
    @PostMapping("/delete")
    @Operation(summary = "[删除] 测试数据--分表")
    public ApiResp delete(@RequestBody DemoEntityShardingUpsertDto dto) {
        demoEntityShardingService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** [查询] 测试数据--分表 */
    @GetMapping("/get")
    @Operation(summary = "[查询] 测试数据--分表")
    public ApiResp.Entity<DemoEntitySharding> get(@RequestBody DemoEntityShardingUpsertDto dto) {
        return new ApiResp.Entity<>(demoEntityShardingService.get(dto.getId()));
    }

    /** [列表查询] 测试数据--分表 */
    @PostMapping("/getList")
    @Operation(summary = "[列表查询] 测试数据--分表")
    public ApiResp.ListEntity<DemoEntitySharding> getList(@RequestBody DemoEntityShardingUpsertDto dto) {
        return new ApiResp.ListEntity<>(demoEntityShardingService.getList());
    }

    /** [分页查询] 测试数据--分表 */
    @PostMapping("/getPage")
    @Operation(summary = "[分页查询] 测试数据--分表")
    public ApiResp.PageEntity<DemoEntitySharding> getPage(@RequestBody DemoEntityShardingQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityShardingService.getPage(dto));
    }

    /** [分页查询] 并表数据 */
    @PostMapping("/getMergedPage")
    @Operation(summary = "[分页查询] 并表数据")
    public ApiResp.PageEntity<Map<String, Object>> getMergedPage(@RequestBody DemoEntityShardingQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityShardingService.getMergedPage(dto));
    }
}
