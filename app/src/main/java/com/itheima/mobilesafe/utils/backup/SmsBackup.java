package com.itheima.mobilesafe.utils.backup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

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
    private PrecessListener listener;
    private int max;
    private int process;

    public SmsBackup(Context ctx) {
        this.ctx = ctx;
    }

    public interface PrecessListener {
        void currentProcess(int process);
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
        int curCursor = 0;
        while (cursor.moveToNext()) {
            try {
                Thread.sleep(500);//测试资料量大
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            curCursor++;
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

            if (listener != null)
                listener.currentProcess(curCursor);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    @Override
    public void recovery() {

    }

    public int getMaxProgress() {
        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        max = cursor.getCount();
        return max;
    }

    public void setPrecessListener(PrecessListener listener) {
        this.listener = listener;
    }
}
