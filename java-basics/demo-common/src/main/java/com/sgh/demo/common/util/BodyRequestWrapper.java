package com.sgh.demo.common.util;


import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 更改 Http Request 中 Body 的值 (配合 Filter )
 *
 * @author Song gh on 2022/5/11.
 */
public class BodyRequestWrapper extends HttpServletRequestWrapper {

    // Body 中的数据
    private final String bodyStr;

    public BodyRequestWrapper(HttpServletRequest request, String context) {
        super(request);
        bodyStr = context;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bodyStr.getBytes("UTF-8"));
        return new ServletInputStream() {
            @Override
            public int read() {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}




