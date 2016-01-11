package com.dsunny.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dsunny.engine.AppConstants;
import com.infrastructure.utils.UtilsLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 地铁数据库
 */
public class SubwayDB {

    private SQLiteDatabase db;
    private static SubwayDB subway;

    /**
     * 单例模式
     */
    private SubwayDB() {
        db = SQLiteDatabase.openOrCreateDatabase(AppConstants.DB_FILE_PATH, null);
    }

    /**
     * 获取地铁数据库实例
     *
     * @return 地铁数据库实例
     */
    public static SubwayDB getInstance() {
        if (subway == null) {
            subway = new SubwayDB();
        }
        return subway;
    }

    /**
     * 检索SQL
     *
     * @param sql SQL文
     * @return 检索结果(Cursor)
     */
    public Cursor query(String sql) {
        return db.rawQuery(sql, null);
    }

    /**
     * 检索Count
     *
     * @param sql SQL文
     * @return 检索结果(Count)
     */
    public int queryCount(String sql) {
        Cursor c = query(sql);
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
    public int queryInt(String sql) {
        int result = -1;

        Cursor c = query(sql);
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
    public String queryString(String sql) {
        String result = "";

        Cursor c = query(sql);
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
    public <T> T queryBean(String sql, Class<T> tClass) {
        T t = null;

        Cursor c = query(sql);
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
    public List<Integer> queryListInt(String sql) {
        List<Integer> results = new ArrayList<>();

        Cursor c = query(sql);
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
    public List<String> queryListString(String sql) {
        List<String> results = new ArrayList<>();

        Cursor c = query(sql);
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
    public <T> List<T> queryListBean(String sql, Class<T> tClass) {
        List<T> results = new ArrayList<>();

        Cursor c = query(sql);
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UtilsLog.d(UtilsLog.TAG_SQL, sql);
        UtilsLog.d(UtilsLog.TAG_SQL, results.toString());

        c.close();
        return results;
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        db.close();
    }
}
