package com.infrastructure.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/1/4.
 */
public class RequestManager {
    ArrayList<Request> mRequestList = null;

    public RequestManager() {
        // 异步请求列表
        mRequestList = new ArrayList<>();
    }

    /**
     * 添加Request到列表
     */
    public void addRequest(final Request request) {
        mRequestList.add(request);
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        if (mRequestList != null && mRequestList.size() > 0) {
            for (final Request request : mRequestList) {
                request.abort();
            }
            mRequestList.clear();
        }
    }

    /**
     * 无参数调用
     */
    public Request createRequest(final URLData urlData, final RequestCallback requestCallback) {
        final Request request = new HttpClientRequest(urlData, null, requestCallback);
        addRequest(request);
        return request;
    }

    /**
     * 有参数调用
     */
    public Request createRequest(final URLData urlData, final List<RequestParameter> parameters, final RequestCallback requestCallback) {
        final Request request = new HttpClientRequest(urlData, parameters, requestCallback);
        addRequest(request);
        return request;
    }
}
