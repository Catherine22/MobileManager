package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.AppsLockDbOpenHelper;

/**
 * Created by Catherine on 2016/11/8.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class AppsLockDao implements BaseDao {
    private final static String TAG = "AppsLockDao";
    private AppsLockDbOpenHelper dbOpenHelper;
    private final String TABLE = "apps";

    public AppsLockDao(Context ctx) {
        dbOpenHelper = new AppsLockDbOpenHelper(ctx);
    }

    public void add(String packageName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("name", packageName);
            db.insert(TABLE, null, values);
            db.close();
        }
    }


    @Override
    public boolean find(String packageName) {
        boolean isExist = false;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE + " where name=?", new String[]{packageName});
            if (cursor.moveToNext())
                isExist = true;
            cursor.close();
            db.close();
        }
        return isExist;
    }

    @Override
    public void remove(String packageName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE, "name=?", new String[]{packageName});
            db.close();
        }
    }

    @Override
    public Object queryAll() {
        return null;
    }
}