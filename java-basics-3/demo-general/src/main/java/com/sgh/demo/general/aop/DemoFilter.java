package com.sgh.demo.common.aop;

import com.alibaba.fastjson2.JSONObject;
import com.sgh.demo.common.exception.BaseException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 过滤器
 *
 * @author Song gh on 2022/5/11.
 */
@Slf4j
@Component
public class DemoFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        log.info("过滤器启动, uri: {}", request.getRequestURI());

        // 获取 parameter
        String msg = request.getParameter("message");

        // 获取 json
        String bodyStr = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(msg) && StringUtils.isBlank(bodyStr)) {
            throw new BaseException("Filter 报错: 无数据传入");
        }

        // 处理 request body
        JSONObject processed = new JSONObject();
        processed.put("originalMsg", msg);
        processed.put("jsonMsg", bodyStr);
        processed.put("filterAdded", "filter 添加的信息");
        request.setAttribute("processedMsg", processed);

        log.info("过滤器完成, uri: {}", request.getRequestURI());

        chain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
