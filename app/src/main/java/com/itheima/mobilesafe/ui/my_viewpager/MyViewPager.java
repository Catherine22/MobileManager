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

    /**
     * 预设,可翻页
     */
    public static final int NONE = -1;
    /**
     * 设置让ViewPager完全不能翻页
     */
    public static final int ALL = 0;
    /**
     * 设置让ViewPager不能翻到下一页
     */
    public static final int FORWARD = 1;
    /**
     * 设置让ViewPager不能翻到上一页
     */
    public static final int BACK = 2;


    private boolean enabled;
    private int type;
    private float x1, x2, dx;

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
        this.type = NONE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                CLog.d(TAG, "ACTION_DOWN" + x1);
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                CLog.d(TAG, "ACTION_UP" + x2);
                break;
            case MotionEvent.ACTION_MOVE:
                dx = event.getX();
                CLog.d(TAG, "ACTION_MOVE" + dx);
                break;
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
     * 设置让ViewPager不能翻页
     *
     * @param enabled 能不能翻页
     * @param type    翻页类型
     */
    public void setPagingEnabled(boolean enabled, int type) {
        this.type = type;
        this.enabled = enabled;
    }
}
