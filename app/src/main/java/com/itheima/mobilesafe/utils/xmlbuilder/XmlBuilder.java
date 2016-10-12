package com.itheima.mobilesafe.utils.xmlbuilder;

import android.os.Environment;
import android.util.Xml;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.MemoryUtils;
import com.itheima.mobilesafe.utils.objects.XmlElement;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Catherine on 2016/10/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class XmlBuilder {
    private final static String TAG = "XmlBuilder";
    private XmlEntity entity;
    private XmlSerializer serializer;

    public XmlBuilder() {
        //default value
        entity = new XmlEntity();
        entity.setEncoding("utf-8");
        entity.setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        entity.setFileName("default.xml");
    }

    public void build() throws IOException {
        MemoryUtils.Result checkPath = MemoryUtils.formatPath(entity.getFilePath());
        if (checkPath.getWhat() == MemoryUtils.Result.ERROR) {
            CLog.e(TAG, checkPath.getMessage());
            return;
        }

        MemoryUtils.Result checkFileName = MemoryUtils.formatFileName(entity.getFileName());
        if (checkFileName.getWhat() == MemoryUtils.Result.ERROR) {
            if (checkFileName.getMessage().equals("请输入副档名")) {
                String temp = entity.getFileName();
                entity.setFileName(temp + ".xml");
            } else {
                CLog.e(TAG, checkFileName.getMessage());
                return;
            }
        }

        File file = new File(entity.getFilePath() + entity.getFileName());
        FileOutputStream fos = new FileOutputStream(file);

        XmlElement xmlElement = entity.getElement();

        //序列化：把内存中的东西写进文件里
        serializer = Xml.newSerializer();//xml的生成器（序列化器）
        serializer.setOutput(fos, entity.getEncoding());
        serializer.startDocument(entity.getEncoding(), true);//对应endDocument()
        serializer.startTag(xmlElement.getNamespace(), xmlElement.getName());//endTag()
        if (xmlElement.getAttribute() != null)
            serializer.attribute(xmlElement.getAttribute().getNamespace(), xmlElement.getAttribute().getName(), xmlElement.getAttribute().getValue());

        if (xmlElement.getElement() != null && xmlElement.getElement().size() != 0) {
            boolean stop = false;
            List<XmlElement> elements = xmlElement.getElement();
            while (!stop) {
                for (int i = 0; i < elements.size(); i++) {
                    XmlElement item = xmlElement.getElement().get(i);
                    serializer.startTag(item.getNamespace(), item.getName());
                    if (item.getAttribute() != null)
                        serializer.attribute(item.getAttribute().getNamespace(), item.getAttribute().getName(), item.getAttribute().getValue());

                    if (item.getElement() != null && item.getElement().size() != 0) {
                        elements = item.getElement();
                        stop = false;
                    } else {
                        serializer.text(item.getText());
                        stop = true;
                    }
                    serializer.endTag(item.getNamespace(), item.getName());
                }
            }

        } else
            serializer.text(xmlElement.getText());

        serializer.endTag(xmlElement.getNamespace(), xmlElement.getName());
        serializer.endDocument();

        fos.flush();
        fos.close();
    }


    public void setEncoding(String encoding) {
        entity.setEncoding(encoding);
    }

    public void setFilePath(String path) {
        entity.setFilePath(path);
    }

    public void setFileName(String name) {
        entity.setFileName(name);
    }

    public void setElement(XmlElement element) {
        entity.setElement(element);
    }
}
