package com.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yi-Jing on 2016/9/28.
 */

public class BlacklistDbOpenHelper extends SQLiteOpenHelper {
    public BlacklistDbOpenHelper(Context context) {
        super(context, "blacklist.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacklist (_id integer primary key autoincrement, number varchar(20), mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
