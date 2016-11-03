package com.itheima.mobilesafe.utils.objects;

import android.graphics.drawable.Drawable;

/**
 * Created by Catherine on 2016/10/27.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class AppInfo {
    private Drawable icon;
    private String name;
    private String packageName;
    private String firstInstallTime;
    private String lastUpdateTime;
    private String versionName;
    private boolean inRom;
    private boolean isUserApp;


    @Override
    public String toString() {
        return "AppInfo{" +
                "firstInstallTime='" + firstInstallTime + '\'' +
                ", icon=" + icon +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", versionName='" + versionName + '\'' +
                ", inRom=" + inRom +
                ", isUserApp=" + isUserApp +
                '}';
    }

    public String getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(String firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
