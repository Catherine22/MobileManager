package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
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
    private List<Fragment> fragments;
    private Client client;
    private MyFragmentStatePagerAdapter adapter;

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.setup1));
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.bind));
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.phone));
        fragments.add(SetupBackgroundFragment.newInstance(R.drawable.phone));
        return fragments;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        fragments = getFragments();
        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                CLog.d(TAG, "You got " + result.isBoolean());
                if (result.isBoolean())
                    adapter.setCount(4);
                else
                    adapter.setCount(2);
//
//                cb_container.setPagingEnabled(1, result.isBoolean());
            }
        };
        client = new Client(getActivity(), cr);
        client.gotMessages("DISABLE_SWIPING");

        adapter = new MyFragmentStatePagerAdapter(getFragmentManager());

        cb_container = (CustomBanner) view.findViewById(R.id.cb_container);
        cb_container.setView(PAGE_COUNT, TransitionEffect.DEFAULT, TransitionEffect.FADE);
        cb_container.setAdapter(adapter);
        cb_container.setBackgroundAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        client.release();
        super.onDestroy();
    }
}
