package com.dsunny.common;

import android.app.Application;

import com.dsunny.util.AppUtil;
import com.dsunny.util.SharedPreferencesUtil;
import com.infrastructure.cache.CacheManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;

/**
 * 本地Application
 */
public class SubwayApplication extends Application {


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
        // Bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "900032862", false);
    }

    /**
     * 版本升级初始化数据库
     */
    private void initDataBase() {
        final int appVersionCode = AppUtil.getVersionCode(this);
        final int localVersionCode = SharedPreferencesUtil.getAppVersionCode();
        if (appVersionCode != localVersionCode) {
            // 版本更新，删除旧数据库
            File file = new File(AppConstants.SUBWAY_DB_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
            // 拷贝新数据库
            AppUtil.copyDBFile(this);
            // 将VersionCode写入本地SharedPreferences
            SharedPreferencesUtil.saveAppVersionCode(appVersionCode);
        }
    }
}
