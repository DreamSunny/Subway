package com.dsunny.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.dsunny.engine.AppConstants;
import com.dsunny.common.SubwayApplication;
import com.infrastructure.util.BaseUtil;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 本地工具类
 */
public class Util extends BaseUtil {

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

    public static void toast(final String msg) {
        Toast.makeText(SubwayApplication.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast信息
     *
     * @param context 当前Activity
     * @param msg     信息
     */
    public static void toast(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast信息
     *
     * @param context 当前Activity
     * @param msg     信息
     */
    public static void toast2(final Context context, final String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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
     * 获取SharedPreferences
     *
     * @param context 当前Activity
     * @return SharedPreferences
     */
    public static SharedPreferences getSharedPreference(final Context context) {
        return context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
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

    /**
     * 判断字符串是否是字母，数字或汉字
     *
     * @param s 字符串
     * @return true，是字母，数字或汉字；false，不是字母，数字或汉字
     */
    public static boolean isAlphanumeric(final String s) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9\\u4e00-\\u9fa5]+$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 将字符串数字转换为Int值
     *
     * @param number 字符串数字
     * @return 字符串数字的数值
     */
    public static int string2Int(String number) {
        try {
            return Integer.parseInt(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Integer.MAX_VALUE;
    }

    /**
     * 返回字符串中第一个出现的数字，例如：str="线路2:大约用时50分钟,票价5元"，则返回2
     *
     * @param str 字符串
     * @return 字符串中第一个整数，不存在则返回空
     */
    public static String getFirstNumberInString(String str) {
        String[] nums = str.split("\\D+");
        for (String num : nums) {
            if (!IsStringEmpty(num)) {
                return num;
            }
        }
        return "";
    }

}