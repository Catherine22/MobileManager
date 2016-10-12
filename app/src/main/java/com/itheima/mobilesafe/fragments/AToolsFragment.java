package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.utils.BackupFactory;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.backup.BackupConstants;
import com.itheima.mobilesafe.utils.backup.SmsBackup;

import java.io.IOException;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AToolsFragment extends Fragment {

    private static final String TAG = "AToolsFragment";
    private TextView tv_number_query, tv_sms_backup, tv_sms_recovery;
    private MainInterface mainInterface;
    private BackupFactory backupFactory;
    private SmsBackup sb;

    public static AToolsFragment newInstance() {
        return new AToolsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_tools, container, false);
        mainInterface = (MainInterface) getActivity();
        backupFactory = new BackupFactory();

        tv_number_query = (TextView) view.findViewById(R.id.tv_number_query);
        tv_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.callFragment(Constants.NUM_ADDRESS_QUERY_FRAG);
            }
        });
        tv_sms_backup = (TextView) view.findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb = (SmsBackup) backupFactory.createBackup(getActivity(), BackupConstants.SMS_BACKUP);
                try {
                    sb.backup();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tv_sms_recovery = (TextView) view.findViewById(R.id.tv_sms_recovery);
        tv_sms_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb = (SmsBackup) backupFactory.createBackup(getActivity(), BackupConstants.SMS_BACKUP);
                sb.recovery();
            }
        });
        return view;
    }
}
