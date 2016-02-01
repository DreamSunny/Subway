package com.dsunny.engine;

import android.app.Application;
import android.content.SharedPreferences;

import com.dsunny.utils.Utils;
import com.infrastructure.cache.CacheManager;

import java.io.File;

/**
 * 本地Application
 */
public class SubwayApplication extends Application {

    private static final String KEY_APP_VERSION_CODE = "versioncode";

    @Override
    public void onCreate() {
        super.onCreate();

        // 版本升级则拷贝数据库
        SharedPreferences sharedPreferences = Utils.getSharedPreference(this);
        final int appVersionCode = Utils.getVersionCode(this);
        final int localVersionCode = sharedPreferences.getInt(KEY_APP_VERSION_CODE, 1);
        if (appVersionCode != localVersionCode) {
            // 版本更新，删除旧数据库
            File file = new File(AppConstants.SUBWAY_DB_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
            // 拷贝新数据库
            Utils.copyDBFile(this);
            // 将VersionCode写入本地SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(KEY_APP_VERSION_CODE, appVersionCode);
            editor.apply();
        }

        // 初始化缓存目录
        CacheManager.getInstance().initCacheDir();
    }
}
