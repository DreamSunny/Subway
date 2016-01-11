package com.dsunny.activity;

import android.os.Bundle;

import com.dsunny.base.AppBaseActivity;
import com.dsunny.engine.AppHttpRequest;
import com.dsunny.subway.R;
import com.infrastructure.net.RequestCallback;
import com.infrastructure.net.RequestParameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于我页面
 */
public class AboutMeActivity extends AppBaseActivity {

    private RequestCallback mRequestCallback;
    
    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_me);
    }

    @Override
    protected void loadData() {
        List<RequestParameter> params = new ArrayList<>();
        params.add(new RequestParameter("cityId", "101010100"));
        params.add(new RequestParameter("cityName", "Beijing"));

        mRequestCallback = new RequestCallback() {
            @Override
            public void onSuccess(String content) {

            }

            @Override
            public void onFail(String errorMsg) {

            }

            @Override
            public void onCookieExpired() {

            }
        };

        AppHttpRequest.getInstance().performRequest(this, "getWeatherInfo", params, mRequestCallback);
    }
}
