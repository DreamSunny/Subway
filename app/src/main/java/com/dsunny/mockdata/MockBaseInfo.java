package com.dsunny.mockdata;

import com.infrastructure.net.HttpRequest;
import com.infrastructure.net.Response;

/**
 * Created by user on 2016/1/5.
 */
public abstract class MockBaseInfo {
    public abstract String getJsonData();

    public Response getSuccessResponse() {
        Response response = new Response();
        response.setError(false);
        response.setErrorType(HttpRequest.RESPONSE_SUCCESS);
        response.setErrorMessage("");
        return response;
    }

    public Response getFailResponse(int errorType, String errorMessage) {
        Response response = new Response();
        response.setError(true);
        response.setErrorType(errorType);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
