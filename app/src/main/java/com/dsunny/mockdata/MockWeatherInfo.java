package com.dsunny.mockdata;

import com.alibaba.fastjson.JSON;
import com.dsunny.entity.WeatherInfo;
import com.infrastructure.net.Response;

/**
 * 模拟天气接口
 */
public class MockWeatherInfo extends MockBaseInfo {
    @Override
    public String getJsonData() {
        WeatherInfo weather = new WeatherInfo();
        weather.setCity("Beijing");
        weather.setCityid("10000");

        Response response = getSuccessResponse();
        response.setResult(JSON.toJSONString(weather));
        return JSON.toJSONString(response);
    }
}
