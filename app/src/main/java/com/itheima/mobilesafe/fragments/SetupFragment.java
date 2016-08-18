package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.MyFragmentStatePagerAdapter;
import com.itheima.mobilesafe.ui.my_viewpager.CustomBanner;
import com.itheima.mobilesafe.ui.my_viewpager.TransitionEffect;
import com.itheima.mobilesafe.utils.CLog;

import java.util.ArrayList;
import java.util.List;

import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Result;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class SetupFragment extends Fragment {

    private static final String TAG = "SetupFragment";
    private CustomBanner cb_container;
    private int PAGE_COUNT = 4;
    private List<Fragment> bFragments, fFragments;
    private Client client;
    private MyFragmentStatePagerAdapter forwardAdapter, backAdapter;

    private List<Fragment> getBFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.setup1));
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.bind));
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.phone));
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.phone));
        return fragments;
    }

    private List<Fragment> getFFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(Setup1Fragment.newInstance());
        fragments.add(Setup2Fragment.newInstance());
        fragments.add(Setup3Fragment.newInstance());
        fragments.add(Setup4Fragment.newInstance());
        return fragments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        bFragments = getBFragments();
        fFragments = getFFragments();

        forwardAdapter = new MyFragmentStatePagerAdapter(getFragmentManager(), fFragments);
        backAdapter = new MyFragmentStatePagerAdapter(getFragmentManager(), bFragments);

        cb_container = (CustomBanner) view.findViewById(R.id.cb_container);
        cb_container.setView(PAGE_COUNT, TransitionEffect.DEFAULT, TransitionEffect.FADE);
        cb_container.setAdapter(forwardAdapter);
        cb_container.setBackgroundAdapter(backAdapter);

        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                CLog.d(TAG, "You got " + result.isBoolean());
                if (result.isBoolean()) {
                    forwardAdapter.setCount(4);
                    backAdapter.setCount(4);
                } else {
                    forwardAdapter.setCount(2);
                    backAdapter.setCount(2);
                }
            }
        };
        client = new Client(getActivity(), cr);
        client.gotMessages("DISABLE_SWIPING");

        return view;
    }

    @Override
    public void onDestroy() {
        client.release();
        super.onDestroy();
    }
}
