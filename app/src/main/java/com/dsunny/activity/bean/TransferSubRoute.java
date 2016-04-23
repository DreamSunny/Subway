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
    public int totalDistance;// 运行距离(米)
    public String lineName;// 线路名
    public String transferDirection;// 换乘方向
    public List<String> lstStationNames;// 途径车站

    @Override
    public String toString() {
        return "TransferSubRoute{" +
                "fromStationName='" + fromStationName + '\'' +
                ", toStationName='" + toStationName + '\'' +
                ", totalDistance=" + totalDistance +
                ", lineName='" + lineName + '\'' +
                ", transferDirection='" + transferDirection + '\'' +
                ", lstStationNames=" + lstStationNames +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferSubRoute subRoute = (TransferSubRoute) o;

        if (totalDistance != subRoute.totalDistance) return false;
        if (fromStationName != null ? !fromStationName.equals(subRoute.fromStationName) : subRoute.fromStationName != null)
            return false;
        if (toStationName != null ? !toStationName.equals(subRoute.toStationName) : subRoute.toStationName != null)
            return false;
        if (lineName != null ? !lineName.equals(subRoute.lineName) : subRoute.lineName != null)
            return false;
        if (transferDirection != null ? !transferDirection.equals(subRoute.transferDirection) : subRoute.transferDirection != null)
            return false;
        return !(lstStationNames != null ? !lstStationNames.equals(subRoute.lstStationNames) : subRoute.lstStationNames != null);

    }

    @Override
    public int hashCode() {
        int result = fromStationName != null ? fromStationName.hashCode() : 0;
        result = 31 * result + (toStationName != null ? toStationName.hashCode() : 0);
        result = 31 * result + totalDistance;
        result = 31 * result + (lineName != null ? lineName.hashCode() : 0);
        result = 31 * result + (transferDirection != null ? transferDirection.hashCode() : 0);
        result = 31 * result + (lstStationNames != null ? lstStationNames.hashCode() : 0);
        return result;
    }
}
