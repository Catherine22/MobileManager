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
    private List<Fragment> fragments;
    private int counts;

    public MyFragmentStatePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        counts = fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        CLog.d("MyFragmentStatePagerAdapter", "getItem" + position);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return counts;
    }

    public void setCounts(int counts){
        this.counts = counts;
        notifyDataSetChanged();
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
        notifyDataSetChanged();
    }
}
