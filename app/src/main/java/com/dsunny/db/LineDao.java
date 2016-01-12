package com.dsunny.db;

import com.dsunny.engine.SubwayData;

/**
 * LINE表的查询类
 */
public class LineDao extends BaseDao {

    /**
     * 获取指定线路ID的线路名
     *
     * @param lid 线路ID
     * @return 线路名
     */
    public String getLineName(String lid) {
        for (int i = 0; i < SubwayData.LINES_ID.length; i++) {
            if (SubwayData.LINES_ID[i].equals(lid)) {
                return SubwayData.LINES_NAME[i];
            }
        }
        return "";
    }

    /**
     * 判断线路是否是环线，用于计算换乘方向
     *
     * @param lid 线路ID
     * @return true，是环线；false，非环线
     */
    public boolean isCircularLine(String lid) {
        for (String id : SubwayData.ID_CIRCULAR_LINES) {
            if (id.equals(lid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断线路是否存在环路，用于计算同一线路上的两车站的最短换乘路径
     *
     * @param lid 线路ID
     * @return true，存在环路；false，不存在环路
     */
    public boolean isLineExistLoop(String lid) {
        for (String id : SubwayData.ID_LINES_EXIST_LOOP) {
            if (id.equals(lid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定线路ID的最小车站ID的车站名
     *
     * @param lid 线路ID
     * @return 车站名
     */
    public String getLineFirstStation(String lid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT Name ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE STATION.ID = ( ");
        sql.append("     SELECT MIN(ID) ");
        sql.append("     FROM STATION ");
        sql.append("     WHERE ID LIKE '").append(lid).append("' ");
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
    public String getLineLastStation(String lid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT Name ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE STATION.ID = ( ");
        sql.append("     SELECT MAX(ID) ");
        sql.append("     FROM STATION ");
        sql.append("     WHERE ID LIKE '").append(lid).append("' ");
        sql.append("     AND State = '1' ");
        sql.append(" ) ");

        return queryString(sql.toString());
    }

    /**
     * 获取车站相临接的换乘车站或终点站ID
     *
     * @param sid 车站ID
     * @return [车站ID较小值，车站ID较大值]
     */
    public String[] getAdjacentSids(String sid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT MAX(L1.SID) ");
        sql.append(" FROM ( ");
        sql.append("     SELECT LINE.SID ");
        sql.append("     FROM STATION, LINE ");
        sql.append("     WHERE STATION.ID LIKE '").append(sid.substring(0, 2)).append("%' ");
        sql.append("     AND STATION.State = '1' ");
        sql.append("     AND LINE.SType <> '0' ");
        sql.append("     AND STATION.ID = LINE.SID ");
        sql.append(" ) L1 ");
        sql.append(" WHERE L1.SID < '").append(sid).append("' ");
        sql.append(" UNION ALL ");
        sql.append(" SELECT MIN(L2.SID) ");
        sql.append(" FROM ( ");
        sql.append("     SELECT LINE.SID ");
        sql.append("     FROM STATION, LINE ");
        sql.append("     WHERE STATION.ID LIKE '").append(sid.substring(0, 2)).append("%' ");
        sql.append("     AND STATION.State = '1' ");
        sql.append("     AND LINE.SType <> '0' ");
        sql.append("     AND STATION.ID = LINE.SID ");
        sql.append(" ) L2 ");
        sql.append(" WHERE L2.SID > '").append(sid).append("' ");

        return queryListString(sql.toString()).toArray(new String[2]);
    }


    /**
     * 获取两站之间的距离
     *
     * @param from 两站起始站ID
     * @param to   两站终止站ID
     * @return 两站之间的距离，单位m
     */
    public int getIntervalDistance(String from, String to) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT SUM(Distance) ");
        sql.append(" FROM LINE ");
        if (from.compareTo(to) < 0) {
            sql.append(" WHERE SID >= '").append(from).append("' ");
            sql.append(" AND SID < '").append(to).append("' ");
        } else {
            sql.append(" WHERE SID >= '").append(to).append("' ");
            sql.append(" AND SID < '").append(from).append("' ");
        }

        return queryInt(sql.toString());
    }

}
