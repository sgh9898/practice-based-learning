package com.demo.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * Http(s) 连接工具 (OkHttp)
 *
 * @author Song gh on 2022/1/18.
 */
@Slf4j
public abstract class OkHttpUtils {

    // 基本配置
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType APPLICATION_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");
    // ------------------------------ 参数 ------------------------------
    // 超时时间
    public static final int CONNECT_TIME_OUT = 60;
    public static final int READ_TIME_OUT = 60;
    public static final int WRITE_TIME_OUT = 60;
    public static final ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_3)
            .cipherSuites(
                    CipherSuite.TLS_AES_256_GCM_SHA384,
                    CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
            .build();
    // 构建 client
    public static final OkHttpClient client = new OkHttpClient.Builder()
            //* 超时时间
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
            .connectionSpecs(Collections.singletonList(spec))


            //* 验证
//            // 信任指定 host (替换"信任所有 host")
//             .hostnameVerifier(new StandardHostnameVerifier(trustedHosts))
            // 信任所有 host
            .hostnameVerifier((hostName, session) -> true)
            // 信任所有证书
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
            .build();
    // 信任的 host
    private static HashSet<String> trustedHosts;

// ------------------------------ Public ------------------------------

    /**
     * get 访问
     *
     * @param url 接口 url
     */
    public static String get(String url) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        Request request = new Request.Builder().url(url).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交 json
     *
     * @param url  接口 url
     * @param json 参数, json string 格式
     */
    public static String postJson(String url, String json) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url不能为空");
        }
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交 json, 自定义 Headers
     *
     * @param url     接口 url
     * @param headers headers
     * @param json    RequestBody
     */
    public static String postJsonWithHeaders(String url, Headers headers, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).headers(headers).post(body).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交 form
     *
     * @param url  接口 url
     * @param form 参数, 如: param1=value1&param2=value2
     */
    public static String postForm(String url, String form) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url不能为空");
        }
        RequestBody body = RequestBody.create(APPLICATION_FORM_URLENCODED, form);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交 form (参数使用 UTF-8 编码)
     *
     * @param url    接口 url
     * @param params 参数
     */
    public static String postForm(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url不能为空");
        }
        RequestBody body = RequestBody.create(APPLICATION_FORM_URLENCODED, Objects.requireNonNull(encodeValue(params, "UTF-8")));
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /**
     * post 访问, 提交 form (参数使用指定字符集编码)
     *
     * @param url    接口 url
     * @param params 参数
     * @param encode 字符集, {@link java.nio.charset.StandardCharsets}
     */
    public static String postForm(String url, LinkedHashMap<String, Object> params, String encode) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        RequestBody body = RequestBody.create(APPLICATION_FORM_URLENCODED, encodeValue(params, encode));
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

// ------------------------------ Private ------------------------------

    /** 返回 url 访问结果 (String) */
    private static String getStrResponse(Request request, String url) {
        try (Response response = client.newCall(request).execute()) {
            return response.body() == null ? null : response.body().string();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("OkHttp 访问失败: " + url);
        }
    }

    /**
     * 将参数值进行编码
     *
     * @param params 参数
     * @param encode 字符集, {@link java.nio.charset.StandardCharsets}
     * @return like  a=v1&b=v2
     */
    private static String encodeValue(Map<String, Object> params, String encode) {
        if (MapUtils.isNotEmpty(params)) {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                sb.append(key).append("=");
                if (value instanceof String) {
                    try {
                        sb.append(URLEncoder.encode((String) value, encode)).append("&");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("encode失败：" + key + "=" + value, e);
                    }
                } else {
                    sb.append(value).append("&");
                }
            }
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }

    /** 信任所有证书 */
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

    /**
     * 信任指定 host
     *
     * @see OkHttpUtils#trustedHosts
     */
    private static class StandardHostnameVerifier implements HostnameVerifier {

        private final HashSet<String> trustedHosts;

        public StandardHostnameVerifier(HashSet<String> trustedHosts) {
            this.trustedHosts = trustedHosts;
        }

        /** 判断 host 是否在 {@link #trustedHosts} 中 */
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

    /** 信任所有证书 */
    private static class TrustAllCerts implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
