package com.demo.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Http(s) 连接工具 (OkHttp)
 * <br>较 HttpClient 更为简洁, 非单例模式下响应更快
 *
 * @author Song gh on 2022/1/18.
 */
public class OkHttpUtil {

    static final Logger log = LoggerFactory.getLogger(OkHttpUtil.class);

    // 基本配置
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //* 自定义参数
    // 超时时间
    public static final int CONNECT_TIME_OUT = 60;
    public static final int READ_TIME_OUT = 60;
    public static final int WRITE_TIME_OUT = 60;

    // 信任的 host
    @Value("${okhttp.trustedHosts}")
    private static HashSet<String> trustedHosts;

    // 构建 client
    public static final OkHttpClient client = new OkHttpClient.Builder()
            //* 超时时间
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)

            //* 验证
            // 信任所有 host (不推荐)
            .hostnameVerifier((hostName, session) -> true)
            // 信任指定 host
            // .hostnameVerifier(new StandardHostnameVerifier(trustedHosts))
            // 信任所有证书 (不推荐)
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())

            // 默认允许重定向
            // .followRedirects(true)

            .build();

    /**
     * post 访问, <b>Json Body</b>
     *
     * @param url  接口 url
     * @param json RequestBody
     * @return (String) url 返回结果
     */
    public static String postJson(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();

        return getStrResponse(request, url);
    }

    /**
     * post 访问, 自定义 Headers, <b>Json Body</b>
     *
     * @param url     接口 url
     * @param headers headers
     * @param json    RequestBody
     * @return (String) url 返回结果
     */
    public static String postJsonWithHeaders(String url, Headers headers, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).headers(headers).post(body).build();

        return getStrResponse(request, url);
    }

    /**
     * 信任指定 host
     *
     * @see OkHttpUtil#trustedHosts
     */
    private static class StandardHostnameVerifier implements HostnameVerifier {

        private final HashSet<String> trustedHosts;

        public StandardHostnameVerifier(HashSet<String> trustedHosts) {
            this.trustedHosts = trustedHosts;
        }

        /** 判断 host 是否在 {@link OkHttpUtil#trustedHosts} 中 */
        @Override
        public boolean verify(String hostname, SSLSession session) {
            if (trustedHosts.contains(hostname)) {
                return true;
            } else {
                HostnameVerifier verifier = HttpsURLConnection.getDefaultHostnameVerifier();
                return verifier.verify(hostname, session);
            }
        }
    }

    /** 信任所有证书 (不推荐) */
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    /** 信任所有证书 (不推荐) */
    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /** 返回 url 访问结果 (String) */
    private static String getStrResponse(Request request, String url) {
        try (Response response = client.newCall(request).execute()) {
            return response.body() == null ? null : response.body().string();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("OkHttp 访问失败: " + url);
        }
    }
}

