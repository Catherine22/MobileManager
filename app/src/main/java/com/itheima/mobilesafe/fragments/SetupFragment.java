package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.my_viewpager.BasePageTransformer;
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
    private MainInterface mainInterface;
    private ViewPager vp_container;
    private TextView tv_title;
    private String[] titles = new String[]{"欢迎使用手机防盗", "手机卡绑定", "设置安全号码", "恭喜您设置完成"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        mainInterface = (MainInterface) getActivity();
        tv_title = (TextView)view.findViewById(R.id.tv_title);
        vp_container = (ViewPager) view.findViewById(R.id.vp_container);
        vp_container.setPageTransformer(true, BasePageTransformer.getPageTransformer(TransitionEffect.FADE));
        vp_container.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {

                                    @Override
                                    public int getCount() {
                                        return titles.length;
                                    }

                                    @Override
                                    public Fragment getItem(int position) {
                                        tv_title.setText(titles[position]);
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
        return view;
    }
}
