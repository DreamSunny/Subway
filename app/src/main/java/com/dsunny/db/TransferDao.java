package com.dsunny.db;

import com.dsunny.Bean.Transfer;

import java.util.List;

/**
 * TRANSFER表的查询类
 */
public class TransferDao extends BaseDao {

    /**
     * 计算乘车价格
     *
     * @param airportLineDistance 机场线乘车距离
     * @param otherLinesDistance  其他线路乘车距离
     * @return 乘车价格
     */
    public int getTransferPrice(int airportLineDistance, int otherLinesDistance) {
        int price = 0;
        if (airportLineDistance != 0) {
            price += 25;
        }
        if (otherLinesDistance == 0) {
            price += 0;
        } else if (otherLinesDistance <= 6000) {
            price += 3;
        } else if (otherLinesDistance <= 12000) {
            price += 4;
        } else if (otherLinesDistance <= 22000) {
            price += 5;
        } else if (otherLinesDistance <= 32000) {
            price += 6;
        } else {
            price += 7 + (otherLinesDistance - 32000) / 20000;
        }

        return price;
    }

    /**
     * 计算乘车时间
     *
     * @param airportLineDistance 机场线乘车距离
     * @param otherLinesDistance  其他线路乘车距离
     * @return 乘车时间
     */
    public int getTransferElapsedTime(int airportLineDistance, int otherLinesDistance) {
        return airportLineDistance / 1000 + otherLinesDistance / 500 + 1;
    }

    /**
     * 获取指定线路ID的换乘信息
     *
     * @param lid 线路id
     * @return 该线路的换乘信息
     */
    public List<Transfer> getLineTransfers(String lid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * ");
        sql.append(" FROM TRANSFER ");
        sql.append(" WHERE LID = '").append(lid).append("' ");

        return queryListBean(sql.toString(), Transfer.class);
    }

    /**
     * 获取指定数组线路ID换乘信息
     *
     * @param lids 线路id数组
     * @return 线路数组的换乘信息
     */
    public List<Transfer> getLinesTransfers(String[] lids) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * ");
        sql.append(" FROM TRANSFER ");
        sql.append(" WHERE LID IN ( ");
        for (String lid : lids) {
            sql.append(" '").append(lid).append("', ");
        }
        sql.append(" '') ");

        return queryListBean(sql.toString(), Transfer.class);
    }

    /**
     * 获取TRANSFER表的换乘信息(所有线路)
     *
     * @return 所有线路的换乘信息
     */
    public List<Transfer> getAllTransfers() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * ");
        sql.append(" FROM TRANSFER ");

        return queryListBean(sql.toString(), Transfer.class);
    }
}
