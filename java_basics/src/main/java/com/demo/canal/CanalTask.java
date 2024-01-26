package com.demo.canal;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Canal Client 启动器
 *
 * @author Song gh
 * @version 2024/1/25
 */
@Component
public class CanalTask {

    @Resource
    private CanalClient canalClient;

    /**
     * 以定时任务的方式启动 canal, 以确保在意外中断时自动重连
     */
    @Scheduled(fixedDelayString = "PT1S", initialDelayString = "PT10S")
    private void canalStart() {
        canalClient.start();
    }
}
