package com.itheima.mobilesafe.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.itheima.mobilesafe.fragments.Setup1Fragment;
import com.itheima.mobilesafe.fragments.Setup2Fragment;
import com.itheima.mobilesafe.fragments.Setup3Fragment;
import com.itheima.mobilesafe.fragments.Setup4Fragment;
import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/8/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    int count = 4;

    public MyFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        CLog.d("MyFragmentStatePagerAdapter", "getItem");
        switch (position) {
            case 0:
                return new Setup1Fragment();
            case 1:
                return new Setup2Fragment();
            case 2:
                return new Setup3Fragment();
            case 3:
                return new Setup4Fragment();
            default:
                return new Setup1Fragment();
        }
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
