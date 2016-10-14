package com.itheima.mobilesafe.utils.backup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.util.Xml;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Settings;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
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

    public SmsBackup(Context ctx) {
        this.ctx = ctx;
    }

    public interface PrecessListener {
        void currentProcess(int process);

        void maxProcess(int max);
    }

    /**
     * 备份数据于本地BACKUP_PATH{@link Settings}
     *
     * @throws IOException
     */
    @Override
    public void backupToLocal() throws IOException {

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
        if (listener != null)
            listener.maxProcess(cursor.getCount());
        serializer.attribute(null, "length", cursor.getCount() + "");//存入数据长度, 方便解析时计算进度
        while (cursor.moveToNext()) {
//            try {
//                Thread.sleep(500);//模拟资料量大
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

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

    /**
     * 回复本地数据于本地BACKUP_PATH{@link Settings}
     *
     * @param delete 是否删除之前的短信
     * @throws IOException
     */
    @Override
    public void restoreFromLocal(boolean delete) throws IOException, XmlPullParserException {
//        File file = new File(Settings.BACKUP_PATH, "sms.xml");
//        FileInputStream fis = new FileInputStream(file);
//
//        // 1.获取pull解析器的实例
//        XmlPullParser parser = Xml.newPullParser();
//        // 2.设置解析器的一些参数
//        // 必须确定文件和eclipse中文件的properties都是同编码
//        parser.setInput(fis, "utf-8");
//        // 获取pull解析器的事件类型
//        int type = parser.getEventType();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

        }

//
//        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            boolean canWriteSms = false;
//            if (!SmsWriteOpUtil.isWriteEnabled(ctx.getApplicationContext())) {
//                CLog.d(TAG, "disable");
//                canWriteSms = SmsWriteOpUtil.setWriteEnabled(ctx.getApplicationContext(), true);
//            }
//            CLog.d(TAG, "canWriteSms " + canWriteSms);
//        }

        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        if (delete) {
            resolver.delete(uri, null, null);
        }


        //写入sms数据

        ContentValues cv = new ContentValues();
        cv.put("address", "123");
        cv.put("date", "1475486819908");
        cv.put("type", "1");
        cv.put("body", "还原成功");
        resolver.insert(uri, cv);


        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);

        CLog.d(TAG, "count:" + cursor.getCount());

//        fis.close();
    }

    public void setPrecessListener(PrecessListener listener) {
        this.listener = listener;
    }
}
