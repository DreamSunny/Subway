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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransferRoute that = (TransferRoute) o;

        if (airportLineDistance != that.airportLineDistance) return false;
        if (otherLineDistance != that.otherLineDistance) return false;
        if (elapsedTime != that.elapsedTime) return false;
        if (fromStationName != null ? !fromStationName.equals(that.fromStationName) : that.fromStationName != null)
            return false;
        if (toStationName != null ? !toStationName.equals(that.toStationName) : that.toStationName != null)
            return false;
        return !(lstTransferSubRoute != null ? !lstTransferSubRoute.equals(that.lstTransferSubRoute) : that.lstTransferSubRoute != null);

    }

    @Override
    public int hashCode() {
        int result = fromStationName != null ? fromStationName.hashCode() : 0;
        result = 31 * result + (toStationName != null ? toStationName.hashCode() : 0);
        result = 31 * result + airportLineDistance;
        result = 31 * result + otherLineDistance;
        result = 31 * result + elapsedTime;
        result = 31 * result + (lstTransferSubRoute != null ? lstTransferSubRoute.hashCode() : 0);
        return result;
    }
}
