package com.itheima.mobilesafe.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Catherine on 2017/6/23.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class Algorithm {
    public final static String CHARSET = "UTF8";
    /**
     * 在Android平台的JCE中，非对称Key的常用算法有“RSA”、“DSA”、“Diffie−Hellman”、“Elliptic Curve
     * (EC)”等。
     */
    public final static String KEYPAIR_ALGORITHM = "RSA";
    public final static String SINGLE_KEY_ALGORITHM = "DES";
    public final static Map<String, String> rules = new HashMap<>();;
    static {
        rules.put("DES", "DES/CBC/PKCS5Padding");
        rules.put("RSA", "RSA/ECB/PKCS1Padding");
    }
}
