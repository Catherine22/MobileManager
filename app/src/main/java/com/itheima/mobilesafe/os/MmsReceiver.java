package com.itheima.mobilesafe.os;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/10/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MmsReceiver extends BroadcastReceiver {
    public static final String TAG = "MmsReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        CLog.d(TAG, "Mms received!");
    }
}
