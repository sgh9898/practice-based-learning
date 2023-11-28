package com.demo.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Http(s) 连接工具
 * 1. static 方法默认使用 TLSv1.2 协议
 * 2. 出现 handshake_failure 时需要切换 TLS 协议版本
 *
 * @author Song gh on 2023/11/21.
 */
@Slf4j
public class OkHttpUtils {

    // 基本配置
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType APPLICATION_FORM_URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

// ------------------------------ 参数 ------------------------------

    // 超时时间(秒)
    private static final int CONNECT_TIME_OUT = 6;
    private static final int READ_TIME_OUT = 6;
    private static final int WRITE_TIME_OUT = 6;
    // 信任的 host, 仅在需要时配置
    private static final HashSet<String> trustedHosts = null;

    // 构建默认 client, 协议版本 TLSv1.2
    private static final OkHttpClient defaultClientTLS12 = new OkHttpClient.Builder()
            // 超时时间
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)

//            // 信任指定 host (替换"信任所有 host")
//            .hostnameVerifier(new StandardHostnameVerifier(trustedHosts))
            // 信任所有 host
            .hostnameVerifier((hostName, session) -> true)
            // 信任所有证书, 协议版本 TLSv1.2, handshake_failure 需要考虑更换使用其他协议的 client
            .sslSocketFactory(createSSLSocketFactory(TLSVersion.TLSv12), new TrustAllCerts())
            .build();

// ------------------------------ Public Static ------------------------------

    /** post 传文件 */
    public static String postFile(String url, MultipartFile multipartFile, Map<String, Object> params) {
        File file = null;
        try {
            if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");

            // 二种：文件请求体
            MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
            file = multipartFile2File(multipartFile);
            RequestBody fileBody = RequestBody.create(type, file);


            // 三种：混合参数和文件请求
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.ALTERNATIVE)
                    .addFormDataPart("file", multipartFile.getOriginalFilename(), fileBody);

            for (Entry<String, Object> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), (String) entry.getValue());
            }
            RequestBody multipartBody = builder.build();

            Request request = new Request.Builder().url(url).post(multipartBody)//传参数、文件或者混合，改一下就行请求体就行
                    .build();
            return getStrResponse(request, url);
        } finally {
            if (file != null) file.delete();
        }
    }

    /** get 访问 */
    public static String get(String url) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        Request request = new Request.Builder().url(url).build();
        return getStrResponse(request, url);
    }

    /** get 访问, 可切换协议版本 */
    public static String getTLS(String url) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        Request request = new Request.Builder().url(url).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 json */
    public static String postJson(String url, String bodyJsonStr) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        RequestBody body = RequestBody.create(JSON, bodyJsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 json, 可切换协议版本 */
    public static String postJsonTLS(String url, String bodyJsonStr) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        RequestBody body = RequestBody.create(JSON, bodyJsonStr);
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 json, 自定义 Headers */
    public static String postJsonWithHeaders(String url, Headers headers, String bodyJsonStr) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        RequestBody body = RequestBody.create(JSON, bodyJsonStr);
        Request request = new Request.Builder().url(url).headers(headers).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 json, 自定义 Headers, 可切换协议版本 */
    public static String postJsonWithHeadersTLS(String url, Headers headers, String bodyJsonStr) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        RequestBody body = RequestBody.create(JSON, bodyJsonStr);
        Request request = new Request.Builder().url(url).headers(headers).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 form (参数使用 UTF-8 编码) */
    public static String postForm(String url, Map<String, Object> params) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
        RequestBody body = RequestBody.create(APPLICATION_FORM_URLENCODED, Objects.requireNonNull(encodeValue(params, "UTF-8")));
        Request request = new Request.Builder().url(url).post(body).build();
        return getStrResponse(request, url);
    }

    /** post 访问, 提交 form (参数使用 UTF-8 编码) */
    public static String postFormTLS(String url, Map<String, Object> params, TLSVersion tlsVersion) {
        if (StringUtils.isBlank(url)) throw new RuntimeException("url不能为空");
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

    /** 返回 url 访问结果 (String), 协议版本 TLSv1.2 */
    private static String getStrResponse(Request request, String url) {
        try (Response response = defaultClientTLS12.newCall(request).execute()) {
            return response.body() == null ? null : response.body().string();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("OkHttp 访问失败, url: " + url);
        }
    }

// ------------------------------ Private ------------------------------

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

    /**
     * 信任所有证书
     *
     * @param tlsVersion 协议版本, 默认为 TLSv1.2
     */
    private static SSLSocketFactory createSSLSocketFactory(TLSVersion tlsVersion) {
        SSLSocketFactory ssfFactory;
        String tlsVersionStr = "TLS";
        try {
            // 切换协议版本
            if (tlsVersion == TLSVersion.TLSv13) {
                tlsVersionStr = "TLSv1.3";
            }
            SSLContext sslContext = SSLContext.getInstance(tlsVersionStr);
            sslContext.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException("不支持 " + tlsVersionStr + " 协议");
        }
        return ssfFactory;
    }

    private static File multipartFile2File(MultipartFile multipartFile) {
        String path = "." + File.separator + multipartFile.getOriginalFilename();
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // 底层也是通过io流写入文件file
            FileCopyUtils.copy(multipartFile.getBytes(), file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    /** TLS 协议版本 */
    private enum TLSVersion {
        TLSv12, TLSv13
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
