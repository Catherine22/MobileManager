package com.itheima.mobilesafe.utils;

/**
 * Created by Yi-Jing on 2016/9/16.
 */

import android.accounts.NetworkErrorException;
import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtils {
    private final static String TAG = "NetUtils";
    public static final String DOMAIN = "http://xxx/xxx/api/";
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECT_TIMEOUT = 10000;

    public interface Callback {
        void onResponse(String response);
    }

    // Using handle
    public static void getWithHandler(final String url, final String[] name, final String[] data,
                                      final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtils.sendDataByGet(url, name, data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    // Using AsyncTask
    public static void get(final String url, final String[] name, final String[] data, final Callback callback) {
        new NetAsyncTask(url, name, data, callback).execute("GET");
    }

    // Using handle
    public static void postWithHandler(final String url, final String[] name, final String[] data,
                                       final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtils.sendDataByPost(url, name, data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    // Using AsyncTask
    public static void post(final String url, final String[] name, final String[] data, final Callback callback) {
        new NetAsyncTask(url, name, data, callback).execute("POST");
    }

    // Using handle
    public static void postJSONwithHandler(final String url, final String[] name, final String[] data,
                                           final Callback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String response = NetUtils.sendJSONByPost(url, name, data);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(response);
                    }
                });
            }
        }).start();
    }

    // Using AsyncTask
    public static void postJSON(final String url, final String[] name, final String[] data, final Callback callback) {
        new NetAsyncTask(url, name, data, callback).execute("POST_JSON");
    }

    protected static String sendDataByGet(final String path, final String[] name, final String[] data) {
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
                String result = StreamUtils.toString(is);
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

    protected static String sendDataByPost(final String path, final String[] name, final String[] data) {
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
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// application/x-www-form-urlencoded
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

    protected static String sendJSONByPost(final String path, final String[] name, final String[] data) {
        JSONObject jobj = new JSONObject();
        try {
            for (int i = 0; i < name.length; i++) {
                jobj.put(name[i], data[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String content = jobj.toString();

        HttpURLConnection conn = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", content.length() + "");
            OutputStream os = conn.getOutputStream();
            os.write(content.getBytes());

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