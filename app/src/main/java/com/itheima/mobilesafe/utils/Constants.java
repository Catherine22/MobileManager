package com.itheima.mobilesafe.utils;

import com.itheima.mobilesafe.R;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Constants {
    //Tags of the fragments
    public final static int ANTI_THEFT_FRAG = 0;
    public final static int APPS_MAG_FRAG = 2;
    public final static int TASK_FRAG = 3;
    public final static int A_TOOLS_FRAG = 7;
    public final static int SETTINGS_FRAG = 8;
    public final static int SETUP1_FRAG = 9;
    public final static int SETUP2_FRAG = 10;
    public final static int SETUP3_FRAG = 11;
    public final static int SETUP4_FRAG = 12;
    public final static int SETUP_FRAG = 13;
    public final static int CONTACTS_FRAG = 14;
    public final static int NUM_ADDRESS_QUERY_FRAG = 15;
    public final static int BLACKLIST_FRAG = 16;
    public final static int TRAFFIC_MAG_FRAG = 17;

    //activity request code
    public final static int REQUEST_CODE_ENABLE_ADMIN = 10001;
    public final static int OVERLAY_PERMISSION_REQ_CODE = 10002;
    public final static int PERMISSION_WRITE_SETTINGS = 10003;
    public final static int CHANGEING_DEFAULT_SMS_APP = 10004;
    public final static int ACCOUNT_KIT_REQ_CODE = 10005;
    public final static int UNINSTASLL_APP = 10006;

    //response code
    public static final int FAILED_TO_SEND = 0;
    public static final int SENT_SUCCESSFULLY = 1;
    public static final int TIMEOUT = 2;

    //path
    public static String PACKAGE_NAME;
    public static String DB_NAME;
    public static String DB_PATH;

    public static String[] addressBgs = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    public static int[] addressBgRes = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};

    //Login
    public final static int NONE = 0;
    public final static int ACCOUNTKIT = 1;
    public final static int FB = 2;
    public final static int GOOGLE = 3;
    public final static int SINA = 4;
    public final static int QQ = 5;
    public final static int WEIBO = 6;
    public final static int WECHAT = 7;
    public static String[] loginAccounts = new String[]{"NONE", "AccountKit", "Facebook", "Google", "Sina", "QQ", "Weibo", "WeChat"};
}
