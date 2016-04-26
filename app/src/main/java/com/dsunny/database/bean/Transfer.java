package com.dsunny.database.bean;

/**
 * TRANSFER表的实体类
 */
public class Transfer {

    public String FromSID;// 起始车站
    public String ToSID;// 到达车站
    public String LID;// 所在线路
    public int Distance;// 距离

    @Override
    public String toString() {
        return "Transfer{" +
                "FromSID='" + FromSID + '\'' +
                ", ToSID='" + ToSID + '\'' +
                ", LID='" + LID + '\'' +
                ", Distance=" + Distance +
                '}';
    }
}
