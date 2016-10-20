package com.itheima.mobilesafe.services;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.MyAppWidgetProvider;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * Created by Catherine on 2016/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class AutoCleanService extends Service {
    private final static String TAG = "AutoCleanService";
    private Timer timer;
    private TimerTask timerTask;
    private AppWidgetManager awm;
    private ComponentName cm;
//    private AutoCleanService() {
//    }
//
//    //内部类，在装载该内部类时才会去创建单利对象
//    private static class SingletonHolder {
//        private static AutoCleanService instance = new AutoCleanService();
//    }
//
//    public static AutoCleanService getInstance() {
//        return SingletonHolder.instance;
//    }

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
        cm = new ComponentName(AutoCleanService.this, MyAppWidgetProvider.class);

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                CLog.d(TAG, "timerTask");
                long availMen = SystemInfoUtils.getAvailableMemory(AutoCleanService.this);
                long totalMen = SystemInfoUtils.getTotalMemory(AutoCleanService.this);
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
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AutoCleanService.this, 0, intent, FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.iv_clean, pendingIntent);

                Intent intent2 = new Intent();
                intent2.setAction("com.itheima.mobliesafe.OPEN_APP");
                PendingIntent pendingIntent2 = PendingIntent.getBroadcast(AutoCleanService.this, 0, intent2, FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.tv_widget_memory, pendingIntent2);

                //必须放最后，否则收不到广播
                awm.updateAppWidget(cm, views);
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timerTask.cancel();
        timer = null;
        timerTask = null;
    }
}
