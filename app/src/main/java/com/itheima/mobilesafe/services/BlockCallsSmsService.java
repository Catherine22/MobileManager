package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.factories.DaoFactory;
import com.itheima.mobilesafe.factories.utils.DaoConstants;
import com.itheima.mobilesafe.utils.CLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Catherine on 2016/10/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlockCallsSmsService extends Service {
    private final static String TAG = "BlockCallsSmsReceiver";
    private InnerSmsReceiver receiver;
    private BlacklistDao dao;
    private TelephonyManager tm;
    private MyPhoneStateListener mpsl;
    private DaoFactory daoF;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        daoF = new DaoFactory();
        dao = (BlacklistDao) daoF.getDao(getApplicationContext(), DaoConstants.BLACKLIST);

        receiver = new InnerSmsReceiver();
        registerReceiver(receiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        mpsl = new MyPhoneStateListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(mpsl, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        tm.listen(mpsl, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 短信广播接收者
     */
    private class InnerSmsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            CLog.d(TAG, "收到短信啦!");

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
                            CLog.d(TAG, "拦截短信");
                            break;
                        case BlacklistDao.MODE_BOTH_BLOCKED:
                            abortBroadcast();
                            CLog.d(TAG, "拦截短信");
                            break;
                    }
                }
            } else
                CLog.e(TAG, "null pdus");

        }
    }

    /**
     * 来电监听器
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://铃响
                    CLog.d(TAG, "收到电话(" + incomingNumber + ")啦!");

                    dao = (BlacklistDao) daoF.getDao(getApplicationContext(), DaoConstants.BLACKLIST);
                    int result = dao.findMode(incomingNumber);
                    CLog.d(TAG, "state:" + result);
                    switch (result) {
                        case BlacklistDao.NOT_FOUND:
                            //不是黑名单,不管
                            break;
                        case BlacklistDao.MODE_CALLS_BLOCKED:
                            CLog.d(TAG, "挂断电话");
                            endCall();
                            break;
                        case BlacklistDao.MODE_BOTH_BLOCKED:
                            CLog.d(TAG, "挂断电话");
                            endCall();
                            break;
                    }
                    break;
            }
        }

        /**
         * 使用反射机制加载被隐藏的方法
         */
        private void endCall() {
            //api仍然存在，只是被隐藏而已，所以须使用反射找到方法
            //ServiceManager被隐藏（/** @hide */）所以会报错：Cannot resolve symbol ServiceManager
            //IBinder b =  ServiceManager.getService(Context.TELEPHONY_SERVICE);

            //改用：
            try {
                //加载ServiceManager的字节码
                Class clazz = BlockCallsSmsService.class.getClassLoader().loadClass("android.os.ServiceManager");
                Method method = clazz.getDeclaredMethod("getService", String.class);//呼叫的方法与带入的参数型别
                IBinder b = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE); //the object on which to call this method (or null for static methods)

                ITelephony.Stub.asInterface(b).endCall();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
