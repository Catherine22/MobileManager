package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.utils.DaoFactory;
import com.itheima.mobilesafe.db.dao.DaoConstants;
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
    /**
     * Uri获取方法：(必须看源代码)
     * 1.到https://android.googlesource.com/platform/packages/providers/ContactsProvider/+/master/
     * 2.找到platform_packages_providers_contactsprovider项目，并下载
     * 3.打开该项目的Manifest，找到provider的配置（CallLogProvider）
     * 4.找到android:authorities="call_log"（call_log为主机名），后面数据库的表名须看源码
     * 5.打开该项目的com.android.providers.contacts/CallLogProvider.java
     * 6.搜寻urimatcher（Uri匹配器）
     * 7.找到一堆matcher.addURI()...
     */
    private final static Uri database = Uri.parse("content://call_log/calls");//呼叫记录uri的路径
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
        dao = (BlacklistDao) daoF.createDao(getApplicationContext(), DaoConstants.BLACKLIST);

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

                    dao = (BlacklistDao) daoF.createDao(getApplicationContext(), DaoConstants.BLACKLIST);
                    int result = dao.findMode(incomingNumber);
                    switch (result) {
                        case BlacklistDao.NOT_FOUND:
                            //不是黑名单,不管
                            break;
                        case BlacklistDao.MODE_CALLS_BLOCKED:
                            CLog.d(TAG, "挂断电话");
                            getContentResolver().registerContentObserver(database, true, new CallLogObserve(new Handler(), incomingNumber));
                            endCall();
                            break;
                        case BlacklistDao.MODE_BOTH_BLOCKED:
                            CLog.d(TAG, "挂断电话");
                            getContentResolver().registerContentObserver(database, true, new CallLogObserve(new Handler(), incomingNumber));
                            endCall();
                            break;
                    }
                    break;
            }
        }

        /**
         * 删除呼叫记录
         * 不能紧接着endCall()调用，
         * 因为endCall()是调用另一个进程（不同线程），所以可能endCall()还没执行完生成CallLog。
         * <p>
         * 呼叫时机：观察呼叫记录数据库内容的变化，变化后再调用。
         * 在com.android.providers.contacts/databases/contacts2.db
         *
         * @param incomingNumber 欲删除的号码
         */
        private void deleteCallLog(String incomingNumber) {

            //Uri rawbase = Uri.parse("content://call_log");
            int result = getContentResolver().delete(database, "number=?", new String[]{incomingNumber});
            CLog.d(TAG, "result:" + result);
        }

        /**
         * 使用反射机制加载被隐藏的方法，在另一个进程运行的远程服务的方法
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

        /**
         * 通话记录的内容观察者，一旦数据发生变化则呼叫onChange
         */
        private class CallLogObserve extends ContentObserver {
            String incomingNumber;

            /**
             * Creates a content observer.
             *
             * @param handler The handler to run {@link #onChange} on, or null if none.
             */
            public CallLogObserve(Handler handler, String incomingNumber) {
                super(handler);
                this.incomingNumber = incomingNumber;
            }

            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
//                CLog.d(TAG, "onChange:" + selfChange + "/uri:" + uri);
            }

            @Override
            public boolean deliverSelfNotifications() {
                return super.deliverSelfNotifications();
            }

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
//                CLog.d(TAG, "onChange:" + selfChange);
                getContentResolver().unregisterContentObserver(this);
                deleteCallLog(incomingNumber);
            }
        }
    }
}
