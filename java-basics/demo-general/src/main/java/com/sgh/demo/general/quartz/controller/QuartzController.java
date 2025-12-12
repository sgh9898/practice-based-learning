package com.sgh.demo.general.quartz.controller;

import com.sgh.demo.common.constant.ApiResp;
import com.sgh.demo.general.quartz.dto.QuartzConfigDto;
import com.sgh.demo.general.quartz.quartz.service.QuartzJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * Quartz 任务调度
 *
 * @author Song gh on 2023/12/11.
 */
@Slf4j
@Api(tags = "Quartz 任务调度")
@RestController
@RequestMapping("/quartz")
public class QuartzController {

    @Resource
    private QuartzJobService quartzJobService;

    /** [新增/更新] 添加任务并启动(任务已存在时转为更新) */
    @PostMapping("/upsertJob")
    @ApiOperation("[新增/更新] 添加任务并启动(任务已存在时转为更新)")
    public ApiResp upsertJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.upsertJob(configDTO.getJobClass(), configDTO.getJobName(), configDTO.getGroupName(), configDTO.getCronExpression(), configDTO.getParam());
        return ApiResp.success();
    }

    /** [新增] 添加任务并启动(任务已存在时不执行) */
    @PostMapping("/addJobIfNotExists")
    @ApiOperation("[新增] 添加任务并启动(任务已存在时不执行)")
    public ApiResp addJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.addJobIfNotExists(configDTO.getJobClass(), configDTO.getJobName(), configDTO.getGroupName(), configDTO.getCronExpression(), configDTO.getParam());
        return ApiResp.success();
    }

    /** [更新] 更新任务 */
    @PostMapping("/updateJob")
    @ApiOperation("[更新] 更新任务")
    public ApiResp updateJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.updateJob(configDTO.getJobName(), configDTO.getGroupName(), configDTO.getCronExpression(), configDTO.getParam());
        return ApiResp.success();
    }

    /** 暂停任务 */
    @PostMapping("/pauseJob")
    @ApiOperation("暂停任务")
    public ApiResp pauseJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.pauseJob(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 恢复任务 */
    @PostMapping("/resumeJob")
    @ApiOperation("恢复任务")
    public ApiResp resumeJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.resumeJob(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 立即运行一次定时任务 */
    @PostMapping("/runOnce")
    @ApiOperation("立即运行一次定时任务")
    public ApiResp runOnce(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.runOnce(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 删除任务 */
    @PostMapping("/deleteJob")
    @ApiOperation("删除任务")
    public ApiResp deleteJob(@RequestBody QuartzConfigDto configDTO) {
        quartzJobService.deleteJob(configDTO.getJobName(), configDTO.getGroupName());
        return ApiResp.success();
    }

    /** 启动所有任务 */
    @PostMapping("/startAllJobs")
    @ApiOperation("启动所有任务")
    public ApiResp startAllJobs() {
        quartzJobService.startAllJobs();
        return ApiResp.success();
    }

    /** 暂停所有任务 */
    @PostMapping("/pauseAllJobs")
    @ApiOperation("暂停所有任务")
    public ApiResp pauseAllJobs() {
        quartzJobService.pauseAllJobs();
        return ApiResp.success();
    }

    /** 恢复所有任务 */
    @PostMapping("/resumeAllJobs")
    @ApiOperation("恢复所有任务")
    public ApiResp resumeAllJobs() {
        quartzJobService.resumeAllJobs();
        return ApiResp.success();
    }

    /** 关闭所有任务 */
    @PostMapping("/shutdownAllJobs")
    @ApiOperation("关闭所有任务")
    public ApiResp shutdownAllJobs() {
        quartzJobService.shutdownAllJobs();
        return ApiResp.success();
    }
}