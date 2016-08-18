package com.itheima.mobilesafe.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itheima.mobilesafe.utils.CLog;

import java.util.List;

/**
 * Created by Catherine on 2016/8/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    int count;
    private List<Fragment> fragments;

    public MyFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        count = fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        CLog.d("MyFragmentStatePagerAdapter", "getItem"+position);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }
}
