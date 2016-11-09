package com.itheima.mobilesafe;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.itheima.mobilesafe.adapter.MyGridViewAdapter;
import com.itheima.mobilesafe.fragments.AToolsFragment;
import com.itheima.mobilesafe.fragments.AntiTheftFragment;
import com.itheima.mobilesafe.fragments.AppsManagerFragment;
import com.itheima.mobilesafe.fragments.BlacklistFragment;
import com.itheima.mobilesafe.fragments.ContactsFragment;
import com.itheima.mobilesafe.fragments.NumberAddressQueryFragment;
import com.itheima.mobilesafe.fragments.SettingsFragment;
import com.itheima.mobilesafe.fragments.TaskFragment;
import com.itheima.mobilesafe.fragments.TrafficManagerFragment;
import com.itheima.mobilesafe.fragments.setup.Setup1Fragment;
import com.itheima.mobilesafe.fragments.setup.Setup2Fragment;
import com.itheima.mobilesafe.fragments.setup.Setup3Fragment;
import com.itheima.mobilesafe.fragments.setup.Setup4Fragment;
import com.itheima.mobilesafe.fragments.setup.SetupFragment;
import com.itheima.mobilesafe.interfaces.LoginTypeListener;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.interfaces.MyPermissionsResultListener;
import com.itheima.mobilesafe.utils.BroadcastActions;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.Encryption;
import com.itheima.mobilesafe.utils.MyAdminManager;
import com.itheima.mobilesafe.utils.SpNames;
import com.itheima.mobilesafe.utils.objects.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.BranchEvent;
import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Server;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, MainInterface {
    private final static String TAG = "HomeActivity";
    private SharedPreferences sp;
    private TextView tv_title;
    private FragmentManager fm = getSupportFragmentManager();
    private Stack<String> titles = new Stack<>();
    private MyPermissionsResultListener listener;
    private MyAdminManager myAdminManager;
    private Server sv;
    private final int ACCESS_PERMISSION = 1001;
    private final static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"

    };

    private final static int[] ids = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings
    };

    //Branch.io
    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    CLog.d(TAG, referringParams.toString());
                    try {
                        String eventid = referringParams.optString("eventid");
                        boolean matchGuaranteed = referringParams.getBoolean("+match_guaranteed");
                        if (matchGuaranteed) {//避免呼叫两次
                            if (eventid.equals("ASDF1100"))//在branch.io marketing里设置
                                callFragment(Constants.BLACKLIST_FRAG);

                            //其他处理...
                        }
                    } catch (JSONException e) {
//                        e.printStackTrace();
                    }

                } else
                    CLog.i(TAG, error.getMessage());

            }
        }, this.getIntent().getData(), this);
    }

    //Branch.io
    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        myAdminManager = new MyAdminManager(this);
        AsyncResponse ar = new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.e(TAG, "onFailure" + errorCode);
            }
        };
        sv = new Server(this, ar);
        initComponent();

        //取得sim卡信息
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        try {
            com.itheima.mobilesafe.utils.Settings.simSerialNumber = tm.getSimSerialNumber();
        } catch (SecurityException e) {
            CLog.e(TAG, e.toString());
        }
//        Settings.simSerialNumber = "65123576";

        Branch.getInstance(getApplicationContext()).userCompletedAction(BranchEvent.SHARE_STARTED);
//        BlacklistDao dao = new BlacklistDao(this);
//        for (int i = 0; i < 100; i++)
//            dao.add("Lisi", "1351234567" + i, BlacklistDao.MODE_CALLS_BLOCKED);

        //持久化到内存中，避免无法还原
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
            if (!defaultSmsApp.equals(getPackageName())) {
                defaultSysSmsApp = defaultSmsApp;
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(SpNames.default_sms_app, defaultSysSmsApp);
                editor.apply();
            } else
                defaultSysSmsApp = sp.getString(SpNames.default_sms_app, getPackageName());
        }
        //利用intent（比如在SplashActivity建立的快捷图标）开启
        if (getIntent() != null) {
            if (Constants.TASK_FRAG == getIntent().getIntExtra("OPEN_PAGE", -1)) {
                callFragment(Constants.TASK_FRAG);
            }
        }
    }

    /**
     * 跳页至某Fragment
     *
     * @param ID Tag of the Fragment
     */
    @Override
    public void callFragment(int ID) {
        CLog.d(TAG, "call " + ID);
        Fragment fragment = null;
        String tag = null;
        String title = "";
        switch (ID) {
            case Constants.TASK_FRAG:
                title = "进程管理";
                fragment = new TaskFragment();
                tag = "TASK";
                break;
            case Constants.APPS_MAG_FRAG:
                title = "软件管理";
                fragment = new AppsManagerFragment();
                tag = "APPS_MAG";
                break;
            case Constants.A_TOOLS_FRAG:
                title = "高级工具";
                fragment = new AToolsFragment();
                tag = "ATOOLS";
                break;
            case Constants.SETTINGS_FRAG:
                title = "设置中心";
                fragment = new SettingsFragment();
                tag = "SETTINGS";
                break;
            case Constants.ANTI_THEFT_FRAG:
                title = "手机防盗";
                fragment = new AntiTheftFragment();
                tag = "ANTI_THEFT";
                break;
            case Constants.SETUP1_FRAG:
                title = "欢迎使用手机防盗";
                fragment = new Setup1Fragment();
                tag = "SETUP1";
                break;
            case Constants.SETUP2_FRAG:
                title = "手机卡绑定";
                fragment = new Setup2Fragment();
                tag = "SETUP2";
                break;
            case Constants.SETUP3_FRAG:
                title = "设置安全号码";
                fragment = new Setup3Fragment();
                tag = "SETUP3";
                break;
            case Constants.SETUP4_FRAG:
                title = "恭喜您设置完成";
                fragment = new Setup4Fragment();
                tag = "SETUP4";
                break;
            case Constants.SETUP_FRAG:
                title = "设置";
                fragment = new SetupFragment();
                tag = "SETUP";
                break;
            case Constants.CONTACTS_FRAG:
                title = "选择联络人";
                fragment = new ContactsFragment();
                tag = "CONTACTS";
                break;
            case Constants.NUM_ADDRESS_QUERY_FRAG:
                title = "号码归属地查询";
                fragment = new NumberAddressQueryFragment();
                tag = "NUM_ADDRESS_QUERY";
                break;
            case Constants.BLACKLIST_FRAG:
                title = "黑名单拦截";
                fragment = new BlacklistFragment();
                tag = "BLACKLIST";
                break;
            case Constants.TRAFFIC_MAG_FRAG:
                title = "流量统计";
                fragment = new TrafficManagerFragment();
                tag = "TRAFFIC_MAG";
                break;
        }

        titles.push(title);
        tv_title.setText(title);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();

        if (ID == Constants.SETUP_FRAG)
            tv_title.setVisibility(View.GONE);
        else
            tv_title.setVisibility(View.VISIBLE);
    }

    /**
     * Clear all fragments in stack
     */
    @Override
    public void clearAllFragments() {
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
            titles.pop();
        }
        titles.push("功能列表");
    }

    /**
     * Simulate BackKey event
     */
    @Override
    public void backToPreviousPage() {
        onBackPressed();
    }

    private void initComponent() {
        GridView list_home = (GridView) findViewById(R.id.list_home);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("功能列表");
        titles.push("功能列表");
        MyGridViewAdapter adapter = new MyGridViewAdapter(this, names, ids);
        list_home.setAdapter(adapter);
        list_home.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0://进入手机防盗
                        showAntiTheftDialog();
                        break;
                    case 1://黑名单拦截
                        callFragment(Constants.BLACKLIST_FRAG);
                        break;
                    case 2://软件管理
                        callFragment(Constants.APPS_MAG_FRAG);
                        break;
                    case 3://进程管理
                        getPermissions(new String[]{Manifest.permission.KILL_BACKGROUND_PROCESSES}, new MyPermissionsResultListener() {
                            @Override
                            public void onGranted() {
                                callFragment(Constants.TASK_FRAG);
                            }

                            @Override
                            public void onDenied() {
                                Toast.makeText(HomeActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 4://流量统计
                        callFragment(Constants.TRAFFIC_MAG_FRAG);
                        break;
                    case 7://高级工具
                        callFragment(Constants.A_TOOLS_FRAG);
                        break;
                    case 8://进入设置中心
                        callFragment(Constants.SETTINGS_FRAG);
                        break;
                    default:
                        break;
                }

            }
        });
        getPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new MyPermissionsResultListener() {
            @Override
            public void onGranted() {
                myAdminManager.getAdminPermission();
            }

            @Override
            public void onDenied() {
                Toast.makeText(HomeActivity.this, "权限不足", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }


    private boolean grantedSAW = true;//同意特殊权限(SYSTEM_ALERT_WINDOW)
    private boolean grantedWS = true;//同意特殊权限(WRITE_SETTINGS)
    private boolean grantedAll = true;//同意一般权限
    private int specCount = 0;//等待同意特殊權限數(没有获取就不用添加)

    /**
     * 要求用户打开权限,仅限android 6.0 以上
     * <p/>
     * SYSTEM_ALERT_WINDOW 和 WRITE_SETTINGS, 这两个权限比较特殊，
     * 不能通过代码申请方式获取，必须得用户打开软件设置页手动打开，才能授权。
     *
     * @param permissions 手机权限 e.g. Manifest.permission.ACCESS_FINE_LOCATION
     * @param listener    此变量implements事件的接口,负责传递信息
     */
    @Override
    public void getPermissions(String[] permissions, MyPermissionsResultListener listener) {
        this.listener = listener;
        List<String> deniedPermissionsList = new LinkedList<>();
        specCount = 0;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED && !p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) && !p.equals(Manifest.permission.WRITE_SETTINGS))
                deniedPermissionsList.add(p);
            else if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(HomeActivity.this)) {
                    grantedSAW = false;
                    specCount++;
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + HomeActivity.this.getPackageName()));
                    startActivityForResult(intent, Constants.OVERLAY_PERMISSION_REQ_CODE);
                } else
                    grantedSAW = true;

            } else if (p.equals(Manifest.permission.WRITE_SETTINGS)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(HomeActivity.this)) {
                    grantedWS = false;
                    specCount++;
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + HomeActivity.this.getPackageName()));
                    startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS);
                } else
                    grantedWS = true;

            } else {
                grantedSAW = true;
                grantedWS = true;
                // You've got SYSTEM_ALERT_WINDOW permission.
            }
        }


        if (deniedPermissionsList.size() != 0) {
            grantedAll = false;
            String[] deniedPermissions = new String[deniedPermissionsList.size()];
            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                deniedPermissions[i] = deniedPermissionsList.get(i);
            }
            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
        } else {
            // All of the permissions granted
            grantedAll = true;
            if (grantedSAW && grantedWS)
                listener.onGranted();
        }
    }

    private String defaultSysSmsApp;

    /**
     * 设置预设的短信app，在android 4.4 以上必须设置才能执行部分短信相关操作
     * 没有实际处理短信处理的逻辑, 所以必须在使用完后要求用户改回来
     * {@link com.itheima.mobilesafe.os.HeadlessSmsSendService}
     * {@link com.itheima.mobilesafe.os.MmsReceiver}
     * {@link com.itheima.mobilesafe.os.SmsReceiver}
     *
     * @param setThisAsDefault 是否让此应用成为预设短信app，false则改回原先预设app
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setDefaultSmsApp(boolean setThisAsDefault, @Nullable MyPermissionsResultListener listener) {
        this.listener = listener;
        String currDefault = Telephony.Sms.getDefaultSmsPackage(this);
        CLog.d(TAG, "sys sms app: " + defaultSysSmsApp);
        CLog.d(TAG, "current default app: " + currDefault);
        if (setThisAsDefault) {
            if (currDefault.equals(defaultSysSmsApp)) {
                Intent intent = new Intent();
                intent.setAction(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getPackageName());
                startActivityForResult(intent, Constants.CHANGEING_DEFAULT_SMS_APP);
            } else {
                if (listener != null)
                    listener.onGranted();
            }
        } else {
            Intent intent = new Intent();
            intent.setAction(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSysSmsApp);
            startActivityForResult(intent, Constants.CHANGEING_DEFAULT_SMS_APP);
        }
    }


    @Override
    public void getLoginType(final LoginTypeListener listener) {
        SharedPreferences sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
        int savedLoginType = sp.getInt(SpNames.loginType, Constants.NONE);
        if (savedLoginType == Constants.ACCOUNTKIT) {
            if (AccountKit.getCurrentAccessToken() != null) {
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(final Account account) {
                        setLoginType(Constants.ACCOUNTKIT);
                        listener.onResponse(Constants.ACCOUNTKIT);
                        UserInfo.id = account.getId();
                        final PhoneNumber number = account.getPhoneNumber();
                        UserInfo.phoneNumber = number == null ? null : number.toString();
                        UserInfo.email = account.getEmail();
                    }

                    @Override
                    public void onError(final AccountKitError error) {
                        CLog.e(TAG, "error:" + error.toString());
                    }
                });
            } else {
                setLoginType(Constants.NONE);
                listener.onResponse(Constants.NONE);
            }

        } else {
            setLoginType(Constants.NONE);
            listener.onResponse(Constants.NONE);
        }
    }

    @Override
    public void setLoginType(int type) {
        SharedPreferences sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(SpNames.loginType, type);
        editor.apply();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        int count = 0;
        if (requestCode == ACCESS_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED)
                    count++;
            }
            grantedAll = count == grantResults.length;

            if (grantedAll && grantedSAW && grantedWS)//全部同意
                listener.onGranted();// Permission Granted
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_ENABLE_ADMIN:
                if (resultCode == RESULT_OK) {
                    sv.pushBoolean(BroadcastActions.ADMIN_PERMISSION, true);
                } else {
                    sv.pushBoolean(BroadcastActions.ADMIN_PERMISSION, false);
                }

                break;
            case Constants.OVERLAY_PERMISSION_REQ_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    specCount--;
                    if (!Settings.canDrawOverlays(this)) {
                        // Special permission not granted...
                        grantedSAW = false;
                        listener.onDenied();
                    } else {
                        grantedSAW = true;
                        if (grantedAll && grantedWS)
                            listener.onGranted();
                        // You've got SYSTEM_ALERT_WINDOW permission.
                        if (!grantedSAW && specCount == 1)//表示用戶不同意另一個特殊權限
                            listener.onDenied();
                    }
                }
                break;
            case Constants.PERMISSION_WRITE_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    specCount--;
                    if (!Settings.System.canWrite(this)) {
                        // Special permission not granted...
                        grantedWS = false;
                        listener.onDenied();
                    } else {
                        grantedWS = true;
                        if (grantedAll && grantedSAW)
                            listener.onGranted();
                        // You've got SYSTEM_ALERT_WINDOW permission.
                        if (!grantedSAW && specCount == 1)//表示用戶不同意另一個特殊權限
                            listener.onDenied();
                    }
                }
                break;
            case Constants.CHANGEING_DEFAULT_SMS_APP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
                    CLog.d(TAG, "defaultSmsApp " + defaultSmsApp);
                    if (listener != null && resultCode == RESULT_OK) {
                        if (defaultSmsApp.equals(defaultSysSmsApp))
                            listener.onDenied();
                        else
                            listener.onGranted();
                    }
                }
                break;
            case Constants.ACCOUNT_KIT_REQ_CODE:
                AccountKitLoginResult loginResult = AccountKit.loginResultWithIntent(data);
                String toastMessage;
                if (loginResult == null || loginResult.wasCancelled()) {
                    toastMessage = "Login Cancelled";
                } else if (loginResult.getError() != null) {
                    toastMessage = loginResult.getError().getErrorType().getMessage();
                } else {
                    final AccessToken accessToken = loginResult.getAccessToken();
                    final long tokenRefreshIntervalInSeconds =
                            loginResult.getTokenRefreshIntervalInSeconds();
                    if (accessToken != null) {
                        toastMessage = "Success:" + accessToken.getAccountId()
                                + tokenRefreshIntervalInSeconds;

                        setLoginType(Constants.ACCOUNTKIT);

                        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                            @Override
                            public void onSuccess(final Account account) {
                                UserInfo.id = account.getId();
                                final PhoneNumber number = account.getPhoneNumber();
                                UserInfo.phoneNumber = number == null ? null : number.toString();
                                UserInfo.email = account.getEmail();
                            }

                            @Override
                            public void onError(final AccountKitError error) {
                                CLog.e(TAG, "error:" + error.toString());
                            }
                        });
                    } else {
                        toastMessage = "Unknown response type";
                    }
                }

                // Surface the result to your user in an appropriate way.
                CLog.d(TAG, "toastMessage:" + toastMessage);
                break;

            case Constants.UNINSTASLL_APP:
                sv.pushBoolean(BroadcastActions.FINISHED_UNINSTALLING, true);
                break;
        }
        CLog.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
    }

    /**
     * 检查密码设置
     */
    private void showAntiTheftDialog() {
        if (isSetupPwd())
            showPwdDialog();
        else
            showSetupPwdDialog();
    }

    //Dialog components
    private EditText et_setup_pwd, et_confirm_pwd, et_type_pwd;
    private Button bt_ok, bt_cancel;
    private Dialog alertDialog;

    /**
     * 设置密码对话框
     */
    private void showSetupPwdDialog() {
        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_setup_password);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        et_setup_pwd = (EditText) alertDialog.findViewById(R.id.et_setup_pwd);
        et_confirm_pwd = (EditText) alertDialog.findViewById(R.id.et_confirm_pwd);
        bt_ok = (Button) alertDialog.findViewById(R.id.bt_setup_ok);
        bt_ok.setOnClickListener(this);
        bt_cancel = (Button) alertDialog.findViewById(R.id.bt_setup_cancel);
        bt_cancel.setOnClickListener(this);
    }

    /**
     * 输入密码对话框
     */
    private void showPwdDialog() {
        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_type_password);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        et_type_pwd = (EditText) alertDialog.findViewById(R.id.et_type_pwd);
        bt_ok = (Button) alertDialog.findViewById(R.id.bt_type_ok);
        bt_ok.setOnClickListener(this);
        bt_cancel = (Button) alertDialog.findViewById(R.id.bt_type_cancel);
        bt_cancel.setOnClickListener(this);
    }

    /**
     * 判断是否设置过密码
     *
     * @return Is password empty or not
     */
    private boolean isSetupPwd() {
        String pwd = sp.getString(SpNames.password, null);
        return !TextUtils.isEmpty(pwd);
    }

    /**
     * 点击back键
     */
    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            titles.pop();
            tv_title.setText(titles.peek());

            if (tv_title.getText().toString().equals("设置"))
                tv_title.setVisibility(View.GONE);
            else
                tv_title.setVisibility(View.VISIBLE);
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_setup_ok:
                String password = et_setup_pwd.getText().toString().trim();
                String confirmingPassword = et_confirm_pwd.getText().toString().trim();

                if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmingPassword)) {
                    Toast.makeText(this, "密码不得为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (password.equals(confirmingPassword)) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(SpNames.password, Encryption.doMd5(password));
                    editor.apply();
                    alertDialog.dismiss();

                    sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
                    if (!sp.getBoolean(SpNames.configed, false))
                        callFragment(Constants.SETUP_FRAG);
                    else
                        callFragment(Constants.ANTI_THEFT_FRAG);
                } else {
                    Toast.makeText(this, "密码不一致", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case R.id.bt_setup_cancel:
                alertDialog.dismiss();
                break;
            case R.id.bt_type_ok:
                String input = et_type_pwd.getText().toString().trim();
                String savedPassword = sp.getString(SpNames.password, "");
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(this, "密码不得为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Encryption.doMd5(input).equals(savedPassword)) {
                    alertDialog.dismiss();
                    sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
                    if (!sp.getBoolean(SpNames.configed, false))
                        callFragment(Constants.SETUP_FRAG);
                    else
                        callFragment(Constants.ANTI_THEFT_FRAG);
                } else {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            case R.id.bt_type_cancel:
                alertDialog.dismiss();
                break;
        }
    }

}
