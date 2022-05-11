package com.demo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.util.BodyRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
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

    /**
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * @param servletRequest  The request to process
     * @param servletResponse The response associated with the request
     * @param chain           Provides access to the next filter in the chain for this
     *                        filter to pass the request and response to for further
     *                        processing
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        log.info("过滤器启动, uri: {}", request.getRequestURI());

        // 获取 body
        String bodyStr = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(bodyStr)) {
            log.info("body 为空, uri: {}", request.getRequestURI());
            return;
        }

        // 处理 request body
        JSONObject bodyJson = JSON.parseObject(bodyStr);
        bodyJson.put("filterAdded", "filter 添加的信息");
        request = new BodyRequestWrapper(request, bodyJson.toJSONString());

        chain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
