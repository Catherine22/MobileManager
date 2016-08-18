package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup4Fragment extends Fragment {

    private static final String TAG = "Setup4Fragment";
    private MainInterface mainInterface;
    private Button bt_next;
    private SharedPreferences sp;

    public static final Setup4Fragment newInstance() {
        Setup4Fragment f = new Setup4Fragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup4, container, false);
        mainInterface = (MainInterface) getActivity();
        bt_next = (Button) view.findViewById(R.id.bt_next);
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("configed", true);
                editor.commit();

                mainInterface.clearAllFragments();
                mainInterface.callFragment(Constants.ANTI_THEFT_FRAG);
            }
        });
        return view;
    }
}
