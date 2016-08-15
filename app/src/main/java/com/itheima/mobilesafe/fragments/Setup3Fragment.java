package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.itheima.mobilesafe.Constants;
import com.itheima.mobilesafe.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.SettingItemView;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup3Fragment extends Fragment {

    private static final String TAG = "Setup2Fragment";
    private MainInterface mainInterface;
    private Button bt_next, bt_back;
    private SettingItemView siv_sim;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup2, container, false);
        mainInterface = (MainInterface) getActivity();
        bt_next = (Button) view.findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.callFragment(Constants.SETUP2_FRAG);
            }
        });
        bt_back = (Button) view.findViewById(R.id.bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.backToPreviousPage();
            }
        });
        siv_sim = (SettingItemView) view.findViewById(R.id.siv_sim);
        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_sim.isChecked()) {

                } else {

                }
            }
        });
        return view;
    }
}
