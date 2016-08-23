package com.itheima.mobilesafe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.itheima.mobilesafe.R;

/**
 * Created by Catherine on 2016/8/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class SMSReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsReceiver";
    public static String address;
    public static String content;

    /**
     * ANR异常：
     * 如果主线程阻塞超过10秒，很容易ANR异常。
     * 因为广播接收者的生命周期短，容易被系统回收，连同子线程被销毁。
     * <p/>
     * 广播接收者两种注册方式：
     * 1.Manifest里面注册，只需要继承BroadcastReceiver的class，一旦应用被部署到手机，广播就会生效，ex:SMSReceiver.class。
     * 2.代码里注册：不用在Manifest里注册receiver，在activities里注册，一旦代码所在的进程被杀死，广播也会跟着失效，ex:SMSReceiverActivity.class。
     * <p/>
     * 广播接收者分成两种类型：
     * 1.有序广播：在Manifest设置广播的优先级(-1000~1000)，高到低的顺序，abortBroadcast（）可拦截。
     * 2.无序广播：任何注册广播的应用都能用，没有优先级。
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        CLog.d(TAG, "SMS received!");
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String safePhone = sp.getString("safe_phone", "");
/*
        //测试ANR，阻塞主线程
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/

        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        for (Object pdu : pdus) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);

            //获取短信的正文内容
            content = message.getMessageBody();

            //获取短信的发送者
            address = message.getOriginatingAddress();


            CLog.d(TAG, address + "\n" + content);
            if (address.equals(safePhone)) {
                if (content.contains("#*location*#")) {
                    CLog.d(TAG, "#*location*#");
                    //截获短信，根据AndroidManifest的优先级判断，其他优先级低的应用就不会收到推播
                    abortBroadcast();
                }
                if (content.contains("#*alarm*#")) {
                    CLog.d(TAG, "#*alarm*#");
                    //截获短信，根据AndroidManifest的优先级判断，其他优先级低的应用就不会收到推播
                    abortBroadcast();

                    MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
                    mp.setLooping(true);
                    mp.setVolume(1.0f, 1.0f);
                    mp.start();
                }
                if (content.contains("#*wipedata*#")) {
                    CLog.d(TAG, "#*wipedata*#");
                    //截获短信，根据AndroidManifest的优先级判断，其他优先级低的应用就不会收到推播
                    abortBroadcast();
                }
                if (content.contains("#*lockscreen*#")) {
                    CLog.d(TAG, "#*lockscreen*#");
                    //截获短信，根据AndroidManifest的优先级判断，其他优先级低的应用就不会收到推播
                    abortBroadcast();
                }
            }

            //回传讯息
//            SmsManager manager = SmsManager.getDefault();
//            manager.sendTextMessage(address, null, "Go to hel.", null, null);

        }
    }
}
