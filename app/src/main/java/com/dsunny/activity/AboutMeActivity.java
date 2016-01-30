package com.dsunny.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.dsunny.base.AppBaseActivity;
import com.dsunny.subway.R;
import com.infrastructure.image.ImageLoader;

/**
 * 关于我页面
 */
public class AboutMeActivity extends AppBaseActivity {

    private TextView tvTest;
    private ImageView ivPicture;

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_me);

        tvTest = findAppViewById(R.id.tv_test);
        ivPicture = findAppViewById(R.id.iv_picture);
    }

    @Override
    protected void loadData() {
        ImageLoader.getInstance().displayImage("http://cdn.iciba.com/news/word/2016-01-30.jpg", ivPicture);
    }


}
