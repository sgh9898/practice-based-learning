package com.sgh.demo.general.quartz.controller;

import com.sgh.demo.common.constant.ApiResp;
import com.sgh.demo.common.quartz.dto.QuartzConfigDto;
import com.sgh.demo.common.quartz.quartz.service.QuartzJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Quartz 定时任务
 *
 * @author Song gh on 2023/12/11.
 */
@Slf4j
@Tag(name = "Quartz 定时任务")
@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Resource
    private QuartzJobService quartzJobService;

    /** [新增/更新] 添加任务并启动(任务已存在时转为更新) */
    @PostMapping("/upsertJob")
    @Operation(summary = "[新增/更新] 添加任务并启动(任务已存在时转为更新)")
    public ApiResp upsertJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.upsertJob(configDTO.getJobClass(), configDTO.getJobName(), configDTO.getGroupName(), configDTO.getCronExpression(), configDTO.getParam());
        return ApiResp.success();
    }

    /** [新增] 添加任务并启动(任务已存在时不执行) */
    @PostMapping("/addJobIfNotExists")
    @Operation(summary = "[新增] 添加任务并启动(任务已存在时不执行)")
    public ApiResp addJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.addJobIfNotExists(configDTO.getJobClass(), configDTO.getJobName(), configDTO.getGroupName(), configDTO.getCronExpression(), configDTO.getParam());
        return ApiResp.success();
    }

    /** [更新] 更新任务 */
    @PostMapping("/updateJob")
    @Operation(summary = "[更新] 更新任务")
    public ApiResp updateJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.updateJob(configDTO.getJobName(), configDTO.getGroupName(), configDTO.getCronExpression(), configDTO.getParam());
        return ApiResp.success();
    }

    /** 暂停任务 */
    @PostMapping("/pauseJob")
    @Operation(summary = "暂停任务")
    public ApiResp pauseJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.pauseJob(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 恢复任务 */
    @PostMapping("/resumeJob")
    @Operation(summary = "恢复任务")
    public ApiResp resumeJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.resumeJob(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 立即运行一次定时任务 */
    @PostMapping("/runOnce")
    @Operation(summary = "立即运行一次定时任务")
    public ApiResp runOnce(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.runOnce(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 删除任务 */
    @PostMapping("/deleteJob")
    @Operation(summary = "删除任务")
    public ApiResp deleteJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.deleteJob(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 启动所有任务 */
    @PostMapping("/startAllJobs")
    @Operation(summary = "启动所有任务")
    public ApiResp startAllJobs() {
        quartzJobService.startAllJobs();
        return ApiResp.success();
    }

    /** 暂停所有任务 */
    @PostMapping("/pauseAllJobs")
    @Operation(summary = "暂停所有任务")
    public ApiResp pauseAllJobs() {
        quartzJobService.pauseAllJobs();
        return ApiResp.success();
    }

    /** 恢复所有任务 */
    @PostMapping("/resumeAllJobs")
    @Operation(summary = "恢复所有任务")
    public ApiResp resumeAllJobs() {
        quartzJobService.resumeAllJobs();
        return ApiResp.success();
    }

    /** 关闭所有任务 */
    @PostMapping("/shutdownAllJobs")
    @Operation(summary = "关闭所有任务")
    public ApiResp shutdownAllJobs() {
        quartzJobService.shutdownAllJobs();
        return ApiResp.success();
    }
}