package com.dsunny.common;

/**
 * Subway数据库常量
 */
public class SubwayData {
    // 1号线
    public static final String LINE_01 = "01";
    // 2号线
    public static final String LINE_02 = "02";
    // 10号线
    public static final String LINE_10 = "10";
    // 13号线
    public static final String LINE_13 = "13";
    // 14号线
    public static final String LINE_14 = "14";
    // 八通线
    public static final String LINE_94 = "94";
    // 机场线
    public static final String LINE_99 = "99";

    // 机场线
    public static final String LINE_JICHANGXIAN = "机场线";

    // 四惠
    public static final String STATION_ID_SIHUI = "0122";
    // 四惠东
    public static final String STATION_ID_SIHUIDONG = "0123";
    // T2航站楼
    public static final String STATION_ID_T2 = "9905";
    // T3航站楼
    public static final String STATION_ID_T3 = "9904";

    // 14号线A段
    public static final String LINE_14A = "14A";
    // 14号线A段起点与终点站ID
    public static final String[] LINE_14A_IDS = {"1401", "1407"};
    // 14号线B段
    public static final String LINE_14B = "14B";
    // 14号线B段起点与终点站ID
    public static final String[] LINE_14B_IDS = {"1413", "1437"};
    // 西局
    public static final String STATION_XIJU = "西局";
    // 北京南站
    public static final String STATION_BEIJINGNANZHAN = "北京南站";

    // 环线线路ID
    public static final String[] CIRCULAR_LINE_IDS = {"02", "10"};
    // 存在环线的线路ID
    public static final String[] LINES_EXIST_LOOP_IDS = {"02", "10", "13"};
    // 横穿存在环线的线路ID(线路与存在环线的线路有两个交点)
    public static final String[] CROSS_LINE_02_IDS = {"01", "04", "05", "06", "13"};
    public static final String[] CROSS_LINE_10_IDS = {"01", "04", "05", "06", "13"};
    public static final String[] CROSS_LINE_13_IDS = {"02", "10"};

    public static final String LINE_ALL = "全部";

    // 线路ID
    public static final String[] LINE_IDS = {
            "01", "02", "04", "05", "06", "07", "08", "09", "10",
            "13", "14", "15", "94", "95", "96", "97", "98", "99"
    };
    // 线路名
    public static final String[] LINE_NAMES = {
            "1号线", "2号线", "4号线", "5号线", "6号线", "7号线", "8号线",
            "9号线", "10号线", "13号线", "14号线", "15号线", "八通线", "昌平线",
            "亦庄线", "大兴线", "房山线", "机场线"
    };
    // LINES_TRANSFER数组的边
    public static final String[] LINE_EDGES = {
            "01", "02", "04", "05", "06", "07", "08", "09", "10",
            "13", "14A", "14B", "15", "94", "95", "96", "97", "98", "99"
    };
    // 线路之间最大换乘次数
    public static final int LINE_MAX_TRANSFER_TIMES = 4;
    // 线路间换乘表(14号线分为AB段)，arr[i][j]表示从i号线到j号线至少需要换乘arr[i][j]-1次
    public static final int[][] LINE_TRANSFERS = {
            {0, 1, 1, 1, 2, 2, 2, 1, 1, 2, 2, 1, 2, 1, 3, 2, 2, 2, 2},
            {1, 0, 1, 1, 1, 2, 1, 2, 2, 1, 3, 2, 2, 2, 2, 2, 2, 3, 1},
            {1, 1, 0, 2, 1, 1, 2, 1, 1, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2},
            {1, 1, 2, 0, 1, 1, 2, 2, 1, 1, 2, 1, 1, 2, 2, 1, 3, 3, 2},
            {2, 1, 1, 1, 0, 2, 1, 1, 1, 2, 2, 1, 2, 3, 2, 2, 2, 2, 2},
            {2, 2, 1, 1, 2, 0, 3, 1, 2, 2, 2, 1, 2, 3, 3, 2, 2, 2, 3},
            {2, 1, 2, 2, 1, 3, 0, 2, 1, 1, 2, 2, 1, 3, 1, 2, 3, 3, 2},
            {1, 2, 1, 2, 1, 1, 2, 0, 1, 2, 1, 2, 3, 2, 3, 2, 2, 1, 2},
            {1, 2, 1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 2, 2, 2, 1, 2, 2, 1},
            {2, 1, 1, 1, 2, 2, 1, 2, 1, 0, 2, 2, 1, 3, 1, 2, 2, 3, 1},
            {2, 3, 2, 2, 2, 2, 2, 1, 1, 2, 0, 2, 3, 3, 3, 2, 3, 2, 2},
            {1, 2, 1, 1, 1, 1, 2, 2, 1, 2, 2, 0, 1, 2, 3, 2, 2, 3, 2},
            {2, 2, 2, 1, 2, 2, 1, 3, 2, 1, 3, 1, 0, 3, 2, 2, 3, 4, 2},
            {1, 2, 2, 2, 3, 3, 3, 2, 2, 3, 3, 2, 3, 0, 4, 3, 3, 3, 3},
            {3, 2, 2, 2, 2, 3, 1, 3, 2, 1, 3, 3, 2, 4, 0, 3, 3, 4, 2},
            {2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 3, 3, 0, 3, 3, 2},
            {2, 2, 1, 3, 2, 2, 3, 2, 2, 2, 3, 2, 3, 3, 3, 3, 0, 3, 3},
            {2, 3, 2, 3, 2, 2, 3, 1, 2, 3, 2, 3, 4, 3, 4, 3, 3, 0, 3},
            {2, 1, 2, 2, 2, 3, 2, 2, 1, 1, 2, 2, 2, 3, 2, 2, 3, 3, 0}
    };
}
