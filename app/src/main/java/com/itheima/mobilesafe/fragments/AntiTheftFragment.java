package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.Constants;
import com.itheima.mobilesafe.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.Settings;
import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AntiTheftFragment extends Fragment {

    //    private static final String TAG = "AntiTheftFragment";
    private MainInterface mainInterface;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anti_theft, container, false);
        mainInterface = (MainInterface) getActivity();

        TextView tv_setup = (TextView) view.findViewById(R.id.tv_setup);
        tv_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.callFragment(Constants.SETUP_FRAG);

            }
        });
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        CLog.d("safePhone",Settings.safePhone);
        tv_phone.setText(Settings.safePhone);
        return view;
    }
}
