package com.dsunny.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String工具类
 */
public class StringUtil {

    /**
     * 判断String是否为空
     *
     * @param s 字符串
     * @return true，为空；false，不为空
     */
    public static boolean isEmpty(final String s) {
        return s == null || s.length() == 0;
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
            if (!isEmpty(num)) {
                return num;
            }
        }
        return "";
    }
}
