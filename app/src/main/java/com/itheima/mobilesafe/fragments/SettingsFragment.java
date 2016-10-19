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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.LoginType;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interfaces.LoginTypeListener;
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
import com.itheima.mobilesafe.utils.login.AccountKitUtils;
import com.itheima.mobilesafe.utils.objects.UserInfo;

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
    private AccountKitUtils accountKitUtils;
    private int chosenType;

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
        accountKitUtils = new AccountKitUtils();

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
                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });

        //第三方登入
        snv_login.setTitle("账号登入");
        snv_login.setDesc("尚未登入");
        snv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出对话框
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("选择登入方式");
                chosenType = accountType;
                dialog.setSingleChoiceItems(Constants.loginAccounts, accountType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chosenType = which;
                    }
                });
                if (accountType == Constants.NONE) {
                    dialog.setPositiveButton("登入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (chosenType) {
                                case Constants.ACCOUNTKIT://Account kit
                                    if (accountKitUtils.isLogin()) {
                                        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                                            @Override
                                            public void onSuccess(final Account account) {
                                                UserInfo.id = account.getId();
                                                final PhoneNumber number = account.getPhoneNumber();
                                                UserInfo.phoneNumber = number == null ? null : number.toString();
                                                UserInfo.email = account.getEmail();
                                                setLoginView(Constants.ACCOUNTKIT);
                                            }

                                            @Override
                                            public void onError(final AccountKitError error) {
                                                CLog.e(TAG, "error:" + error.toString());
                                            }
                                        });
                                    } else//Handle new or logged out user
                                        accountKitUtils.login(getActivity(), LoginType.PHONE);

                                    dialog.dismiss();
                                    break;

                            }
                        }
                    });
                } else {
                    dialog.setPositiveButton("登出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (chosenType) {
                                case Constants.ACCOUNTKIT://Account kit
                                    accountKitUtils.logout();
                                    mainInterface.getLoginType(new LoginTypeListener() {
                                        @Override
                                        public void onResponse(final int type) {
                                            accountType = type;
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    setLoginView(type);
                                                }
                                            });
                                        }
                                    });
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    });
                }

                dialog.setNegativeButton("取消", null);
                dialog.show();
            }
        });
    }

    private void setLoginView(int type) {
        if (type == Constants.ACCOUNTKIT) {
            snv_login.setTitle("Account kit登入中");
            if (TextUtils.isEmpty(UserInfo.email))
                snv_login.setDesc(UserInfo.phoneNumber);
            else
                snv_login.setDesc(UserInfo.email);
        } else {
            snv_login.setTitle("账号登入");
            snv_login.setDesc("尚未登入");
        }
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

    private int accountType = 0;

    @Override
    public void onResume() {
        super.onResume();
        CLog.v(TAG, "onResume");
        mainInterface.getLoginType(new LoginTypeListener() {
            @Override
            public void onResponse(final int type) {
                accountType = type;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setLoginView(type);
                    }
                });
            }
        });
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
