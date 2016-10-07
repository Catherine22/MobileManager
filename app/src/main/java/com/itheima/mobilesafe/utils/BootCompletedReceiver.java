package com.itheima.mobilesafe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
    private static final String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            String savedSIM = sp.getString("sim_serial", null);

            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSIM = "";
            try {
                currentSIM = tm.getSimSerialNumber();
            } catch (SecurityException e) {
                CLog.e(TAG,e.toString());
            }
//            String currentSIM = "651235761111";
            if (TextUtils.isEmpty(currentSIM)||TextUtils.isEmpty(savedSIM)) {
                //权限不足
                CLog.e(TAG, "权限不足");
                Toast.makeText(context, "权限不足", Toast.LENGTH_LONG).show();
            }
            else if (!TextUtils.isEmpty(savedSIM) && !currentSIM.equals(savedSIM)) {
                Toast.makeText(context, "SIM卡已变更", Toast.LENGTH_LONG).show();

                //sim卡变更了，需要偷偷发短信
                CLog.e(TAG, "sim卡变更了，需要偷偷发短信");
                SmsManager smsManager = SmsManager.getDefault();
                String destinationAddress = sp.getString("safe_phone", null);
                if (!TextUtils.isEmpty(destinationAddress))
                    smsManager.sendTextMessage(destinationAddress, null, "SIM卡换啦!", null, null);
                //如果没有SIM卡?
                else {
                    //偷偷发email
                }
            } else {
//                Toast.makeText(context, "SIM卡没变", Toast.LENGTH_LONG).show();
            }
        }

    }
}
