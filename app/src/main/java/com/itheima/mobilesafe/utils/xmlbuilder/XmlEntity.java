package com.itheima.mobilesafe.utils.xmlbuilder;

import com.itheima.mobilesafe.utils.objects.XmlElement;

/**
 * Created by Catherine on 2016/10/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class XmlEntity {
    private String fileName;
    private String filePath;
    private String encoding;
    private XmlElement element;

    @Override
    public String toString() {
        return "XmlPlan{" +
                "element=" + element +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", encoding='" + encoding +
                '}';
    }

    public XmlElement getElement() {
        return element;
    }

    public void setElement(XmlElement element) {
        this.element = element;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
