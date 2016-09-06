package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
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
    private View mytoast;//自定义吐司
    private WindowManager.LayoutParams params;
    private SharedPreferences sp;

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
        sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(psListener, PhoneStateListener.LISTEN_CALL_STATE);


        //取得包名, 设置常量
        Constants.DB_NAME = "address.db";
        Constants.PACKAGE_NAME = getPackageName();
        Constants.DB_PATH = "/data/data/" + Constants.PACKAGE_NAME + "/files/" + Constants.DB_NAME;
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
//                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    if (mytoast == null)
                        showMyToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态 e.q. 挂电话, 来电拒接
                    dismissMyToast();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://去电时
                    address = TelephoneUtils.getAddressFromNum(incomingNumber);
//                    Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    if (mytoast == null)
                        showMyToast(address);
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
        public void showMyToast(String text) {
            int index = sp.getInt("address_bg", 0);

            mytoast = View.inflate(AddressService.this, R.layout.toast_show_address, null);
            mytoast.setBackgroundResource(Constants.addressBgRes[index]);
            mytoast.setOnTouchListener(new View.OnTouchListener() {
                int startX, startY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN://手指按下屏幕
                            /**
                             * RawX,RawY 相对于屏幕位置坐标
                             * X,Y 相对于容器的位置坐标
                             */
                            startX = (int) event.getRawX();//获取屏幕原始坐标
                            startY = (int) event.getRawY();//获取屏幕原始坐标
                            break;
                        case MotionEvent.ACTION_MOVE://手指移动
                            int newX = (int) event.getRawX();
                            int newY = (int) event.getRawY();

                            int dX = newX - startX;//x偏移量
                            int dY = newY - startY;//y偏移量
                            CLog.d(TAG, "x偏移" + dX + " y偏移" + dY);
                            params.x += dX;
                            params.y += dY;
                            wm.updateViewLayout(mytoast, params);
                            CLog.d(TAG, "x " + params.x + " y " + params.y);

                            startX = (int) event.getRawX();//重新初始化手指的位置
                            startY = (int) event.getRawY();//重新初始化手指的位置
                            break;
                        case MotionEvent.ACTION_UP://手指离开屏幕
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putInt("address_x", params.x);
                            editor.putInt("address_y", params.y);
                            editor.apply();
                            break;
                    }
                    return true;//true 代表事件处理完毕,不给父布局享用触摸事件; false 代表事件还没处理完
                }
            });
            TextView tv = (TextView) mytoast.findViewById(R.id.tv_address);
            tv.setText(text);

            //窗体的参数
            // XXX This should be changed to use a Dialog, with a Theme.Toast
            // defined that sets up the layout params appropriately.
            params = new WindowManager.LayoutParams();
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.format = PixelFormat.TRANSLUCENT;//半透明
            params.gravity = Gravity.TOP + Gravity.LEFT;//设置窗体位置

            params.x = sp.getInt("address_x", 100);//窗体距离屏幕左边(px)
            params.y = sp.getInt("address_y", 100);//窗体距离屏幕上方(px)
            params.windowAnimations = Resources.getSystem().getIdentifier("Animation_Toast", "style", "android");//com.android.internal.R.style.Animation_Toast;//吐司的动画
//            params.type = WindowManager.LayoutParams.TYPE_TOAST;//吐司优先级,不可触摸,所以无法取得点击事件
            params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;//电话优先级,非常高级,可以显示在任何view上面,并且可以触摸,需添加权限
            params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON//不让锁屏
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//不让吐司获得焦点
//                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;//原本toast是预设不能触摸的,所以无法取得点击事件

            wm.addView(mytoast, params);
        }

        /**
         * 移除自定义toast
         */
        public void dismissMyToast() {
            if (mytoast != null)
                wm.removeView(mytoast);
        }

    }
}
