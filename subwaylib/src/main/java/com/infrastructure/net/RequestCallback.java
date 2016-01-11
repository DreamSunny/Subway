package com.infrastructure.net;

/**
 * 网络请求回调
 */
public interface RequestCallback {
    void onSuccess(String content);

    void onFail(String errorMsg);

    void onCookieExpired();
}
