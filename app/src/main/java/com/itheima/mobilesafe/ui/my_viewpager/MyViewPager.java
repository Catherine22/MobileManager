package com.itheima.mobilesafe.ui.my_viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/8/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class MyViewPager extends ViewPager {
    private static final String TAG = "MyViewPager";

    private boolean enabled;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;

    }

    /**
     * Disable paging swiping
     *
     * @param enabled paging swiping
     */
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;

    }
}
