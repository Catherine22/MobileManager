package com.itheima.mobilesafe.utils;

import android.app.Application;

/**
 * Created by Catherine on 2016/8/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MyApplication extends Application {
    public static MyApplication INSTANCE;

    public static MyApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        INSTANCE = this;
        super.onCreate();
    }

}
