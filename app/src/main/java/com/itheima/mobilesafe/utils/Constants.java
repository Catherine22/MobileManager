package com.itheima.mobilesafe.utils;

import com.itheima.mobilesafe.R;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Constants {
    public final static int ANTI_THEFT_FRAG = 0;
    public final static int TASK_FRAG = 3;
    public final static int A_TOOLS_FRAG = 7;
    public final static int SETTINGS_FRAG = 8;
    public final static int SETUP1_FRAG = 9;
    public final static int SETUP2_FRAG = 10;
    public final static int SETUP3_FRAG = 11;
    public final static int SETUP4_FRAG = 12;
    public final static int SETUP_FRAG = 13;
    public final static int CONSTANTS = 14;
    public final static int NUM_ADDRESS_QUERY_FRAG = 15;

    //activity request code
    public final static int REQUEST_CODE_ENABLE_ADMIN = 10001;
    public final static int OVERLAY_PERMISSION_REQ_CODE = 10002;

    //response code
    public static final int FAILED_TO_SEND = 0;
    public static final int SENT_SUCCESSFULLY = 1;
    public static final int SEND_TIMEOUT = 2;

    //path
    public static String PACKAGE_NAME;
    public static String DB_NAME;
    public static String DB_PATH;

    public static String[] addressBgs = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    public static int[] addressBgRes = new int[]{R.drawable.call_locate_white, R.drawable.call_locate_orange, R.drawable.call_locate_blue, R.drawable.call_locate_gray, R.drawable.call_locate_green};

}
