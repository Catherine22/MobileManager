package com.itheima.mobilesafe.utils;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
/**
 * Created by Yi-Jing on 2016/8/28.
 */

public class ViewKits {
    private Context ctx;

    public ViewKits(Context ctx) {
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

    /**
     * 弹出虚拟键盘
     */
    public void showKeyboard() {
        // Check if no view has focus:
        Activity act = (Activity)ctx;
        View view = act.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}

