package com.itheima.mobilesafe.utils;

import android.app.Application;

import io.branch.referral.Branch;

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
        Branch.getAutoInstance(this);
        super.onCreate();
    }

}
