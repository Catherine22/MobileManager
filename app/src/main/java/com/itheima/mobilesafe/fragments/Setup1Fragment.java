package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup1Fragment extends Fragment {

    private static final String TAG = "Setup1Fragment";

    public static  Setup1Fragment newInstance() {
        return new Setup1Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup1, container, false);
    }

    @Override
    public void onResume() {
        CLog.d(TAG, "onResume");
        super.onResume();
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
