package com.itheima.mobilesafe.ui.my_viewpager.transformers;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.itheima.mobilesafe.ui.my_viewpager.TransitionEffect;

/**
 * Created by Catherine on 2016/8/16.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public abstract class BasePageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        if (position < -1.0f) {
            // [-Infinity,-1)
            // This page is way off-screen to the left.
            handleInvisiblePage(view, position);
        } else if (position <= 0.0f) {// a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            // [-1,0]
            // Use the default slide transition when moving to the left page
            handleLeftPage(view, position);
        } else if (position <= 1.0f) {
            // (0,1]
            handleRightPage(view, position);
        } else {
            // (1,+Infinity]
            // This page is way off-screen to the right.
            handleInvisiblePage(view, position);
        }
    }

    public abstract void handleInvisiblePage(View view, float position);

    public abstract void handleLeftPage(View view, float position);

    public abstract void handleRightPage(View view, float position);

    public static BasePageTransformer getPageTransformer(TransitionEffect effect) {
        switch (effect) {
            case DEFAULT:
                return new DefaultPageTransformer();
            case ALPHA:
                return new AlphaPageTransformer();
            case FADE:
                return new FadePageTransformer();
            case STACK:
                return new StackPageTransformer();
            case ZOOM:
                return new ZoomPageTransformer();
            case ZOOMSTACK:
                return new ZoomStackPageTransformer();
            case ZOOMFADE:
                return new ZoomFadePageTransformer();
            default:
                return new DefaultPageTransformer();
        }
    }
}
