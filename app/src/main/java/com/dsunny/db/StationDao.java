package com.dsunny.db;

import com.dsunny.Bean.Station;

import java.util.List;

/**
 * STATION表的查询类
 */
public class StationDao extends BaseDao {

    /**
     * 获取指定车站ID的车站名
     *
     * @param sid 车站ID
     * @return 车站名
     */
    public String getStationName(String sid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT Name ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE ID = '").append(sid).append("' ");

        return queryString(sql.toString());
    }

    /**
     * 判断指定车站名是否存在
     *
     * @param name 车站名
     * @return true，存在；false，不存在
     */
    public boolean isStationExists(String name) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE Name = '").append(name).append("' ");
        sql.append(" AND State = '1' ");

        return queryCount(sql.toString()) != 0;
    }

    /**
     * 判断指定车站ID是否为普通车站(存在相邻的换乘车站)
     *
     * @param sid 车站ID
     * @return true，是普通车站；false，换乘站或首尾车站或单向车站
     */
    public boolean isOrdinaryStation(String sid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) ");
        sql.append(" FROM LINE ");
        sql.append(" WHERE SID = '").append(sid).append("' ");
        sql.append(" AND SType == '0' ");

        return queryCount(sql.toString()) != 0;
    }

    /**
     * 获取指定车站名的所有车站ID
     *
     * @param name 车站名
     * @return 车站ID集合
     */
    public List<String> getStationIdsByStationName(String name){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE Name = '").append(name).append("' ");
        sql.append(" AND State == '1' ");
        sql.append(" ORDER BY ID ASC ");

        return queryListString(sql.toString());
    }

    /**
     * 获取指定车站ID的所有车站ID
     *
     * @param sid 车站ID
     * @return 车站ID集合
     */
    public List<String> getStationIdsByStationId(String sid){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ID ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE Name = ( ");
        sql.append("     SELECT Name ");
        sql.append("     FROM STATION ");
        sql.append("     WHERE ID = '").append(sid).append("' ");
        sql.append(" ) ");
        sql.append(" AND State = '1' ");
        sql.append(" ORDER BY ID ASC ");

        return queryListString(sql.toString());
    }

    /**
     * 获取指定车站名的所有线路ID(14号线分为AB段，本期不用)
     *
     * @param name 车站名
     * @return 线路ID集合
     */
    public List<String> getLineIdsOfSameStation(String name){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT SUBSTR(ID, 1, 2) ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE Name = '").append(name).append("' ");
        sql.append(" AND State == '1' ");

        return queryListString(sql.toString());
    }

    /**
     * 获取指定线路的所有车站名
     *
     * @param lid 线路id
     * @return 线路id的所有车站名
     */
    public List<String> getLineStationNames(String lid) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT Name ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE ID LIKE '").append(lid).append("%' ");
        sql.append(" AND State = '1' ");
        sql.append(" ORDER BY ID ");

        return queryListString(sql.toString());
    }

    /**
     * 获取车站名和拼音缩写，按照拼音缩写升序
     *
     * @return 车站名和拼音缩写
     */
    public List<Station> getAllStationNamesAndAbbrs(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT DISTINCT Name, Abbr ");
        sql.append(" FROM STATION ");
        sql.append(" WHERE State = '1' ");
        sql.append(" ORDER BY Abbr ASC ");

        return queryListBean(sql.toString(), Station.class);
    }

    /**
     * 获取车站ID之间的车站名
     *
     * @param from 起始车站ID
     * @param to   终止车站ID
     * @return 起始至终止车站ID之间车站名(不包括from和to)
     */
    public List<String> getIntervalStationNames(String from, String to) {
        StringBuilder sql = new StringBuilder();
        if (from.compareTo(to) < 0) {
            sql.append(" SELECT Name ");
            sql.append(" FROM STATION ");
            sql.append(" WHERE ID > '").append(from).append("' ");
            sql.append(" AND ID < '").append(to).append("' ");
            sql.append(" AND State = '1' ");
            sql.append(" ORDER BY ID ASC ");
        } else {
            sql.append(" SELECT Name ");
            sql.append(" FROM STATION ");
            sql.append(" WHERE ID > '").append(to).append("' ");
            sql.append(" AND ID < '").append(from).append("' ");
            sql.append(" AND State = '1' ");
            sql.append(" ORDER BY ID DESC ");
        }

        return queryListString(sql.toString());
    }

    /**
     * 获取环线车站ID之间的车站名(包含起始或终点车站)
     *
     * @param from 起始车站ID
     * @param to   终止车站ID
     * @return 环线起始至终止车站ID之间车站名(不包括from和to)
     */
    public List<String> getIntervalStationNamesInCircularLine(String from, String to) {
        StringBuilder sql = new StringBuilder();
        if (from.compareTo(to) < 0) {
            sql.append(" SELECT * FROM ( ");
            sql.append("     SELECT Name ");
            sql.append("     FROM STATION ");
            sql.append("     WHERE ID LIKE '").append(from.substring(0, 2)).append("%' ");
            sql.append("     AND ID < '").append(from).append("' ");
            sql.append("     AND State = '1' ");
            sql.append("     ORDER BY ID DESC ");
            sql.append(" ) ");
            sql.append(" UNION ALL ");
            sql.append(" SELECT * FROM ( ");
            sql.append("     SELECT Name ");
            sql.append("     FROM STATION ");
            sql.append("     WHERE ID LIKE '").append(from.substring(0, 2)).append("%' ");
            sql.append("     AND ID > '").append(to).append("' ");
            sql.append("     AND State = '1' ");
            sql.append("     ORDER BY ID DESC ");
            sql.append(" ) ");
        } else {
            sql.append(" SELECT * FROM ( ");
            sql.append("     SELECT Name ");
            sql.append("     FROM STATION ");
            sql.append("     WHERE ID LIKE '").append(from.substring(0, 2)).append("%' ");
            sql.append("     AND ID > '").append(from).append("' ");
            sql.append("     AND State = '1' ");
            sql.append("     ORDER BY ID ASC ");
            sql.append(" ) ");
            sql.append(" UNION ALL ");
            sql.append(" SELECT * FROM ( ");
            sql.append("     SELECT Name ");
            sql.append("     FROM STATION ");
            sql.append("     WHERE ID LIKE '").append(from.substring(0, 2)).append("%' ");
            sql.append("     AND ID < '").append(to).append("' ");
            sql.append("     AND State = '1' ");
            sql.append("     ORDER BY ID ASC ");
            sql.append(" ) ");
        }

        return queryListString(sql.toString());
    }
}
