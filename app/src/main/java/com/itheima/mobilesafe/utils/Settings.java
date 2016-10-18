package com.itheima.mobilesafe.utils;

import android.os.Environment;

/**
 * Created by Catherine on 2016/8/17.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Settings {
    public static int DISPLAY_WIDTH_PX;
    public static int DISPLAY_HEIGHT_PX;
    public static String simSerialNumber;
    public static String safePhone;

    public static final String BACKUP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/itheima/Backups/";
    public static final String taoBaoGetAddressUrl = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";//淘宝
    public static final String tenpayUrl = "http://life.tenpay.com/cgi-bin/mobile/MobileQueryAttribution.cgi";//财付通
}
