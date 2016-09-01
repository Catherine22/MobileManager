package com.itheima.mobilesafe.utils;

import android.accounts.NetworkErrorException;
import android.os.Handler;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class HttpUtils {
    private final static String TAG = "HttpUtils";
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 10000;

    public interface Callback {
        void onResponse(String response);
    }

    public static void get(final String url, final String[] name, final String[] data, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = HttpUtils.sendDataByGet(url, name, data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    public static void post(final String url, final String[] name, final String[] data, final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = HttpUtils.sendDataByPost(url, name, data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    private static String sendDataByGet(final String path, final String[] name, final String[] data) {
        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder();
            URL url = null;
            if (name != null && data != null) {
                sb.append(path + "?");
                for (int i = 0; i < name.length; i++) {
                    sb.append(name[i] + "=" + data[i]);
                    if (i != name.length - 1)
                        sb.append("&");
                }
                url = new URL(sb.toString());
            }

            if (name == null && data == null)
                url = new URL(path);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                String result = StreamUtils.toGBKString(is);
                return result;// SENT_SUCCESSFULLY
            } else
                throw new NetworkErrorException(responseCode + "");// FAILED_TO_SEND
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String sendDataByPost(final String path, final String[] name, final String[] data) {
        HttpURLConnection conn = null;
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length; i++) {
                sb.append(name[i] + "=" + data[i]);
                if (i != name.length - 1)
                    sb.append("&");
            }
            String d = sb.toString();
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", d.length() + "");
            OutputStream os = conn.getOutputStream();
            os.write(d.getBytes());

            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                byte[] result = StreamUtils.toBytes(is);
                return new String(result);// SENT_SUCCESSFULLY
            } else {
                throw new NetworkErrorException(code + "");// FAILED_TO_SEND
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
