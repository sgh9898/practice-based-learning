package com.demo.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.Future;

/**
 * Async Service
 *
 * @author Song gh on 2022/5/6.
 */
public interface AsyncService {

    /**
     * 异步任务
     *
     * @param name  任务名
     * @param sleep 模拟任务执行时间(秒)
     */
    Future<Boolean> asyncTask(String name, int sleep) throws InterruptedException;

    /**
     * 同步任务
     *
     * @param name  任务名
     * @param sleep 模拟任务执行时间(秒)
     */
    void syncTask(String name, int sleep) throws InterruptedException;
}
