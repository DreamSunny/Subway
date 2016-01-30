package com.infrastructure.utils;

import android.os.Environment;

/**
 * 常量类
 */
public class BaseConstants {
    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/subway/appdata/";
    public static final String APP_IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/subway/images";

    public static final String CACHE_PATH = Environment.getDataDirectory() + "/data/com.dsunny.subway/cache/";
    public static final String COOKIE_CACHE_PATH = CACHE_PATH + "cookie";
    public static final String USER_CACHE_PATH = CACHE_PATH + "user";
}
