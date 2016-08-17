package com.itheima.mobilesafe.ui.my_viewpager.transformers;

import android.view.View;

import com.itheima.mobilesafe.ui.view_helper.ViewHelper;

/**
 * Created by Catherine on 2016/8/16.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AlphaPageTransformer extends BasePageTransformer {
    private float mMinScale = 0.4f;

    @Override
    public void handleInvisiblePage(View view, float position) {
        ViewHelper.setAlpha(view, 0);
    }

    @Override
    public void handleLeftPage(View view, float position) {
        ViewHelper.setAlpha(view, mMinScale + (1 - mMinScale) * (1 + position));
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewHelper.setAlpha(view, mMinScale + (1 - mMinScale) * (1 - position));
    }

    public void setMinScale(float minScale) {
        if (minScale >= 0.0f && minScale <= 1.0f) {
            mMinScale = minScale;
        }
    }
}
