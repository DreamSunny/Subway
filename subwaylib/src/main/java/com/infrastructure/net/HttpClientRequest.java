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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;


/**
 * 封装HttpClient网络请求
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
        if ((mParameters != null) && (mParameters.size() > 0)) {
            mUrl = mUrl + HOST_PARAMS_SEPARATOR + formatRequestParams();
        }
        String strCacheContent = null;
        if (mExpires > 0) {
            strCacheContent = CacheManager.getInstance().getFileCache(mUrl);
        }
        if (!BaseUtils.IsStringEmpty(strCacheContent)) {
            handleSuccess(strCacheContent);
        } else {
            mRequest = new HttpGet(mUrl);
            // 添加Http参数
            setHttpParams();
            // 添加头信息
            addHttpHeaders();
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
                    String strResponse;
                    if (mResponse.getEntity().getContentEncoding() != null
                            && mResponse.getEntity().getContentEncoding().getValue() != null
                            && mResponse.getEntity().getContentEncoding().getValue().contains("gzip")) {
                        final InputStream is = mResponse.getEntity().getContent();
                        final InputStream gzip = new GZIPInputStream(is);
                        strResponse = BaseUtils.InputStream2String(gzip);
                        gzip.close();
                        is.close();
                    } else {
                        strResponse = EntityUtils.toString(mResponse.getEntity(), "UTF-8");
                    }
                    // 处理返回信息
                    doResponse(strResponse);
                }
            } else {
                handleFail("网络异常");
            }
        }
    }

    @Override
    protected void doPost() throws Exception {
        mRequest = new HttpPost(mUrl);
        // 添加传递参数
        if (!BaseUtils.IsListEmpty(mParameters)) {
            final List<BasicNameValuePair> list = new ArrayList<>();
            for (final RequestParameter p : mParameters) {
                list.add(new BasicNameValuePair(p.getName(), p.getValue()));
            }
            ((HttpPost) mRequest).setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
        }
        // 添加Http参数
        setHttpParams();
        // 添加头信息
        addHttpHeaders();
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
                String strResponse;
                if (mResponse.getEntity().getContentEncoding() != null
                        && mResponse.getEntity().getContentEncoding().getValue() != null
                        && mResponse.getEntity().getContentEncoding().getValue().contains("gzip")) {
                    final InputStream is = mResponse.getEntity().getContent();
                    final InputStream gzip = new GZIPInputStream(is);
                    strResponse = BaseUtils.InputStream2String(gzip);
                    gzip.close();
                    is.close();
                } else {
                    strResponse = EntityUtils.toString(mResponse.getEntity(), "UTF-8");
                }
                // 处理返回信息
                doResponse(strResponse);
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
     * 处理返回信息
     *
     * @param content 请求返回的内容
     */
    protected void doResponse(String content) {
        UtilsLog.d(UtilsLog.TAG_URL, "Entity{" + content + "}");
        final Response responseInJson = JSON.parseObject(content, Response.class);
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
     * 添加参数
     */
    private void setHttpParams() {
        mRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT_MILLISECOND);
        mRequest.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT_MILLISECOND);
    }

    /**
     * 添加头信息
     */
    private void addHttpHeaders() {
        mRequest.addHeader(ACCEPT_CHARSET, "UTF-8,*");
        mRequest.addHeader(USER_AGENT, "Subway Android App");
        mRequest.addHeader(ACCEPT_ENCODING, "gzip");
    }

    /**
     * 将cookie列表保存到本地
     */
    private synchronized void saveCookie() {
        // 获取本次访问的cookie
        final List<Cookie> cookies = mHttpClient.getCookieStore().getCookies();
        // 将普通cookie转换为可序列化的cookie
        List<SerializableCookie> serializableCookies = null;

        if (!BaseUtils.IsListEmpty(cookies)) {
            serializableCookies = new ArrayList<>();
            for (final Cookie cookie : cookies) {
                serializableCookies.add(new SerializableCookie(cookie));
            }
        }

        BaseUtils.SaveObject(BaseConstants.COOKIE_CACHE_PATH, serializableCookies);
    }

    /**
     * 向请求中添加cookie
     */
    private void addCookie() {
        List<SerializableCookie> cookieList = null;
        Object cookieObj = BaseUtils.RestoreObject(BaseConstants.COOKIE_CACHE_PATH);
        if (cookieObj != null) {
            cookieList = (ArrayList<SerializableCookie>) cookieObj;
        }

        if (!BaseUtils.IsListEmpty(cookieList)) {
            final BasicCookieStore cs = new BasicCookieStore();
            cs.addCookies(cookieList.toArray(new Cookie[]{}));
            mHttpClient.setCookieStore(cs);
        } else {
            mHttpClient.setCookieStore(null);
        }
    }
}
