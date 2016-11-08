package com.itheima.mobilesafe.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.MyAppWidgetProvider;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;


import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by Catherine on 2016/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class UpdateWidgetService extends Service {
    private final static String TAG = "AutoCleanService";
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager awm;
    private ComponentName cm;
    private ScreenOffReceiver sOffReceiver;
    private ScreenOnReceiver sOnReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CLog.d(TAG, "onCreate");
        awm = AppWidgetManager.getInstance(this);
        cm = new ComponentName(UpdateWidgetService.this, MyAppWidgetProvider.class);


        //注册屏幕状态receiver
        sOffReceiver = new ScreenOffReceiver();
        sOnReceiver = new ScreenOnReceiver();
        registerReceiver(sOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        registerReceiver(sOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

        startTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        unregisterReceiver(sOffReceiver);
        unregisterReceiver(sOnReceiver);
        sOffReceiver = null;
        sOnReceiver = null;
    }

    /**
     * 注册widget的点击事件与view定期更新
     */
    private void startTimer() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    CLog.d(TAG, "timerTask");
                    long availMen = SystemInfoUtils.getAvailableMemory(UpdateWidgetService.this);
                    long totalMen = SystemInfoUtils.getTotalMemory(UpdateWidgetService.this);
                    //实际上widgets是桌面进程在处理而非手机卫士进程处理
                    //其实不算是view，继承Parcelable将数据放到公共的内存里（在此处是桌面进程调用）
                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.appwidget_regular);
                    String text = String.format(getResources().getString(R.string.widget_memory), SystemInfoUtils.formatFileSize(availMen), SystemInfoUtils.formatFileSize(totalMen));
                    views.setTextViewText(R.id.tv_widget_memory, text);

                    //自定义广播事件
                    Intent intent = new Intent();
                    intent.setAction("com.itheima.mobliesafe.KILLALL");

                    //描述一个动作，该动作是由另一个进程执行（在此处是桌面进程）
                    //此处可理解为自定义一个广播事件，由PendingIntent包装后交由桌面进程发送广播
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetService.this, 0, intent, FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.iv_clean, pendingIntent);

                    Intent intent2 = new Intent();
                    intent2.setAction("com.itheima.mobliesafe.OPEN_APP");
                    PendingIntent pendingIntent2 = PendingIntent.getBroadcast(UpdateWidgetService.this, 0, intent2, FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.tv_widget_memory, pendingIntent2);

                    //必须放最后，否则收不到广播
                    awm.updateAppWidget(cm, views);
                }
            };
            timer.schedule(timerTask, 0, 5000);
        }
    }


    /**
     * 解除widget的点击事件与view定期更新
     */
    private void stopTimer() {
        if (timer != null && timerTask != null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }



    /**
     * 锁屏时禁用，省电
     */
    private class ScreenOffReceiver extends BroadcastReceiver {
        private final static String TAG = "ScreenOffReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            CLog.d(TAG, "屏幕关闭了");

            stopTimer();
        }
    }

    /**
     * 屏幕解锁时启用
     */
    private class ScreenOnReceiver extends BroadcastReceiver {
        private final static String TAG = "ScreenOnReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            CLog.d(TAG, "屏幕解锁了");
            startTimer();
        }
    }
}
