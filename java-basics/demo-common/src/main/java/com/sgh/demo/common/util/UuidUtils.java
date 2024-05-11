package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * UUID 工具类
 *
 * @author Song gh
 * @version 2024/5/10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UuidUtils {

    /** 获取 32位 UUID */
    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    /** 获取 32位 没有连接线的 UUID */
    public static String getUuidNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
