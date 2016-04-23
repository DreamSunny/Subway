package com.infrastructure.net;

import com.infrastructure.util.BaseUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理网络请求
 */
public class RequestManager {
    ArrayList<Request> mRequestList = null;

    public RequestManager() {
        // 异步请求列表
        mRequestList = new ArrayList<>();
    }

    /**
     * 添加Request到列表
     *
     * @param request 网络请求
     */
    public void addRequest(final Request request) {
        mRequestList.add(request);
    }

    /**
     * 取消网络请求
     */
    public void cancelRequest() {
        if (!BaseUtil.IsListEmpty(mRequestList)) {
            for (final Request request : mRequestList) {
                request.abort();
            }
            mRequestList.clear();
        }
    }

    /**
     * 创建网络请求
     *
     * @param urlData         请求url信息
     * @param requestCallback 回调方法
     * @return 创建的请求
     */
    public Request createRequest(final URLData urlData, final RequestCallback requestCallback) {
        final Request request = new HurlRequest(urlData, null, requestCallback);
        addRequest(request);
        return request;
    }

    /**
     * 创建网络请求
     *
     * @param urlData         请求url信息
     * @param parameters      请求参数
     * @param requestCallback 回调方法
     * @return 创建的请求
     */
    public Request createRequest(final URLData urlData, final List<RequestParameter> parameters, final RequestCallback requestCallback) {
        final Request request = new HurlRequest(urlData, parameters, requestCallback);
        addRequest(request);
        return request;
    }
}
