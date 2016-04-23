package com.dsunny.net.mockdata;

import com.alibaba.fastjson.JSON;
import com.dsunny.net.entity.UserInfo;
import com.infrastructure.net.Response;

/**
 * 模拟登陆接口
 */
public class MockUserInfo extends MockBaseInfo {
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
