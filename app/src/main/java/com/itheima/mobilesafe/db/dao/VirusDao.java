package com.itheima.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/11/10.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class VirusDao implements BaseDao {
    private final static String TAG = "VirusDao";
    private SQLiteDatabase sqLiteDatabase;
    private Context ctx;

    public VirusDao(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean find(String md5) throws Exception {
        boolean result = false;
        sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + ctx.getPackageName() + "/files/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
        if (sqLiteDatabase.isOpen()) {
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM datable WHERE md5 = ?", new String[]{md5});
            while (cursor.moveToNext())
                result = true;
            cursor.close();
            sqLiteDatabase.close();
        } else
            result = false;
        return result;
    }

    @Override
    public void remove(String key) throws Exception {

    }

    @Override
    public Object queryAll() throws Exception {
        return null;
    }
}
