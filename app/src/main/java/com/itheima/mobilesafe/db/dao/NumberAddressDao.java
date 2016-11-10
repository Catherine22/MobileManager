package com.itheima.mobilesafe.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.itheima.mobilesafe.utils.Constants;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * 因为初始目录file://android_aset/address.db无法访问
 * 所以一开始得先拷贝数据库到/data/data/包名/files目录下(App启动时执行)
 */
public class NumberAddressDao implements BaseDao {
    private final static String TAG = "NumberAddressDao";
    private SQLiteDatabase sqLiteDatabase;
    private Context ctx;

    public NumberAddressDao(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 传一个手机号码进来,返回电话号码归属地
     *
     * @param number 手机号码
     * @return address
     */
    public String queryNumber(String number) {
        String address = "";
        sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + ctx.getPackageName() + "/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
        try {
            while (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
        }
        return address;
    }

    /**
     * 检查号码是否存在列表中
     *
     * @param number 手机号码
     * @return boolean 是否存在列表中
     */
    @Override
    public boolean find(String number) {
        String address = "";
        sqLiteDatabase = SQLiteDatabase.openDatabase("/data/data/" + ctx.getPackageName() + "/files/address.db", null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
        try {
            while (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (sqLiteDatabase.isOpen())
                sqLiteDatabase.close();
        }
        return !TextUtils.isEmpty(address);
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public Object queryAll() {
        return null;
    }
}
