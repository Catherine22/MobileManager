package com.itheima.mobilesafe.utils;

/**
 * Created by Catherine on 2017/6/15.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SecurityUtils {
    static {
        //relate to LOCAL_MODULE in Android.mk
        System.loadLibrary("keys");
    }

    public native String[] getAuthChain(String key);
    public native String getAuthentication();
    public native int getdynamicID(int timestamp);
}
