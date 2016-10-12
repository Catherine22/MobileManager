package com.itheima.mobilesafe.utils.backup;

import android.content.Context;
import android.os.Environment;
import android.util.Xml;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.MemoryUtils;
import com.itheima.mobilesafe.utils.objects.XmlAttribute;
import com.itheima.mobilesafe.utils.objects.XmlElement;
import com.itheima.mobilesafe.utils.xmlbuilder.XmlBuilder;
import com.itheima.mobilesafe.utils.xmlbuilder.XmlPlan;

import org.xmlpull.v1.XmlSerializer;

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
//        XmlAttribute attr = new XmlAttribute();
//        attr.setName("number");
//        attr.setValue("13512345678");

        List<XmlElement> data= new ArrayList<>();
        XmlElement subElement;
        for (int i = 0; i < 5; i++) {
            subElement = new XmlElement();
            subElement.setName("tag" + i);
            subElement.setText("OO");
            data.add(subElement);
        }

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
//        MemoryUtils.saveStringToSD("Backup", "sms.xml", "YO", new MemoryUtils.OnResponse() {
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
//测试saveToRom
    }

    @Override
    public void recovery() {

    }
}
