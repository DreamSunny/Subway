package com.infrastructure.utils;

import android.os.Environment;

/**
 * Created by user on 2016/1/4.
 */
public class BaseConstants {
    public static final String APP_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/subway/appdata/";
    public static final String CACHE_PATH = Environment.getDataDirectory() + "/data/com.dsunny.subway/cache/";
    public static final String COOKIE_CACHE_PATH = CACHE_PATH + "cookie";
    public static final String USER_CACHE_PATH = CACHE_PATH + "user";
}