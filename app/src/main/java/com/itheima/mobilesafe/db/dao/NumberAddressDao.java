package com.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
public class NumberAddressDao {
    private final static String TAG = "NumberAddressDao";

    /**
     * 传一个手机号码进来,返回电话号码归属地
     *
     * @param number
     * @return
     */
    public String queryNumber(String number) {
        String address = "";
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(Constants.DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
        while (cursor.moveToNext()) {
            String location = cursor.getString(0);
            address = location;
        }
        cursor.close();
        return address;
    }
}
