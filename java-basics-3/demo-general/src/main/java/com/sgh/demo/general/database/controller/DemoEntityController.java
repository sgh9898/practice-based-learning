package com.sgh.demo.general.database.controller;

import com.sgh.demo.general.database.db.entity.DemoEntity;
import com.sgh.demo.general.database.pojo.query.DemoEntityQueryDto;
import com.sgh.demo.general.database.pojo.upsert.DemoEntityUpsertDto;
import com.sgh.demo.general.database.service.DemoEntityService;
import com.sgh.demo.common.exception.BaseException;
import com.sgh.demo.common.constant.ApiResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /** [导入] 测试数据 */
    @PostMapping("/excel/importData")
    @Operation(summary = "[Excel导入] 测试数据", description = "1. 导入成功无返回\n2. 导入失败的数据会下载包含报错描述的 Excel")
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        // 校验
        if (file == null) throw new BaseException("文件为空");
        demoEntityService.importData(file, request, response);
    }

    /** [模板] 测试数据 */
    @GetMapping("/excel/exportTemplate")
    @Operation(summary = "[Excel模板] 测试数据")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        demoEntityService.exportExcelTemplate(request, response);
    }

    /** [导出] 测试数据 */
    @GetMapping("/excel/exportData")
    @Operation(summary = "[Excel导出] 测试数据")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        demoEntityService.exportData(request, response);
    }
}
