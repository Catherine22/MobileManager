package com.itheima.mobilesafe.utils.xmlbuilder;

/**
 * Created by Catherine on 2016/10/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class XmlAttribute {
    @Override
    public String toString() {
        return "XmlAttribute{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    private String namespace;
    private String name;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
