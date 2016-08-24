package com.itheima.mobilesafe.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.itheima.mobilesafe.interfaces.MyPermissionsResultListener;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.Settings;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup4Fragment extends Fragment {

    private static final String TAG = "Setup4Fragment";
    private MainInterface mainInterface;
    private SharedPreferences.Editor editor;
    private CheckBox cb;

    public static Setup4Fragment newInstance() {
        return new Setup4Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup4, container, false);
        mainInterface = (MainInterface) getActivity();

        cb = (CheckBox) view.findViewById(R.id.cb);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked())
                    cb.setText("已开启防盗保护");
                else
                    cb.setText("你没有开启防盗保护");
            }
        });
        Button bt_next = (Button) view.findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("safe_phone", Settings.safePhone);
                editor.putBoolean("configed", true);
                editor.apply();

                mainInterface.clearAllFragments();
                mainInterface.callFragment(Constants.ANTI_THEFT_FRAG);
            }
        });
        return view;
    }
}
