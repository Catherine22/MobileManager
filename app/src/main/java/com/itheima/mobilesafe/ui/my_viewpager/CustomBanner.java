package com.itheima.mobilesafe.ui.my_viewpager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.Settings;
import com.itheima.mobilesafe.ui.my_viewpager.transformers.BasePageTransformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2016/8/16.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class CustomBanner extends RelativeLayout {
    private final static String TAG = "CustomBanner";
    private ViewPager vp_container, vp_background;
    private Context ctx;
    private LinearLayout ll_dots;
    private List<ImageView> dots = new ArrayList<>();
    private boolean showDots;
    private boolean showBackground;
    private int dotsSrcOn;
    private int dotsSrcOff;


    public CustomBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
        initAttr(attrs);
        initView();
    }


    /**
     * 初始化布局文件
     */
    private void initView() {
        View.inflate(ctx, R.layout.banner, this);
        vp_container = (ViewPager) this.findViewById(R.id.vp_container);
        vp_background = (ViewPager) this.findViewById(R.id.vp_background);
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
                    Log.d(TAG, position + " " + positionOffset + " " + positionOffsetPixels);
//                    vp_background.scrollTo((position * Settings.DISPLAY_WIDTH_PX) + positionOffsetPixels, 0);
                }

                @Override
                public void onPageSelected(int position) {
                    for (ImageView dot : dots) {
                        dot.setImageResource(dotsSrcOff);
                    }
                    dots.get(position).setImageResource(dotsSrcOn);
//                    vp_background.setCurrentItem(position);
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
