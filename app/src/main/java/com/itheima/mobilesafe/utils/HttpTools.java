package com.itheima.mobilesafe.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class HttpTools {
    private final static String TAG = "HttpTools";

    /**
     * 通过get请求提交数据到服务器
     *
     * @param path 服务器的地址 e.g. http://xxx.xxx.xxx/web/LoginServlet
     * @param name 参数名称 e.g. [name, password]
     * @param data 参数数值, 位置对照参数名称 e.g. [ZhangSan, 123]
     * @return 服务器返回回来的String数据
     */
    public static void sendDataByGet(final String path, final String[] name,
                                     final String[] data, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append(path + "?");
                    for (int i = 0; i < name.length; i++) {
                        sb.append(name[i] + "=" + data[i]);
                        if (i != name.length - 1)
                            sb.append("&");
                    }
                    // 格式
                    // http://localhost:8080/web/LoginServlet?name=xxx&password=xxx
                    URL url = new URL(sb.toString());
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    // 默认就是GET，所以可省略
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        // 目前还没发送数据给服务器
                        // 只要我们获取任何一个服务器返回的信息，数据就会被提交给服务器，得到服务器返回的流信息
                        InputStream is = conn.getInputStream();
//                    byte[] result = StreamTools.getBytes(is);
                        String result = StreamTools.getString(is);
                        sendMessage(Constants.SENT_SUCCESSFULLY, result, handler);
                    } else {
                        sendMessage(Constants.FAILED_TO_SEND, responseCode + "", handler);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(Constants.FAILED_TO_SEND, e.toString(), handler);
                }

            }
        }).start();
    }

    /**
     * 传送handler信息
     *
     * @param msgCode
     * @param msgContent
     * @param handler
     */
    private static void sendMessage(int msgCode, String msgContent,
                                    Handler handler) {
        CLog.e(TAG, msgCode + " " + msgContent);
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("MSG", msgContent);
        msg.setData(bundle);
        msg.what = msgCode;
        handler.sendMessage(msg);
    }
}
