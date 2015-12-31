package com.dsunny.activity;

import android.os.Bundle;

import com.dsunny.base.AppBaseActivity;
import com.dsunny.subway.R;


public class SearchActivity extends AppBaseActivity {

    @Override
    protected void initVariables() {
        Bundle bundle = getIntent().getExtras();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);
    }

    @Override
    protected void loadData() {

    }
}
