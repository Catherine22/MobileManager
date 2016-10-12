package com.itheima.mobilesafe.utils.objects;

import java.util.List;

/**
 * Created by Catherine on 2016/10/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class XmlElement {

    private String namespace;
    private String name;
    private String text;
    private XmlAttribute attribute;
    private List<XmlElement> element;

    @Override
    public String toString() {
        return "XmlElement{" +
                "attribute=" + attribute +
                ", namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", element=" + element +
                '}';
    }

    public XmlAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(XmlAttribute attribute) {
        this.attribute = attribute;
    }

    public List<XmlElement> getElement() {
        return element;
    }

    public void setElement(List<XmlElement> element) {
        this.element = element;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
