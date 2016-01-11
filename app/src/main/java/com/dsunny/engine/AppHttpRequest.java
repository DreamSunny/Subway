package com.dsunny.engine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dsunny.mockdata.MockBaseInfo;
import com.infrastructure.activity.BaseActivity;
import com.infrastructure.net.Request;
import com.infrastructure.net.RequestCallback;
import com.infrastructure.net.RequestParameter;
import com.infrastructure.net.RequestThreadPool;
import com.infrastructure.net.Response;
import com.infrastructure.net.URLData;
import com.infrastructure.net.UrlConfigManager;
import com.infrastructure.utils.UtilsLog;

import java.util.List;

/**
 * 本地请求管理
 */
public class AppHttpRequest {
    private static AppHttpRequest instance = null;

    private AppHttpRequest() {
    }

    /**
     * 获取请求管理实例
     *
     * @return 请求管理实例
     */
    public static synchronized AppHttpRequest getInstance() {
        if (instance == null) {
            instance = new AppHttpRequest();
        }
        return instance;
    }

    /**
     * 发起request请求，默认不强制更新
     *
     * @param activity 当前Activity
     * @param apiKey   请求url标识
     * @param params   请求参数
     * @param callback 请求回调
     */
    public void performRequest(final BaseActivity activity, final String apiKey, final List<RequestParameter> params, final RequestCallback callback) {
        performRequest(activity, apiKey, params, callback, false);
    }

    /**
     * 发起request请求，可强制更新
     *
     * @param activity    当前Activity
     * @param apiKey      请求url标识
     * @param params      请求参数
     * @param callback    请求回调
     * @param forceUpdate 是否强制更新
     */
    public void performRequest(final BaseActivity activity, final String apiKey, final List<RequestParameter> params, final RequestCallback callback, boolean forceUpdate) {
        final URLData urlData = UrlConfigManager.findURL(activity, apiKey);
        if (urlData != null) {
            if (forceUpdate) {
                urlData.setExpires(0);
            }
            if (!TextUtils.isEmpty(urlData.getMockClass())) {
                try {
                    MockBaseInfo mockInfo = (MockBaseInfo) Class.forName(urlData.getMockClass()).newInstance();
                    String strResponse = mockInfo.getJsonData();
                    final Response responseInJson = JSON.parseObject(strResponse, Response.class);
                    UtilsLog.d(responseInJson);
                    if (callback != null) {
                        if (responseInJson.isError()) {
                            callback.onFail(responseInJson.getErrorMessage());
                        } else {
                            callback.onSuccess(responseInJson.getResult());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Request request = activity.getRequestManager().createRequest(urlData, params, callback);
                RequestThreadPool.getInstance().execute(request);
            }
        } else {
            throw new IllegalArgumentException("urlData is null!");
        }
    }
}
