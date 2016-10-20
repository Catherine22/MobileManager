package com.itheima.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.support.v4.content.ContextCompat;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.objects.TaskInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yi-Jing on 2016/9/11.
 * 系统信息的工具类
 * <p>
 * PackageManager  包管理器，相当于程序管理器，静态的内容。
 * ActivityManager 进程管理器，管理手机的活动信息，动态的内容。
 * <p>
 * PackageManager pm = ctx.getPackageManager();
 */

public class SystemInfoUtils {

    /**
     * 获取运行中的进程数
     *
     * @param ctx
     * @return
     */
    public static int getRunningProcessCount(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }

    /**
     * 获取手机可用的剩余内存
     *
     * @param ctx
     * @return byte
     */
    public static long getAvailableMemory(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mInfo);
        return mInfo.availMem;
    }

    /**
     * 获取手机可用的总内存
     *
     * @param ctx
     * @return byte
     */
    public static long getTotalMemory(Context ctx) {
        long result = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mInfo);
            result = mInfo.totalMem;
        } else {
            try {
                File file = new File("/proc/meminfo");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                String line = br.readLine();//MemTotal:      xxxxxx kb
                StringBuffer sb = new StringBuffer();
                for (char c : line.toCharArray()) {
                    if (c >= '0' && c <= '9') {//取得数字0~9
                        sb.append(c);
                    }
                }
                result = Long.parseLong(sb.toString()) * 1024;
            } catch (IOException e) {
                e.printStackTrace();
                result = -1;
            }
        }
        return result;
    }

    /**
     * 自动转换单位
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" KB");
        } else {
            hrSize = dec.format(b).concat(" Bytes");
        }

        return hrSize;
    }

    /**
     * 获取所有的进程信息
     *
     * @param ctx
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context ctx) {
        List<TaskInfo> returns = new ArrayList<>();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo processInfo : infos) {
            TaskInfo tInfo = new TaskInfo();
            try {
                tInfo.packageName = processInfo.processName;
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.processName, 0);

                tInfo.name = applicationInfo.loadLabel(pm).toString();
                tInfo.icon = applicationInfo.loadIcon(pm);

                Debug.MemoryInfo[] mInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
                tInfo.memSize = mInfos[0].getTotalPrivateDirty();

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //用户进程
                    tInfo.userTask = true;
                } else {
                    //系统进程
                    tInfo.userTask = false;
                }
                returns.add(tInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                //之所以没有包名是因为程序不是完全用JAVA写的，本身没有apk
                tInfo.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_default);
                tInfo.name = processInfo.processName;
                //系统进程
                tInfo.userTask = false;
                returns.add(tInfo);
            }
        }
        return returns;
    }

    public static void killProcess(Context ctx, String packageName) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);

    }

    public static void killAllProcess(Context ctx, List<String> packagenames) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (String packageName : packagenames)
            am.killBackgroundProcesses(packageName);
    }

    public static void killAllProcess(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : infos) {
            try {
                String packageName = processInfo.processName;
                am.killBackgroundProcesses(packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
