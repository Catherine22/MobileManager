package com.itheima.mobilesafe.services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.itheima.mobilesafe.TypePwdActivity;
import com.itheima.mobilesafe.db.dao.AppsLockDao;
import com.itheima.mobilesafe.utils.BroadcastActions;
import com.itheima.mobilesafe.utils.CLog;

import java.util.List;

import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Result;

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
    private AppsLockDao dao;
    private boolean flag = true;//服务销毁时关闭看门狗
    private Client client;
    private String unlockPackname;
    private ScreenOffReceiver sOffReceiver;


    @Override
    public void onCreate() {
        super.onCreate();
        dao = new AppsLockDao(WatchDogService.this);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //注册屏幕状态receiver
        sOffReceiver = new ScreenOffReceiver();
        registerReceiver(sOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        //接受来自解锁画面传来的值
        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                unlockPackname = result.getString();
            }
        };
        client = new Client(WatchDogService.this, cr);
        client.gotMessages(BroadcastActions.WATCHDOG_STOP);
        //取得当前应用程序
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Thread() {
                public void run() {
                    while (flag) {//所有应用程序的看门狗都是while true的死循环
                        /**
                         * 每开启一个应用程序就会创建一个任务栈，存放用户开启的activities
                         * （所以当所有的activities都退出后，任务栈就清空了）
                         */
                        tasks = am.getRunningTasks(100);
                        //拿到栈顶的activity也就是当前运行的activity
                        packname = tasks.get(0).topActivity.getPackageName();
//                        CLog.d(TAG, "当前用户操作：" + packname);
                        protectApp(packname);
                        try {
                            Thread.sleep(50);//重要代码，用来提高CPU效能
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        } else {
            new Thread() {
                public void run() {
                    while (flag) {
                        /**
                         * LOLLIPOP以上用getRunningAppProcesses().get(0).processName取代am.getRunningTasks(100).get(0).topActivity.getPackageName()，
                         * 添加权限<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />
                         */
                        packname = am.getRunningAppProcesses().get(0).processName;
//                        CLog.w(TAG, "当前用户操作：" + packname);
                        protectApp(packname);

                        try {
                            Thread.sleep(50);//重要代码，用来提高CPU效能
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();


        }
    }

    /**
     * 检查应用是否有被用户指定加密，如果有则弹出解锁画面
     *
     * @param packname 包名
     */
    private void protectApp(String packname) {
        if (dao.find(packname) && !packname.equals(unlockPackname)) {
            CLog.d(TAG, "解锁 " + packname);
            //弹出解锁介面
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), TypePwdActivity.class);
            //因为服务没有任务栈信息，所以必须给activity指定运行的任务栈
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("packname", packname);
            startActivity(intent);
        }
        //不弹出解锁介面
    }


    /**
     * 锁屏时禁用，省电
     */
    private class ScreenOffReceiver extends BroadcastReceiver {
        private final static String TAG = "ScreenOffReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            CLog.d(TAG, "屏幕关闭了");

            unlockPackname = null;
        }
    }

    @Override
    public void onDestroy() {
        CLog.d(TAG, "onDestroy()");
        flag = false;
        unregisterReceiver(sOffReceiver);
        client.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
