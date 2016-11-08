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
    private boolean flag = true;//服务销毁时关闭看门狗
    private Client client;
    private String unlockPackname;
    private ScreenOffReceiver sOffReceiver;
    private ScreenOnReceiver sOnReceiver;
    private List<String> protectedApps;
    private Intent intentTypePwd;


    @Override
    public void onCreate() {
        super.onCreate();
        final AppsLockDao dao = new AppsLockDao(WatchDogService.this);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //注册屏幕状态receiver
        sOffReceiver = new ScreenOffReceiver();
        sOnReceiver = new ScreenOnReceiver();
        registerReceiver(sOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(sOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                if (result.getString() != null)//接受来自解锁画面传来的值
                    unlockPackname = result.getString();
                else//接受改变数据的请求
                    protectedApps = dao.queryAll();

            }
        };
        client = new Client(WatchDogService.this, cr);
        client.gotMessages(BroadcastActions.STOP_WATCHDOG);
        client.gotMessages(BroadcastActions.UPDATE_WATCHDOG);


        /**
         * 把intent搬出来先初始化，不要放在子线程里再初始，提高效率
         */
        intentTypePwd = new Intent();
        intentTypePwd.setClass(getApplicationContext(), TypePwdActivity.class);
        //因为服务没有任务栈信息，所以必须给activity指定运行的任务栈
        intentTypePwd.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /**
         * 使用queryAll拿到全部的数据，再用list暂存，以后要查询时就直接查list
         * 查内存的速度比查文件或数据库快10倍以上
         */
        protectedApps = dao.queryAll();

        //取得当前应用程序
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            new Thread() {
                public void run() {
                    while (flag) {//所有应用程序的看门狗都是while true的死循环
                        /**
                         * 每开启一个应用程序就会创建一个任务栈，存放用户开启的activities
                         * （所以当所有的activities都退出后，任务栈就清空了）
                         */
                        tasks = am.getRunningTasks(1);//少query提高效率
                        //拿到栈顶的activity也就是当前运行的activity
                        packname = tasks.get(0).topActivity.getPackageName();
//                        CLog.d(TAG, "当前用户操作：" + packname);//不打印logs提高效率
                        protectApp(packname);
                        try {
                            Thread.sleep(20);//重要代码，用来提高CPU效能
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
//                        CLog.w(TAG, "当前用户操作：" + packname);//不打印logs提高效率
                        protectApp(packname);

                        try {
                            Thread.sleep(20);//重要代码，用来提高CPU效能
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
        if (protectedApps.contains(packname) && !packname.equals(unlockPackname)) {
//            CLog.d(TAG, "解锁 " + packname);
            //弹出解锁介面
            intentTypePwd.putExtra("packname", packname);
            startActivity(intentTypePwd);
        }
        //不弹出解锁介面
    }


    /**
     * 锁屏时禁用，移除packname，重新上锁
     */
    private class ScreenOffReceiver extends BroadcastReceiver {
        private final static String TAG = "ScreenOffReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
//            CLog.d(TAG, "屏幕关闭了");
            unlockPackname = null;
            flag = false;//省电
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {
        private final static String TAG = "ScreenOnReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
//            CLog.d(TAG, "屏幕开启了");
            flag = true;//省电
        }
    }

    @Override
    public void onDestroy() {
        CLog.d(TAG, "onDestroy()");
        flag = false;
        unregisterReceiver(sOffReceiver);
        unregisterReceiver(sOnReceiver);
        client.release();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
