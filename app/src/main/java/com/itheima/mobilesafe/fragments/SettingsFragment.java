package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class SettingsFragment extends Fragment {
    private final static String TAG = "SettingsFragment";
    private SettingItemView siv_update;
    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        CLog.d(TAG, "onCreateView");

        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        siv_update = (SettingItemView) view.findViewById(R.id.siv_update);

        boolean update = sp.getBoolean("update", false);
        if(update){
            //自动升级已经开启
            siv_update.setChecked(true);
        }else{
            //自动升级已经关闭
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否有选中
                //已经打开自动升级了
                if(siv_update.isChecked()){
                    siv_update.setChecked(false);
                    editor.putBoolean("update", false);
                }else{
                    //没有打开自动升级
                    siv_update.setChecked(true);
                    editor.putBoolean("update", true);
                }
                editor.commit();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CLog.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CLog.d(TAG, "onDestroyView");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(TAG, "onCreate");
    }

    @Override
    public void onStop() {
        super.onStop();
        CLog.d(TAG, "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        CLog.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        CLog.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        CLog.d(TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CLog.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CLog.d(TAG, "onViewCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        CLog.d(TAG, "onViewStateRestored");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CLog.d(TAG, "onDetach");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CLog.d(TAG, "onAttach");
    }
}
