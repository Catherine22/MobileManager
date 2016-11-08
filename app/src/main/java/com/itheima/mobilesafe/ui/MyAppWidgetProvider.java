package com.itheima.mobilesafe.ui;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.itheima.mobilesafe.services.UpdateWidgetService;
import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyAppWidgetProvider extends AppWidgetProvider {
    private final static String TAG = "MyAppWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        CLog.d(TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        CLog.d(TAG, "onReceive:" + action);
        super.onReceive(context, intent);
    }

    @Override
    public void onDisabled(Context context) {
        CLog.d(TAG, "onDisabled");
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        CLog.d(TAG, "onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        CLog.d(TAG, "onAppWidgetOptionsChanged");

        Intent service = new Intent(context, UpdateWidgetService.class);
        context.getApplicationContext().startService(service);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        CLog.d(TAG, "onDeleted");
        Intent service = new Intent(context, UpdateWidgetService.class);
        context.getApplicationContext().stopService(service);
    }
}
