package com.dsunny.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dsunny.common.AppConstants;

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
        db = SQLiteDatabase.openOrCreateDatabase(AppConstants.SUBWAY_DB_FILE_PATH, null);
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
     * 关闭数据库
     */
    public void closeDB() {
        if (db != null) {
            db.close();
        }
    }
}
