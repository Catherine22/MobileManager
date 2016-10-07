package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.itheima.mobilesafe.db.BlacklistDbOpenHelper;
import com.itheima.mobilesafe.utils.objects.BlockedCaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2016/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistDao implements BaseDao {
    private BlacklistDbOpenHelper dbOpenHelper;
    /**
     * block calls and sms
     */
    @SuppressWarnings("unused")
    public static final int MODE_BOTH_BLOCKED = 0;
    /**
     * block calls
     */
    @SuppressWarnings("unused")
    public static final int MODE_CALLS_BLOCKED = 1;
    /**
     * block sms
     */
    @SuppressWarnings("unused")
    public static final int MODE_SMS_BLOCKED = 2;
    public static final String MODES[] = {"全部拦截", "短信拦截", "电话拦截"};
    private final String TABLE = "blacklist";

    public BlacklistDao(Context ctx) {
        dbOpenHelper = new BlacklistDbOpenHelper(ctx);
    }

    /**
     * Callback
     */
    public interface OnResponse {
        void OnFinish();

        void onFail();
    }

    /**
     * Query the blacklist and check the number
     *
     * @param number phone number
     * @return whether the phone number is in the blacklist
     */
    @Override
    public boolean find(String number) {
        boolean isExist = false;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE + " where number=?", new String[]{number});
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
     * @param caller   number as an id
     *                 name:the name would be add into the blacklist
     *                 number:the number would be add into the blacklist
     *                 MODE:MODE_BOTH_BLOCKED, MODE_CALLS_BLOCKED or MODE_SMS_BLOCKED
     * @param response return the result of inserting data
     */
    public void add(BlockedCaller caller, @Nullable OnResponse response) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("name", caller.getName());
            values.put("number", caller.getNumber());
            values.put("mode", caller.getMODE());
            db.insert(TABLE, null, values);
            db.close();
            if (response != null)
                response.OnFinish();
        } else {
            if (response != null)
                response.onFail();
        }
    }

    /**
     * Modify mode and name of the blocked-caller
     *
     * @param caller   modify data by identifying number
     *                 name:the name would be add into the blacklist
     *                 number:the number would be add into the blacklist
     *                 MODE:MODE_BOTH_BLOCKED, MODE_CALLS_BLOCKED or MODE_SMS_BLOCKED
     * @param response return the result of modifying data
     */
    public void modify(BlockedCaller caller, @Nullable OnResponse response) {
        if (find(caller.getNumber())) {
            SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
            if (db.isOpen()) {
                ContentValues values = new ContentValues();
                values.put("number", caller.getNumber());
                values.put("mode", caller.getMODE());
                values.put("name", caller.getName());
                db.update(TABLE, values, "number=?", new String[]{caller.getNumber()});
                db.close();
                if (response != null)
                    response.OnFinish();
            } else{
                if (response != null)
                    response.onFail();
            }
        }else {
            add(caller, response);
        }
    }

    /**
     * Remove the number from blacklist
     *
     * @param number modify data by identifying number
     */
    @Override
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
     * @return contents of blacklist the table
     */
    @Override
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
