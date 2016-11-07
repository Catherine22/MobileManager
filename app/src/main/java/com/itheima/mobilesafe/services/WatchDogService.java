package com.itheima.mobilesafe.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.itheima.mobilesafe.utils.CLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Catherine on 2016/11/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

/**
 * 用来监视应用程序的状态
 */
public class WatchDogService extends Service {
    private final static String TAG = "WatchDogService";
    private ActivityManager am;
    private List<ActivityManager.RunningTaskInfo> tasks;
    private String packname;
    private boolean flag = true;//服务销毁时关闭看门狗

    @Override
    public void onCreate() {
        super.onCreate();
        CLog.d(TAG, "onCreate");
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Thread() {
                public void run() {
                    CLog.d(TAG, "run()");
                    while (flag) {//所有应用程序的看门狗都是while true的死循环
                        /**
                         * 每开启一个应用程序就会创建一个任务栈，存放用户开启的activities
                         * （所以当所有的activities都退出后，任务栈就清空了）
                         */
                        tasks = am.getRunningTasks(100);
                        //拿到栈顶的activity也就是当前运行的activity
                        packname = tasks.get(0).topActivity.getPackageName();
                        CLog.d(TAG, "当前用户操作：" + packname);

                        try {
                            Thread.sleep(50);//重要代码，用来提高CPU效能
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } else {
            try {
                Class<?> clazz = Class.forName("android.app.ActivityManager");
                Method methods[] = clazz.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    CLog.d(TAG, "找到的方法：" + methods[i].toString());
                }
                final Method method = clazz.getMethod("getRunningTasks", int.class);
                new Thread() {
                    public void run() {
                        while (flag) {
                            try {
                                tasks = (List<ActivityManager.RunningTaskInfo>) method.invoke(am, 100);
                                packname = tasks.get(0).topActivity.getPackageName();
                                CLog.w(TAG, "当前用户操作：" + packname);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }


                            try {
                                Thread.sleep(50);//重要代码，用来提高CPU效能
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        flag = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
