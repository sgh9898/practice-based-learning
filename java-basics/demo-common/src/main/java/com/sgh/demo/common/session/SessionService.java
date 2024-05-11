package com.sgh.demo.common.session;

import javax.servlet.http.HttpServletRequest;

/**
 * Session 功能
 *
 * @author Song gh on 2023/3/6.
 */
public interface SessionService {

    /** 添加 session */
    String createSession(HttpServletRequest request);

    /** 读取 session */
    Object readSession(HttpServletRequest request);
}
