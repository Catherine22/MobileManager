package com.itheima.mobilesafe.utils.backup;

import android.content.Context;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.MemoryUtils;

import java.io.IOException;

/**
 * Created by Catherine on 2016/10/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SmsBackup implements BaseBackup {
    private final static String TAG = "SmsBackup";
    private Context ctx;

    public SmsBackup(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void backup() throws IOException {
        MemoryUtils.saveToSD("Backup", "sms.xml", "YO", new MemoryUtils.OnResponse() {
            @Override
            public void onSuccess() {
                CLog.d(TAG,"onSuccess()");
            }

            @Override
            public void onFail(int what, String errorMessage) {
                CLog.d(TAG,"onFail()");

            }
        });

    }

    @Override
    public void recovery() {

    }
}
