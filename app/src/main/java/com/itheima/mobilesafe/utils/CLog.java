package com.itheima.mobilesafe.utils;

import android.util.Log;

import tw.com.softworld.messagescenter.Config;

/**
 * Created by Catherine on 2016/8/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class CLog {
    public static void v(String tab, String message) {
        if (Settings.showDebugLog || MyApplication.getInstance().getPackageName().contains(".test")) {
            Config.showDebugLog = true;
            Log.v(tab, message);
        }
    }

    public static void d(String tab, String message) {
        if (Settings.showDebugLog || MyApplication.getInstance().getPackageName().contains(".test")) {
            Config.showDebugLog = true;
            Log.d(tab, message);
        }
    }

    public static void e(String tab, String message) {
        if (Settings.showDebugLog || MyApplication.getInstance().getPackageName().contains(".test")) {
            Config.showDebugLog = true;
            Log.e(tab, message);
        }
    }

    public static void w(String tab, String message) {
        if (Settings.showDebugLog || MyApplication.getInstance().getPackageName().contains(".test")) {
            Config.showDebugLog = true;
            Log.w(tab, message);
        }
    }

    public static void i(String tab, String message) {
        if (Settings.showDebugLog || MyApplication.getInstance().getPackageName().contains(".test")) {
            Config.showDebugLog = true;
            Log.i(tab, message);
        }
    }

}
