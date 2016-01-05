package com.infrastructure.utils;

import android.os.Environment;

/**
 * Created by user on 2016/1/4.
 */
public class BaseConstants {
    public static final String CACHE_DIR = Environment.getDataDirectory() + "/data/com.dsunny.subway/cache/";
    public static final String COOKIE_CACHE_PATH = CACHE_DIR + "cookie";
    public static final String USER_CACHE_PATH = CACHE_DIR + "user";
}
