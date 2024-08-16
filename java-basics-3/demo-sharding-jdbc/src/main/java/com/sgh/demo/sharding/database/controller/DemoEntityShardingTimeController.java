package com.sgh.demo.sharding.database.controller;

import com.sgh.demo.common.constant.ApiResp;
import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingTime;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingTimeQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingTimeUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingTimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * [接口] 测试数据--时间分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Slf4j
@Tag(name = "测试数据--时间分表")
@RestController
@RequestMapping("/demoEntityShardingTime")
public class DemoEntityShardingTimeController {

    @Resource
    private DemoEntityShardingTimeService demoEntityShardingTimeService;

    /** [新增/更新] 测试数据--时间分表 */
    @PostMapping("/upsert")
    @Operation(summary = "[新增&更新] 测试数据--时间分表")
    public ApiResp upsert(@RequestBody DemoEntityShardingTimeUpsertDto dto) {
        demoEntityShardingTimeService.upsert(dto);
        return ApiResp.success();
    }

    /** [删除] 测试数据--时间分表 */
    @PostMapping("/delete")
    @Operation(summary = "[删除] 测试数据--时间分表")
    public ApiResp delete(@RequestBody DemoEntityShardingTimeUpsertDto dto) {
        demoEntityShardingTimeService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** 查询测试数据--时间分表 */
    @GetMapping("/get")
    @Operation(summary = "查询测试数据--时间分表")
    public ApiResp.Entity<DemoEntityShardingTime> get(@RequestBody DemoEntityShardingTimeUpsertDto dto) {
        return new ApiResp.Entity<>(demoEntityShardingTimeService.get(dto.getId()));
    }

    /** [列表] 查询测试数据--时间分表 */
    @PostMapping("/getList")
    @Operation(summary = "[列表] 查询测试数据--时间分表")
    public ApiResp.ListEntity<DemoEntityShardingTime> getList(@RequestBody DemoEntityShardingTimeUpsertDto dto) {
        return new ApiResp.ListEntity<>(demoEntityShardingTimeService.getList());
    }

    /** [分页] 查询测试数据--时间分表 */
    @PostMapping("/getPage")
    @Operation(summary = "[分页] 查询测试数据--时间分表")
    public ApiResp.PageEntity<DemoEntityShardingTime> getPage(@RequestBody DemoEntityShardingTimeQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityShardingTimeService.getPage(dto));
    }
}
