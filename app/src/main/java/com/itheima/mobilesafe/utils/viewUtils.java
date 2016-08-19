package com.itheima.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Catherine on 2016/8/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class ViewUtils {
    private Context ctx;

    public ViewUtils(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 收起虚拟键盘
     */
    public void hideKeyboard() {
        // Check if no view has focus:
        Activity act = (Activity)ctx;
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
