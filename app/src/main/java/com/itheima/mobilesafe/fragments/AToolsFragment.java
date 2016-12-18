package com.itheima.mobilesafe.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.interfaces.MyPermissionsResultListener;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.backup.BackupConstants;
import com.itheima.mobilesafe.utils.backup.BackupFactory;
import com.itheima.mobilesafe.utils.backup.SmsBackup;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AToolsFragment extends Fragment {

//    private static final String TAG = "AToolsFragment";
    private MainInterface mainInterface;
    private BackupFactory backupFactory;
    private SmsBackup sb;
    private ProgressBar pb;

    public static AToolsFragment newInstance() {
        return new AToolsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_tools, container, false);
        mainInterface = (MainInterface) getActivity();
        backupFactory = new BackupFactory();

        pb = (ProgressBar) view.findViewById(R.id.pb);
        pb.setVisibility(View.INVISIBLE);

        TextView tv_number_query = (TextView) view.findViewById(R.id.tv_number_query);
        tv_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.callFragment(Constants.NUM_ADDRESS_QUERY_FRAG);
            }
        });
        TextView tv_sms_backup = (TextView) view.findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.getPermissions(new String[]{Manifest.permission.READ_SMS}, new MyPermissionsResultListener() {
                    @Override
                    public void onGranted() {


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sb = (SmsBackup) backupFactory.createBackup(getActivity(), BackupConstants.SMS_BACKUP);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pb.setProgress(0);
                                        pb.setVisibility(View.VISIBLE);
                                        sb.setPrecessListener(new SmsBackup.PrecessListener() {
                                            @Override
                                            public void currentProcess(int process) {
                                                pb.setProgress(process);
                                            }

                                            @Override
                                            public void maxProcess(int max) {
                                                pb.setMax(max);
                                            }
                                        });
                                    }
                                });

                                try {
                                    sb.backupToLocal();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "备份成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getActivity(), "备份失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } finally {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pb.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }
                        }).start();


                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "权限不足", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        TextView tv_sms_recovery = (TextView) view.findViewById(R.id.tv_sms_recovery);
        tv_sms_recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.setDefaultSmsApp(true, new MyPermissionsResultListener() {
                    @Override
                    public void onGranted() {
                        sb = (SmsBackup) backupFactory.createBackup(getActivity(), BackupConstants.SMS_BACKUP);
                        try {
                            sb.restoreFromLocal(true);
                            Toast.makeText(getActivity(), "还原成功", Toast.LENGTH_SHORT).show();
                        } catch (IOException | XmlPullParserException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "还原失败", Toast.LENGTH_SHORT).show();
                        }
                        mainInterface.setDefaultSmsApp(false, null);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "获取权限失败", Toast.LENGTH_SHORT).show();
                        mainInterface.setDefaultSmsApp(false, null);
                    }
                });

            }
        });
        return view;
    }
}
