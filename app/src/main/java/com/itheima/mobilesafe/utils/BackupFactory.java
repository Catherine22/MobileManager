package com.itheima.mobilesafe.utils;

import android.content.Context;

import com.itheima.mobilesafe.utils.backup.BackupConstants;
import com.itheima.mobilesafe.utils.backup.BaseBackup;
import com.itheima.mobilesafe.utils.backup.SmsBackup;

/**
 * Created by Catherine on 2016/10/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BackupFactory {
    public BaseBackup createBackup(Context ctx, int type) {
        BaseBackup b;
        switch (type) {
            case BackupConstants.SMS_BACKUP:
                b = new SmsBackup(ctx);
                break;
            default:
                b = null;
        }
        return b;
    }
}
