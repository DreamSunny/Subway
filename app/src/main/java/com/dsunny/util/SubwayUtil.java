package com.dsunny.util;

import com.dsunny.common.SubwayData;

/**
 * 地铁相关工具类
 */
public class SubwayUtil {
    /**
     * 获取指定线路ID的线路名
     *
     * @param lid 线路ID
     * @return 线路名
     */
    public static String getLineName(String lid) {
        for (int i = 0; i < SubwayData.LINE_IDS.length; i++) {
            if (SubwayData.LINE_IDS[i].equals(lid)) {
                return SubwayData.LINE_NAMES[i];
            }
        }
        return "";
    }

    /**
     * 判断线路是否是环线，用于计算换乘方向
     *
     * @param lid 线路ID
     * @return true，是环线；false，非环线
     */
    public static boolean isCircularLine(String lid) {
        for (String id : SubwayData.CIRCULAR_LINE_IDS) {
            if (id.equals(lid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断线路是否存在环路，用于计算同一线路上的两车站的最短换乘路径
     *
     * @param lid 线路ID
     * @return true，存在环路；false，不存在环路
     */
    public static boolean isLineExistLoop(String lid) {
        for (String id : SubwayData.LINES_EXIST_LOOP_IDS) {
            if (id.equals(lid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 线路存在环路，获取横穿lid的线路ID集合
     *
     * @param lid 线路ID
     * @return 横穿lid的线路ID集合
     */
    public static String[] getCrossLineIds(String lid) {
        if (SubwayData.LINE_02.equals(lid)) {
            return SubwayData.CROSS_LINE_02_IDS;
        } else if (SubwayData.LINE_10.equals(lid)) {
            return SubwayData.CROSS_LINE_10_IDS;
        } else if (SubwayData.LINE_13.equals(lid)) {
            return SubwayData.CROSS_LINE_13_IDS;
        }
        return null;
    }

    /**
     * 计算乘车价格
     *
     * @param airportLineDistance 机场线乘车距离
     * @param otherLinesDistance  其他线路乘车距离
     * @return 乘车价格
     */
    public static int getTransferPrice(int airportLineDistance, int otherLinesDistance) {
        int price = 0;
        if (airportLineDistance != 0) {
            price += 25;
        }
        if (otherLinesDistance == 0) {
            price += 0;
        } else if (otherLinesDistance <= 6000) {
            price += 3;
        } else if (otherLinesDistance <= 12000) {
            price += 4;
        } else if (otherLinesDistance <= 22000) {
            price += 5;
        } else if (otherLinesDistance <= 32000) {
            price += 6;
        } else {
            price += 7 + (otherLinesDistance - 32000) / 20000;
        }

        return price;
    }

    /**
     * 计算乘车时间
     *
     * @param airportLineDistance 机场线乘车距离
     * @return 乘车时间
     */
    public static int getAirportLineElapsedTime(int airportLineDistance) {
        return airportLineDistance / SubwayData.AIRPORT_LINE_SPEED;
    }

    /**
     * 计算乘车时间
     *
     * @param otherLineDistance 其他线路乘车距离
     * @return 乘车时间
     */
    public static int getLineElapsedTime(int otherLineDistance) {
        return otherLineDistance / SubwayData.OTHER_LINE_SPEED;
    }

    /**
     * 计算乘车时间
     *
     * @param airportLineDistance 机场线乘车距离
     * @param otherLineDistance   其他线路乘车距离
     * @param transferTimes       换乘次数
     * @return 乘车时间
     */
    public static int getTransferElapsedTime(int airportLineDistance, int otherLineDistance, int transferTimes) {
        return airportLineDistance / SubwayData.AIRPORT_LINE_SPEED + otherLineDistance / SubwayData.OTHER_LINE_SPEED + transferTimes * SubwayData.TRANSFER_MINUTE + 1;
    }
}
