package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.Settings;
import com.itheima.mobilesafe.ui.SettingItemView;

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
    private Server sv;
    private SharedPreferences.Editor editor;

    public static final Setup2Fragment newInstance() {
        Setup2Fragment f = new Setup2Fragment();
        return f;
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
                    sv.pushBoolean("DISABLE_SWIPING", false);
                    editor.putString("simSerialNumber", null);
                    editor.commit();
                } else {
                    siv_sim.setChecked(true);
                    sv.pushBoolean("DISABLE_SWIPING", true);
                    editor.putString("simSerialNumber", Settings.simSerialNumber);
                    editor.commit();
                }
            }
        });
        return view;
    }

    private void initData() {
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sp.edit();
        AsyncResponse ar = new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
            }
        };

        sv = new Server(getActivity(), ar);
    }

    @Override
    public void onResume() {
        if (TextUtils.isEmpty(sp.getString("simSerialNumber", null))) {
            siv_sim.setChecked(false);
            sv.pushBoolean("DISABLE_SWIPING", false);
        } else {
            siv_sim.setChecked(true);
            sv.pushBoolean("DISABLE_SWIPING", true);
        }
        super.onResume();
    }
}
