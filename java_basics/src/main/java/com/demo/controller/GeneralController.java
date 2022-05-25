package com.demo.controller;

import com.demo.service.AsyncService;
import com.demo.util.ResultUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 基础或未分类功能
 *
 * @author Song gh on 2022/5/18.
 */
@Tag(name = "General Controller", description = "基础或未分类功能")
@Slf4j
@RestController
@RequestMapping("/general")
public class GeneralController {

    @Resource
    private AsyncService asyncService;

    @Operation(summary = "异步任务")
    @PostMapping("/async")
    public Map<String, Object> demoAsync() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();

        Future<Boolean> async1 = asyncService.asyncTask("1", 1);
        Future<Boolean> async2 = asyncService.asyncTask("2", 3);
        Future<Boolean> async3 = asyncService.asyncTask("3", 5);

        // 调用 get() 阻塞主线程
        async1.get();
        async2.get();
        async3.get();

        long time = System.currentTimeMillis() - start;
        log.info("异步任务总耗时: {}", time);
        return ResultUtil.success();
    }

    @Operation(summary = "同步任务")
    @PostMapping("/sync")
    public Map<String, Object> demoSync() throws InterruptedException {
        long start = System.currentTimeMillis();

        asyncService.syncTask("1", 1);
        asyncService.syncTask("2", 3);
        asyncService.syncTask("3", 5);

        long time = System.currentTimeMillis() - start;
        log.info("同步任务总耗时: {}", time);
        return ResultUtil.success();
    }
}
