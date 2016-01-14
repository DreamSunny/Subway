package com.dsunny.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * 换乘子路线，例如：9号线-1号线-2号线，记录9号线-1号线和1号线-2号线的换乘信息
 */
public class TransferSubRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    public int totalDistance;// 运行距离(米)
    public String lineName;// 线路名
    public String transferDirection;// 换乘方向
    public List<String> lstStationNames;// 途径车站


}
