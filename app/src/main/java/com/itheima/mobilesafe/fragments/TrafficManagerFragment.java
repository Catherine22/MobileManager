package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class TrafficManagerFragment extends Fragment {

    private static final String TAG = "TrafficManagerFragment";

    public static TrafficManagerFragment newInstance() {
        return new TrafficManagerFragment();
    }

    private ListView lv;
    private List<Item> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_traffic_manager, container, false);
        mList = new ArrayList<>();
        PackageManager pm = getActivity().getPackageManager();
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
        for (ApplicationInfo info : installedApplications) {
            int uid = info.uid;
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);

            Item item = new Item();
            item.packname = info.packageName;
            item.rcv = SystemInfoUtils.formatFileSize(rx);
            item.snt = SystemInfoUtils.formatFileSize(tx);
            mList.add(item);
        }

        lv = (ListView) view.findViewById(R.id.lv);
        lv.setAdapter(new mAdapter());

        return view;
    }

    private class mAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            Holder holder = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_item_traffic, null);
                holder = new Holder();

                holder.name = (TextView) view.findViewById(R.id.tv_name);
                holder.data = (TextView) view.findViewById(R.id.tv_traffic);
                view.setTag(holder);
            } else
                holder = (Holder) view.getTag();

            holder.name.setText(mList.get(position).packname);
            String content = String.format(getString(R.string.traffic), mList.get(position).snt, mList.get(position).rcv);
            holder.data.setText(content);

            return view;
        }
    }

    private class Item {
        public String packname;
        public String snt;
        public String rcv;
    }

    private class Holder {
        public TextView name, data;
    }
}
