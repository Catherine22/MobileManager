package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.AutoResizeTextView;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AppsManagerFragment extends Fragment {

    private static final String TAG = "AppsManagerFragment";
    private AutoResizeTextView tv_memory_info;
    private TextView tv_installed_apps_count;
    private RecyclerView rv_apps;
    private LinearLayout ll_loading;

    public static AppsManagerFragment newInstance() {
        return new AppsManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_manager, container, false);

        tv_memory_info = (AutoResizeTextView) view.findViewById(R.id.tv_memory_info);
        tv_installed_apps_count = (TextView) view.findViewById(R.id.tv_installed_apps_count);
        rv_apps = (RecyclerView) view.findViewById(R.id.rv_apps);
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);

        tv_installed_apps_count.setEnabled(false);
        tv_installed_apps_count.setClickable(false);


        return view;
    }


}
