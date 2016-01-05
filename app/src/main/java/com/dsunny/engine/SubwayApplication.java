package com.dsunny.engine;

import android.app.Application;

import com.infrastructure.cache.CacheManager;

/**
 * Created by user on 2016/1/4.
 */
public class SubwayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 拷贝数据库
        // 初始化缓存目录
        CacheManager.getInstance().initCacheDir();
    }
}
