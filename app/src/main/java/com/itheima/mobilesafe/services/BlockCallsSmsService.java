package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;

import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.factories.DaoFactory;
import com.itheima.mobilesafe.factories.utils.DaoConstants;
import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/10/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlockCallsSmsService extends Service {
    private final static String TAG = "BlockCallsSmsReceiver";
    private InnerSmsReceiver receiver;
    private BlacklistDao dao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = new InnerSmsReceiver();
        registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_DELIVER"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CLog.d(TAG, "收到短信啦!");

            DaoFactory daoF = new DaoFactory();
            dao = (BlacklistDao) daoF.getDao(context, DaoConstants.BLACKLIST);
            //取得手机号码(模版代码)
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);

                    //获取短信的正文内容
                    String content = message.getMessageBody();
                    //获取短信的发送者
                    String address = message.getOriginatingAddress();
                    CLog.d(TAG, address + "\n" + content);


                    int result = dao.findMode(address);
                    switch (result) {
                        case BlacklistDao.NOT_FOUND:
                            //不是黑名单,不管
                            break;
                        case BlacklistDao.MODE_SMS_BLOCKED:
                            abortBroadcast();
                            CLog.d(TAG,"拦截短信");
                            break;
                        case BlacklistDao.MODE_BOTH_BLOCKED:
                            abortBroadcast();
                            CLog.d(TAG,"拦截短信");
                            break;
                    }
                }
            }else
                CLog.e(TAG,"null pdus");

        }
    }
}
