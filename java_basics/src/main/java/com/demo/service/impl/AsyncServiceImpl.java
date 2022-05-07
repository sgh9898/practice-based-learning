package com.demo.service.impl;

import com.demo.service.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Async Service
 *
 * @author Song gh on 2022/5/6.
 */
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    /**
     * 异步任务
     *
     * @param name  任务名
     * @param sleep 模拟任务执行时间(秒)
     */
    @Async
    @Override
    public Future<Boolean> asyncTask(String name, int sleep) throws InterruptedException {
        log.info("[Async]任务 {} 开始, 预计时间 {} 秒", name, sleep);
        TimeUnit.SECONDS.sleep(sleep);
        log.info("[Async]任务 {} 完成", name);
        return new AsyncResult<>(Boolean.TRUE);
    }

    /**
     * 同步任务
     *
     * @param name  任务名
     * @param sleep 模拟任务执行时间(秒)
     */
    @Override
    public void syncTask(String name, int sleep) throws InterruptedException {
        log.info("[Sync]任务 {} 开始, 预计时间 {} 秒", name, sleep);
        TimeUnit.SECONDS.sleep(sleep);
        log.info("[Sync]任务 {} 完成", name);
    }
}
