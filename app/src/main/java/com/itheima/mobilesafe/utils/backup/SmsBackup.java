package com.itheima.mobilesafe.utils.backup;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.MemoryUtils;
import com.itheima.mobilesafe.utils.xmlbuilder.XmlElement;
import com.itheima.mobilesafe.utils.xmlbuilder.XmlBuilder;

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

    public SmsBackup(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void backup() throws IOException {

        List<XmlElement> data = new ArrayList<>();

        List<XmlElement> includes;
        XmlElement address = new XmlElement();
        XmlElement date = new XmlElement();
        XmlElement type = new XmlElement();
        XmlElement body = new XmlElement();

        ContentResolver resolver = ctx.getContentResolver();
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        while (cursor.moveToNext()) {
            includes = new ArrayList<>();

            address.setName("address");
            address.setText(cursor.getString(0));
            includes.add(address);

            date.setName("date");
            date.setText(cursor.getString(1));
            includes.add(date);

            type.setName("type");
            type.setText(cursor.getString(2));
            includes.add(type);

            body.setName("body");
            body.setText(cursor.getString(3));
            includes.add(body);

            CLog.d(TAG, includes.toString());

            XmlElement subElement = new XmlElement();
            subElement.setName("sms");
            subElement.setElement(includes);
            data.add(subElement);
        }
        cursor.close();
//        XmlAttribute attr = new XmlAttribute();
//        attr.setName("number");
//        attr.setValue("13512345678");

        CLog.d(TAG, "size:" + data.size());

        XmlElement element = new XmlElement();
        element.setName("smss");
        element.setElement(data);
//        element.setText("text");
//        element.setAttribute(attr);

        XmlBuilder sb = new XmlBuilder();
        sb.setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        sb.setFileName("default.xml");
        sb.setEncoding("utf-8");
        sb.setElement(element);
        sb.build();


//        MemoryUtils.saveStringToRom(ctx, "Backup", "sms.xml", "YO", new MemoryUtils.OnResponse() {
//            @Override
//            public void onSuccess() {
//                CLog.d(TAG, "onSuccess()");
//            }
//
//            @Override
//            public void onFail(int what, String errorMessage) {
//                CLog.d(TAG, "onFail()");
//
//            }
//        });
    }

    @Override
    public void recovery() {

    }
}
