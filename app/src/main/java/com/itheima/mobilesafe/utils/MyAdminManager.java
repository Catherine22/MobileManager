package com.itheima.mobilesafe.utils;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Catherine on 2016/8/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MyAdminManager {
    private Context ctx;
    private DevicePolicyManager dpm;
    private ComponentName myAdmin;

    public MyAdminManager(Context ctx) {
        this.ctx = ctx;
        dpm = (DevicePolicyManager) ctx.getSystemService(Context.DEVICE_POLICY_SERVICE);
        myAdmin = new ComponentName(ctx, MyDeviceAdminReceiver.class);
    }

    /**
     * 取得管理员权限
     */
    public void getAdminPermission() {
        // Launch the activity to have the user enable our admin.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, myAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "哥们开启我可以一键锁屏，解放按钮");
        Activity activity = (Activity) ctx;
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_ENABLE_ADMIN);
    }

    /**
     * 锁屏
     */
    public void lockScreen() {
        if (dpm.isAdminActive(myAdmin))
            dpm.lockNow();
        else
            Toast.makeText(ctx, "哥们还没开启管理员权限", Toast.LENGTH_SHORT).show();
    }

    /**
     * 重设锁屏密码
     */
    public void resetScreenPassword(String password) {
        if (dpm.isAdminActive(myAdmin))
            dpm.resetPassword(password, 0);
        else
            Toast.makeText(ctx, "哥们还没开启管理员权限", Toast.LENGTH_SHORT).show();
    }

    /**
     * 清除锁屏密码
     */
    public void unlockScreen() {
        if (dpm.isAdminActive(myAdmin)) {
            dpm.resetPassword("", 0);
        } else
            Toast.makeText(ctx, "哥们还没开启管理员权限", Toast.LENGTH_SHORT).show();
    }

    /**
     * 清除数据
     */
    public void wipeData() {
        if (dpm.isAdminActive(myAdmin)) {
            dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除sd card
            dpm.wipeData(0);//恢复出厂设置
        } else
            Toast.makeText(ctx, "哥们还没开启管理员权限", Toast.LENGTH_SHORT).show();
    }

    /**
     * 卸载步骤:
     * 1. 先变成普通软件(清除管理员权限)
     * 2. 正常卸载
     */
    public void unInstall() {
        removeAdmin();

        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
        ctx.startActivity(intent);
    }

    /**
     * 检查管理员权限
     *
     * @return
     */
    public boolean isAdmin() {
        if (dpm.isAdminActive(myAdmin))
            return true;
        else
            return false;
    }

    /**
     * 清除管理员权限
     */
    public void removeAdmin() {
        if (dpm.isAdminActive(myAdmin))
            dpm.removeActiveAdmin(myAdmin);
    }

}
