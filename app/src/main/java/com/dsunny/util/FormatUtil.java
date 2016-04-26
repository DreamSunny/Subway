package com.dsunny.util;

/**
 * 格式化工具类
 */
public class FormatUtil {

    /**
     * @param value 浮点数
     * @return 保留一位小数
     */
    public static String double1(double value) {
        return String.format("%.1f", value);
    }

    /**
     * @param value 浮点数
     * @return 保留两位小数
     */
    public static String double2(double value) {
        return String.format("%.2f", value);
    }

}
