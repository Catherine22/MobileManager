package com.itheima.mobilesafe.fragments;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.itheima.mobilesafe.Constants;
import com.itheima.mobilesafe.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.SettingItemView;
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

    private int[] resources = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5};
    private String[] titles = new String[]{"第1页", "第2页", "第3页", "第4页", "第5页"};

    private List<ImageView> images = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);
        mainInterface = (MainInterface) getActivity();


        initData();


        vp_container = (ViewPager) view.findViewById(R.id.vp_container);
        vp_container.setPageTransformer(true, BasePageTransformer.getPageTransformer(TransitionEffect.Alpha));
        vp_container.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return resources.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                Log.d(TAG, "isViewFromObject");
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Log.d(TAG, "destroyItem");
                container.removeView(images.get(position));
            }

            @Override
            public int getItemPosition(Object object) {
                Log.d(TAG, "getItemPosition");
                return super.getItemPosition(object);
            }

            @Override
            public void notifyDataSetChanged() {
                Log.d(TAG, "notifyDataSetChanged");
                super.notifyDataSetChanged();
            }

            @Override
            public void restoreState(Parcelable state, ClassLoader loader) {
                Log.d(TAG, "restoreState");
                super.restoreState(state, loader);
            }

            @Override
            public Parcelable saveState() {
                Log.d(TAG, "saveState");
                return super.saveState();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public float getPageWidth(int position) {
                Log.d(TAG, "getPageWidth" + super.getPageWidth(position));
                return super.getPageWidth(position);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(images.get(position));
                return images.get(position);
            }
        });
        return view;
    }

    private void initData() {
        for (int resource :
                resources) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(resource);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            images.add(imageView);
        }
    }
}
