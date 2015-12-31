package com.infrastructure.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by user on 2015/12/30.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initVariables();
        initViews(savedInstanceState);
        loadData();
    }
    
    protected abstract void initVariables();
    protected abstract void initViews(Bundle savedInstanceState);
    protected abstract void loadData();
    
}
