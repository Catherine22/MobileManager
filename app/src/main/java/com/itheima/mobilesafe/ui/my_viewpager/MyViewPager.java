package com.itheima.mobilesafe.ui.my_viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Catherine on 2016/8/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MyViewPager extends ViewPager {
    MyViewPager mCustomPager;
    private boolean forSuper;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!forSuper) {

            mCustomPager.forSuper(true);
            mCustomPager.onInterceptTouchEvent(arg0);
            mCustomPager.forSuper(false);
        }
        return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!forSuper) {
            mCustomPager.forSuper(true);
            mCustomPager.onTouchEvent(arg0);
            mCustomPager.forSuper(false);
        }
        return super.onTouchEvent(arg0);
    }

    public void setViewPager(MyViewPager customPager) {
        mCustomPager = customPager;
    }

    public void forSuper(boolean forSuper) {
        this.forSuper = forSuper;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        if (!forSuper) {
            mCustomPager.forSuper(true);
            mCustomPager.setCurrentItem(item, smoothScroll);
            mCustomPager.forSuper(false);
        }
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        if (!forSuper) {
            mCustomPager.forSuper(true);
            mCustomPager.setCurrentItem(item);
            mCustomPager.forSuper(false);
        }
        super.setCurrentItem(item);

    }

}
