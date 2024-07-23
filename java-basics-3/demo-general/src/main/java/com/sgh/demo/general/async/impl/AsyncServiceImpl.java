package com.sgh.demo.common.async.impl;

import com.sgh.demo.common.async.AsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 异步功能
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
        log.info("异步任务 {} 开始", name);
        long start = System.currentTimeMillis();
        TimeUnit.SECONDS.sleep(sleep);
        log.info("异步任务 {} 完成, 用时 {} ms", name, System.currentTimeMillis() - start);
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
        log.info("同步任务 {} 开始", name);
        long start = System.currentTimeMillis();
        TimeUnit.SECONDS.sleep(sleep);
        log.info("同步任务 {} 完成, 用时 {} ms", name, System.currentTimeMillis() - start);
    }
}
