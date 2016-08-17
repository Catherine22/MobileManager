package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.my_viewpager.CustomBanner;
import com.itheima.mobilesafe.ui.my_viewpager.TransitionEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class SetupFragment extends Fragment {

    private static final String TAG = "SetupFragment";
    private CustomBanner cb_container;
    private final int PAGE_COUNT = 4;
    private List<Fragment> fragments;

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
        cb_container = (CustomBanner) view.findViewById(R.id.cb_container);
        cb_container.setView(PAGE_COUNT, TransitionEffect.DEFAULT, TransitionEffect.FADE);
        cb_container.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {

                                    @Override
                                    public int getCount() {
                                        return PAGE_COUNT;
                                    }

                                    @Override
                                    public Fragment getItem(int position) {
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
                                }

        );
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
}
