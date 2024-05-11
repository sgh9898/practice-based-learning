package com.sgh.demo.minio.controller;

import com.sgh.demo.common.util.ApiResp;
import com.sgh.demo.minio.db.entity.DemoMinio;
import com.sgh.demo.minio.pojo.query.DemoMinioQueryDto;
import com.sgh.demo.minio.pojo.upsert.DemoMinioUpsertDto;
import com.sgh.demo.minio.service.DemoMinioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * [接口] Minio 文件管理
 *
 * @author Song gh
 * @version 2024/04/03
 */
@Slf4j
@Api(tags = "Minio 文件管理")
@RestController
@RequestMapping("/demoMinio")
public class DemoMinioController {

    @Resource
    private DemoMinioService demoMinioService;

    /** [新增] 上传 Minio 文件 */
    @PostMapping("/upload")
    @ApiOperation("[新增] 上传 Minio 文件")
    public ApiResp upload(MultipartFile file) {
        demoMinioService.uploadFileWithUuidName(file);
        return ApiResp.success();
    }

    /** 下载 Minio 文件 */
    @PostMapping("/download")
    @ApiOperation("下载 Minio 文件")
    public ApiResp download(HttpServletResponse response, @RequestBody DemoMinioQueryDto dto) {
        demoMinioService.download(response, dto.getId());
        return ApiResp.success();
    }

    /** [删除] Minio 文件 */
    @PostMapping("/delete")
    @ApiOperation("[删除] Minio 文件管理")
    public ApiResp delete(@RequestBody DemoMinioUpsertDto dto) {
        demoMinioService.delete(dto.getIdList());
        return ApiResp.success();
    }

    /** 查询 Minio 文件 */
    @PostMapping("/get")
    @ApiOperation("查询Minio 文件管理")
    public ApiResp.Entity<DemoMinio> get(@RequestBody DemoMinioQueryDto dto) {
        return new ApiResp.Entity<>(demoMinioService.get(dto.getId()));
    }

    /** [列表] 查询 Minio 文件 */
    @PostMapping("/getList")
    @ApiOperation("[列表] 查询Minio 文件管理")
    public ApiResp.ListEntity<DemoMinio> getList(@RequestBody DemoMinioQueryDto dto) {
        return new ApiResp.ListEntity<>(demoMinioService.getList());
    }

    /** [分页] 查询 Minio 文件 */
    @PostMapping("/getPage")
    @ApiOperation("[分页] 查询Minio 文件管理")
    public ApiResp.PageEntity<DemoMinio> getPage(@RequestBody DemoMinioQueryDto dto) {
        return new ApiResp.PageEntity<>(demoMinioService.getPage(dto));
    }
}
