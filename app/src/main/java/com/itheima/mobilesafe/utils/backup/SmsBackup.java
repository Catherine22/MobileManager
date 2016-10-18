package com.itheima.mobilesafe.utils.backup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Xml;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Settings;
import com.itheima.mobilesafe.utils.objects.Sms;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        CLog.d(TAG, cursor.getCount() + "");
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

            if (!TextUtils.isEmpty(cursor.getString(0))) {
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
            }


            serializer.endTag(null, "sms");

            if (listener != null)
                listener.currentProcess(curCursor);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    private List<Sms> values;
    private Sms sms;

    /**
     * 回复本地数据于本地BACKUP_PATH{@link Settings}
     *
     * @param delete 是否删除之前的短信
     * @throws IOException
     */
    @Override
    public void restoreFromLocal(boolean delete) throws IOException, XmlPullParserException {
        File file = new File(Settings.BACKUP_PATH, "sms.xml");
        FileInputStream fis = new FileInputStream(file);
        values = new ArrayList<>();

        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        if (delete) {
            resolver.delete(uri, null, null);
        }

        try {
            // 1.获取pull解析器的实例
            XmlPullParser parser = Xml.newPullParser();
            // 2.设置解析器的一些参数
            // 必须确定文件和eclipse中文件的properties都是同编码
            parser.setInput(fis, "utf-8");
            // 获取pull解析器的事件类型
            int event = parser.getEventType();
            // XmlPullParser.END_DOCUMENT文档的结束
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        CLog.d(TAG, "START_DOCUMENT ");
                        break;
                    case XmlPullParser.START_TAG:
                        if ("sms".equals(parser.getName())) {
                            sms = new Sms();
                        } else if ("address".equals(parser.getName())) {
                            sms.setAddress(parser.nextText());
                        } else if ("date".equals(parser.getName())) {
                            sms.setDate(parser.nextText());
                        } else if ("type".equals(parser.getName())) {
                            sms.setType(parser.nextText());
                        } else if ("body".equals(parser.getName())) {
                            sms.setBody(parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        CLog.d(TAG, "END_TAG");
                        if ("sms".equals(parser.getName())) {
                            values.add(sms);
                            sms = null;
                        }
                        break;
                }
                event = parser.next();
            }
            for (Sms sms : values) {
                try {
                    ContentValues cv = new ContentValues();
                    cv.put("address", sms.getAddress());
                    cv.put("date", sms.getDate());
                    cv.put("type", sms.getType());
                    cv.put("body", sms.getBody());
                    resolver.insert(uri, cv);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
//            boolean canWriteSms = false;
//            if (!SmsWriteOpUtil.isWriteEnabled(ctx.getApplicationContext())) {
//                CLog.d(TAG, "disable");
//                canWriteSms = SmsWriteOpUtil.setWriteEnabled(ctx.getApplicationContext(), true);
//            }
//            CLog.d(TAG, "canWriteSms " + canWriteSms);
//        }


        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);

        CLog.d(TAG, "count:" + cursor.getCount());

//        fis.close();
    }

    public void setPrecessListener(PrecessListener listener) {
        this.listener = listener;
    }
}
