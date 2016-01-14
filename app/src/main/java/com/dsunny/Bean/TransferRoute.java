package com.dsunny.Bean;

import java.io.Serializable;
import java.util.List;

/**
 * 换乘路线，例如：丰台科技园-北京站，包含两条换乘子路径
 */
public class TransferRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    public String fromStationName;// 查询起始站
    public String toStationName;// 查询终点站
    public int ticketPrice;// 票价(元)
    public int elapsedTime;// 运行时间(分钟)
    public List<TransferSubRoute> lstTransferPath;// 换乘子路径


}
