package com.itheima.mobilesafe.designpattern.factory;

import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Blue  implements Color{
    @Override
    public void onDraw() {
        CLog.d("Factory","Drew blue.");
    }
}

