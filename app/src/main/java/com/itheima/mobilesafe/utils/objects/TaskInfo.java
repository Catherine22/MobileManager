package com.itheima.mobilesafe.utils.objects;

import android.graphics.drawable.Drawable;

import java.text.DecimalFormat;

/**
 * Created by Yi-Jing on 2016/9/11.
 * 进程信息的业务bean
 */

public class TaskInfo {
    public Drawable icon;
    public String name;
    public String packageName;
    public long memSize;
    /**
     * true 用户进程
     * false 系统进程
     */
    public boolean userTask;

    public String toString() {
        return "name:" + name + " packageName:" + packageName + " memSize:" + memSize + " userTask:" + userTask;
    }
}
