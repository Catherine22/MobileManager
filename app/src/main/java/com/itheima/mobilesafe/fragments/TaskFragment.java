package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.TaskInfoListAdapter;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.ui.AutoResizeTextView;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.itheima.mobilesafe.utils.objects.TaskInfo;

import java.util.ArrayList;
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
    private TextView tv_progress_count, tv_user_tasks_count, tv_sys_tasks_count;
    private LinearLayout ll_loading;
    private ListView ll_user_tasks, ll_sys_tasks;
    private List<TaskInfo> returns;
    private TaskInfoListAdapter userAdapter, sysAdapter;
    private MainInterface mainInterface;

    public static TaskFragment newInstance() {
        return new TaskFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        mainInterface = (MainInterface) getActivity();

        tv_progress_count = (TextView) view.findViewById(R.id.tv_progress_count);
        tv_user_tasks_count = (TextView) view.findViewById(R.id.tv_user_tasks_count);
        tv_sys_tasks_count = (TextView) view.findViewById(R.id.tv_sys_tasks_count);
        tv_memory_info = (AutoResizeTextView) view.findViewById(R.id.tv_memory_info);
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);
        ll_user_tasks = (ListView) view.findViewById(R.id.ll_user_tasks);
        ll_sys_tasks = (ListView) view.findViewById(R.id.ll_sys_tasks);

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

                userAdapter = new TaskInfoListAdapter(getActivity(), userInfo);
                sysAdapter = new TaskInfoListAdapter(getActivity(), sysInfo);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.GONE);
                        tv_user_tasks_count.setText("用户进程（" + userAdapter.getCount() + "）");
                        tv_sys_tasks_count.setText("系统进程（" + sysAdapter.getCount()+ "）");
                        ll_user_tasks.setAdapter(userAdapter);
                        ll_sys_tasks.setAdapter(sysAdapter);
                    }
                });
            }
        }.start();

    }
}
