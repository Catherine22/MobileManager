package com.itheima.mobilesafe.interfaces;

/**
 * Created by Catherine on 2016/8/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
/**
 * 自定义传递的信息类型,作为接口
 */
public interface MyPermissionsResultListener {
    /**
     * 用户开启权限
     */
    void onGranted();

    /**
     * 用户拒绝打开权限
     */
    void onDenied();
}
