package com.sgh.demo.sharding.database.controller;

import com.sgh.demo.common.constant.ApiResp;
import com.sgh.demo.sharding.database.pojo.upsert.DictDataUpsertDto;
import com.sgh.demo.sharding.database.service.DictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [接口] 数据字典
 *
 * @author Song gh
 * @version 2024/03/27
 */
@Slf4j
@Tag(name = "数据字典")
@RestController
@RequestMapping("/dictData")
public class DictDataController {

    @Resource
    private DictDataService dictDataService;

    /** [新增/更新] 数据字典 */
    @PostMapping("/upsert")
    @Operation(summary = "[新增&更新] 数据字典")
    public ApiResp upsert(@RequestBody DictDataUpsertDto dto) {
        dictDataService.upsert(dto);
        return ApiResp.success();
    }
}
