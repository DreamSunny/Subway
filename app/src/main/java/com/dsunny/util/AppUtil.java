package com.dsunny.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.dsunny.common.AppConstants;
import com.infrastructure.util.BaseUtil;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 本地工具类
 */
public class AppUtil extends BaseUtil {

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(final float dpValue) {
        final float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dip(final float pxValue) {
        final float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }

    /**
     * 关闭输入法
     *
     * @param activity 当前Activity
     */
    public static void closeInputMethod(final Activity activity) {
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
    public static void closeInputMethod(final Activity activity, final View view) {
        final InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 复制文本到剪贴板
     *
     * @param context 当前Activity
     * @param text    文本内容
     */
    public static void copyToClipboard(final Context context, final String text) {
        final ClipboardManager cbm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cbm.setPrimaryClip(ClipData.newPlainText(context.getPackageName(), text));
    }

    /**
     * 是否有网络
     *
     * @param context 当前Activity
     * @return 文本内容
     */
    public static boolean isNetWorkAvilable(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo == null || !activeNetInfo.isAvailable();
    }

    /**
     * 获取当前App版本号
     *
     * @param context 当前Activity或Application
     * @return App版本号
     */
    public static int getVersionCode(Context context) {
        try {
            final PackageInfo packInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 拷贝数据库文件
     *
     * @param context Application Context
     */
    public static void copyDBFile(final Context context) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getResources().getAssets().open(AppConstants.SUBWAY_DB_NAME);
            fos = new FileOutputStream(AppConstants.SUBWAY_DB_FILE_PATH);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStream(is);
            closeStream(fos);
        }
    }

    /**
     * 将数组转换为String形式表示(For log)
     *
     * @param array 数组
     * @return 数组的String形式
     */
    public static <T> String ArrayAsString(final T[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(TextUtils.join(",", array)).append("]");
        return sb.toString();
    }


    /**
     * 将List<T[]>转换为String形式表示(For log)
     *
     * @param lstStringArray 数组List
     * @return List<T[]>的String形式
     */
    public static <T> String ListArrayAsString(final List<T[]> lstStringArray) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (T[] sArr : lstStringArray) {
            sb.append("[").append(TextUtils.join(",", sArr)).append("]");
        }
        sb.append("}");
        return sb.toString();
    }

}