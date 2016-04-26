package com.dsunny.database;

import java.util.List;

/**
 * LINE表的查询类
 */
public class LineDao extends BaseDao {

    /**
     * 获取指定线路ID的最小车站ID的车站名
     *
     * @param lid 线路ID
     * @return 车站名
     */
    public String getLineFirstStationName(String lid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT Name ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE ID = ( ");
        sql.append("     SELECT MIN(ID) ");
        sql.append("     FROM STATION ");
        sql.append("     WHERE ID LIKE '").append(lid).append("%' ");
        sql.append("     AND State = '1' ");
        sql.append(" ) ");

        return queryString(sql.toString());
    }

    /**
     * 获取指定线路ID的最大车站ID的车站名
     *
     * @param lid 线路ID
     * @return 车站名
     */
    public String getLineLastStationName(String lid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT Name ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE ID = ( ");
        sql.append("     SELECT MAX(ID) ");
        sql.append("     FROM STATION ");
        sql.append("     WHERE ID LIKE '").append(lid).append("%' ");
        sql.append("     AND State = '1' ");
        sql.append(" ) ");

        return queryString(sql.toString());
    }

    /**
     * 获取车站相临接的换乘车站或终点站的车站ID
     *
     * @param sid 车站ID
     * @return [车站ID较小值，车站ID较大值]
     */
    public List<String> getAdjacentStationIds(String sid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT MAX(L1.SID) ");
        sql.append(" FROM ( ");
        sql.append("     SELECT SID ");
        sql.append("     FROM LINE ");
        sql.append("     WHERE SID LIKE '").append(sid.substring(0, 2)).append("%' ");
        sql.append("     AND SType <> '0' ");
        sql.append(" ) L1 ");
        sql.append(" WHERE L1.SID < '").append(sid).append("' ");
        sql.append(" UNION ALL ");
        sql.append(" SELECT MIN(L2.SID) ");
        sql.append(" FROM ( ");
        sql.append("     SELECT SID ");
        sql.append("     FROM LINE ");
        sql.append("     WHERE SID LIKE '").append(sid.substring(0, 2)).append("%' ");
        sql.append("     AND SType <> '0' ");
        sql.append(" ) L2 ");
        sql.append(" WHERE L2.SID > '").append(sid).append("' ");

        return queryListString(sql.toString());
    }


    /**
     * 获取两站之间的距离
     *
     * @param fromSid 两站起点站ID
     * @param toSid   两站终点站ID
     * @return 两站之间的距离，单位m
     */
    public int getIntervalDistance(String fromSid, String toSid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT SUM(Distance) ");
        sql.append(" FROM LINE ");
        if (fromSid.compareTo(toSid) < 0) {
            sql.append(" WHERE SID >= '").append(fromSid).append("' ");
            sql.append(" AND SID < '").append(toSid).append("' ");
        } else {
            sql.append(" WHERE SID >= '").append(toSid).append("' ");
            sql.append(" AND SID < '").append(fromSid).append("' ");
        }

        return queryInt(sql.toString());
    }

    /**
     * 判断同一线路的两个普通车站是否在相同区间
     *
     * @param fromSid 两站起点站ID
     * @param toSid   两站终点站ID
     * @return true，在相同区间；false，不在相同区间
     */
    public boolean isFromToStationInSameInterval(String fromSid, String toSid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) ");
        sql.append(" FROM LINE ");
        if (fromSid.compareTo(toSid) < 0) {
            sql.append(" WHERE SType <> '0' ");
            sql.append(" AND SID < '").append(toSid).append("' ");
            sql.append(" AND SID > '").append(fromSid).append("' ");
        } else {
            sql.append(" WHERE SType <> '0' ");
            sql.append(" AND SID < '").append(fromSid).append("' ");
            sql.append(" AND SID > '").append(toSid).append("' ");
        }

        return queryCount(sql.toString()) == 0;
    }

}
