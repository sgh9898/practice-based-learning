package com.collin.demo.database.controller;

import com.collin.demo.database.pojo.query.DemoEntityQueryDto;
import com.collin.demo.database.pojo.upsert.DemoEntityUpsertDto;
import com.collin.demo.database.db.entity.DemoEntity;
import com.collin.demo.database.service.DemoEntityService;
import com.collin.demo.exception.BaseException;
import com.collin.demo.util.ApiResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * [接口] 测试数据
 *
 * @author Song gh
 * @version 2024/03/18
 */
@Slf4j
@Api(tags = "测试数据")
@RestController
@RequestMapping("/demoEntity")
public class DemoEntityController {

    @Resource
    private DemoEntityService demoEntityService;

    /** [新增/更新] 测试数据 */
    @PostMapping("/upsert")
    @ApiOperation(value = "[新增/更新] 测试数据", notes = "添加新数据时不要传 id")
    public ApiResp upsert(@RequestBody DemoEntityUpsertDto dto) {
        demoEntityService.upsert(dto);
        return ApiResp.success();
    }

    /** [删除] 测试数据 */
    @PostMapping("/delete")
    @ApiOperation("[删除] 测试数据")
    public ApiResp delete(@RequestBody DemoEntityUpsertDto dto) {
        demoEntityService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** [查询] 测试数据 */
    @GetMapping("/get")
    @ApiOperation("[查询] 测试数据")
    public ApiResp.Entity<DemoEntity> get(@RequestBody DemoEntityUpsertDto dto) {
        return new ApiResp.Entity<>(demoEntityService.get(dto.getId()));
    }

    /** [列表] 测试数据 */
    @PostMapping("/getList")
    @ApiOperation("[列表查询] 测试数据")
    public ApiResp.ListEntity<DemoEntity> getList(@RequestBody DemoEntityUpsertDto dto) {
        return new ApiResp.ListEntity<>(demoEntityService.getList());
    }

    /** [分页] 测试数据 */
    @PostMapping("/getPage")
    @ApiOperation("[分页查询] 测试数据")
    public ApiResp.PageEntity<DemoEntity> getPage(@RequestBody DemoEntityQueryDto dto) {
        return new ApiResp.PageEntity<>(demoEntityService.getPage(dto));
    }

    /** [导入] 测试数据 */
    @PostMapping("/excel/importData")
    @ApiOperation(value = "[Excel导入] 测试数据", notes = "1. 导入成功无返回\n2. 导入失败的数据会下载包含报错描述的 Excel")
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        // 校验
        if (file == null) throw new BaseException("文件为空");
        demoEntityService.importData(file, request, response);
    }

    /** [模板] 测试数据 */
    @GetMapping("/excel/exportTemplate")
    @ApiOperation("[Excel模板] 测试数据")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        demoEntityService.exportExcelTemplate(request, response);
    }

    /** [导出] 测试数据 */
    @GetMapping("/excel/exportData")
    @ApiOperation("[Excel导出] 测试数据")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        demoEntityService.exportData(request, response);
    }
}
