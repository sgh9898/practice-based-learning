package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * UUID 工具类
 *
 * @author Song gh
 * @version 2024/7/12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UuidUtils {

    /** 获取 32位 UUID */
    public static String getUuid() {
        return UUID.randomUUID().toString();
    }

    /** 获取 32位 UUID */
    public static String getUuid(int length) {
        return StringUtils.substring(UUID.randomUUID().toString(), 0, length);
    }

    /** 获取 32位 没有连接线的 UUID */
    public static String getUuidNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** 获取 32位 没有连接线的 UUID */
    public static String getUuidNoDash(int length) {
        return StringUtils.substring(UUID.randomUUID().toString().replace("-", ""), 0, length);
    }
}
