package com.itheima.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.objects.AppInfo;
import com.itheima.mobilesafe.utils.objects.TaskInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
     * @param ctx 上下文
     * @return 运行中的进程数
     */
    public static int getRunningProcessCount(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        return infos.size();
    }

    /**
     * 获取手机可用的剩余内存
     *
     * @param ctx 上下文
     * @return bytes
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
     * @param ctx 上下文
     * @return bytes
     */
    public static long getTotalMemory(Context ctx) {
        long result;
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
     * 获取所有的进程信息
     *
     * @param ctx 上下文
     * @return 目前运行中的进程信息
     */
    public static List<TaskInfo> getTaskInfos(Context ctx) {
        List<TaskInfo> mList = new ArrayList<>();
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

                //用户进程 or 系统进程
                tInfo.userTask = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
                mList.add(tInfo);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                //之所以没有包名是因为程序不是完全用JAVA写的，本身没有apk
                tInfo.icon = ContextCompat.getDrawable(ctx, R.drawable.ic_default);
                tInfo.name = processInfo.processName;
                //系统进程
                tInfo.userTask = false;
                mList.add(tInfo);
            }
        }
        return mList;
    }

    /**
     * 获取所有的应用信息
     *
     * @param ctx 上下文
     * @return 所有应用信息
     */
    public static List<AppInfo> getAppInfos(Context ctx) {
        List<AppInfo> mList = new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();

        //看的Additional option flags代表是可选的，一般带入0为预设
        List<PackageInfo> PackageInfos = pm.getInstalledPackages(0);
        for (PackageInfo pin : PackageInfos)//PackageInfo相当于一个apk包的清单文件（对照Manifest）
        {
            AppInfo appInfo = new AppInfo();
            appInfo.setName(pin.applicationInfo.loadLabel(pm).toString());
            appInfo.setPackageName(pin.packageName);
            appInfo.setIcon(pin.applicationInfo.loadIcon(pm));

            appInfo.setVersionName(pin.versionName);
            appInfo.setFirstInstallTime(String.valueOf(pin.firstInstallTime));
            appInfo.setLastUpdateTime(String.valueOf(pin.lastUpdateTime));

            if (pin.applicationInfo.flags == pin.applicationInfo.FLAG_SYSTEM)
                appInfo.setUserApp(false);
            else
                appInfo.setUserApp(true);

            mList.add(appInfo);
        }

        return mList;
    }

    /**
     * 关闭进程
     *
     * @param ctx         上下文
     * @param packageName 希望关闭的进程
     */
    public static void killProcess(Context ctx, String packageName) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(packageName);

    }

    /**
     * 关闭列表里全部进程
     *
     * @param ctx          上下文
     * @param packagenames 关闭列表
     */
    public static void killAllProcess(Context ctx, List<String> packagenames) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (String packageName : packagenames)
            am.killBackgroundProcesses(packageName);
    }

    /**
     * 关闭全部进程
     *
     * @param ctx 上下文
     */
    public static void killAllProcess(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
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

    /**
     * 获取可用空间大小
     *
     * @param path 路径
     * @return 空间大小
     */
    public static long getAvailableSpace(String path) {
        StatFs statFs = new StatFs(path);
        long blockSize, availableBlocks;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.getBlockSizeLong();//获取分区数
            availableBlocks = statFs.getAvailableBlocksLong();//获取可用的每个区块大小
        } else {
            blockSize = statFs.getBlockSize();//获取分区数
            availableBlocks = statFs.getAvailableBlocks();//获取可用的每个区块大小
        }

        return availableBlocks * blockSize;
    }

    /**
     * 获取全部空间大小
     *
     * @param path 路径
     * @return 空间大小
     */
    public static long getTotalSpace(String path) {
        StatFs statFs = new StatFs(path);
        long blockCount, blockSize;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockCount = statFs.getBlockCountLong();//获取分区数
            blockSize = statFs.getBlockSizeLong();//获取每个分区大小
        } else {
            blockCount = statFs.getBlockCount();//获取分区数
            blockSize = statFs.getBlockSize();//获取每个分区大小
        }

        return blockCount * blockSize;
    }

    /**
     * 自动转换单位
     *
     * @param size 字节Bytes
     * @return Bytes，KB，MB,GB,TB
     */
    public static String formatFileSize(long size) {
        String hrSize;

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
}
