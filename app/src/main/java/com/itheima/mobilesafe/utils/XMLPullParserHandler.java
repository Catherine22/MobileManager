package com.itheima.mobilesafe.utils;

import com.itheima.mobilesafe.utils.objects.MobileQuery;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class XMLPullParserHandler {
    private MobileQuery mobileQuery;
    private String text;

    public MobileQuery parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("root")) {
                            // create a new instance of employee
                            mobileQuery = new MobileQuery();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("root")) {
                            // add mobileQuery object to list
                        } else if (tagname.equalsIgnoreCase("chgmobile")) {
                            mobileQuery.setChgmobile(text);
                        } else if (tagname.equalsIgnoreCase("city")) {
                            mobileQuery.setCity(text);
                        } else if (tagname.equalsIgnoreCase("province")) {
                            mobileQuery.setProvince(text);
                        } else if (tagname.equalsIgnoreCase("retcode")) {
                            mobileQuery.setRetcode(text);
                        } else if (tagname.equalsIgnoreCase("retmsg")) {
                            mobileQuery.setRetmsg(text);
                        } else if (tagname.equalsIgnoreCase("supplier")) {
                            mobileQuery.setSupplier(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mobileQuery;
    }
}
