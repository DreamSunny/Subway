package com.infrastructure.net;

import com.alibaba.fastjson.JSON;
import com.infrastructure.cache.CacheManager;
import com.infrastructure.utils.BaseConstants;
import com.infrastructure.utils.BaseUtils;
import com.infrastructure.utils.UtilsLog;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


/**
 * Created by user on 2016/1/4.
 */
public class HttpClientRequest extends Request {
    private HttpUriRequest mRequest = null;
    private HttpResponse mResponse = null;
    private DefaultHttpClient mHttpClient;

    public HttpClientRequest(final URLData urlData, final List<RequestParameter> params, final RequestCallback callback) {
        super(urlData, params, callback);
        mHttpClient = new DefaultHttpClient();
    }

    @Override
    protected void doGet() throws Exception {
        mRequest = new HttpGet(mUrl);
        // 添加Http参数
        setHttpParams();
        // 添加头信息
        addHeaders();
        // 添加Cookie到请求头中
        addCookie();
        // 发送请求
        mResponse = mHttpClient.execute(mRequest);
        if (mResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 保存Cookie
            saveCookie();
            // 更新服务器时间和本地时间的差值
            updateDeltaBetweenServerAndClientTime(mResponse.getLastHeader("Date").getValue());
            // 处理返回内容
            if (mCallback != null) {
                dealResponse();
            }
        } else {
            handleFail("网络异常");
        }
    }

    @Override
    protected void doPost() throws Exception {
        mRequest = new HttpPost(mUrl);
        // 添加传递参数
        if ((mParameters != null) && (mParameters.size() > 0)) {
            final List<BasicNameValuePair> list = new ArrayList<>();
            for (final RequestParameter p : mParameters) {
                list.add(new BasicNameValuePair(p.getName(), p.getValue()));
            }
            ((HttpPost) mRequest).setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        }
        // 添加Http参数
        setHttpParams();
        // 添加头信息
        addHeaders();
        // 添加Cookie到请求头中
        addCookie();
        // 发送请求
        mResponse = mHttpClient.execute(mRequest);
        if (mResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            // 保存Cookie
            saveCookie();
            // 更新服务器时间和本地时间的差值
            updateDeltaBetweenServerAndClientTime(mResponse.getLastHeader("Date").getValue());
            // 处理返回内容
            if (mCallback != null) {
                dealResponse();
            }
        } else {
            handleFail("网络异常");
        }
    }

    @Override
    protected void abort() {
        if (mRequest != null) {
            try {
                mRequest.abort();
            } catch (final UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加参数
     */
    private void setHttpParams() {
        mRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT_MILLISECOND);
        mRequest.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT_MILLISECOND);
    }

    /**
     * 添加头信息
     */
    private void addHeaders() {
        mRequest.addHeader(ACCEPT_CHARSET, "UTF-8,*");
        mRequest.addHeader(USER_AGENT, "Subway Android App");
        mRequest.addHeader(ACCEPT_ENCODING, "gzip");
    }

    /**
     * 处理返回
     */
    private void dealResponse() throws IOException {
        String strResponse;
        if (mResponse.getEntity().getContentEncoding() != null
                && mResponse.getEntity().getContentEncoding().getValue() != null
                && mResponse.getEntity().getContentEncoding().getValue().contains("gzip")) {
            final InputStream in = mResponse.getEntity()
                    .getContent();
            final InputStream is = new GZIPInputStream(in);
            strResponse = InputStream2String(is);
            is.close();
        } else {
            strResponse = EntityUtils.toString(mResponse.getEntity(), "UTF-8");
        }

        final Response responseInJson = JSON.parseObject(strResponse, Response.class);
        UtilsLog.d(UtilsLog.TAG_URL, responseInJson);
        if (responseInJson.isError()) {
            if (responseInJson.getErrorType() == RESPONSE_ERROR_COOKIE_EXPIRED) {
                handleCookieExpired();
            } else {
                handleFail(responseInJson.getErrorMessage());
            }
        } else {
            handleSuccess(responseInJson.getResult());
            // 把成功获取到的数据记录到缓存
            if (REQUEST_GET.equals(mNetType) && mExpires > 0) {
                CacheManager.getInstance().putFileCache(mUrl, responseInJson.getResult(), mExpires);
            }
        }
    }

    /**
     * InputStream转换为String
     */
    static String InputStream2String(final InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


    /**
     * 将cookie列表保存到本地
     */
    synchronized void saveCookie() {
        // 获取本次访问的cookie
        final List<Cookie> cookies = mHttpClient.getCookieStore().getCookies();
        // 将普通cookie转换为可序列化的cookie
        List<SerializableCookie> serializableCookies = null;

        if ((cookies != null) && (cookies.size() > 0)) {
            serializableCookies = new ArrayList<>();
            for (final Cookie cookie : cookies) {
                serializableCookies.add(new SerializableCookie(cookie));
            }
        }

        BaseUtils.SaveObject(BaseConstants.COOKIE_CACHE_PATH, serializableCookies);
    }

    /**
     * 从本地获取cookie列表，添加到到请求头中
     */
    void addCookie() {
        List<SerializableCookie> cookieList = null;
        Object cookieObj = BaseUtils.restoreObject(BaseConstants.COOKIE_CACHE_PATH);
        if (cookieObj != null) {
            cookieList = (ArrayList<SerializableCookie>) cookieObj;
        }

        if ((cookieList != null) && (cookieList.size() > 0)) {
            final BasicCookieStore cs = new BasicCookieStore();
            cs.addCookies(cookieList.toArray(new Cookie[]{}));
            mHttpClient.setCookieStore(cs);
        } else {
            mHttpClient.setCookieStore(null);
        }
    }
}
