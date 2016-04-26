package com.infrastructure.request;

import com.alibaba.fastjson.JSON;
import com.infrastructure.cache.CacheManager;
import com.infrastructure.commom.BaseConstants;
import com.infrastructure.util.BaseUtil;
import com.infrastructure.util.LogUtil;

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

import java.io.IOException;
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
    protected void doGet() {
        if ((mParameters != null) && (mParameters.size() > 0)) {
            mUrl = mUrl + HOST_PARAMS_SEPARATOR + formatRequestParams();
        }
        String strCacheContent = null;
        if (mExpires > 0) {
            strCacheContent = CacheManager.getInstance().getFileCache(mUrl);
        }
        if (!BaseUtil.IsStringEmpty(strCacheContent)) {
            handleSuccess(strCacheContent);
        } else {
            InputStream is = null;
            InputStream gzip = null;
            try {
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
                            is = mResponse.getEntity().getContent();
                            gzip = new GZIPInputStream(is);
                            strResponse = BaseUtil.InputStream2String(gzip);
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
            } catch (IOException e) {
                handleFail("网络异常");
            } finally {
                BaseUtil.closeStream(is);
                BaseUtil.closeStream(gzip);
            }
        }
    }

    @Override
    protected void doPost() {
        InputStream is = null;
        InputStream gzip = null;
        try {
            mRequest = new HttpPost(mUrl);
            // 添加传递参数
            if (!BaseUtil.IsListEmpty(mParameters)) {
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
                        is = mResponse.getEntity().getContent();
                        gzip = new GZIPInputStream(is);
                        strResponse = BaseUtil.InputStream2String(gzip);
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
        } catch (IOException e) {
            handleFail("网络异常");
        } finally {
            BaseUtil.closeStream(is);
            BaseUtil.closeStream(gzip);
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
        LogUtil.d(LogUtil.TAG_URL, "Entity{" + content + "}");
        final Response responseInJson = JSON.parseObject(content, Response.class);
        LogUtil.d(LogUtil.TAG_URL, responseInJson);
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

        if (!BaseUtil.IsListEmpty(cookies)) {
            serializableCookies = new ArrayList<>();
            for (final Cookie cookie : cookies) {
                serializableCookies.add(new SerializableCookie(cookie));
            }
        }

        BaseUtil.SaveObject(BaseConstants.COOKIE_CACHE_PATH, serializableCookies);
    }

    /**
     * 向请求中添加cookie
     */
    private void addCookie() {
        List<SerializableCookie> cookieList = null;
        Object cookieObj = BaseUtil.RestoreObject(BaseConstants.COOKIE_CACHE_PATH);
        if (cookieObj != null) {
            cookieList = (ArrayList<SerializableCookie>) cookieObj;
        }

        if (!BaseUtil.IsListEmpty(cookieList)) {
            final BasicCookieStore cs = new BasicCookieStore();
            cs.addCookies(cookieList.toArray(new Cookie[]{}));
            mHttpClient.setCookieStore(cs);
        } else {
            mHttpClient.setCookieStore(null);
        }
    }
}
