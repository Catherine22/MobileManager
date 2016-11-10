package com.itheima.mobilesafe.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.itheima.mobilesafe.db.dao.DaoConstants;
import com.itheima.mobilesafe.db.dao.NumberAddressDao;
import com.itheima.mobilesafe.utils.objects.MobileQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Catherine on 2016/9/1.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class TelephoneUtils {
    private static String address = "查无此号";

    public interface Callback {
        void onFinish(String content);
    }

    /**
     * 取得号码归属地,先用内建的数据库查询,若数据库版本过旧查不到则通过第三方的API查询
     *
     * @param ctx        Context
     * @param number     phone number
     * @param myCallback return response
     */
    @Nullable
    public static void getAddressFromNum(final Context ctx, String number, final Callback myCallback) {

        switch (number.length()) {
            case 3:
                // 110
                myCallback.onFinish("匪警号码");
                break;
            case 4:
                // 5554
                myCallback.onFinish("模拟器");
                break;
            case 5:
                // 10086
                myCallback.onFinish("客服号码");
                break;
            case 7:
                //
                myCallback.onFinish("本地号码");
                break;

            case 8:
                myCallback.onFinish("本地号码");
                break;
            case 11:
                //使用正则表达式过滤错误号码
                //https://msdn.microsoft.com/zh-cn/library/ae5bf541(v=vs.100).aspx
                /**
                 * ^ 开头
                 * 1 第一位限定1
                 * [3456] 第二位是3、4、5、6任一都行
                 * [0-9] 效果等同于 \d，适用于之后的九位数字，所以是 \d\d\d\d\d\d\d\d\d 等同于 \d{9}
                 * $ 结尾
                 *
                 * 正则式为 ^1[3456]\d{9}$
                 */
                //符合规则
                if (number.matches("^1[3456]\\d{9}$")) {
                    //手机号码
                    DaoFactory daoF = new DaoFactory();
                    NumberAddressDao nad = (NumberAddressDao) daoF.createDao(ctx, DaoConstants.NUMBERADDRESS);
                    address = nad.queryNumber(number);
                    if (TextUtils.isEmpty(address)) {
                        final String url = Settings.taoBaoGetAddressUrl;
                        NetUtils.get(url, new String[]{"tel"}, new String[]{number}, new NetUtils.Callback() {
                            @Override
                            public void onResponse(String response) {
                                if (!TextUtils.isEmpty(response)) {
                                    CLog.d("TelephoneUtils", response);
                                    if (url.equals(Settings.tenpayUrl)) {
                                        try {
                                            InputStream stream = new ByteArrayInputStream(response.getBytes("GBK"));
                                            XMLPullParserHandler xmlParser = new XMLPullParserHandler();
                                            MobileQuery mobileQuery = xmlParser.parse(stream);
                                            if (mobileQuery.getRetmsg().equals("OK")) {
                                                myCallback.onFinish(mobileQuery.getCity() + mobileQuery.getSupplier());
                                            } else
                                                myCallback.onFinish("查无此号");
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                            myCallback.onFinish("查无此号");
                                        }
                                    } else if (url.equals(Settings.taoBaoGetAddressUrl)) {
                                        String formatString[] = response.split("=");
                                        try {
                                            JSONObject jsonObject = new JSONObject(formatString[1]);
                                            address = jsonObject.optString("carrier");
                                            myCallback.onFinish(address);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            myCallback.onFinish("查无此号");
                                        }
                                    }

                                } else
                                    myCallback.onFinish("查无此号");
                            }
                        });
                    } else {
                        myCallback.onFinish(address);
                    }
                }
                break;
            default:
                myCallback.onFinish("查无此号");
                break;
        }
    }


}
