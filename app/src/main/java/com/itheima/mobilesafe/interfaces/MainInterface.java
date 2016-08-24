package com.itheima.mobilesafe.interfaces;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public interface MainInterface {
    void callFragment(int ID);

    void clearAllFragments();

    void backToPreviousPage();

    void getPermissions(String[] permissions,MyPermissionsResultListener listener);
}
