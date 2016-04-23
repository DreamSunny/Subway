package com.dsunny.activity.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 换乘详情，包含多条换乘线路
 */
public class TransferDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    public String fromStationName;// 查询起始站
    public String toStationName;// 查询终点站
    public int ticketPrice;// 票价(元)
    public List<TransferRoute> lstTransferRoute;// 换乘线路

    @Override
    public String toString() {
        return "TransferDetail{" +
                "fromStationName='" + fromStationName + '\'' +
                ", toStationName='" + toStationName + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", lstTransferRoute=" + lstTransferRoute +
                '}';
    }
}
