package com.itheima.mobilesafe.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.itheima.mobilesafe.services.gcj02.ModifyOffset;
import com.itheima.mobilesafe.services.gcj02.PointDouble;
import com.itheima.mobilesafe.utils.BroadcastActions;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SpNames;

import java.io.IOException;
import java.io.InputStream;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/8/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class GPSService extends Service {
    private final static String TAG = "GPSService";
    private LocationListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        CLog.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CLog.d(TAG, "onCreate");

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        List<String> providers = lm.getAllProviders();
//        CLog.d(TAG, "所有的提供者:"+providers.toString());

        //注册位置监听服务
        //给位置提供者设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置最大精度
//        criteria.setAltitudeRequired(true);//要不要海拔信息
//        criteria.setBearingRequired(true);//要不要方位信息
//        criteria.setCostAllowed(true);//是否允许付费
//        criteria.setPowerRequirement(Criteria.POWER_LOW);//对电量的要求

        String provider = lm.getBestProvider(criteria, true);
        CLog.d(TAG, "running provider:" + provider);
        listener = new MyLocationListener();
        try {
            lm.requestLocationUpdates(provider, 0, 0, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            float accutacy = location.getAccuracy();

            //当位于中国境内时,改用火星坐标,所以要转换以减少误差
            double EAST = 135.0383333333;
            double WEST = 73.0666666667;
            double NORTH = 3.0666666667;
            double SOUTH = 53.55;

            if (longitude <= EAST && longitude >= WEST && latitude <= SOUTH && latitude >= NORTH) {
                //位于中国境内
                try {
                    InputStream is = getAssets().open("axisoffset.dat");
                    ModifyOffset mo = ModifyOffset.getInstance(is);
                    PointDouble npoint = mo.s2c(new PointDouble(location.getLongitude(), location.getLatitude()));

                    longitude = npoint.getX();
                    latitude = npoint.getY();
                    CLog.d(TAG, "In China");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            SharedPreferences sp = getSharedPreferences(SpNames.FILE_CONFIG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SpNames.longitude, longitude + "");
            editor.putString(SpNames.latitude, latitude + "");
            editor.putString(SpNames.accutacy, accutacy + "");
            editor.apply();

            AsyncResponse ar = new AsyncResponse() {
                @Override
                public void onFailure(int errorCode) {
                    CLog.e(TAG, "onFailure" + errorCode);
                }
            };
            Server sv = new Server(GPSService.this, ar);
            Bundle bundle = new Bundle();
            bundle.putString(SpNames.longitude, longitude + "");
            bundle.putString(SpNames.latitude, latitude + "");
            bundle.putString(SpNames.accutacy, accutacy + "");
            sv.pushBundle(BroadcastActions.LOCATION_INFO, bundle);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}