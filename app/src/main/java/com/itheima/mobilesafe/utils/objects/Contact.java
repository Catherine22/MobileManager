package com.itheima.mobilesafe.utils.objects;

/**
 * Created by Catherine on 2016/8/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

import android.graphics.Bitmap;

import java.util.LinkedList;
import java.util.List;

/**
 * 手机联系人
 */
public class Contact {
    public String id;
    public String name;
    public List<String> phone = new LinkedList<>();
    public String email;
    public String company;
    public String address;
    public Bitmap photo;
    public Bitmap circlarPhoto;

    @Override
    public String toString() {
        return "Contact{" +
                "address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                ", company='" + company + '\'' +
                ", photo=" + photo +
                ", circlarPhoto=" + circlarPhoto +
                '}';
    }
}
