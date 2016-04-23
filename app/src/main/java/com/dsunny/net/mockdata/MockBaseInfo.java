package com.dsunny.net.mockdata;

import com.infrastructure.net.Request;
import com.infrastructure.net.Response;

/**
 * Mock基类
 */
public abstract class MockBaseInfo {
    /**
     * 模拟接口数据
     *
     * @return 返回信息
     */
    public abstract String getJsonData();

    /**
     * 模拟接口返回成功
     *
     * @return 返回信息
     */
    public Response getSuccessResponse() {
        Response response = new Response();
        response.setError(false);
        response.setErrorType(Request.RESPONSE_SUCCESS);
        response.setErrorMessage("");
        return response;
    }

    /**
     * 模拟接口返回失败
     *
     * @param errorType    错误类型
     * @param errorMessage 错误消息
     * @return 返回信息
     */
    public Response getFailResponse(int errorType, String errorMessage) {
        Response response = new Response();
        response.setError(true);
        response.setErrorType(errorType);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
