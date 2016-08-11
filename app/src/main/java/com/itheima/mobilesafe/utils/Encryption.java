package com.itheima.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Catherine on 2016/8/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Encryption {

    /**
     * MD5加密
     *
     * @param value
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String doMd5(String value) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("md5");
            byte[] bytes = messageDigest.digest(value.getBytes());
            StringBuffer sb = new StringBuffer();
            //把每一个byte做一个与运算 0xff
            for (byte b : bytes) {
                //与运算
                int num = b & 0xff;//加盐
                String str = Integer.toHexString(num);
                if (str.length() == 1) {
                    //长度为1时前面补0
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
