package com.itheima.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Catherine on 2016/9/1.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class ServiceUtils {

    /**
     * 校验服务是否运行中
     *
     * @param ctx
     * @param serviceName 完整包名 e.g. com.itheima.mobilesafe.services.AddressService
     * @return
     */
    public static boolean isRunningService(Context ctx, String serviceName) {
        boolean isRunning = false;
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);//代表取得服务数量的最大值
        for (ActivityManager.RunningServiceInfo info : infos) {
            String runningServiceName = info.service.getClassName();
            if (runningServiceName.equals(serviceName))
                isRunning = true;
        }
        return isRunning;
    }
}
