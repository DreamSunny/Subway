package com.dsunny.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dsunny.engine.AppConstants;
import com.infrastructure.utils.BaseUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 本地工具类
 */
public class Utils extends BaseUtils {

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue) {
        final float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dip(float pxValue) {
        final float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * Toast信息
     *
     * @param context 当前Activity
     * @param msg     信息
     */
    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast信息
     *
     * @param context 当前Activity
     * @param msg     信息
     */
    public static void toast2(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 取屏幕宽度
     *
     * @return 屏幕宽度
     */
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * 取屏幕高度
     *
     * @return 屏幕高度
     */
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * 关闭输入法
     *
     * @param activity 当前Activity
     */
    public static void closeInputMethod(Activity activity) {
        final View view = activity.getCurrentFocus();
        if (view != null) {
            closeInputMethod(activity, view);
        }
    }

    /**
     * 关闭输入法
     *
     * @param activity 当前Activity
     * @param view     打开输入法的View
     */
    public static void closeInputMethod(Activity activity, View view) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 复制文本到剪贴板
     *
     * @param context 当前Activity
     * @param text    文本内容
     */
    public static void copyToClipboard(Context context, String text) {
        final ClipboardManager cbm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cbm.setPrimaryClip(ClipData.newPlainText(context.getPackageName(), text));
    }

    /**
     * 是否有网络
     *
     * @param context 当前Activity
     * @return 文本内容
     */
    public static boolean isNetWorkAvilable(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo == null || !activeNetInfo.isAvailable();
    }

    /**
     * 获取SharedPreferences
     *
     * @param context 当前Activity
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    /**
     * 拷贝数据库文件
     *
     * @param context Application Context
     */
    public static void copyDBFile(Context context) {
        if (!(new File(AppConstants.SUBWAY_DB_FILE_PATH).exists())) {
            try {
                InputStream is = context.getResources().getAssets().open(AppConstants.SUBWAY_DB_NAME);
                FileOutputStream fos = new FileOutputStream(AppConstants.SUBWAY_DB_FILE_PATH);
                byte[] buffer = new byte[1024];
                int count;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}