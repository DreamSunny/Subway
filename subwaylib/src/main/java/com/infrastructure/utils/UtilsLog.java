package com.infrastructure.utils;

import android.util.Log;

/**
 * Log工具类
 */
public class UtilsLog {
    public static final boolean DEBUG = true;

    public static final String TAG_APP = "subway";
    public static final String TAG_URL = "url";
    public static final String TAG_SDCARD = "sdcard";
    public static final String TAG_SQL = "sql";

    /**
     * 打印log
     *
     * @param msg log信息
     */
    public static void d(String msg) {
        d(TAG_APP, msg);
    }

    /**
     * @param o log信息
     */
    public static void d(Object o) {
        d(TAG_APP, o);
    }

    /**
     * 打印log
     *
     * @param tag log标签
     * @param msg log信息
     */
    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, BaseUtils.IsStringEmpty(msg) ? "" : msg);
        }
    }

    /**
     * 打印log
     *
     * @param tag log标签
     * @param o   log信息
     */
    public static void d(String tag, Object o) {
        if (DEBUG) {
            Log.d(tag, o == null ? "null" : o.toString());
        }
    }

}
