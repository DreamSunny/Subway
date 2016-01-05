package com.infrastructure.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by user on 2016/1/5.
 */
public class UtilsLog {
    public static final String TAG = "subway";
    public static final boolean isTest = true;


    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(Object o) {
        d(TAG, o);
    }

    public static void d(String tag, String msg) {
        if (isTest) {
            Log.d(tag, TextUtils.isEmpty(msg) ? "" : msg);
        }
    }

    public static void d(String tag, Object o) {
        if (isTest) {
            Log.d(tag, o == null ? "null" : o.toString());
        }
    }

}
