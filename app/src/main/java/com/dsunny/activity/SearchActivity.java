package com.dsunny.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dsunny.base.AppBaseActivity;
import com.dsunny.engine.AppHttpRequest;
import com.dsunny.entity.WeatherInfo;
import com.dsunny.subway.R;
import com.infrastructure.net.RequestCallback;
import com.infrastructure.net.RequestParameter;
import com.infrastructure.utils.UtilsLog;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppBaseActivity {

    private TextView tvHelloWorld;
    private RequestCallback mRequestCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        Bundle bundle = getIntent().getExtras();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        tvHelloWorld = findView(R.id.tv_helloworld);
    }

    @Override
    protected void loadData() {
//        String url = "http://www.weather.com.cn/data/sk/101010100.html";
//
//        RequestAsyncTask task = new RequestAsyncTask() {
//            @Override
//            public void onSuccess(String content) {
//                Log.d("mzy", "content = " + content);
//                WeatherInfoEntity weatherEntity = JSON.parseObject(content, WeatherInfoEntity.class);
//                WeatherInfo weatherInfo = weatherEntity.getWeatherInfo();
//                if (weatherInfo != null) {
//                    tvHelloWorld.setText(weatherInfo.getCity() + "-" + weatherInfo.getCityid());
//                }
//            }
//
//            @Override
//            public void onFail(String errorMsg) {
//                Log.d("mzy", "errorMsg = " + errorMsg);
//                new AlertDialog.Builder(mContext).setTitle("出错啦").setMessage(errorMsg).setPositiveButton("确定", null).show();
//            }
//        };
//        task.execute(url);

        List<RequestParameter> params = new ArrayList<>();
        params.add(new RequestParameter("cityId", "101010100"));
        params.add(new RequestParameter("cityName", "Beijing"));

        mRequestCallback = new RequestCallback() {
            @Override
            public void onSuccess(String content) {
                WeatherInfo weatherInfo = JSON.parseObject(content, WeatherInfo.class);
                UtilsLog.d(weatherInfo);
                if (weatherInfo != null) {
                    tvHelloWorld.setText(weatherInfo.getCity() + "-" + weatherInfo.getCityid());
                }
            }

            @Override
            public void onFail(String errorMsg) {
                new AlertDialog.Builder(mContext).setTitle("出错啦").setMessage(errorMsg).setPositiveButton("确定", null).show();
            }

            @Override
            public void onCookieExpired() {

            }
        };

        AppHttpRequest.getInstance().performRequest(this, "getWeatherInfo", params, mRequestCallback);

        setActionBarTitle("北京地铁");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
