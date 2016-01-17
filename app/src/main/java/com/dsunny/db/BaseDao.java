package com.dsunny.db;

import android.database.Cursor;

import com.infrastructure.utils.UtilsLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Subway数据库查询的基类
 */
public class BaseDao {
    protected SubwayDB db;

    /**
     * 构造函数
     */
    public BaseDao() {
        db = SubwayDB.getInstance();
    }

    /**
     * 检索Count
     *
     * @param sql SQL文
     * @return 检索结果(Count)
     */
    protected int queryCount(String sql) {
        Cursor c = db.query(sql);
        int result = c.getCount();

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, result);

        c.close();
        return result;
    }

    /**
     * 检索Int值
     *
     * @param sql SQL文
     * @return 检索结果(Int)
     */
    protected int queryInt(String sql) {
        int result = -1;

        Cursor c = db.query(sql);
        if (c.moveToFirst()) {
            result = c.getInt(0);
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, result);

        c.close();
        return result;
    }

    /**
     * 检索String值
     *
     * @param sql SQL文
     * @return 检索结果(String)
     */
    protected String queryString(String sql) {
        String result = "";

        Cursor c = db.query(sql);
        if (c.moveToFirst()) {
            result = c.getString(0);
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, result);

        c.close();
        return result;
    }

    /**
     * 检索Bean
     *
     * @param sql    SQL文
     * @param tClass Bean的实体类
     * @param <T>    Bean的实体类
     * @return 检索结果(Bean)
     */
    protected <T> T queryBean(String sql, Class<T> tClass) {
        T t = null;

        Cursor c = db.query(sql);
        try {
            if (c.moveToFirst()) {
                int count = c.getColumnCount();
                t = tClass.newInstance();
                for (int i = 0; i < count; i++) {
                    Field field = t.getClass().getField(c.getColumnName(i));
                    if (field != null) {
                        if (field.getGenericType().toString().equals("class java.lang.String")) {
                            field.set(t, c.getString(i));
                        } else if (field.getGenericType().toString().equals("class java.lang.Integer")) {
                            field.set(t, c.getInt(i));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, t == null ? "" : t.toString());

        c.close();
        return t;
    }

    /**
     * 检索List<Integer>
     *
     * @param sql SQL文
     * @return 检索结果(List<Integer>)
     */
    protected List<Integer> queryListInt(String sql) {
        List<Integer> results = new ArrayList<>();

        Cursor c = db.query(sql);
        while (c.moveToNext()) {
            results.add(c.getInt(0));
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, results.toString());

        c.close();
        return results;
    }

    /**
     * 检索List<String>
     *
     * @param sql SQL文
     * @return 检索结果(List<String>)
     */
    protected List<String> queryListString(String sql) {
        List<String> results = new ArrayList<>();

        Cursor c = db.query(sql);
        while (c.moveToNext()) {
            results.add(c.getString(0));
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, results.toString());

        c.close();
        return results;
    }

    /**
     * 检索List<Bean>
     *
     * @param sql    SQL文
     * @param tClass Bean的实体类
     * @param <T>    Bean的实体类
     * @return 检索List<Bean>
     */
    protected <T> List<T> queryListBean(String sql, Class<T> tClass) {
        List<T> results = new ArrayList<>();

        Cursor c = db.query(sql);
        try {
            int count = c.getColumnCount();
            while (c.moveToNext()) {
                T t = tClass.newInstance();
                for (int i = 0; i < count; i++) {
                    Field field = t.getClass().getField(c.getColumnName(i));
                    if (field != null) {
                        if (field.getGenericType().toString().equals("class java.lang.String")) {
                            field.set(t, c.getString(i));
                        } else if (field.getGenericType().toString().equals("class java.lang.Integer")) {
                            field.set(t, c.getInt(i));
                        }
                    }
                }
                results.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, results.toString());

        c.close();
        return results;
    }
}
