package com.itheima.mobilesafe.os;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.itheima.mobilesafe.utils.CLog;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Catherine on 2016/10/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = "os.SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        CLog.d(TAG, "Sms received!");

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        if (pdus != null) {
            for (Object pdu : pdus) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                final SmsManager manager = SmsManager.getDefault();

                //获取短信的正文内容
                String content = message.getMessageBody();
                //获取短信的发送者
                String address = message.getOriginatingAddress();

                CLog.d(TAG, address + "\n" + content);

                Calendar buildDate = new GregorianCalendar(2011, 8, 18);    // 18 Sep 2011
                Calendar nowDate = new GregorianCalendar();
                long now = System.currentTimeMillis();
                CLog.d(TAG, now + "");

                //应写入数据库
            }
        }
    }
}
