package com.dsunny.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dsunny.base.AppBaseActivity;
import com.dsunny.entity.WeatherEntity;
import com.dsunny.entity.WeatherInfo;
import com.dsunny.subway.R;
import com.infrastructure.net.RequestAsyncTask;


public class SearchActivity extends AppBaseActivity {

    TextView tvHelloWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 拷贝数据库
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initVariables() {
        Bundle bundle = getIntent().getExtras();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
        tvHelloWorld = $(R.id.tv_helloworld);
    }

    @Override
    protected void loadData() {
        String url = "http://www.weather.com.cn/data/sk/101010100.html";

        RequestAsyncTask task = new RequestAsyncTask() {
            @Override
            public void onSuccess(String content) {
                Log.d("mzy", "content = " + content);
                WeatherEntity weatherEntity = JSON.parseObject(content, WeatherEntity.class);
                WeatherInfo weatherInfo = weatherEntity.getWeatherInfo();
                if (weatherInfo != null) {
                    tvHelloWorld.setText(weatherInfo.getCity() + "-" + weatherInfo.getCityid());
                }
            }

            @Override
            public void onFail(String errorMessage) {
                Log.d("mzy", "errorMessage = " + errorMessage);
                new AlertDialog.Builder(mContext).setTitle("出错啦").setMessage(errorMessage).setPositiveButton("确定", null).show();
            }
        };
        task.execute(url);

        setActionBarTitle("北京地铁");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }
}
