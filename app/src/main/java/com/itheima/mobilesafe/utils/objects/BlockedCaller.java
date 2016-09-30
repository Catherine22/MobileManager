package com.itheima.mobilesafe.utils.objects;

/**
 * Created by Catherine on 2016/9/29.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlockedCaller {
    public String name;
    public String number;
    public int MODE;

    @Override
    public String toString() {
        return "BlockedCaller{" +
                ", name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", MODE=" + MODE +
                '}';
    }
}
