package com.dsunny.mockdata;

import com.alibaba.fastjson.JSON;
import com.dsunny.entity.UserInfo;
import com.infrastructure.net.Response;

/**
 * Created by user on 2016/1/5.
 */
public class MockLoginSuccessInfo extends MockBaseInfo {
    @Override
    public String getJsonData() {
        UserInfo userInfo = new UserInfo();
        userInfo.setLoginName("DreaamSunny");
        userInfo.setUserName("yaku");
        userInfo.setScore(100);

        Response response = getSuccessResponse();
        response.setResult(JSON.toJSONString(userInfo));
        return JSON.toJSONString(response);
    }
}
