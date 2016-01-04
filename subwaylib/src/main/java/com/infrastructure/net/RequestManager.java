package com.infrastructure.net;


import org.apache.http.HttpRequest;

import java.util.ArrayList;

/**
 * Created by user on 2016/1/4.
 */
public class RequestManager {
    ArrayList<HttpRequest> requestList = null;

    public RequestManager() {
        // 异步请求列表
        requestList = new ArrayList<>();
    }

    /**
     * 添加Request到列表
     */
    public void addRequest(final HttpRequest request) {
        requestList.add(request);
    }

    public void cancelRequest() {
        if (requestList != null && requestList.size() > 0) {
            for (final HttpRequest request : requestList) {

            }
        }
    }
}
