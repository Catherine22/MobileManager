package com.itheima.mobilesafe.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.interfaces.MyPermissionsResultListener;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Settings;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup2Fragment extends Fragment {

    private static final String TAG = "Setup2Fragment";
    private SettingItemView siv_sim;
    private SharedPreferences sp;
    private MainInterface mainInterface;
    private Server sv;
    private SharedPreferences.Editor editor;

    public static Setup2Fragment newInstance() {
        return new Setup2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup2, container, false);
        initData();

        siv_sim = (SettingItemView) view.findViewById(R.id.siv_sim);
        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有选中
                if (siv_sim.isChecked()) {
                    siv_sim.setChecked(false);
                    sv.pushInt("DISABLE_SWIPING", 2);
                    editor = sp.edit();
                    editor.putString("sim_serial", null);
                    editor.apply();
                } else {
                    mainInterface.getPermissions(new String[]{
                                    Manifest.permission.WAKE_LOCK,
                                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.RECEIVE_SMS,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            new MyPermissionsResultListener() {
                                @Override
                                public void onGranted() {
                                    CLog.d(TAG, "onGranted");
                                    siv_sim.setChecked(true);
                                    sv.pushInt("DISABLE_SWIPING", -1);
                                    editor = sp.edit();
                                    editor.putString("sim_serial", Settings.simSerialNumber);
                                    editor.apply();
                                }

                                @Override
                                public void onDenied() {
                                    CLog.d(TAG, "onDenied");
                                    getActivity().finish();
                                }
                            }
                    );
                }
            }
        });
        return view;
    }

    private void initData() {
        mainInterface = (MainInterface) getActivity();
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        AsyncResponse ar = new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.e(TAG, "onFailure" + errorCode);
            }
        };
        sv = new Server(getActivity(), ar);
        //取得sim卡信息
        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//        Settings.simSerialNumber = tm.getSimSerialNumber();
        Settings.simSerialNumber = "65123576";
    }

    @Override
    public void onResume() {
        if (TextUtils.isEmpty(sp.getString("sim_serial", null))) {
            siv_sim.setChecked(false);
            sv.pushInt("DISABLE_SWIPING", 2);
        } else {
            siv_sim.setChecked(true);
            sv.pushInt("DISABLE_SWIPING", -1);
        }
        super.onResume();
    }
}
