package com.dsunny.database;

import com.dsunny.database.bean.Transfer;

import java.util.List;

/**
 * TRANSFER表的查询类
 */
public class TransferDao extends BaseDao {

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
