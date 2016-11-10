package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.itheima.mobilesafe.ui.MyToast;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.TelephoneUtils;

/**
 * Created by Catherine on 2016/9/1.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AddressService extends Service {

    private final static String TAG = "AddressService";
    private TelephonyManager tm;//监听来电
    private MyPhoneStateListener psListener;
    private MyToast mytoast;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        psListener = new MyPhoneStateListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(psListener, PhoneStateListener.LISTEN_CALL_STATE);
        mytoast = new MyToast(this);
        mytoast.setOnDoubleClickListener(new MyToast.OnDoubleClickListener() {
            @Override
            public void onClick(View v) {
                CLog.d(TAG, "Double clicked!");
            }
        });
        mytoast.setOnClickListener(new MyToast.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLog.d(TAG, "clicked!");

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //取消监听来电
        tm.listen(psListener, PhoneStateListener.LISTEN_NONE);
        psListener = null;
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, "state " + state + "\nincomingNumber " + incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://铃声响起时,也就是来电时
                    TelephoneUtils.getAddressFromNum(getApplicationContext(), incomingNumber, new TelephoneUtils.Callback() {
                        @Override
                        public void onFinish(String content) {
                            mytoast.showMyToast(content);
                        }
                    });
//                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();

                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态 e.q. 挂电话, 来电拒接
                    mytoast.dismissMyToast();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://去电时
                    TelephoneUtils.getAddressFromNum(getApplicationContext(), incomingNumber, new TelephoneUtils.Callback() {
                        @Override
                        public void onFinish(String content) {
//                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                            mytoast.showMyToast(content);
                        }
                    });
                    break;
            }
        }


    }
}
