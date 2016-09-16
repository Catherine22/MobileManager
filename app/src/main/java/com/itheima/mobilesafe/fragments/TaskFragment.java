package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
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
import com.itheima.mobilesafe.adapter.TaskInfoListAdapter;
import com.itheima.mobilesafe.ui.AutoResizeTextView;
import com.itheima.mobilesafe.ui.recycler_view.DividerItemDecoration;
import com.itheima.mobilesafe.ui.recycler_view.ItemTouchCallback;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.itheima.mobilesafe.utils.objects.TaskInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Catherine on 2016/9/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class TaskFragment extends Fragment {

    private static final String TAG = "TaskFragment";
    private AutoResizeTextView tv_memory_info;
    private TextView tv_progress_count, tv_user_tasks_count, tv_sys_tasks_count, tv_release_all;
    private LinearLayout ll_loading;
    private RecyclerView rv_user_tasks, rv_sys_tasks;
    private List<TaskInfo> returns;
    private TaskInfoListAdapter userAdapter, sysAdapter;
    private ItemTouchHelper mItemTouchHelper;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        tv_progress_count = (TextView) view.findViewById(R.id.tv_progress_count);
        tv_user_tasks_count = (TextView) view.findViewById(R.id.tv_user_tasks_count);
        tv_sys_tasks_count = (TextView) view.findViewById(R.id.tv_sys_tasks_count);
        tv_memory_info = (AutoResizeTextView) view.findViewById(R.id.tv_memory_info);
        tv_release_all = (TextView) view.findViewById(R.id.tv_release_all);
        tv_release_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CLog.d(TAG, "onClick");

            }
        });
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
        rv_user_tasks = (RecyclerView) view.findViewById(R.id.rv_user_tasks);
        //添加分割线
        rv_user_tasks.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //设置布局管理器,可实现GridVIew
        rv_user_tasks.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        rv_sys_tasks = (RecyclerView) view.findViewById(R.id.rv_sys_tasks);
        //添加分割线
        rv_sys_tasks.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //设置布局管理器,可实现GridVIew
        rv_sys_tasks.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        int progressCount = SystemInfoUtils.getRunningProcessCount(getActivity());
        tv_progress_count.setText("运行中进程：" + progressCount + "个");

        long availMen = SystemInfoUtils.getAvailableMemory(getActivity());
        long totalMen = SystemInfoUtils.getTotalMemory(getActivity());
        tv_memory_info.setText("剩余/总内存：" + SystemInfoUtils.formatFileSize(availMen) + "/" + SystemInfoUtils.formatFileSize(totalMen));

        fillInData();
        return view;
    }

    private List<TaskInfo> userInfo, sysInfo;

    private void fillInData() {
        ll_loading.setVisibility(View.VISIBLE);

        new Thread() {
            public void run() {
                userInfo = new LinkedList<>();
                sysInfo = new LinkedList<>();
                returns = SystemInfoUtils.getTaskInfos(getActivity());

                for (TaskInfo info : returns) {
                    if (info.userTask)
                        userInfo.add(info);
                    else
                        sysInfo.add(info);
                }

                userAdapter = new TaskInfoListAdapter(getActivity(), userInfo, new TaskInfoListAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CLog.d(TAG, "onItemClick");
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        CLog.d(TAG, "onItemLongClick");

                    }
                });
                sysAdapter = new TaskInfoListAdapter(getActivity(), sysInfo, new TaskInfoListAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        CLog.d(TAG, "onItemClick");

                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        CLog.d(TAG, "onItemLongClick");

                    }
                });
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);
                            tv_user_tasks_count.setText("用户进程（" + userAdapter.getItemCount() + "）");
                            tv_sys_tasks_count.setText("系统进程（" + sysAdapter.getItemCount() + "）");
                            rv_user_tasks.setAdapter(userAdapter);
                            mItemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(userAdapter));
                            mItemTouchHelper.attachToRecyclerView(rv_user_tasks);

                            rv_sys_tasks.setAdapter(sysAdapter);
                            mItemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(sysAdapter));
                            mItemTouchHelper.attachToRecyclerView(rv_sys_tasks);


                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.tran_in);
//                        tv_user_tasks_count.startAnimation(animation);
//                        tv_sys_tasks_count.startAnimation(animation);
                            rv_user_tasks.startAnimation(animation);
                            rv_sys_tasks.startAnimation(animation);
                        }
                    });
                }
            }
        }.start();

    }
}
