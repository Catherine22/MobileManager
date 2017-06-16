package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Base64;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Catherine on 2017/6/15.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class SecurityUtils {

    private final static String TAG = "SecurityUtils";

    private Encryptor encryptor;
    private Decryptor decryptor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void generateKeyPair() {
        encryptor = new Encryptor();

        try {
            decryptor = new Decryptor();
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }


        try {
            final byte[] encryptedText = encryptor.encryptText(TAG, "你好啊");
            CLog.d(TAG, Base64.encodeToString(encryptedText, Base64.DEFAULT));
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | NoSuchProviderException |
                KeyStoreException | IOException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | SignatureException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        try {
            CLog.d(TAG, decryptor.decryptData(TAG, encryptor.getEncryption(), encryptor.getIv()));
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                KeyStoreException | NoSuchPaddingException | NoSuchProviderException |
                IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

    private final static String APK_KEY = "A6550F699983450DBBF65E5C41687B";

    /**
     *
     * @param ctx
     * @return
     * @throws PackageManager.NameNotFoundException
     * @throws NoSuchAlgorithmException
     */
    public boolean verifyApk(Context ctx) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
        PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA | PackageManager.GET_SIGNATURES);
        Bundle bundle = pkgInfo.applicationInfo.metaData;
        if (!bundle.containsKey("Catherine.secret.key")) {
            CLog.e(TAG, "Error meta-data");
            return false;
        } else {
            String SDKKey = bundle.getString("Catherine.secret.key");

            //SHA1 fingerprint
            String strResult = "";
            for (Signature signature : pkgInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                for (byte b : md.digest()) {
                    String strAppend = Integer.toString(b & 0xff, 16);
                    if (strAppend.length() == 1)
                        strResult += "0";
                    strResult += strAppend;
                }
                strResult = strResult.toUpperCase();
            }
            CLog.d(TAG, strResult);

            //两两交换位置
            String rawSignature = strResult;
            try {
                String[] a = rawSignature.split("");
                String[] b = rawSignature.split("");
                String temp = "";
                for (int i = 1; i <= rawSignature.length() - 1; i = i + 2) {
                    if ((i + 1) > rawSignature.length()) break;
                    String c = a[i];
                    a[i] = b[i + 1];
                    b[i + 1] = c;
                    temp += a[i] + b[i + 1];
                }
                rawSignature = temp;
            } catch (Exception e) {
                rawSignature = "";
            }
            CLog.d(TAG, rawSignature);

            String encodeKey;
            byte[] data;
            try {
                data = rawSignature.getBytes("UTF-8");
                encodeKey = Base64.encodeToString(data, Base64.DEFAULT);
            } catch (Exception e) {
                encodeKey = "";
            }
            CLog.d(TAG, "encodeKey:" + encodeKey);

            String decodeKey;
            try {
                byte[] data1 = Base64.decode(SDKKey, Base64.DEFAULT);

                decodeKey = new String(data1, "UTF-8");
            } catch (Exception e) {
                decodeKey = "";
            }
            CLog.d(TAG, "decodeKey:" + decodeKey);

            //private key = "Catherine"
            // 根据定义secret key的规则调整(目前是base64编码(yyyyMMdd + "Catherine" + (yyyyMMdd%2)))，所以还原private key时要判断前面的逻辑。
            String privateKey;
            try {
                String rawF = decodeKey.substring(0, 8);
                String key = decodeKey.substring(8, decodeKey.length() - 1);
                String checkSum = decodeKey.substring(decodeKey.length() - 1, decodeKey.length());

                if ((Integer.parseInt(rawF) + Integer.parseInt(checkSum)) % 2 == 0)
                    privateKey = key;
                else
                    privateKey = "";
            } catch (Exception e) {
                privateKey = "";
            }
            CLog.d(TAG, "privateKey:" + privateKey);

            String apkKey = md5(privateKey + encodeKey).toUpperCase();
            CLog.d(TAG, "apkKey:" + apkKey);

            return APK_KEY.equals(apkKey);
        }
    }

    private static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest)
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));
            return hexString.toString();
        } catch (Exception e) {
            return "";
        }
    }

    static {
        //relate to LOCAL_MODULE in Android.mk
        System.loadLibrary("keys");
    }

    public native String[] getAuthChain(String key);

    public native String getAuthentication();

    public native int getdynamicID(int timestamp);
}
