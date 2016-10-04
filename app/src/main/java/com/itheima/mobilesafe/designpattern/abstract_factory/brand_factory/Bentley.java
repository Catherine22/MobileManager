package com.itheima.mobilesafe.designpattern.abstract_factory.brand_factory;

import com.itheima.mobilesafe.utils.CLog;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Bentley implements Brand{
    @Override
    public void show() {
        CLog.d("Factory","Bentley");
    }
}
