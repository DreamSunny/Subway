package com.dsunny.engine;

import android.os.Environment;

import com.infrastructure.utils.BaseConstants;

/**
 * 本地常量
 */
public class AppConstants extends BaseConstants {
    public static final String DB_NAME = "subway.db";
    public static final String DB_FILE_PATH = Environment.getDataDirectory() + "/data/com.dsunny.subway/subway.db";
    // Activity路径
    public static final String ACTIVITY_SEARCH = "com.dsunny.activity.SearchActivity";
    public static final String ACTIVITY_ABOUT_ME = "com.dsunny.activity.AboutMeActivity";
}
