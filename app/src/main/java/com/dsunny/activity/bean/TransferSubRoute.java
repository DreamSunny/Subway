package com.dsunny.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 换乘子路线，例如：丰台科技园-军事博物馆，xx，9号线，国家图书馆站方向，xx
 */
public class TransferSubRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    public String fromStationName;// 子路线起点站
    public String toStationName;// 子路线终点站
    public int distance;// 运行距离(米)
    public String lineName;// 线路名
    public String direction;// 换乘方向
    public List<String> lstStationNames;// 途径车站

    @Override
    public String toString() {
        return "TransferSubRoute{" +
                "fromStationName='" + fromStationName + '\'' +
                ", toStationName='" + toStationName + '\'' +
                ", distance=" + distance +
                ", lineName='" + lineName + '\'' +
                ", direction='" + direction + '\'' +
                ", lstStationNames=" + lstStationNames +
                '}';
    }
}
