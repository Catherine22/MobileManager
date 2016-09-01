package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        super.onCreate();
        psListener = new MyPhoneStateListener();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(psListener, PhoneStateListener.LISTEN_CALL_STATE);
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
                    String address = TelephoneUtils.getAddressFromNum(incomingNumber);
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
