package com.itheima.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.itheima.mobilesafe.db.BlacklistDbOpenHelper;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.objects.BlockedCaller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2016/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistDao implements BaseDao {
    private final static String TAG = "BlacklistDao";
    private BlacklistDbOpenHelper dbOpenHelper;
    /**
     * null
     */
    @SuppressWarnings("unused")
    public static final int NOT_FOUND = -1;
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
    public static final String MODES[] = {"全部拦截", "电话拦截", "短信拦截"};
    private final String TABLE = "blacklist";

    public BlacklistDao(Context ctx) {
        dbOpenHelper = new BlacklistDbOpenHelper(ctx);
    }

    /**
     * block sms
     */
    @SuppressWarnings("unused")
    public static final int DB_ERROR = 101;
    public static final int DUPLICATE_DARA = 102;

    /**
     * Callback
     */
    public interface OnResponse {
        void OnFinish();

        void onFail(int what, String errorMessage);
    }

    /**
     * Query the blacklist and check the number
     *
     * @param number phone number
     * @return whether the phone number is in the blacklist,
     * returning -1 if it's not found, and returning mode of blocked-mode
     */
    public int findMode(String number) {
        int mode = NOT_FOUND;
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE + " where number=?", new String[]{number});
            if (cursor.moveToNext()) {
                index = cursor.getColumnIndex("mode");
                mode = Integer.parseInt(cursor.getString(index));
            }
            cursor.close();
            db.close();
        }
        return mode;
    }

    /**
     * Query the blacklist and check the number
     *
     * @param number phone number
     * @return whether the phone number is in the blacklist.
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
        if (!find(caller.getNumber())) {
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
                    response.onFail(DB_ERROR, "数据库错误");
            }
        } else {
            if (response != null)
                response.onFail(DUPLICATE_DARA, "重复添加");
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
            } else {
                if (response != null)
                    response.onFail(DB_ERROR, "数据库错误");
            }
        } else {
            add(caller, response);
        }
    }


    /**
     * Modify number, mode and name of the blocked-caller
     *
     * @param caller   modify data by identifying _id, and this is used to do swap
     *                 name:the name would be add into the blacklist
     *                 number:the number would be add into the blacklist
     *                 MODE:MODE_BOTH_BLOCKED, MODE_CALLS_BLOCKED or MODE_SMS_BLOCKED
     * @param response return the result of modifying data
     */
    private void modifyById(String _id, BlockedCaller caller, @Nullable OnResponse response) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put("number", caller.getNumber());
            values.put("mode", caller.getMODE());
            values.put("name", caller.getName());
            db.update(TABLE, values, "_id=?", new String[]{_id});
            db.close();
            if (response != null)
                response.OnFinish();
        } else {
            if (response != null)
                response.onFail(DB_ERROR, "数据库错误");
        }
    }


    private int index;
    private String id1 = null;
    private String id2 = null;

    /**
     * Swap data1 and data2 in the table
     *
     * @param caller1  data1
     * @param caller2  data2
     * @param response return the result of swapping data
     */
    public void swap(final BlockedCaller caller1, BlockedCaller caller2, @Nullable final OnResponse response) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor1 = db.rawQuery("select * from " + TABLE + " where number=?", new String[]{caller1.getNumber()});

            // cursor.moveToFirst()代表游标移动到第一笔数据
            if (cursor1.moveToFirst()) {
                // 得到_id在表中是第几列
                index = cursor1.getColumnIndex("_id");
                id1 = cursor1.getString(index);
            }

            Cursor cursor2 = db.rawQuery("select * from " + TABLE + " where number=?", new String[]{caller2.getNumber()});
            if (cursor2.moveToFirst()) {
                index = cursor2.getColumnIndex("_id");
                id2 = cursor2.getString(index);
            }
            CLog.d(TAG, "id1=" + id1 + "/id2=" + id2);
            modifyById(id1, caller2, new OnResponse() {
                @Override
                public void OnFinish() {
                    modifyById(id2, caller1, response);
                }

                @Override
                public void onFail(int what, String errorMessage) {
                    if (response != null)
                        response.onFail(DB_ERROR, "数据库错误");
                }
            });

        } else {
            if (response != null)
                response.onFail(DB_ERROR, "数据库错误");
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
