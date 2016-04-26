package com.dsunny.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dsunny.common.SubwayApplication;

/**
 * SharedPreferences工具类
 */
public class SharedPreferencesUtil {

    private static final String KEY_APP_VERSION_CODE = "app_version_code";

    /**
     * 将当前版本号写入本地
     *
     * @param appVersionCode 版本号
     */
    public static void saveAppVersionCode(int appVersionCode) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SubwayApplication.getInstance());
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY_APP_VERSION_CODE, appVersionCode);
        editor.apply();
    }

    /**
     * @return 获取本地版本号
     */
    public static int getAppVersionCode() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SubwayApplication.getInstance());
        return sp.getInt(KEY_APP_VERSION_CODE, 0);
    }
}
