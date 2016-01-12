package com.infrastructure.net;

import android.text.TextUtils;

import com.infrastructure.cache.CacheManager;
import com.infrastructure.utils.BaseConstants;
import com.infrastructure.utils.BaseUtils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 封装HttpURLConnection网络请求
 */
public class HurlRequest extends Request {
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private HttpURLConnection urlConn;

    public HurlRequest(final URLData urlData, final List<RequestParameter> params, final RequestCallback callback) {
        super(urlData, params, callback);
    }

    @Override
    protected void doGet() throws Exception {
        if (!BaseUtils.IsListEmpty(mParameters)) {
            mUrl = mUrl + HOST_PARAMS_SEPARATOR + formatRequestParams();
        }
        String cacheContent = null;
        if (mExpires > 0) {
            cacheContent = CacheManager.getInstance().getFileCache(mUrl);
        }
        if (!BaseUtils.IsStringEmpty(cacheContent)) {
            handleSuccess(cacheContent);
        } else {
            // 打开一个HttpURLConnection连接
            openConnection();
            // 添加Coocie
            addCoocie();
            // 添加头部信息
            addRequestProperties();
            // 添加连接参数
            setConnectionParametersForRequest(REQUEST_GET);
            // 开始连接
            urlConn.connect();
            // 判断请求是否成功
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // 保存Coocie
                storeCookie();
                // 获取返回的数据
                InputStream is = urlConn.getInputStream();
                String response = BaseUtils.InputStream2String(is);
                is.close();
                // 把成功获取到的数据记录到缓存
                if (mExpires > 0) {
                    CacheManager.getInstance().putFileCache(mUrl, response, mExpires);
                }
                // 处理返回信息
                doResponse(response);
            } else {
                handleFail("网络异常");
            }
            urlConn.disconnect();
        }
    }

    @Override
    protected void doPost() throws Exception {
        // 打开一个HttpURLConnection连接
        openConnection();
        // 添加Coocie
        addCoocie();
        // 添加头部信息
        addRequestProperties();
        // 添加连接参数
        setConnectionParametersForRequest(REQUEST_POST);
        // 开始连接
        urlConn.connect();
        // 发送请求参数
        if (!BaseUtils.IsListEmpty(mParameters)) {
            DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
            byte[] postData = URLEncoder.encode(formatRequestParams(), "UTF-8").getBytes();
            dos.write(postData);
            dos.flush();
            dos.close();
        }
        // 判断请求是否成功
        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // 保存Coocie
            storeCookie();
            // 获取返回的数据
            InputStream is = urlConn.getInputStream();
            String response = BaseUtils.InputStream2String(is);
            is.close();
            // 处理返回信息
            doResponse(response);
        } else {
            handleFail("网络异常");
        }
        urlConn.disconnect();
    }

    @Override
    protected void abort() {
        if (urlConn != null) {
            urlConn.disconnect();
        }
    }

    /**
     * 处理返回信息
     *
     * @param content 请求返回的内容
     */
    protected void doResponse(String content) {
        handleSuccess(content);
    }

    /**
     * 打开连接
     */
    private void openConnection() throws Exception {
        final URL url = new URL(mUrl);
        urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(TIME_OUT_MILLISECOND);
        urlConn.setReadTimeout(TIME_OUT_MILLISECOND);
        urlConn.setUseCaches(false);
        urlConn.setDoInput(true);
    }

    /**
     * 添加头部信息
     */
    private void addRequestProperties() {
        urlConn.addRequestProperty(ACCEPT_CHARSET, "UTF-8,*");
        urlConn.addRequestProperty(USER_AGENT, "Subway Android App");
        // urlConn.addRequestProperty(ACCEPT_ENCODING, "gzip");
    }

    /**
     * 添加连接参数
     */
    private void setConnectionParametersForRequest(String type) throws Exception {
        if (REQUEST_GET.equals(type)) {
            urlConn.setRequestMethod(REQUEST_GET);
        } else if (REQUEST_POST.equals(type)) {
            urlConn.setRequestMethod(REQUEST_POST);
            urlConn.setDoOutput(true);
            urlConn.setChunkedStreamingMode(0);
            //urlConn.addRequestProperty(HEADER_CONTENT_TYPE, "application/x-www-form-urlencode");
        }
    }

    /**
     * 添加Coocie
     */
    private void addCoocie() {
        String strCookie = restoreCoocie();
        if (!BaseUtils.IsStringEmpty(strCookie)) {
            urlConn.setRequestProperty(COOKIE, strCookie);
        }
    }


    /**
     * 将cookie保存到本地
     */
    private synchronized void storeCookie() {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        Map<String, List<String>> headerFields = urlConn.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(SET_COOKIE);
        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            BaseUtils.SaveObject(BaseConstants.COOKIE_CACHE_PATH, TextUtils.join(";", cookieManager.getCookieStore().getCookies()));
        } else {
            BaseUtils.SaveObject(BaseConstants.COOKIE_CACHE_PATH, "");
        }
    }

    /**
     * 从本地获取cookie列表
     */
    private String restoreCoocie() {
        Object object = BaseUtils.RestoreObject(BaseConstants.COOKIE_CACHE_PATH);
        return object == null ? null : (String) object;
    }
}
