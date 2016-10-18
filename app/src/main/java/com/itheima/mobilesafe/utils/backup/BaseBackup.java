package com.itheima.mobilesafe.utils.backup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Catherine on 2016/10/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public interface BaseBackup {
    void backupToLocal() throws IOException;

    void restoreFromLocal(boolean delete) throws IOException, XmlPullParserException;
}
