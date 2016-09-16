package com.itheima.mobilesafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.ui.AutoResizeTextView;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.itheima.mobilesafe.utils.objects.TaskInfo;

import java.util.List;

/**
 * Created by Catherine on 2016/8/19.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class TaskInfoListAdapter extends BaseAdapter {
    private Context ctx;
    private List<TaskInfo> taskInfos;

    public TaskInfoListAdapter(Context ctx, List<TaskInfo> taskInfos) {
        this.ctx = ctx;
        this.taskInfos = taskInfos;
    }

    @Override
    public int getCount() {
        return taskInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return taskInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CLog.d("TaskInfoListAdapter", "getView");
        LayoutInflater inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_task_info, null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        AutoResizeTextView tv_name = (AutoResizeTextView) view.findViewById(R.id.tv_name);
        AutoResizeTextView tv_memory_info = (AutoResizeTextView) view.findViewById(R.id.tv_memory_info);

        iv_icon.setImageDrawable(taskInfos.get(position).icon);
        tv_name.setText(taskInfos.get(position).name);
        tv_memory_info.setText(SystemInfoUtils.formatFileSize(taskInfos.get(position).memSize));



//        Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.tran_in);
//        view.startAnimation(animation);
        return view;

    }
}
