package com.dsunny.engine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.dsunny.mockdata.MockBaseInfo;
import com.infrastructure.activity.BaseActivity;
import com.infrastructure.net.DefaultThreadPool;
import com.infrastructure.net.HttpRequest;
import com.infrastructure.net.RequestCallback;
import com.infrastructure.net.RequestParameter;
import com.infrastructure.net.Response;
import com.infrastructure.net.URLData;
import com.infrastructure.net.UrlConfigManager;
import com.infrastructure.utils.UtilsLog;

import java.util.List;

/**
 * Created by user on 2016/1/4.
 */
public class AppHttpRequest {
    private static AppHttpRequest instance = null;

    private AppHttpRequest() {
    }

    public static synchronized AppHttpRequest getInstance() {
        if (instance == null) {
            instance = new AppHttpRequest();
        }
        return instance;
    }

    public void invoke(final BaseActivity activity, final String apiKey, final List<RequestParameter> params, final RequestCallback callback) {
        final URLData urlData = UrlConfigManager.findURL(activity, apiKey);
        if (urlData != null && !TextUtils.isEmpty(urlData.getMockClass())) {
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
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            HttpRequest request = activity.getRequestManager().createRequest(urlData, params, callback);
            DefaultThreadPool.getInstance().execute(request);
        }
    }

}
