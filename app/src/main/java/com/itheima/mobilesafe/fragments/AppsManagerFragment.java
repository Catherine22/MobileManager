package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.AppInfoListAdapter;
import com.itheima.mobilesafe.adapter.TaskInfoListAdapter;
import com.itheima.mobilesafe.ui.AutoResizeTextView;
import com.itheima.mobilesafe.ui.recycler_view.DividerItemDecoration;
import com.itheima.mobilesafe.ui.recycler_view.ItemTouchCallback;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.itheima.mobilesafe.utils.objects.AppInfo;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AppsManagerFragment extends Fragment {

    private static final String TAG = "AppsManagerFragment";
    private final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String romPath = Environment.getDataDirectory().getAbsolutePath();


    private AutoResizeTextView tv_sd_info, tv_rom_info;
    private TextView tv_installed_apps_count, tv_sys_apps_count;
    private RecyclerView rv_apps, rv_sys_apps;
    private AppInfoListAdapter userAdapter, sysAdapter;
    private LinearLayout ll_loading;
    private List<AppInfo> userInfo, sysInfo;
    private ItemTouchHelper userItemTouchHelper, sysItemTouchHelper;

    public static AppsManagerFragment newInstance() {
        return new AppsManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_manager, container, false);

        tv_sd_info = (AutoResizeTextView) view.findViewById(R.id.tv_sd_info);
        tv_rom_info = (AutoResizeTextView) view.findViewById(R.id.tv_rom_info);
        tv_installed_apps_count = (TextView) view.findViewById(R.id.tv_installed_apps_count);
        tv_sys_apps_count = (TextView) view.findViewById(R.id.tv_sys_apps_count);
        rv_apps = (RecyclerView) view.findViewById(R.id.rv_apps);
        rv_sys_apps = (RecyclerView) view.findViewById(R.id.rv_sys_apps);
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);

        //添加分割线
        rv_apps.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        rv_sys_apps.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //设置布局管理器,可实现GridVIew
        rv_apps.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv_sys_apps.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));


        fillInData();


        return view;
    }

    private void fillInData() {
        ll_loading.setVisibility(View.VISIBLE);

        DecimalFormat df = new DecimalFormat("#.##");
        //SD卡可用空间
        String sdSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(sdPath));
        String sdP = df.format((float) SystemInfoUtils.getAvailableSpace(sdPath) / (float) SystemInfoUtils.getTotalSpace(sdPath) * 100) + "%";
        //ROM可用空间
        String romSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(romPath));
        String romP = df.format((float) SystemInfoUtils.getAvailableSpace(romPath) / (float) SystemInfoUtils.getTotalSpace(romPath) * 100) + "%";

        tv_sd_info.setText(String.format(getActivity().getString(R.string.sd_size), sdSize, sdP));
        tv_rom_info.setText(String.format(getActivity().getString(R.string.rom_size), romSize, romP));

        SystemInfoUtils.getAppInfos(getActivity()).toString();
        CLog.d(TAG, SystemInfoUtils.getAppInfos(getActivity()).toString());

        new Thread() {
            public void run() {
                userInfo = new LinkedList<>();
                sysInfo = new LinkedList<>();

                List<AppInfo> infos = SystemInfoUtils.getAppInfos(getActivity());
                for (AppInfo info : infos) {
                    if (info.isUserApp())
                        userInfo.add(info);
                    else
                        sysInfo.add(info);
                }

                userAdapter = new AppInfoListAdapter(getActivity(), userInfo);
                userAdapter.setOnItemClickLitener(new AppInfoListAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CLog.d(TAG, "onItemClick");
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        CLog.d(TAG, "onItemLongClick");

                    }
                });
                userAdapter.setOnItemMoveLitener(new AppInfoListAdapter.OnItemMoveListener() {
                    @Override
                    public void onItemSwap(int fromPosition, int toPosition) {
                        CLog.d(TAG, "from " + fromPosition + " to " + toPosition);
                    }

                    @Override
                    public void onItemSwipe(int position) {
                        CLog.d(TAG, userAdapter.getItemName(position));
                        refresh(false);
                    }
                });

                sysAdapter = new AppInfoListAdapter(getActivity(), sysInfo);
                sysAdapter.setOnItemClickLitener(new AppInfoListAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CLog.d(TAG, "onItemClick");
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        CLog.d(TAG, "onItemLongClick");

                    }
                });
                sysAdapter.setOnItemMoveLitener(new AppInfoListAdapter.OnItemMoveListener() {
                    @Override
                    public void onItemSwap(int fromPosition, int toPosition) {
                        CLog.d(TAG, "from " + fromPosition + " to " + toPosition);
                    }

                    @Override
                    public void onItemSwipe(int position) {
                        CLog.d(TAG, sysAdapter.getItemName(position));
                        refresh(false);
                    }
                });

                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);

                            String userPCount = String.format(getResources().getString(R.string.user_running_process), userAdapter.getItemCount());
                            String sysPCount = String.format(getResources().getString(R.string.sys_running_process), sysAdapter.getItemCount());
                            tv_installed_apps_count.setText(userPCount);
                            tv_sys_apps_count.setText(sysPCount);

                            if (userAdapter.getItemCount() == 0)
                                tv_installed_apps_count.setVisibility(View.GONE);
                            else
                                tv_installed_apps_count.setVisibility(View.VISIBLE);
                            if (sysAdapter.getItemCount() == 0)
                                tv_sys_apps_count.setVisibility(View.GONE);
                            else
                                tv_sys_apps_count.setVisibility(View.VISIBLE);

                            rv_apps.setAdapter(userAdapter);
                            userItemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(userAdapter));
                            userItemTouchHelper.attachToRecyclerView(rv_apps);

                            rv_sys_apps.setAdapter(sysAdapter);
                            sysItemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(sysAdapter));
                            sysItemTouchHelper.attachToRecyclerView(rv_sys_apps);


                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.tran_in);
                            rv_apps.startAnimation(animation);
                            rv_sys_apps.startAnimation(animation);
                        }
                    });
                }
            }
        }.start();
    }

    private void refresh(boolean refreshList) {
        DecimalFormat df = new DecimalFormat("#.##");
        //SD卡可用空间
        String sdSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(sdPath));
        String sdP = df.format((float) SystemInfoUtils.getAvailableSpace(sdPath) / (float) SystemInfoUtils.getTotalSpace(sdPath) * 100) + "%";
        //ROM可用空间
        String romSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(romPath));
        String romP = df.format((float) SystemInfoUtils.getAvailableSpace(romPath) / (float) SystemInfoUtils.getTotalSpace(romPath) * 100) + "%";

        tv_sd_info.setText(String.format(getActivity().getString(R.string.sd_size), sdSize, sdP));
        tv_rom_info.setText(String.format(getActivity().getString(R.string.rom_size), romSize, romP));

        if (refreshList)
            fillInData();
    }

}
