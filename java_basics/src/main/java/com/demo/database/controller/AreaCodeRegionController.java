package com.demo.database.controller;

import com.demo.database.db.entity.AreaCodeRegion;
import com.demo.database.pojo.query.AreaCodeRegionQueryDto;
import com.demo.database.pojo.upsert.AreaCodeRegionUpsertDto;
import com.demo.exception.BaseException;
import com.demo.util.ApiResp;
import com.demo.database.service.AreaCodeRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * [接口] 区号
 *
 * @author Song gh on 2023/12/04.
 */
@Slf4j
@Api(tags = "区号")
@RestController
@RequestMapping("/areaCodeRegion")
public class AreaCodeRegionController {
    
    @Resource
    private AreaCodeRegionService areaCodeRegionService;
    
    /** [新增/更新] 区号 */
    @PostMapping("/upsert")
    @ApiOperation(value = "[新增/更新] 区号", notes = "添加新数据时不要传 id")
    public ApiResp upsert(@RequestBody AreaCodeRegionUpsertDto dto) {
        areaCodeRegionService.upsert(dto);
        return ApiResp.success();
    }
    
    /** [删除] 区号 */
    @PostMapping("/delete")
    @ApiOperation("[删除] 区号")
    public ApiResp delete(@RequestBody AreaCodeRegionUpsertDto dto) {
        areaCodeRegionService.delete(dto.getIdList());
        return ApiResp.success();
    }
    
    /** [查询] 区号 */
    @GetMapping("/get")
    @ApiOperation("[查询] 区号")
    public ApiResp.Entity<AreaCodeRegion> get(@RequestBody AreaCodeRegionUpsertDto dto) {
        return new ApiResp.Entity<>(areaCodeRegionService.get(dto.getId()));
    }
    
    /** [列表] 区号 */
    @GetMapping("/getList")
    @ApiOperation("[列表查询] 区号")
    public ApiResp.ListEntity<AreaCodeRegion> getList(@RequestBody AreaCodeRegionUpsertDto dto) {
        return new ApiResp.ListEntity<>(areaCodeRegionService.getList());
    }
    
    /** [分页] 区号 */
    @GetMapping("/getPage")
    @ApiOperation("[分页查询] 区号")
    public ApiResp.PageEntity<AreaCodeRegion> getPage(@RequestBody AreaCodeRegionQueryDto dto) {
        return new ApiResp.PageEntity<>(areaCodeRegionService.getPage(dto));
    }

    /** [导入] 区号 */
    @PostMapping("/excel/importData")
    @ApiOperation(value = "[Excel导入] 区号", notes = "1. 导入成功无返回\n2. 导入失败的数据会下载包含报错描述的 Excel")
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        // 校验
        if (file == null) throw new BaseException("文件为空");
        areaCodeRegionService.importData(file, request, response);
    }

    /** [模板] 区号 */
    @GetMapping("/excel/exportTemplate")
    @ApiOperation("[Excel模板] 区号")
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response) {
        areaCodeRegionService.exportExcelTemplate(request, response);
    }

    /** [导出] 区号 */
    @GetMapping("/excel/exportData")
    @ApiOperation("[Excel导出] 区号")
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        areaCodeRegionService.exportData(request, response);
    }
}
