package com.demo.database.controller;

import com.demo.database.entity.DemoEntity;
import com.demo.database.pojo.DemoEntityDto;
import com.demo.database.service.DemoEntityService;
import com.demo.exception.BaseException;
import com.demo.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 演示类
 *
 * @author Song gh on 2023/02/28.
 */
@Slf4j
@RestController
@Api(tags = "演示类")
@RequestMapping("/demoEntity")
public class DemoEntityController {
    
    @Resource
    private DemoEntityService demoEntityService;
    
    /** [新增/更新] 演示类 */
    @PostMapping("/upsert")
    @ApiOperation(value = "[添加/更新] 演示类", notes = "添加新数据时不要传 id")
    public Map<String, Object> upsert(@RequestBody DemoEntityDto dto) {
        log.debug("[添加/更新] 演示类 : {}", dto);
        demoEntityService.upsert(dto);
        return ResultUtil.success();
    }
    
    /** [逻辑删除] 演示类 */
    @PostMapping("/delete")
    @ApiOperation("[逻辑删除] 演示类")
    @ApiImplicitParam(name = "idList", value = "list of 演示类id")
    public Map<String, Object> delete(@RequestParam List<Long> idList) {
        log.debug("[逻辑删除] 演示类, id = {}", idList);
        demoEntityService.delete(idList);
        return ResultUtil.success();
    }
    
    /** [单查询] 演示类 */
    @GetMapping("/get")
    @ApiOperation(value = "[单查询] 演示类", response = DemoEntity.class)
    @ApiImplicitParam(name = "id", value = "演示类id")
    public Map<String, Object> get(Long id) {
        return ResultUtil.success("data", demoEntityService.get(id));
    }
    
    /** [列表查询] 演示类 */
    @GetMapping("/getList")
    @ApiOperation(value = "[列表查询] 演示类", response = DemoEntity.class)
    @ApiImplicitParams({})
    public Map<String, Object> getList() {
        return ResultUtil.success("data", demoEntityService.getList());
    }
    
    /** [分页查询] 演示类 */
    @GetMapping("/getPage")
    @ApiOperation(value = "[分页查询] 演示类", response = DemoEntity.class)
    @ApiImplicitParams({})
    public Map<String, Object> getPage(Pageable pageable) {
        Page<DemoEntity> pageResult = demoEntityService.getPage(pageable);
        Map<String, Object> returnMap = ResultUtil.success();
        returnMap.put("data", pageResult.getContent());
        returnMap.put("totalNum", pageResult.getTotalElements());
        returnMap.put("totalPage", pageResult.getTotalPages());
        return returnMap;
    }

    /** [导入数据] 演示类 */
    @PostMapping("/excel/importData")
    @ApiOperation(value = "[导入数据] 演示类", notes = "1. 导入成功无返回\n2. 导入失败的数据会以 Excel 的形式返回")
    public void importData(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        // 校验
        if (file == null) {
            throw new BaseException("文件为空");
        }
        demoEntityService.importData(file, request, response);
    }

    /** [导出数据] 演示类 */
    @GetMapping("/excel/exportData")
    @ApiOperation("[导出数据] 演示类")
    @ApiImplicitParams({})
    public void exportData(HttpServletRequest request, HttpServletResponse response) {
        demoEntityService.exportData(request, response);
    }
}
