package com.itheima.mobilesafe.utils.backup;

import java.io.IOException;

/**
 * Created by Catherine on 2016/10/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface BaseBackup {
    void backup() throws IOException;

    void recovery();
}
