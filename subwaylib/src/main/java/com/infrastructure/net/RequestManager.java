package com.infrastructure.net;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/1/4.
 */
public class RequestManager {
    ArrayList<HttpRequest> mRequestList = null;

    public RequestManager() {
        // 异步请求列表
        mRequestList = new ArrayList<>();
    }

    /**
     * 添加Request到列表
     */
    public void addRequest(final HttpRequest request) {
        mRequestList.add(request);
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        if (mRequestList != null && mRequestList.size() > 0) {
            for (final HttpRequest request : mRequestList) {
                if (request.getRequest() != null) {
                    try {
                        request.getRequest().abort();
                    } catch (final UnsupportedOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
            mRequestList.clear();
        }
    }

    /**
     * 无参数调用
     */
    public HttpRequest createRequest(final URLData urlData, final RequestCallback requestCallback) {
        final HttpRequest request = new HttpRequest(urlData, null, requestCallback);
        addRequest(request);
        return request;
    }

    /**
     * 有参数调用
     */
    public HttpRequest createRequest(final URLData urlData, final List<RequestParameter> parameters, final RequestCallback requestCallback) {
        final HttpRequest request = new HttpRequest(urlData, parameters, requestCallback);
        addRequest(request);
        return request;
    }
}
