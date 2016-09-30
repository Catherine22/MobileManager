package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.BlacklistAdapter;
import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.ui.recycler_view.DividerItemDecoration;
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
    private TextView tv_no_data;
    private List<BlockedCaller> blockedCallers;

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blacklist, container, false);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        rv_blacklist = (RecyclerView) view.findViewById(R.id.rv_blacklist);
        BlacklistDao dao = new BlacklistDao(getActivity());
        blockedCallers = dao.queryAll();
        if (blockedCallers != null) {
            tv_no_data.setVisibility(View.INVISIBLE);

            rv_blacklist.addItemDecoration(new DividerItemDecoration(
                    getActivity(), DividerItemDecoration.VERTICAL_LIST));
            rv_blacklist.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

            rv_blacklist.setAdapter(new BlacklistAdapter(getActivity(), blockedCallers, new BlacklistAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    blockedCallers.get(position).toString();
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }));
        } else
            tv_no_data.setVisibility(View.VISIBLE);

        return view;
    }
}