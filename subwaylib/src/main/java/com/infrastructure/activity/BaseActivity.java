package com.infrastructure.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.infrastructure.net.RequestManager;

/**
 * Created by user on 2015/12/30.
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    protected Context mContext;
    protected ActionBar mActionBar;
    protected RequestManager mRequestManager = null;// 请求列表管理器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = BaseActivity.this;
        mActionBar = getSupportActionBar();
        mRequestManager = new RequestManager();

        initVariables();
        initViews(savedInstanceState);
        loadData();
    }

    @Override
    protected void onPause() {
        // 在activity停止的时候同时设置停止请求，停止线程请求回调
        if (mRequestManager != null) {
            mRequestManager.cancelRequest();
        }
        super.onPause();
    }

    protected abstract void initVariables();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void loadData();

    public <T extends View> T findView(int id) {
        return (T) super.findViewById(id);
    }

    public <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    public RequestManager getRequestManager() {
        return mRequestManager;
    }
}
