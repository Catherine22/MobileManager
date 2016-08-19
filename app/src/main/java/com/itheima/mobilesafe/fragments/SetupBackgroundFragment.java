package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.itheima.mobilesafe.R;

/**
 * Created by Catherine on 2016/8/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class SetupBackgroundFragment extends Fragment {
//    private final static String TAG = "SetupBackgroundFragment";
    public static final String IMAGE = "IMAGE";
    private int resImageId = 0;

    public static SetupBackgroundFragment newInstance(int resImageId) {
        SetupBackgroundFragment f = new SetupBackgroundFragment();
        Bundle bdl = new Bundle(1);
        bdl.putInt(IMAGE, resImageId);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resImageId = getArguments().getInt(IMAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup_background, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView img = (ImageView) view.findViewById(R.id.iv_bg);
        img.setImageResource(resImageId);
    }
}