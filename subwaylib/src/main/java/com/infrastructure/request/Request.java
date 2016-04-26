package com.infrastructure.request;

import android.os.Handler;

import com.infrastructure.util.BaseUtil;
import com.infrastructure.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 网络请求基类
 */
public abstract class Request implements Runnable {
    public static final int TIME_OUT_MILLISECOND = 30 * 1000;// 连接超时时间
    public static long DELTA_BETWEEN_SERVER_AND_CLIENT_TIME = 0;// 服务器时间和客户端时间的差值

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_ERROR_COOKIE_EXPIRED = 1;
    public static final int RESPONSE_ERROR_NETWORK_ANOMALIES = -1;

    protected static final String ACCEPT_CHARSET = "Accept-Charset";
    protected static final String USER_AGENT = "User-Agent";
    protected static final String ACCEPT_ENCODING = "Accept-Encoding";

    protected static final String REQUEST_GET = "GET";
    protected static final String REQUEST_POST = "POST";

    protected static final char NAME_VALUE_SEPARATOR = '=';
    protected static final char FIELD_SEPARATOR = '&';
    protected static final char HOST_PARAMS_SEPARATOR = '?';

    protected Handler mHandler;
    protected String mUrl;
    protected String mNetType;
    protected long mExpires;
    protected List<RequestParameter> mParameters = null;
    protected RequestCallback mCallback = null;

    public Request(final URLData urlData, final List<RequestParameter> params, final RequestCallback callback) {
        mUrl = urlData.getUrl();
        mNetType = urlData.getNetType();
        mExpires = urlData.getExpires();
        mParameters = params;
        mCallback = callback;
        mHandler = new Handler();
    }

    @Override
    public void run() {
        if (REQUEST_GET.equals(mNetType.toUpperCase())) {
            doGet();
        } else if (REQUEST_POST.equals(mNetType.toUpperCase())) {
            doPost();
        } else {
            throw new IllegalArgumentException("NetType is " + mNetType);
        }
        LogUtil.d(LogUtil.TAG_URL, mUrl);
    }

    /**
     * Get方式请求
     */
    protected abstract void doGet();

    /**
     * Post方式请求
     */
    protected abstract void doPost();

    /**
     * 终止请求
     */
    protected abstract void abort();

    /**
     * Get请求添加Url参数
     *
     * @return 拼接的参数
     */
    protected String formatRequestParams() {
        StringBuilder param = new StringBuilder();
        for (final RequestParameter p : mParameters) {
            if (param.length() == 0) {
                param.append(p.getName());
                param.append(NAME_VALUE_SEPARATOR);
                param.append(BaseUtil.UrlEncodeUnicode(p.getValue()));
            } else {
                param.append(FIELD_SEPARATOR);
                param.append(p.getName());
                param.append(NAME_VALUE_SEPARATOR);
                param.append(BaseUtil.UrlEncodeUnicode(p.getValue()));
            }
        }
        return param.toString();
    }

    /**
     * Request 成功回调
     *
     * @param content 返回的内容
     */
    protected void handleSuccess(final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(LogUtil.TAG_URL, "onSuccess{" + content + "}");
                mCallback.onSuccess(content);
            }
        });
    }

    /**
     * Request 失败回调
     *
     * @param msg 错误提示
     */
    protected void handleFail(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(LogUtil.TAG_URL, "onFail{" + msg + "}");
                mCallback.onFail(msg);
            }
        });
    }

    /**
     * Request Cookie失效回调
     */
    protected void handleCookieExpired() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(LogUtil.TAG_URL, "onCookieExpired{}");
                mCallback.onCookieExpired();
            }
        });
    }

    /**
     * 更新服务器时间和本地时间的差值
     *
     * @param serverDate 服务器时间
     */
    protected void updateDeltaBetweenServerAndClientTime(String serverDate) {
        try {
            if (!BaseUtil.IsStringEmpty(serverDate)) {
                final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                Date serverDateUAT = sdf.parse(serverDate);
                DELTA_BETWEEN_SERVER_AND_CLIENT_TIME = serverDateUAT.getTime() + 8 * 60 * 60 * 1000 - System.currentTimeMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
