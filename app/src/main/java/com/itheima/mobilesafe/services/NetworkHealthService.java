package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2017/3/24.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class NetworkHealthService extends Service {
    private final static String TAG = "NetworkHealthService";
    private InternetConnectivityReceiver internetReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CLog.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        CLog.d(TAG, "onCreate");
        super.onCreate();
        internetReceiver = new InternetConnectivityReceiver();
        IntentFilter internetIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        internetReceiver = new InternetConnectivityReceiver();
        registerReceiver(internetReceiver, internetIntentFilter);
    }

    public class InternetConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                CLog.d(TAG, "onReceive");
                if (intent.getExtras() != null) {
                    final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                    if (ni != null && ni.isConnectedOrConnecting()) {
                        Toast.makeText(NetworkHealthService.this, "Network " + ni.getTypeName() + " connected", Toast.LENGTH_LONG).show();
                    } else {
                        CLog.e(TAG, "Network disabled");
                        Toast.makeText(NetworkHealthService.this, "Network disabled", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                unregisterReceiver(internetReceiver);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        CLog.d(TAG, "onDestroy");
        try {
            unregisterReceiver(internetReceiver);
        } catch (Exception e) {
            unregisterReceiver(internetReceiver);
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        CLog.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }
}
