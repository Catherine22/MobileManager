package com.itheima.mobilesafe.ui.my_viewpager.transformers;

import android.view.View;

import com.itheima.mobilesafe.ui.view_helper.ViewHelper;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/6/19 上午8:41
 * 描述:
 */
public class StackPageTransformer extends BasePageTransformer {

    @Override
    public void handleInvisiblePage(View view, float position) {
    }

    @Override
    public void handleLeftPage(View view, float position) {
    }

    @Override
    public void handleRightPage(View view, float position) {
        ViewHelper.setTranslationX(view, -view.getWidth() * position);
    }

}