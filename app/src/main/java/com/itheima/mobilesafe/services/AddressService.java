package com.itheima.mobilesafe.services;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
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
    private WindowManager wm;//窗体管理者,也是一个服务


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
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
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
            String address;
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://铃声响起时,也就是来电时
                    address = TelephoneUtils.getAddressFromNum(incomingNumber);
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    myToast(address);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://去电时
                    address = TelephoneUtils.getAddressFromNum(incomingNumber);
                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    myToast(address);
                    break;
            }
        }

        /**
         * 找到吐司的背景图
         * <p/>
         * 从Toast-makeText,发现有一个layout文件transient_notification,
         * 看到代码中吐司的背景为?android:attr/toastFrameBackground,
         * 进到目前layout的sdk目录(e.g. android-sdk-macosx/platforms/android-24/data/res)下搜寻toastFrameBackground,
         * 在themes中找到 <item name="toastFrameBackground">@drawable/toast_frame</item>,
         * 再回到sdk目录下找到toast_frame图片
         */
        public void myToast(String text) {
            TextView tv = new TextView(getApplicationContext());
            tv.setText(text);
            tv.setTextSize(22);
            tv.setTextColor(Color.BLUE);

            //窗体的参数
            // XXX This should be changed to use a Dialog, with a Theme.Toast
            // defined that sets up the layout params appropriately.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;//半透明
            params.windowAnimations = com.android.internal.R.style.Animation_Toast;//吐司的动画
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//不让锁屏
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE//不让吐司获得焦点
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }

    }
}
