package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlacklistDbOpenHelper;
import com.itheima.mobilesafe.utils.objects.BlockedCaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2016/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistDao {
    private BlacklistDbOpenHelper dbOpenHelper;
    public static final int MODE_BOTH_BLOCKED = 0;
    public static final int MODE_CALLS_BLOCKED = 1;
    public static final int MODE_SMS_BLOCKED = 2;
    public static final String MODES[] = {"全部拦截", "短信拦截", "电话拦截"};
    private final String TABLE = "blacklist";

    public BlacklistDao(Context ctx) {
        dbOpenHelper = new BlacklistDbOpenHelper(ctx);
    }

    /**
     * Query the blacklist and check the number
     *
     * @param number
     * @return whether the phone number is in the blacklist
     */
    public boolean find(String number) {
        boolean isExist = false;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE + "where number=?", new String[]{number});
            if (cursor.moveToNext())
                isExist = true;
            cursor.close();
            db.close();
        }
        return isExist;
    }

    /**
     * Add a blocked-number and set mode
     *
     * @param name   the name would be add into the blacklist
     * @param number the number would be add into the blacklist
     * @param MODE   MODE_BOTH_BLOCKED, MODE_CALLS_BLOCKED or MODE_SMS_BLOCKED
     */
    public void add(String name, String number, int MODE) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("number", number);
            values.put("mode", MODE);
            db.insert(TABLE, null, values);
            db.close();
        }
    }

    /**
     * Modify mode and name of the blocked-caller
     *
     * @param name
     * @param number identify
     * @param mode
     */
    public void modify(String name, String number, int mode) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("number", number);
            values.put("mode", mode);
            values.put("name", name);
            db.update(TABLE, values, "number=?", new String[]{number});
            db.close();
        }
    }

    /**
     * Remove the number from blacklist
     *
     * @param number
     */
    public void remove(String number) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE, "number=?", new String[]{number});
            db.close();
        }
    }

    /**
     * Get all blocked-callers
     *
     * @return
     */
    public List<BlockedCaller> queryAll() {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        List<BlockedCaller> myList = null;
        if (db.isOpen()) {
            myList = new ArrayList<>();
            Cursor cursor = db.rawQuery("select * from " + TABLE, null);
            while (cursor.moveToNext()) {
                int numberIndex = cursor.getColumnIndex("number");
                int nameIndex = cursor.getColumnIndex("name");
                int modeIndex = cursor.getColumnIndex("mode");

                BlockedCaller blockedCaller = new BlockedCaller();
                blockedCaller.setNumber(cursor.getString(numberIndex));
                blockedCaller.setName(cursor.getString(nameIndex));
                blockedCaller.setMODE(cursor.getInt(modeIndex));
                myList.add(blockedCaller);
            }
            cursor.close();
            db.close();
        }
        return myList;
    }
}
