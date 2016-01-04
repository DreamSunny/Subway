package com.infrastructure.net;

/**
 * Created by user on 2016/1/4.
 */
public interface RequestCallback {
    void onSuccess(String content);

    void onFail(String errorMsg);

    void onCookieExpired();
}
