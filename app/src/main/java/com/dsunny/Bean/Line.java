package com.dsunny.Bean;

/**
 * LINE表的实体类
 */
public class Line {

    public String SID;// 车站ID
    public int Distance;// 站间距离
    public String SType;// 车站换乘类型

    @Override
    public String toString() {
        return "Line{" +
                "SID='" + SID + '\'' +
                ", Distance=" + Distance +
                ", SType='" + SType + '\'' +
                '}';
    }
}
