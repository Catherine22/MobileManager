package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class TrafficManagerFragment extends Fragment {

    private static final String TAG = "TrafficManagerFragment";

    public static TrafficManagerFragment newInstance() {
        return new TrafficManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traffic_manager, container, false);
        return view;
    }
}
