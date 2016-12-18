package com.itheima.mobilesafe.utils.objects;

/**
 * Created by Catherine on 2016/10/18.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Sms {

    private String address;
    private String type;
    private String date;
    private String body;

    @Override
    public String toString() {
        return "Sms{" +
                "address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", date='" + date + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
