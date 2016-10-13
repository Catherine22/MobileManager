package com.itheima.mobilesafe.utils.backup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.MemoryUtils;
import com.itheima.mobilesafe.utils.Settings;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Catherine on 2016/10/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SmsBackup implements BaseBackup {
    private final static String TAG = "SmsBackup";
    private Context ctx;
    private XmlSerializer serializer;

    public SmsBackup(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void backup() throws IOException {

        File dir = new File(Settings.BACKUP_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "sms.xml");
        if (!file.exists())
            file.createNewFile();

        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "Backup/sms.xml");
        FileOutputStream fos = new FileOutputStream(file);

        //序列化：把内存中的东西写进文件里
        serializer = Xml.newSerializer();//xml的生成器（序列化器）
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);//对应endDocument()
        serializer.startTag(null, "smss");//endTag()

        //获取sms数据
        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        while (cursor.moveToNext()) {
            serializer.startTag(null, "sms");

            serializer.startTag(null, "address");
            serializer.text(cursor.getString(0));
            serializer.endTag(null, "address");

            serializer.startTag(null, "date");
            serializer.text(cursor.getString(1));
            serializer.endTag(null, "date");

            serializer.startTag(null, "type");
            serializer.text(cursor.getString(2));
            serializer.endTag(null, "type");

            serializer.startTag(null, "body");
            serializer.text(cursor.getString(3));
            serializer.endTag(null, "body");

            serializer.endTag(null, "sms");
        }
        cursor.close();
        fos.close();

        serializer.endTag(null, "smss");
        serializer.endDocument();

        MemoryUtils.saveStringToRom(ctx, "Backup", "sms.xml", "YO", new MemoryUtils.OnResponse() {
            @Override
            public void onSuccess() {
                CLog.d(TAG, "onSuccess()");
            }

            @Override
            public void onFail(int what, String errorMessage) {
                CLog.d(TAG, "onFail()");

            }
        });
    }

    @Override
    public void recovery() {

    }
}
