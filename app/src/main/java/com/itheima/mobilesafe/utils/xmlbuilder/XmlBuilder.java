package com.itheima.mobilesafe.utils.xmlbuilder;

import android.os.Environment;
import android.util.Xml;

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
    private XmlPlan entity;
    private XmlSerializer serizlizer;

    public XmlBuilder() {
        //default value
        entity = new XmlPlan();
        entity.setEncoding("utf-8");
        entity.setFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        entity.setFileName("default.xml");
    }

    public void build() throws IOException {
        File file = new File(entity.getFilePath() + entity.getFileName());
        FileOutputStream fos = new FileOutputStream(file);

        XmlElement xmlElement = entity.getElement();

        //序列化：把内存中的东西写进文件里
        serizlizer = Xml.newSerializer();//xml的生成器（序列化器）
        serizlizer.setOutput(fos, entity.getEncoding());
        serizlizer.startDocument(entity.getEncoding(), true);//对应endDocument()
        serizlizer.startTag(xmlElement.getNamespace(), xmlElement.getName());//endTag()
        if (xmlElement.getAttribute() != null)
            serizlizer.attribute(xmlElement.getAttribute().getNamespace(), xmlElement.getAttribute().getName(), xmlElement.getAttribute().getValue());

        if (xmlElement.getElement() != null && xmlElement.getElement().size() != 0) {
            boolean stop = false;
            List<XmlElement> elements = xmlElement.getElement();
            while (!stop) {
                for (int i = 0; i < elements.size(); i++) {
                    XmlElement item = xmlElement.getElement().get(i);
                    serizlizer.startTag(item.getNamespace(), item.getName());
                    if (item.getAttribute() != null)
                        serizlizer.attribute(item.getAttribute().getNamespace(), item.getAttribute().getName(), item.getAttribute().getValue());

                    if (item.getElement() != null && item.getElement().size() != 0) {
                        elements = item.getElement();
                        stop = false;
                    } else {
                        serizlizer.text(item.getText());
                        stop = true;
                    }
                    serizlizer.endTag(item.getNamespace(), item.getName());
                }
            }

        } else
            serizlizer.text(xmlElement.getText());

        serizlizer.endTag(xmlElement.getNamespace(), xmlElement.getName());
        serizlizer.endDocument();

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

    public void setElementValue(List<XmlElement> elements) {
        entity.getElement().setElement(elements);
    }

    public void setElementValue(String text) {
        entity.getElement().setText(text);
    }
}
