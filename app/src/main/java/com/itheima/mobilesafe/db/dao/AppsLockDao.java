package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.AppsLockDbOpenHelper;
import com.itheima.mobilesafe.utils.BroadcastActions;
import com.itheima.mobilesafe.utils.SpNames;

import java.util.ArrayList;
import java.util.List;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/11/8.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class AppsLockDao implements BaseDao {
    private final static String TAG = "AppsLockDao";
    private AppsLockDbOpenHelper dbOpenHelper;
    private final String TABLE = "apps";
    private Server sv;

    public AppsLockDao(Context ctx) {
        dbOpenHelper = new AppsLockDbOpenHelper(ctx);
        sv = new Server(ctx, new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {

            }
        });
    }

    public void add(String packageName) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("name", packageName);
            db.insert(TABLE, null, values);
            db.close();
            sv.pushBoolean(BroadcastActions.UPDATE_WATCHDOG, true);
        }
    }

    /**
     * 查询数据库太慢，改成查内存（使用queryAll()）
     *
     * @param packageName
     * @return
     */
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
            sv.pushBoolean(BroadcastActions.UPDATE_WATCHDOG, true);
        }
    }

    /**
     * 使用queryAll拿到全部的数据，再用list暂存，以后要查询时就直接查list
     * 查内存的速度比查文件或数据库快10倍以上
     *
     * @return
     */
    @Override
    public List<String> queryAll() {
        List<String> packnames = new ArrayList<>();
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.query(TABLE, new String[]{"name"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                packnames.add(cursor.getString(0));
            }
            cursor.close();
            db.close();
        }
        return packnames;
    }
}