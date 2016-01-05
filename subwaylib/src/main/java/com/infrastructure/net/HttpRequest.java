package com.infrastructure.net;

import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.infrastructure.cache.CacheManager;
import com.infrastructure.utils.BaseConstants;
import com.infrastructure.utils.BaseUtils;
import com.infrastructure.utils.UtilsLog;

import org.apache.http.Header;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;


/**
 * Created by user on 2016/1/4.
 */
public class HttpRequest implements Runnable {
    public static final int TIME_OUT_MILLISECOND = 30000;

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_ERROR_COOKIE_EXPIRED = 1;
    public static final int RESPONSE_ERROR_NETWORK_ANOMALIES = -1;

    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    // 区分get还是post的枚举
    public static final String REQUEST_GET = "get";
    public static final String REQUEST_POST = "post";

    private HttpUriRequest mRequest = null;
    private HttpResponse mResponse = null;
    private URLData mUrlData = null;
    private RequestCallback mRequestCallback = null;
    private List<RequestParameter> mParameters = null;
    private String mOriginalUrl = null;// 原始url
    private String mNewUrl = null;// 拼接key-value后的url
    private DefaultHttpClient mHttpClient;

    protected Handler mHandler;// 切换回UI线程
    protected boolean mCacheRequestData = true;

    HashMap<String, String> mHeaders;
    static long mDeltaBetweenServerAndClientTime;// 服务器时间和客户端时间的差值

    public HttpRequest(final URLData data, final List<RequestParameter> params, final RequestCallback callback) {
        mUrlData = data;
        mOriginalUrl = mUrlData.getUrl();
        mParameters = params;
        mRequestCallback = callback;
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
        }
        mHandler = new Handler();
        mHeaders = new HashMap<>();
    }

    /**
     * 获取HttpUriRequest请求
     */
    public HttpUriRequest getRequest() {
        return mRequest;
    }


    /**
     * 获取服务器时间
     */
    public static Date getServerTime() {
        return new Date(System.currentTimeMillis() + mDeltaBetweenServerAndClientTime);
    }

    @Override
    public void run() {
        try {
            if (mUrlData.getNetType().equals(REQUEST_GET)) {
                final StringBuffer paramBuffer = new StringBuffer();
                if ((mParameters != null) && (mParameters.size() > 0)) {
                    // 对key进行排序
                    sortKeys();

                    for (final RequestParameter p : mParameters) {
                        if (paramBuffer.length() == 0) {
                            paramBuffer.append(p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
                        } else {
                            paramBuffer.append("&" + p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
                        }
                    }

                    mNewUrl = mOriginalUrl + "?" + paramBuffer.toString();
                } else {
                    mNewUrl = mOriginalUrl;
                }
                UtilsLog.d("url", mNewUrl);
                // 如果这个get的API有缓存时间
                if (mUrlData.getExpires() > 0) {
                    final String content = CacheManager.getInstance().getFileCache(mNewUrl);
                    if (content != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mRequestCallback.onSuccess(content);
                            }
                        });
                        return;
                    }
                }

                mRequest = new HttpGet(mNewUrl);
            } else if (mUrlData.getNetType().equals(REQUEST_POST)) {
                mRequest = new HttpPost(mOriginalUrl);
                // 添加参数
                if ((mParameters != null) && (mParameters.size() > 0)) {
                    final List<BasicNameValuePair> list = new ArrayList<>();
                    for (final RequestParameter p : mParameters) {
                        list.add(new BasicNameValuePair(p.getName(), p.getValue()));
                    }

                    ((HttpPost) mRequest).setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
                }
            } else {
                return;
            }

            mRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT_MILLISECOND);
            mRequest.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIME_OUT_MILLISECOND);

            // 添加必要的头信息
            setHttpHeaders(mRequest);

            // 添加Cookie到请求头中
            addCookie();

            // 发送请求
            mResponse = mHttpClient.execute(mRequest);

            // 获取状态
            final int statusCode = mResponse.getStatusLine().getStatusCode();
            // 设置回调函数，但如果requestCallback，说明不需要回调，不需要知道返回结果
            if (mRequestCallback != null) {
                if (statusCode == HttpStatus.SC_OK) {
                    // 更新服务器时间和本地时间的差值
                    updateDeltaBetweenServerAndClientTime();

                    //根据是否支持gzip来使用不同的解析方式
                    final ByteArrayOutputStream content = new ByteArrayOutputStream();
                    String strResponse = "";
                    if ((mResponse.getEntity().getContentEncoding() != null) && (mResponse.getEntity().getContentEncoding().getValue() != null)) {
                        if (mResponse.getEntity().getContentEncoding().getValue().contains("gzip")) {
                            final InputStream in = mResponse.getEntity().getContent();
                            final InputStream is = new GZIPInputStream(in);
                            strResponse = InputStream2String(is);
                            is.close();
                        } else {
                            mResponse.getEntity().writeTo(content);
                            strResponse = new String(content.toByteArray()).trim();
                        }
                    } else {
                        mResponse.getEntity().writeTo(content);
                        strResponse = new String(content.toByteArray()).trim();
                    }

                    UtilsLog.d("url", "Response = " + strResponse);
                    final Response responseInJson = JSON.parseObject(strResponse, Response.class);
                    UtilsLog.d("url", responseInJson);
                    if (responseInJson.isError()) {
                        if (responseInJson.getErrorType() == RESPONSE_ERROR_COOKIE_EXPIRED) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRequestCallback.onCookieExpired();
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRequestCallback.onFail(responseInJson.getErrorMessage());
                                }
                            });
                        }
                    } else {
                        // 把成功获取到的数据记录到缓存
                        if (mUrlData.getNetType().equals(REQUEST_GET) && mUrlData.getExpires() > 0) {
                            CacheManager.getInstance().putFileCache(mNewUrl, responseInJson.getResult(), mUrlData.getExpires());
                        }

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mRequestCallback.onSuccess(responseInJson.getResult());
                            }
                        });

                        // 保存Cookie
                        saveCookie();
                    }
                } else {
                    handleNetworkError("网络异常");
                }
            } else {
                handleNetworkError("网络异常");
            }
        } catch (final IllegalArgumentException e) {
            handleNetworkError("网络异常");
        } catch (final UnsupportedEncodingException e) {
            handleNetworkError("网络异常");
        } catch (final IOException e) {
            handleNetworkError("网络异常");
        }
    }

    /**
     * 处理网络异常
     */
    public void handleNetworkError(final String errorMsg) {
        if (mRequestCallback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mRequestCallback.onFail(errorMsg);
                }
            });
        }
    }

    /**
     * InputStream转换为String
     */
    static String InputStream2String(final InputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * key值排序
     */
    void sortKeys() {
        int size = mParameters.size();
        for (int i = 1; i < size; i++) {
            for (int j = i; j > 0; j--) {
                RequestParameter p1 = mParameters.get(j - 1);
                RequestParameter p2 = mParameters.get(j);
                if (p1.getName().compareToIgnoreCase(p2.getName()) > 0) {
                    final String name = p1.getName();
                    final String value = p1.getValue();

                    p1.setName(p2.getName());
                    p1.setValue(p2.getValue());

                    p2.setName(name);
                    p2.setValue(value);
                }
            }
        }
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

    /**
     * 添加头信息
     */
    void setHttpHeaders(final HttpUriRequest httpHeaders) {
        mHeaders.clear();
        mHeaders.put(ACCEPT_CHARSET, "UTF-8,*");
        mHeaders.put(USER_AGENT, "Subway Android App");
        mHeaders.put(ACCEPT_ENCODING, "gzip");

        if ((httpHeaders != null) && (mHeaders != null)) {
            for (final Map.Entry<String, String> entry : mHeaders.entrySet()) {
                if (entry.getKey() != null) {
                    httpHeaders.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * 更新服务器时间和本地时间的差值
     */
    void updateDeltaBetweenServerAndClientTime() {
        if (mResponse != null) {
            final Header header = mResponse.getLastHeader("Date");
            if (header != null) {
                final String strServerDate = header.getValue();
                try {
                    if (!TextUtils.isEmpty(strServerDate)) {
                        final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                        Date serverDateUAT = sdf.parse(strServerDate);
                        mDeltaBetweenServerAndClientTime = serverDateUAT.getTime() + 8 * 60 * 60 * 1000 - System.currentTimeMillis();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
