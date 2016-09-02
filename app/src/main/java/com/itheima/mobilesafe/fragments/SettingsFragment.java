package com.itheima.mobilesafe.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.services.AddressService;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.MyAdminManager;
import com.itheima.mobilesafe.utils.ServiceUtils;

import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Result;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class SettingsFragment extends Fragment {
    private final static String TAG = "SettingsFragment";
    private SettingItemView siv_update, siv_admin, siv_show_address;
    private TextView tv_uninstall;
    private SharedPreferences sp;
    private MyAdminManager myAdminManager;
    private Client client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        CLog.d(TAG, "onCreateView");
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        myAdminManager = new MyAdminManager(getActivity());


        initView(view);
        return view;
    }

    private void initView(View view) {
        siv_update = (SettingItemView) view.findViewById(R.id.siv_update);
        siv_admin = (SettingItemView) view.findViewById(R.id.siv_admin);
        siv_show_address = (SettingItemView) view.findViewById(R.id.siv_show_address);
        tv_uninstall = (TextView) view.findViewById(R.id.tv_uninstall);
        tv_uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdminManager.unInstall();
            }
        });

        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                if (result.isBoolean())
                    siv_admin.setChecked(true);
                else
                    siv_admin.setChecked(false);
            }
        };
        client = new Client(getActivity(), cr);
        client.gotMessages("ADMIN_PERMISSION");

        if (myAdminManager.isAdmin()) {
            //装置管理员已经开启
            siv_admin.setChecked(true);
        } else {
            //装置管理员已经关闭
            siv_admin.setChecked(false);
        }
        siv_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_admin.isChecked()) {
                    myAdminManager.removeAdmin();
                    siv_admin.setChecked(false);
                } else {
                    myAdminManager.getAdminPermission();
                    siv_admin.setChecked(true);
                }
            }
        });

        boolean showAddress = ServiceUtils.isRunningService(getActivity(), "com.itheima.mobilesafe.services.AddressService");
        if (showAddress) {
            //查询手机号码归属地已经开启
            siv_show_address.setChecked(true);
        } else {
//            getActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                }
//            });
            //查询手机号码归属地已经关闭
            siv_show_address.setChecked(false);

        }
        //用户可手动关闭服务,所以需做一个工具来监听服务是否正在运作

        siv_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddressService.class);

                if (siv_show_address.isChecked()) {
                    siv_show_address.setChecked(false);
                    getActivity().stopService(intent);
                } else {
                    siv_show_address.setChecked(true);
                    getActivity().startService(intent);
                }
            }
        });

        boolean update = sp.getBoolean("update", false);
        if (update) {
            //自动升级已经开启
            siv_update.setChecked(true);
        } else {
            //自动升级已经关闭
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否有选中
                //已经打开自动升级了
                if (siv_update.isChecked()) {
                    siv_update.setChecked(false);
                    editor.putBoolean("update", false);
                } else {
                    //没有打开自动升级
                    siv_update.setChecked(true);
                    editor.putBoolean("update", true);
                }
                editor.apply();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.release();
        CLog.d(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CLog.d(TAG, "onDestroyView");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(TAG, "onCreate");
    }

    @Override
    public void onStop() {
        super.onStop();
        CLog.d(TAG, "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        CLog.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        CLog.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        CLog.d(TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CLog.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CLog.d(TAG, "onViewCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        CLog.d(TAG, "onViewStateRestored");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CLog.d(TAG, "onDetach");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CLog.d(TAG, "onAttach");
    }
}
