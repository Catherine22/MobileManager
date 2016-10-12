package com.itheima.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.objects.Contact;

import java.util.List;

/**
 * Created by Catherine on 2016/8/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class ContactsListAdapter extends BaseAdapter {
    private Context ctx;
    private List<Contact> contactMap;

    public ContactsListAdapter(Context ctx, List<Contact> contactMap) {
        this.ctx = ctx;
        this.contactMap = contactMap;
    }

    @Override
    public int getCount() {
        return contactMap.size();
    }

    @Override
    public Object getItem(int position) {
        return contactMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_contact, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        ImageView iv_photo = (ImageView) view.findViewById(R.id.iv_photo);

        tv_name.setText(contactMap.get(position).name);
        String phone = "";
        for (String s : contactMap.get(position).phone)
            phone += s + "\n";
        tv_phone.setText(phone);
        iv_photo.setImageBitmap(contactMap.get(position).circlarPhoto);
        return view;

    }
}
