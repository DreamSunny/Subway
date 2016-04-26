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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferSubRoute that = (TransferSubRoute) o;

        if (distance != that.distance) return false;
        if (fromStationName != null ? !fromStationName.equals(that.fromStationName) : that.fromStationName != null)
            return false;
        if (toStationName != null ? !toStationName.equals(that.toStationName) : that.toStationName != null)
            return false;
        if (lineName != null ? !lineName.equals(that.lineName) : that.lineName != null)
            return false;
        if (direction != null ? !direction.equals(that.direction) : that.direction != null)
            return false;
        return !(lstStationNames != null ? !lstStationNames.equals(that.lstStationNames) : that.lstStationNames != null);

    }

    @Override
    public int hashCode() {
        int result = fromStationName != null ? fromStationName.hashCode() : 0;
        result = 31 * result + (toStationName != null ? toStationName.hashCode() : 0);
        result = 31 * result + distance;
        result = 31 * result + (lineName != null ? lineName.hashCode() : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + (lstStationNames != null ? lstStationNames.hashCode() : 0);
        return result;
    }
}
