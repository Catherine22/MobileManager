package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.Settings;
import com.itheima.mobilesafe.ui.SettingItemView;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup2Fragment extends Fragment {

    private static final String TAG = "Setup2Fragment";
    private MainInterface mainInterface;
    private SettingItemView siv_sim;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup2, container, false);
        mainInterface = (MainInterface) getActivity();
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sp.edit();
        siv_sim = (SettingItemView) view.findViewById(R.id.siv_sim);
        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否有选中
                if (siv_sim.isChecked()) {
                    siv_sim.setChecked(false);
                    editor.putString("simSerialNumber", null);
                    editor.commit();
                } else {
                    siv_sim.setChecked(true);
                    editor.putString("simSerialNumber", Settings.simSerialNumber);
                    editor.commit();
                }
            }
        });
        if (TextUtils.isEmpty(sp.getString("simSerialNumber", null)))
            siv_sim.setChecked(false);
        else
            siv_sim.setChecked(true);
        return view;
    }
}
