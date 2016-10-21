package com.itheima.mobilesafe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

/**
 * Created by Catherine on 2016/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class WidgetReceiver extends BroadcastReceiver {
    private static final String TAG = "WidgetReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        CLog.d(TAG, "WidgetReceiver");
        SystemInfoUtils.killAllProcess(context);
    }
}
