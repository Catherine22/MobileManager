package com.itheima.mobilesafe.utils;

import android.util.Log;

import com.itheima.mobilesafe.BuildConfig;

import tw.com.softworld.messagescenter.Config;

/**
 * Created by Catherine on 2016/8/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class CLog {
    public static final boolean DEBUG = BuildConfig.SHOW_LOG;

    public static void v(String tab, String message) {
        if (MyApplication.getInstance().getPackageName().contains(".test") || DEBUG) {
            Config.showDebugLog = true;
            Log.v(tab, message);
        }
    }

    public static void d(String tab, String message) {
        if (MyApplication.getInstance().getPackageName().contains(".test") || DEBUG) {
            Config.showDebugLog = true;
            Log.d(tab, message);
        }
    }

    public static void e(String tab, String message) {
        if (MyApplication.getInstance().getPackageName().contains(".test") || DEBUG) {
            Config.showDebugLog = true;
            Log.e(tab, message);
        }
    }

    public static void w(String tab, String message) {
        if (MyApplication.getInstance().getPackageName().contains(".test") || DEBUG) {
            Config.showDebugLog = true;
            Log.w(tab, message);
        }
    }

    public static void i(String tab, String message) {
        if (MyApplication.getInstance().getPackageName().contains(".test") || DEBUG) {
            Config.showDebugLog = true;
            Log.i(tab, message);
        }
    }

}
