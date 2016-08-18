package com.itheima.mobilesafe.ui.my_viewpager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.my_viewpager.transformers.BasePageTransformer;
import com.itheima.mobilesafe.utils.CLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Created by Catherine on 2016/8/16.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class CustomBanner extends RelativeLayout {
    private final static String TAG = "CustomBanner";
    private MyViewPager vp_container, vp_background;
    private Context ctx;
    private LinearLayout ll_dots;
    private List<ImageView> dots = new ArrayList<>();
    private boolean showDots;
    private boolean showBackground;
    private int dotsSrcOn;
    private int dotsSrcOff;
    private boolean enableSwiping = true;
    private int enableSwipingPage = -1;
    private int currentPosition;
    private Set<Integer> disablePages;//记录要被禁用的页数


    public CustomBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        disablePages = new HashSet<>();
        initAttr(attrs);
        initView();
    }


    /**
     * Initialize components
     */
    private void initView() {
        View.inflate(ctx, R.layout.banner, this);
        vp_container = (MyViewPager) this.findViewById(R.id.vp_container);
        vp_container.setPagingEnabled(enableSwiping);
        vp_background = (MyViewPager) this.findViewById(R.id.vp_background);
        ll_dots = (LinearLayout) this.findViewById(R.id.ll_dots);

        if (showDots)
            ll_dots.setVisibility(VISIBLE);
        else
            ll_dots.setVisibility(GONE);

        if (showBackground)
            vp_background.setVisibility(VISIBLE);
        else
            vp_background.setVisibility(GONE);
    }

    /**
     * Set PagerAdapter on ViewPager
     *
     * @param adapter
     */
    public void setAdapter(PagerAdapter adapter) {
        if (vp_container != null)
            vp_container.setAdapter(adapter);
    }

    /**
     * Set PagerAdapter on Background ViewPager
     *
     * @param adapter
     */
    public void setBackgroundAdapter(PagerAdapter adapter) {
        if (vp_background != null)
            vp_background.setAdapter(adapter);
    }

    /**
     * To enable / disable the swiping on container view
     *
     * @param page
     * @param b
     */
    public void setPagingEnabled(int page, boolean b) {
        enableSwipingPage = page;
        enableSwiping = b;
        if (!b) {
            disablePages.add(page);
            disable();
        } else {
            if (disablePages.contains(page))
                disablePages.remove(page);
            else {
                disable();
            }
        }
    }

    /**
     * Set items and effect
     *
     * @param itemCount
     * @param effect
     * @param backgroundEffect
     */
    public void setView(int itemCount, TransitionEffect effect, TransitionEffect backgroundEffect) {
        if (vp_container != null) {
            vp_container.setPageTransformer(true, BasePageTransformer.getPageTransformer(effect));
            vp_container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                    CLog.d(TAG, position + " " + positionOffset + " " + positionOffsetPixels);
//                    vp_background.scrollTo((position * Settings.DISPLAY_WIDTH_PX) + positionOffsetPixels, 0);

                }

                @Override
                public void onPageSelected(int position) {
                    currentPosition = position;
                    for (ImageView dot : dots) {
                        dot.setImageResource(dotsSrcOff);
                    }
                    dots.get(position).setImageResource(dotsSrcOn);
                    vp_background.setCurrentItem(position, true);

                    if (!enableSwiping && disablePages.contains(enableSwipingPage)) {
                        disable();
                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        if (vp_background != null) {
            vp_background.setPageTransformer(true, BasePageTransformer.getPageTransformer(backgroundEffect));
        }
        setDotsView(itemCount);
    }


    private void disable() {
        if (currentPosition == enableSwipingPage) {
            vp_container.setPagingEnabled(enableSwiping);
            disablePages.remove(enableSwipingPage);
        } else
            vp_container.setPagingEnabled(true);
    }

    private void setDotsView(int itemCount) {
        for (int i = 0; i < itemCount; i++) {
            ImageView dot = new ImageView(ctx);
            if (i == 0)
                dot.setImageResource(dotsSrcOn);
            else
                dot.setImageResource(dotsSrcOff);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imgParams.setMargins(10, 10, 10, 10);
            imgParams.gravity = Gravity.CENTER;
            dot.setLayoutParams(imgParams);
            dots.add(dot);
            ll_dots.addView(dot, imgParams);
        }
    }

    private void initAttr(AttributeSet attrs) {
        showBackground = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "show_background", false);
        showDots = attrs.getAttributeBooleanValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "show_dots", false);
        dotsSrcOn = attrs.getAttributeIntValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "dots_src_on", android.R.drawable.presence_online);
        dotsSrcOff = attrs.getAttributeIntValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "dots_src_off", android.R.drawable.presence_invisible);
    }
}
