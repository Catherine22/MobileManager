package com.itheima.mobilesafe.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.interfaces.MyPermissionsResultListener;
import com.itheima.mobilesafe.services.AddressService;
import com.itheima.mobilesafe.services.BlockCallsSmsService;
import com.itheima.mobilesafe.ui.SettingItemView;
import com.itheima.mobilesafe.ui.SettingNextView;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
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
    private SettingItemView siv_update, siv_admin, siv_show_address, siv_block;
    private SettingNextView snv_set_background, snv_login;
    private TextView tv_uninstall;
    private SharedPreferences sp;
    private MainInterface mainInterface;
    private MyAdminManager myAdminManager;
    private Intent addService, blockService;
    private Client client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        CLog.v(TAG, "onCreateView");
        sp = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
        addService = new Intent(getActivity(), AddressService.class);
        blockService = new Intent(getActivity(), BlockCallsSmsService.class);
        mainInterface = (MainInterface) getActivity();
        myAdminManager = new MyAdminManager(getActivity());
        initView(view);

        AccountKit.initialize(getActivity().getApplicationContext());


        return view;
    }

    private void initView(View view) {
        siv_update = (SettingItemView) view.findViewById(R.id.siv_update);
        siv_admin = (SettingItemView) view.findViewById(R.id.siv_admin);
        siv_show_address = (SettingItemView) view.findViewById(R.id.siv_show_address);
        snv_set_background = (SettingNextView) view.findViewById(R.id.snv_set_background);
        snv_login = (SettingNextView) view.findViewById(R.id.snv_login);
        siv_block = (SettingItemView) view.findViewById(R.id.siv_block);
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
                mainInterface.getPermissions(new String[]{
                                Manifest.permission.SYSTEM_ALERT_WINDOW},
                        new MyPermissionsResultListener() {
                            @Override
                            public void onGranted() {
                                CLog.d(TAG, "onGranted()");
                                if (siv_show_address.isChecked()) {
                                    siv_show_address.setChecked(false);
                                    getActivity().stopService(addService);
                                } else {
                                    siv_show_address.setChecked(true);
                                    getActivity().startService(addService);
                                }
                            }

                            @Override
                            public void onDenied() {
                                CLog.d(TAG, "onDenied()");
                                siv_show_address.setChecked(false);
                                getActivity().stopService(addService);
                            }
                        });
            }
        });

        boolean update = sp.getBoolean("update", true);

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

        boolean block = ServiceUtils.isRunningService(getActivity(), "com.itheima.mobilesafe.services.BlockCallsSmsService");

        if (block) {
            //黑名单拦截已经开启
            siv_block.setChecked(true);
        } else {
            //黑名单拦截已经关闭
            siv_block.setChecked(false);
        }

        siv_block.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainInterface.getPermissions(new String[]{
                                Manifest.permission.READ_CALL_LOG,
                                Manifest.permission.WRITE_CALL_LOG,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS},
                        new MyPermissionsResultListener() {
                            @Override
                            public void onGranted() {
                                CLog.d(TAG, "onGranted()");
                                if (siv_block.isChecked()) {
                                    siv_block.setChecked(false);
                                    getActivity().stopService(blockService);
                                } else {
                                    siv_block.setChecked(true);
                                    getActivity().startService(blockService);
                                }
                            }

                            @Override
                            public void onDenied() {
                                CLog.d(TAG, "onDenied()");
                                siv_show_address.setChecked(false);
                                getActivity().stopService(addService);
                            }
                        });
            }
        });

        //设置归属地限时框背景
        snv_set_background.setTitle("归属地提示框风格");
        int skin = sp.getInt("address_bg", 0);
        snv_set_background.setDesc(Constants.addressBgs[skin]);

        snv_set_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("归属地提示框风格");
                dialog.setSingleChoiceItems(Constants.addressBgs, sp.getInt("address_bg", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        snv_set_background.setDesc(Constants.addressBgs[which]);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("address_bg", which);
                        editor.apply();
                        dialog.dismiss();
                    }
                });
                dialog.setNegativeButton("cancel", null);
                dialog.show();
            }
        });

        //第三方登入
        snv_login.setTitle("账号登入");
        int type = sp.getInt("account", -1);
        //检查登入状态（根据登入账号）
        if (type == -1)
            snv_login.setDesc("尚未登入");
        else
            snv_login.setDesc(Constants.loginAccounts[type]);
        snv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("选择登入方式");
                dialog.setSingleChoiceItems(Constants.loginAccounts, sp.getInt("account", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        snv_login.setDesc(Constants.loginAccounts[which]);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("account", which);
                        editor.apply();
                        dialog.dismiss();

                        switch (which) {
                            case 0://Account kit
                                AccessToken accessToken = AccountKit.getCurrentAccessToken();

                                if (accessToken != null) {
                                    //Handle Returning User
                                    Toast.makeText(getActivity(), "Token存在(已登入，Token在时效内)", Toast.LENGTH_SHORT).show();

                                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                                        @Override
                                        public void onSuccess(final Account account) {
                                            // Get Account Kit ID
                                            String accountKitId = account.getId();
                                            CLog.d(TAG, "accountKitId:" + accountKitId);

                                            // Get phone number
                                            PhoneNumber phoneNumber = account.getPhoneNumber();
                                            String phoneNumberString = phoneNumber.toString();
                                            CLog.d(TAG, "phone:" + phoneNumberString);

                                            // Get email
                                            String email = account.getEmail();
                                            CLog.d(TAG, "email:" + email);
                                        }

                                        @Override
                                        public void onError(final AccountKitError error) {
                                            // Handle Error
                                            CLog.d(TAG, "error:" + error.toString());
                                        }
                                    });
                                } else {
                                    //Handle new or logged out user
                                    onLoginPhone();
                                }
                        }
                    }
                });
                dialog.setNegativeButton("cancel", null);
                dialog.show();
            }
        });
    }

    public void onLoginPhone() {
        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, Constants.ACCOUNT_KIT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.release();
        CLog.v(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CLog.v(TAG, "onDestroyView");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.v(TAG, "onCreate");
    }

    @Override
    public void onStop() {
        super.onStop();
        CLog.v(TAG, "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        CLog.v(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        CLog.v(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        CLog.v(TAG, "onPause");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CLog.v(TAG, "onSaveInstanceState");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CLog.v(TAG, "onViewCreated");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        CLog.v(TAG, "onViewStateRestored");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        CLog.v(TAG, "onDetach");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CLog.v(TAG, "onAttach");
    }
}
