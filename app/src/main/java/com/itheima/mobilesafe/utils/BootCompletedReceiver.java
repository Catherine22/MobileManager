package com.itheima.mobilesafe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Catherine on 2016/8/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 * <p/>
 * 1. 读取之前保存的SIM卡信息
 * 2. 读取当前SIM卡信息
 * 3. 比对,如果不同就发短信给安全号码
 */
public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String savedSIM = sp.getString("simSerialNumber", null);

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String currentSIM = tm.getSimSerialNumber();
            String currentSIM = "651235761111";
            if (!currentSIM.equals(savedSIM)) {
                Toast.makeText(context, "SIM卡已变更", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "SIM卡没变", Toast.LENGTH_LONG).show();
            }
        }

    }
}
