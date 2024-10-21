package com.sgh.demo.common.database.controller;

import com.sgh.demo.common.constant.ApiResp;
import com.sgh.demo.common.database.db.entity.DemoEntity;
import com.sgh.demo.common.database.pojo.query.DemoEntityQueryDto;
import com.sgh.demo.common.database.pojo.upsert.DemoEntityUpsertDto;
import com.sgh.demo.common.database.service.DemoEntityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * [接口] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Slf4j
@Tag(name = "测试数据")
@RestController
@RequestMapping("/demoEntity")
public class DemoEntityController {

    @Resource
    private DemoEntityService demoEntityService;

    /** [新增/更新] 测试数据 */
    @PostMapping("/upsert")
    @Operation(summary = "[新增/更新] 测试数据")
    public ApiResp upsert(@RequestBody DemoEntityUpsertDto dto) {
        demoEntityService.upsert(dto);
        return ApiResp.success();
    }

    /** [删除] 测试数据 */
    @PostMapping("/delete")
    @Operation(summary = "[删除] 测试数据")
    public ApiResp delete(@RequestBody DemoEntityUpsertDto dto) {
        demoEntityService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** [查询] 测试数据 */
    @GetMapping("/get")
    @Operation(summary = "[查询] 测试数据")
    public ApiResp.Entity<DemoEntity> get(@RequestBody DemoEntityUpsertDto dto) {
        return new ApiResp.Entity<>(demoEntityService.get(dto.getId()));
    }

    /** [列表] 测试数据 */
    @PostMapping("/getList")
    @Operation(summary = "[列表查询] 测试数据")
    public ApiResp.ListEntity<DemoEntity> getList(@RequestBody DemoEntityUpsertDto dto) {
        return new ApiResp.ListEntity<>(demoEntityService.getList());
    }

    /** [分页] 测试数据 */
    @PostMapping("/getPage")
    @Operation(summary = "[分页查询] 测试数据")
    public ApiResp.PageEntity<DemoEntity> getPage(@RequestBody DemoEntityQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityService.getPage(dto));
    }
}
