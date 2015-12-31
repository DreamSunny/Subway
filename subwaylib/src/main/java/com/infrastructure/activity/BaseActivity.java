package com.infrastructure.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by user on 2015/12/30.
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    protected Context mContext;
    protected ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = BaseActivity.this;
        mActionBar = getSupportActionBar();

        initVariables();
        initViews(savedInstanceState);
        loadData();
    }
    
    protected abstract void initVariables();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void loadData();

    public <T extends View> T $(int id){
        return (T) super.findViewById(id);
    }

    public <T extends View> T $(View view, int id){
        return (T) view.findViewById(id);
    }

}
