package com.dsunny.engine;

import android.os.Environment;

import com.infrastructure.utils.BaseConstants;

/**
 * 本地常量
 */
public class AppConstants extends BaseConstants {
    public static final String SUBWAY_DB_NAME = "subway.db";
    public static final String SUBWAY_DB_FILE_PATH = Environment.getDataDirectory() + "/data/com.dsunny.subway/subway.db";
    // Activity路径
    public static final String ACTIVITY_SEARCH = "com.dsunny.activity.SearchActivity";
    public static final String ACTIVITY_TRANSFER_DETAIL = "com.dsunny.activity.TransferDetailActivity";
    public static final String ACTIVITY_TRANSFER_ROUTE = "com.dsunny.activity.TransferRouteActivity";
    public static final String ACTIVITY_ABOUT_ME = "com.dsunny.activity.AboutMeActivity";
    // Activity传值Key
    public static final String KEY_TRANSFER_DETAIL = "transferdetail";
    public static final String KEY_TRANSFER_ROUTE_NUMBER = "transferroutenumber";
}
