package com.itheima.mobilesafe.fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.ContactsListAdapter;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Settings;
import com.itheima.mobilesafe.utils.objects.Contact;

import java.util.LinkedList;
import java.util.List;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/8/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class ContactsFragment extends Fragment {
    private List<Contact> contacts;
    private MainInterface mainInterface;
    private final static String TAG = "ContactsFragment";
    private Server sv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initData();

        ListView lv_contacts = (ListView) view.findViewById(R.id.lv_contacts);
        lv_contacts.setAdapter(new ContactsListAdapter(getActivity(), contacts));
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (contacts.get(position).phone.size() > 1) {
                    String phones[] = new String[contacts.get(position).phone.size()];
                    for (int i = 0; i < contacts.get(position).phone.size(); i++)
                        phones[i] = contacts.get(position).phone.get(i);
                    showPhoneDialog(phones);
                } else {
                    Settings.safePhone = contacts.get(position).phone.get(0);
                    sv.pushString("SAFE_PHONE", Settings.safePhone);
                    CLog.e(TAG, contacts.get(position).phone.get(0));
                    mainInterface.backToPreviousPage();
                }
            }
        });
        return view;
    }

    private void initData() {
        mainInterface = (MainInterface) getActivity();
        try {
            contacts = getContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        AsyncResponse ar = new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.e(TAG, "onFailure" + errorCode);
            }
        };
        sv = new Server(getActivity(), ar);
    }

    private Dialog alertDialog;

    private void showPhoneDialog(String[] phones) {
        final String[] myPhones = phones;
        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_phones_selector);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        ListView lv_phones = (ListView) alertDialog.findViewById(R.id.lv_phones);
        lv_phones.setAdapter(new ArrayAdapter<>(
                getActivity(), R.layout.list_item_phone,
                R.id.tv_name, myPhones));
        lv_phones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Settings.safePhone = myPhones[position];
                sv.pushString("SAFE_PHONE", Settings.safePhone);
                CLog.e(TAG, myPhones[position]);
                alertDialog.dismiss();
                mainInterface.backToPreviousPage();
            }
        });

    }

    /**
     * 内容参考:    
     * data/data/com.android.providers.contacts/databases/contacts2.db
     * 重要table有三个:
     * 1.data
     * -存放联络人具体的data信息
     * 重要栏位：mimetype_id(对照mimetypes表),raw_contact_id(对照raw_contacts表)
     *
     * 2.raw_contacts
     * -生成联系人的主键
     * 重要栏位：_id,display_name
     *
     * 3.mimetypes 
     * 内容：
     * _id  mimetype
     * 1 　　vnd.android.cursor.item/email_v2
     * 2 　　vnd.android.cursor.item/im
     * 3 　　vnd.android.cursor.item/nickname
     * 4 　　vnd.android.cursor.item/organization
     * 5 　　vnd.android.cursor.item/phone_v2
     * 6 　　vnd.android.cursor.item/sip_address
     * 7 　　vnd.android.cursor.item/name
     * 8 　　vnd.android.cursor.item/postal-address_v2
     * 9 　　vnd.android.cursor.item/identity
     * 10 　vnd.android.cursor.item/photo
     * 11 　vnd.android.cursor.item/group_membership
     */


    /**
     * 获取用户发联系人的内容
     *
     * @throws Exception
     */
    private List<Contact> getContacts() throws Exception {
        List<Contact> myContacts = new LinkedList<>();
        /**
         * Uri获取方法：(必须看源代码)         
         * 1.到https://github.com/android        
         * 2.搜寻providers_contacts
         * 3.找到platform_packages_providers_contactsprovider项目，并下载
         * 4.打开该项目的Manifest，找到provider的配置
         * 5.找到android:authorities="contacts;com.android.contacts"（分号代表contacts或com.android.contacts都可以）
         * 6.打开该项目的com.android.providers.contacts/ContactsProvider2.java
         * 7.搜寻urimatcher *8.找到一堆matcher.addURI()...
         */
        Uri rawbase = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri database = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = getContext().getContentResolver().query(rawbase, null,
                null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Contact item = new Contact();
                item.id = cursor.getString(cursor.getColumnIndex("_id"));
                //            CLog.d(TAG, "id=" + item.id);

                Cursor dataCursor = getContext().getContentResolver().query(
                        database, null, "raw_contact_id=?", new String[]{item.id},
                        null);
                if (dataCursor != null) {
                    while (dataCursor.moveToNext()) {
                        String mimetype = dataCursor.getString(dataCursor
                                .getColumnIndex("mimetype"));
                        //                CLog.d(TAG, "mimetype=" + mimetype);

                        if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            item.name = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1"));
                            //                    CLog.d(TAG, "名字=" + item.name);
                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            item.phone.add(dataCursor.getString(dataCursor
                                    .getColumnIndex("data1")));
                            //                    CLog.d(TAG, "电话号码=" + item.phone);
                        } else if ("vnd.android.cursor.item/email_v2".equals(mimetype)) {
                            item.email = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1"));
                            //                    CLog.d(TAG, "邮箱=" + item.email);
                        } else if ("vnd.android.cursor.item/organization"
                                .equals(mimetype)) {
                            item.company = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1"));
                            //                    CLog.d(TAG, "公司=" + item.company);
                        } else if ("vnd.android.cursor.item/postal-address_v2"
                                .equals(mimetype)) {
                            item.address = dataCursor.getString(dataCursor
                                    .getColumnIndex("data1"));
                            //                    CLog.d(TAG, "地址=" + item.address);
                        }
                    }
                    dataCursor.close();
                }
                if (item.phone.size() != 0)
                    myContacts.add(item);
            }
            cursor.close();
        }
        return myContacts;
    }

}
