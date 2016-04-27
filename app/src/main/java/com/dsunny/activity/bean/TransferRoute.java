package com.dsunny.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 一条换乘路线，包含换乘子路线，例如：丰台科技园-北京站
 */
public class TransferRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    public String fromStationName;// 查询起始站
    public String toStationName;// 查询终点站
    public int airportLineDistance;// 机场线距离
    public int otherLineDistance;// 其余线路总距离
    public int elapsedTime;// 用时
    public List<TransferSubRoute> lstTransferSubRoute;// 换乘子线路

    @Override
    public String toString() {
        return "TransferRoute{" +
                "fromStationName='" + fromStationName + '\'' +
                ", toStationName='" + toStationName + '\'' +
                ", airportLineDistance=" + airportLineDistance +
                ", otherLineDistance=" + otherLineDistance +
                ", elapsedTime=" + elapsedTime +
                ", lstTransferSubRoute=" + lstTransferSubRoute +
                '}';
    }
}
