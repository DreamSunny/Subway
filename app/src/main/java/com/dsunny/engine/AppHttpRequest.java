package com.dsunny.engine;

import com.alibaba.fastjson.JSON;
import com.dsunny.network.mockdata.MockBaseInfo;
import com.dsunny.util.AppUtil;
import com.infrastructure.activity.BaseActivity;
import com.infrastructure.request.Request;
import com.infrastructure.request.RequestCallback;
import com.infrastructure.request.RequestParameter;
import com.infrastructure.request.RequestThreadPool;
import com.infrastructure.request.Response;
import com.infrastructure.request.URLData;
import com.infrastructure.request.UrlConfigManager;
import com.infrastructure.util.LogUtil;

import java.util.List;

/**
 * 本地请求管理
 */
public class AppHttpRequest {

    private AppHttpRequest() {
    }

    /**
     * 获取请求管理实例
     *
     * @return 请求管理实例
     */
    public static AppHttpRequest getInstance() {
        return AppHttpRequestHolder.INSTANCE;
    }

    /**
     * 内部类实现单例
     */
    private static class AppHttpRequestHolder {
        private static final AppHttpRequest INSTANCE = new AppHttpRequest();
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
            if (!AppUtil.IsStringEmpty(urlData.getMockClass())) {
                try {
                    MockBaseInfo mockInfo = (MockBaseInfo) Class.forName(urlData.getMockClass()).newInstance();
                    String strResponse = mockInfo.getJsonData();
                    final Response responseInJson = JSON.parseObject(strResponse, Response.class);
                    LogUtil.d(responseInJson);
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
