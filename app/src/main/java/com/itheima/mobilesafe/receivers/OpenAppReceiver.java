package com.itheima.mobilesafe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Catherine on 2016/10/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class OpenAppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        context.startActivity(i);
    }
}
