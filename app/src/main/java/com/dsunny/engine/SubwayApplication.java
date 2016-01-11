package com.dsunny.engine;

import android.app.Application;

import com.dsunny.utils.Utils;
import com.infrastructure.cache.CacheManager;

/**
 * 本地Application
 */
public class SubwayApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 拷贝数据库
        Utils.copyDBFile(this);
        // 初始化缓存目录
        CacheManager.getInstance().initCacheDir();
    }
}
