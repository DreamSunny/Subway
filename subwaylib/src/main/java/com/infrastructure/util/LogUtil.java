package com.infrastructure.util;

import android.util.Log;

/**
 * Log工具类
 */
public class LogUtil {
    public static final boolean DEBUG = true;

    public static final String TAG_APP = "subway";
    public static final String TAG_URL = "url";
    public static final String TAG_SDCARD = "sdcard";
    public static final String TAG_SQL = "sql";
    public static final String TAG_IMAGE = "image";

    /**
     * 打印log
     *
     * @param msg log信息
     */
    public static void d(final String msg) {
        d(TAG_APP, msg);
    }

    /**
     * @param o log信息
     */
    public static void d(final Object o) {
        d(TAG_APP, o);
    }

    /**
     * 打印log
     *
     * @param tag log标签
     * @param msg log信息
     */
    public static void d(final String tag, final String msg) {
        if (DEBUG) {
            Log.d(tag, BaseUtil.IsStringEmpty(msg) ? "" : msg);
        }
    }

    /**
     * 打印log
     *
     * @param tag log标签
     * @param o   log信息
     */
    public static void d(final String tag, final Object o) {
        if (DEBUG) {
            Log.d(tag, o == null ? "null" : o.toString());
        }
    }

}
