package com.itheima.mobilesafe.designpattern.singleton;

/**
 * Created by Catherine on 2016/10/4.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

import com.itheima.mobilesafe.utils.CLog;

/**
 * 创建枚举默认就是线程安全的，所以不需要担心double checked locking，而且还能防止反序列化导致重新创建新的对象。
 * 一般情况下直接使用饿汉式就好了，如果明确要求要懒加载（lazy initialization）会倾向于使用静态内部类，如果涉及到反序列化创建对象时会试着使用枚举的方式来实现单例。
 */
public enum EnumSingleton {
    INSTANCE;

    public void print() {
        CLog.d("Singleton", "EnumSingleton");
    }
}
