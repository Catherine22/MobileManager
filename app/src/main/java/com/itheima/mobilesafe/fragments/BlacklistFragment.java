package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.objects.BlockedCaller;

import java.util.List;

/**
 * Created by Catherine on 2016/9/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistFragment extends Fragment {

    private static final String TAG = "BlacklistFragment";
    private RecyclerView rv_blacklist;
    private List<BlockedCaller> blockedCallers;

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blacklist, container, false);
        rv_blacklist = (RecyclerView) view.findViewById(R.id.rv_blacklist);
        BlacklistDao dao = new BlacklistDao(getActivity());
        blockedCallers = dao.queryAll();
        for (BlockedCaller b : blockedCallers)
            CLog.d(TAG, b.toString());
        return view;
    }
}