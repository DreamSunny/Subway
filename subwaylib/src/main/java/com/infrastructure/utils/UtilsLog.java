package com.infrastructure.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by user on 2016/1/5.
 */
public class UtilsLog {
    public static final boolean DEBUG = true;

    public static final String TAG_APP = "subway";
    public static final String TAG_URL = "url";
    public static final String TAG_SDCARD = "sdcard";

    public static void d(String msg) {
        d(TAG_APP, msg);
    }

    public static void d(Object o) {
        d(TAG_APP, o);
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, TextUtils.isEmpty(msg) ? "" : msg);
        }
    }

    public static void d(String tag, Object o) {
        if (DEBUG) {
            Log.d(tag, o == null ? "null" : o.toString());
        }
    }

}
