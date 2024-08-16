package com.sgh.demo.sharding.database.controller;

import com.sgh.demo.common.constant.ApiResp;
import com.sgh.demo.sharding.database.db.entity.DemoEntityShardingCopy;
import com.sgh.demo.sharding.database.pojo.query.DemoEntityShardingCopyQueryDto;
import com.sgh.demo.sharding.database.pojo.upsert.DemoEntityShardingCopyUpsertDto;
import com.sgh.demo.sharding.database.service.DemoEntityShardingCopyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * [接口] 测试数据--分表--copy
 *
 * @author Song gh
 * @version 2024/03/22
 */
@Slf4j
@Tag(name = "测试数据--分表--copy")
@RestController
@RequestMapping("/demoEntityShardingCopy")
public class DemoEntityShardingCopyController {

    @Resource
    private DemoEntityShardingCopyService demoEntityShardingCopyService;

    /** [新增/更新] 测试数据--分表--copy */
    @PostMapping("/upsert")
    @Operation(summary = "[新增&更新] 测试数据--分表--copy")
    public ApiResp upsert(@RequestBody DemoEntityShardingCopyUpsertDto dto) {
        demoEntityShardingCopyService.upsert(dto);
        return ApiResp.success();
    }

    /** [删除] 测试数据--分表--copy */
    @PostMapping("/delete")
    @Operation(summary = "[删除] 测试数据--分表--copy")
    public ApiResp delete(@RequestBody DemoEntityShardingCopyUpsertDto dto) {
        demoEntityShardingCopyService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** [查询] 测试数据--分表--copy */
    @GetMapping("/get")
    @Operation(summary = "[查询] 测试数据--分表--copy")
    public ApiResp.Entity<DemoEntityShardingCopy> get(@RequestBody DemoEntityShardingCopyUpsertDto dto) {
        return new ApiResp.Entity<>(demoEntityShardingCopyService.get(dto.getId()));
    }

    /** [列表查询] 测试数据--分表--copy */
    @PostMapping("/getList")
    @Operation(summary = "[列表查询] 测试数据--分表--copy")
    public ApiResp.ListEntity<DemoEntityShardingCopy> getList(@RequestBody DemoEntityShardingCopyUpsertDto dto) {
        return new ApiResp.ListEntity<>(demoEntityShardingCopyService.getList());
    }

    /** [分页查询] 测试数据--分表--copy */
    @PostMapping("/getPage")
    @Operation(summary = "[分页查询] 测试数据--分表--copy")
    public ApiResp.PageEntity<DemoEntityShardingCopy> getPage(@RequestBody DemoEntityShardingCopyQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityShardingCopyService.getPage(dto));
    }
}
