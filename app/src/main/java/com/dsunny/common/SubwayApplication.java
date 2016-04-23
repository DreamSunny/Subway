package com.dsunny.common;

import android.app.Application;
import android.content.SharedPreferences;

import com.dsunny.engine.AppConstants;
import com.dsunny.util.Util;
import com.infrastructure.cache.CacheManager;

import java.io.File;

/**
 * 本地Application
 */
public class SubwayApplication extends Application {

    private static final String KEY_APP_VERSION_CODE = "version_code";

    private static SubwayApplication instance;

    public static SubwayApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 版本升级则拷贝数据库
        initDataBase();
        // 初始化缓存目录
        CacheManager.getInstance().initCacheDir();
    }

    /**
     * 版本升级初始化数据库
     */
    private void initDataBase() {
        SharedPreferences sharedPreferences = Util.getSharedPreference(this);
        final int appVersionCode = Util.getVersionCode(this);
        final int localVersionCode = sharedPreferences.getInt(KEY_APP_VERSION_CODE, 1);
        if (appVersionCode != localVersionCode) {
            // 版本更新，删除旧数据库
            File file = new File(AppConstants.SUBWAY_DB_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
            // 拷贝新数据库
            Util.copyDBFile(this);
            // 将VersionCode写入本地SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_APP_VERSION_CODE, appVersionCode);
            editor.apply();
        }
    }
}
