package com.itheima.mobilesafe.utils.login;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Catherine on 2016/10/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface BaseLogin {

    void login(Activity activity, Object TYPE);

    void logout();

    boolean isLogin();
}
