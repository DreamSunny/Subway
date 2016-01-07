package com.infrastructure.net;

import android.os.Handler;
import android.text.TextUtils;

import com.infrastructure.cache.CacheManager;
import com.infrastructure.utils.BaseUtils;
import com.infrastructure.utils.UtilsLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by user on 2016/1/6.
 */
public abstract class Request implements Runnable {
    public static final int TIME_OUT_MILLISECOND = 30 * 1000;

    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_ERROR_COOKIE_EXPIRED = 1;
    public static final int RESPONSE_ERROR_NETWORK_ANOMALIES = -1;

    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    public static final String REQUEST_GET = "get";
    public static final String REQUEST_POST = "post";

    public static long DELTA_BETWEEN_SERVER_AND_CLIENT_TIME = 0;// 服务器时间和客户端时间的差值

    protected Handler mHandler;
    protected String mUrl;
    protected String mNetType;
    protected long mExpires;
    protected List<RequestParameter> mParameters = null;
    protected RequestCallback mCallback = null;

    public Request(final URLData urlData) {
        this(urlData, null, null);
    }

    public Request(final URLData urlData, final List<RequestParameter> params) {
        this(urlData, params, null);
    }

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
        if (REQUEST_GET.equals(mNetType)) {
            if ((mParameters != null) && (mParameters.size() > 0)) {
                addUrlParams();
            }
            String strCacheContent = null;
            if (mExpires > 0) {
                strCacheContent = CacheManager.getInstance().getFileCache(mUrl);
            }
            if (!TextUtils.isEmpty(strCacheContent)) {
                handleSuccess(strCacheContent);
            } else {
                try {
                    doGet();
                } catch (Exception e) {
                    handleFail("网络异常");
                }
            }
        } else if (REQUEST_POST.equals(mNetType)) {
            try {
                doPost();
            } catch (Exception e) {
                handleFail("网络异常");
            }
        } else {
            throw new IllegalArgumentException("mNetType = " + mNetType);
        }
    }

    protected abstract void doGet() throws Exception;

    protected abstract void doPost() throws Exception;

    protected abstract void abort();

    /**
     * Get请求添加Url参数
     */
    private void addUrlParams() {
        StringBuilder param = new StringBuilder();
        for (final RequestParameter p : mParameters) {
            if (param.length() == 0) {
                param.append("?");
                param.append(p.getName());
                param.append("=");
                param.append(BaseUtils.UrlEncodeUnicode(p.getValue()));
            } else {
                param.append("&");
                param.append(p.getName());
                param.append("=");
                param.append(BaseUtils.UrlEncodeUnicode(p.getValue()));
            }
        }
        mUrl = mUrl + param.toString();
        UtilsLog.d(UtilsLog.TAG_URL, mUrl);
    }

    /**
     * Request 成功回调
     */
    protected void handleSuccess(final String content) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UtilsLog.d(UtilsLog.TAG_URL, "onSuccess{" + content + "}");
                mCallback.onSuccess(content);
            }
        });
    }

    /**
     * Request 失败回调
     */
    protected void handleFail(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                UtilsLog.d(UtilsLog.TAG_URL, "onFail{" + msg + "}");
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
                UtilsLog.d(UtilsLog.TAG_URL, "onCookieExpired{}");
                mCallback.onCookieExpired();
            }
        });
    }

    /**
     * 更新服务器时间和本地时间的差值
     */
    protected void updateDeltaBetweenServerAndClientTime(String serverDate) {
        try {
            if (!TextUtils.isEmpty(serverDate)) {
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
