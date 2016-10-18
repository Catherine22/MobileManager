package com.itheima.mobilesafe.os;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Catherine on 2016/10/14.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class HeadlessSmsSendService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
