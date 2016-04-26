package com.dsunny.database.bean;

/**
 * STATION表的实体类
 */
public class Station {

    public String ID;// 车站ID
    public String Name;// 车站名
    public String Abbr;// 缩写
    public String State;// 有效标识

    @Override
    public String toString() {
        return "Station{" +
                "ID='" + ID + '\'' +
                ", Name='" + Name + '\'' +
                ", Abbr='" + Abbr + '\'' +
                ", State='" + State + '\'' +
                '}';
    }
}
