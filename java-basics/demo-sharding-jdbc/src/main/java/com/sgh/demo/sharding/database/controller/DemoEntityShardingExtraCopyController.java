package com.sgh.demo.sharding.database.controller;

import com.sgh.demo.common.util.ApiResp;
import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingExtraCopy;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingExtraCopyQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingExtraCopyUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingExtraCopyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * [接口] 测试数据--分库分表
 *
 * @author Song gh
 * @version 2024/03/28
 */
@Slf4j
@Api(tags = "测试数据--分库分表")
@RestController
@RequestMapping("/demoEntityShardingExtraCopy")
public class DemoEntityShardingExtraCopyController {

    @Resource
    private DemoEntityShardingExtraCopyService demoEntityShardingExtraCopyService;

    /** [新增/更新] 测试数据--分库分表 */
    @PostMapping("/upsert")
    @ApiOperation(value = "[新增&更新] 测试数据--分库分表", notes = "添加新数据时不要传 id")
    public ApiResp upsert(@RequestBody DemoEntityShardingExtraCopyUpsertDto dto) {
        demoEntityShardingExtraCopyService.upsert(dto);
        return ApiResp.success();
    }

    /** [删除] 测试数据--分库分表 */
    @PostMapping("/delete")
    @ApiOperation("[删除] 测试数据--分库分表")
    public ApiResp delete(@RequestBody DemoEntityShardingExtraCopyUpsertDto dto) {
        demoEntityShardingExtraCopyService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** 查询测试数据--分库分表 */
    @GetMapping("/get")
    @ApiOperation("查询测试数据--分库分表")
    public ApiResp.Entity<DemoEntityShardingExtraCopy> get(@RequestBody DemoEntityShardingExtraCopyUpsertDto dto) {
        return new ApiResp.Entity<>(demoEntityShardingExtraCopyService.get(dto.getId()));
    }

    /** [列表] 查询测试数据--分库分表 */
    @PostMapping("/getList")
    @ApiOperation("[列表] 查询测试数据--分库分表")
    public ApiResp.ListEntity<DemoEntityShardingExtraCopy> getList(@RequestBody DemoEntityShardingExtraCopyUpsertDto dto) {
        return new ApiResp.ListEntity<>(demoEntityShardingExtraCopyService.getList());
    }

    /** [分页] 查询测试数据--分库分表 */
    @PostMapping("/getPage")
    @ApiOperation("[分页] 查询测试数据--分库分表")
    public ApiResp.PageEntity<DemoEntityShardingExtraCopy> getPage(@RequestBody DemoEntityShardingExtraCopyQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityShardingExtraCopyService.getPage(dto));
    }
}
