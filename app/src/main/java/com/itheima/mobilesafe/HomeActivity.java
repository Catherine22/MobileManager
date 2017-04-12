package com.itheima.mobilesafe;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import com.itheima.mobilesafe.fragments.AntiVirusFragment;
import com.itheima.mobilesafe.fragments.AppsManagerFragment;
import com.itheima.mobilesafe.fragments.BlacklistFragment;
import com.itheima.mobilesafe.fragments.ClearCacheFragment;
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
import com.itheima.mobilesafe.interfaces.OnRequestPermissionsListener;
import com.itheima.mobilesafe.services.NetworkHealthService;
import com.itheima.mobilesafe.utils.BroadcastActions;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.Encryption;
import com.itheima.mobilesafe.utils.MyAdminManager;
import com.itheima.mobilesafe.utils.SpNames;
import com.itheima.mobilesafe.utils.objects.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.LinkProperties;
import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Result;
import tw.com.softworld.messagescenter.Server;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, MainInterface {
    private final static String TAG = "HomeActivity";
    private SharedPreferences sp;
    private TextView tv_title;
    private FragmentManager fm = getSupportFragmentManager();
    private Stack<String> titles = new Stack<>();
    private OnRequestPermissionsListener listener;
    private MyAdminManager myAdminManager;
    private Server sv;
    private Client client;
    private Intent nhs;
    //由其它应用通过intent开启（比如google play），还是用户手动开启。
    private boolean launchFromIntent;
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
        Intent intent = getIntent();
        //app自然启动时launcher页面是SplashActivity，所以检查intent是否带包名在SplashActivity执行
        launchFromIntent = intent.getBooleanExtra("launchFromIntent", false);
        CLog.i(TAG, "launchFromIntent:" + launchFromIntent);
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    CLog.d(TAG, referringParams.toString());
                    try {
                        String eventid = referringParams.optString("eventid");
                        boolean matchGuaranteed = referringParams.optBoolean("+match_guaranteed", false);
                        if (matchGuaranteed) {//避免呼叫两次
                            if (eventid.equals("ASDF1100"))//在branch.io marketing里设置
                                callFragment(Constants.BLACKLIST_FRAG);
                            else if (eventid.equals("ASDF1200"))//在branch.io marketing里设置
                                callFragment(Constants.ANTI_VIRUS_FRAG);
                            //其他处理...
                        } else {
                            //检查intent是否带有包名（google play通过intent开启activity，一旦通过intent开启其它应用就必须带包名，用户直接开启app则不会有包名），有带包名表示用户是因为浏览器不给力，找不到应用因而导向google play而非直接开启，这时候我们就当成合法的branch.io请求。
                            if (launchFromIntent) {
                                if (eventid.equals("ASDF1100"))//在branch.io marketing里设置
                                    callFragment(Constants.BLACKLIST_FRAG);
                                else if (eventid.equals("ASDF1200"))//在branch.io marketing里设置
                                    callFragment(Constants.ANTI_VIRUS_FRAG);
                                //其他处理...

                                launchFromIntent = false;
                            }
                        }
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }

                } else
                    CLog.i(TAG, error.getMessage());

            }
        }, this.getIntent().getData(), this);
//        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
//            @Override
//            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
//                if (error == null) {
//                    try {
//                        HashMap<String, String> metadata = branchUniversalObject.getMetadata();
//                        CLog.i(TAG, "getMetadata:" + branchUniversalObject.getMetadata());
//                        CLog.i(TAG, "getControlParams:" + linkProperties.getControlParams());
//                        CLog.i(TAG, "getMatchDuration:" + linkProperties.getMatchDuration());
//                        CLog.i(TAG, "getTags:" + linkProperties.getTags());
//                        CLog.i(TAG, "getAlias:" + linkProperties.getAlias());
//                        CLog.i(TAG, "getChannel:" + linkProperties.getChannel());
//                        CLog.i(TAG, "getCampaign:" + linkProperties.getCampaign());
//                        CLog.i(TAG, "getStage:" + linkProperties.getStage());
//
//                        //自定义上传参数
//                        Branch.getInstance().setIdentity("AA0001");
//                        JSONObject jo = new JSONObject();
//                        jo.put("timestamp", System.currentTimeMillis() / 1000 + "");
//                        Branch.getInstance().userCompletedAction("log event", jo);
//
////                        Branch.getInstance().getCreditHistory(new Branch.BranchListResponseListener() {
////                            @Override
////                            public void onReceivingResponse(JSONArray list, BranchError error) {
////                                CLog.i(TAG, "CreditHistory:"+list.toString());
////                            }
////                        });
////                        branchUniversalObject.setCanonicalIdentifier("YO");
////                        branchUniversalObject.generateShortUrl(HomeActivity.this, linkProperties, new Branch.BranchLinkCreateListener() {
////                            @Override
////                            public void onLinkCreate(String url, BranchError error) {
////                                if (error == null) {
////                                    //神奇的功能，生成新链接
////                                    CLog.i(TAG, "got my Branch link to share: " + url);
////                                }
////                            }
////                        });
//
//
//                        if (metadata.containsKey("eventid")) {
//                            String eventid = metadata.get("eventid");
//                            if (eventid.equals("ASDF1100"))//在branch.io marketing里设置
//                                callFragment(Constants.BLACKLIST_FRAG);
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
//                    // params will be empty if no data found
//                    // ... insert custom logic here ...
//                } else {
//                    CLog.i(TAG, error.getMessage());
//                }
//            }
//        }, this.getIntent().getData(), this);
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
        //记录是否为初次启动，getPermission会用到
        isFirstTime = getSharedPreferences("isFirstTime", MODE_PRIVATE);
        isFirstTimeRun = isFirstTime.getBoolean("isFirstTimeRun", true);

        myAdminManager = new MyAdminManager(this);
        AsyncResponse ar = new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.e(TAG, "onFailure" + errorCode);
            }
        };
        sv = new Server(this, ar);

        myAdminManager.getAdminPermission();
        client = new Client(this, new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                CLog.d(TAG, "getAdminPermission:" + result.isBoolean());
                if (result.isBoolean()) {
                    init();
                } else {
                    Toast.makeText(HomeActivity.this, "权限不足", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        });
        client.gotMessages(BroadcastActions.ADMIN_PERMISSION);

        // 监听网络状态
        nhs = new Intent(HomeActivity.this, NetworkHealthService.class);
        startService(nhs);
    }

    @Override
    protected void onDestroy() {
        stopService(nhs);
        super.onDestroy();
    }

    private void init() {
        getPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                SharedPreferences.Editor ed = isFirstTime.edit();
                ed.putBoolean("isFirstTimeRun", false);
                ed.apply();
                isFirstTimeRun = false;

                initComponent();

                //取得sim卡信息
                TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                try {
                    com.itheima.mobilesafe.utils.Settings.simSerialNumber = tm.getSimSerialNumber();
                } catch (SecurityException e) {
                    CLog.e(TAG, e.toString());
                }
//        Settings.simSerialNumber = "65123576";

//        BlacklistDao dao = new BlacklistDao(this);
//        for (int i = 0; i < 100; i++)
//            dao.add("Lisi", "1351234567" + i, BlacklistDao.MODE_CALLS_BLOCKED);

                //持久化到内存中，避免无法还原
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(HomeActivity.this);
                    if (!getPackageName().equals(defaultSmsApp)) {
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

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions, @Nullable final List<String> neverAskAgainPermissions) {
                SharedPreferences.Editor editor = isFirstTime.edit();
                editor.putBoolean("isFirstTimeRun", false);
                editor.apply();
                isFirstTimeRun = false;

                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.INTERNET.equals(p)) {
                            context.append("网络、");
                        } else if (Manifest.permission.READ_PHONE_STATE.equals(p)) {
                            context.append("电话、");
                        } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }

                if (neverAskAgainPermissions != null) {
                    for (String p : neverAskAgainPermissions) {
                        if (Manifest.permission.INTERNET.equals(p)) {
                            context.append("网络、");
                        } else if (Manifest.permission.READ_PHONE_STATE.equals(p)) {
                            context.append("电话、");
                        } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }

                context.deleteCharAt(context.length() - 1);

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(HomeActivity.this);
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%1$s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (neverAskAgainPermissions != null && neverAskAgainPermissions.size() != 0) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                            finish();
                        } else
                            init();
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myAlertDialog.show();
            }
        });

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
            case Constants.ANTI_VIRUS_FRAG:
                title = "手机杀毒";
                fragment = new AntiVirusFragment();
                tag = "ANTI_VIRUS";
                break;
            case Constants.CLEAR_CACHE_FRAG:
                title = "缓存清理";
                fragment = new ClearCacheFragment();
                tag = "CLEAR_CACHE";
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
                        getPermissions(new String[]{Manifest.permission.KILL_BACKGROUND_PROCESSES}, new OnRequestPermissionsListener() {
                            @Override
                            public void onGranted() {
                                callFragment(Constants.TASK_FRAG);
                            }

                            @Override
                            public void onDenied(@Nullable List<String> deniedPermissions, @Nullable List<String> neverAskAgainPermissions) {
                                Toast.makeText(HomeActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case 4://流量统计
                        callFragment(Constants.TRAFFIC_MAG_FRAG);
                        break;
                    case 5://手机杀毒
                        callFragment(Constants.ANTI_VIRUS_FRAG);
                        break;
                    case 6://缓存清理
                        clearCache();
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
    }

    private void clearCache() {
        getPermissions(new String[]{Manifest.permission.GET_PACKAGE_SIZE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                callFragment(Constants.CLEAR_CACHE_FRAG);
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions, @Nullable final List<String> neverAskAgainPermissions) {
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.GET_PACKAGE_SIZE.equals(p)) {
                            context.append("获取应用大小");
                        }
                    }
                }

                if (neverAskAgainPermissions != null) {
                    for (String p : neverAskAgainPermissions) {
                        if (Manifest.permission.GET_PACKAGE_SIZE.equals(p)) {
                            context.append("获取应用大小");
                        }
                    }
                }

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(HomeActivity.this);
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%1$s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (neverAskAgainPermissions != null && neverAskAgainPermissions.size() != 0) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                            finish();
                        } else
                            clearCache();
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myAlertDialog.show();
            }
        });
    }

    private final int GRANTED_SAW = 0x0001;     //同意特殊权限(SYSTEM_ALERT_WINDOW)
    private final int GRANTED_WS = 0x0010;      //同意特殊权限(WRITE_SETTINGS)
    private int requestSpec = 0x0000;           //需要的特殊权限
    private int grantedSpec = 0x0000;           //已取得的特殊权限
    private int confirmedSpec = 0x0000;         //已询问的特殊权限
    private List<String> deniedPermissionsList; //被拒绝的权限
    private List<String> neverAskAgainList;     //被标记"不再询问"的权限
    private SharedPreferences isFirstTime;      //记录是否为初次启动app的SharePreferences
    private boolean isFirstTimeRun;             //是否为初次启动app

    /**
     * 1. 如果gradle中设定compileSdkVersion与targetSdkVersion低于23，在Android M后的手机就会预设没有获取权限。<br></>
     * 2. 所以如果不想每次打开App都要逐一检查权限，就直接设compileSdkVersion与targetSdkVersion为22（或以下），预设一开始就取得Manifest所有权限。<br></>
     * 3. 通过此方法获取的权限仅限于manifest有注册的权限，如果没注册这边也无法获取。
     * <p>
     * 要求用户打开权限,仅限android 6.0 以上
     * <p/>
     * SYSTEM_ALERT_WINDOW 和 WRITE_SETTINGS, 这两个权限比较特殊，<br>
     * 不能通过代码申请方式获取，必须得用户打开软件设置页手动打开，才能授权。<br>
     * 使用时一样填入String[]即可。<br>
     * <p>
     *
     * @param permissions 手机权限 e.g. Manifest.permission.ACCESS_FINE_LOCATION
     * @param listener    此变量implements事件的接口,负责传递信息
     */
    @Override
    public void getPermissions(String[] permissions, OnRequestPermissionsListener listener) {
        this.listener = listener;
        deniedPermissionsList = new LinkedList<>();
        neverAskAgainList = new LinkedList<>();

        for (String p : permissions) {
            if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                requestSpec |= GRANTED_SAW;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(HomeActivity.this)) {
                    grantedSpec &= GRANTED_SAW;
                } else
                    grantedSpec |= GRANTED_SAW;
            } else if (p.equals(Manifest.permission.WRITE_SETTINGS)) {
                requestSpec |= GRANTED_WS;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(HomeActivity.this)) {
                    grantedSpec &= GRANTED_WS;
                } else
                    grantedSpec |= GRANTED_WS;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, p) || isFirstTimeRun) {
                deniedPermissionsList.add(p);
            } else if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                neverAskAgainList.add(p);
            }
        }

        if (requestSpec != grantedSpec) {
            getASpecPermission(requestSpec | grantedSpec);
        } else {// Granted all of the special permissions
            if (deniedPermissionsList.size() != 0) {
                //Ask for the permissions
                String[] deniedPermissions = new String[deniedPermissionsList.size()];
                for (int i = 0; i < deniedPermissionsList.size(); i++) {
                    deniedPermissions[i] = deniedPermissionsList.get(i);
                }
                ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
            } else {
                if (neverAskAgainList.size() != 0)
                    listener.onDenied(null, neverAskAgainList);
                else
                    listener.onGranted();

                requestSpec = 0x0000;
                grantedSpec = 0x0000;
                confirmedSpec = 0x0000;
                deniedPermissionsList = null;
            }
        }
    }

    private void getASpecPermission(int permissions) {
        CLog.d(TAG, "getSpec " + permissions);
        if ((permissions & GRANTED_SAW) == GRANTED_SAW) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + HomeActivity.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_OVERLAY);
        }

        if ((permissions & GRANTED_WS) == GRANTED_WS) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + HomeActivity.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS);
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
    public void setDefaultSmsApp(boolean setThisAsDefault, @Nullable OnRequestPermissionsListener listener) {
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
        CLog.d(TAG, "onRequestPermissionsResult");

        List<String> deniedResults = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedResults.add(permissions[i]);
            }
        }

        if ((requestSpec ^ grantedSpec) == GRANTED_WS) {
            deniedResults.add("Manifest.permission.WRITE_SETTINGS");
        }

        if ((requestSpec ^ grantedSpec) == GRANTED_SAW) {
            deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");
        }

        if (deniedResults.size() != 0 || neverAskAgainList.size() != 0) {
            listener.onDenied(deniedResults, neverAskAgainList);
        } else {
            listener.onGranted();
        }

        requestSpec = 0x0000;
        grantedSpec = 0x0000;
        confirmedSpec = 0x0000;
        deniedPermissionsList = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CLog.d(TAG, "request:" + requestCode + "/resultCode" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_ENABLE_ADMIN:
                if (resultCode == RESULT_OK) {
                    sv.pushBoolean(BroadcastActions.ADMIN_PERMISSION, true);
                } else {
                    sv.pushBoolean(BroadcastActions.ADMIN_PERMISSION, false);
                }

                break;
            case Constants.PERMISSION_OVERLAY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    confirmedSpec |= GRANTED_SAW;

                    if (!android.provider.Settings.canDrawOverlays(this)) {
                        //denied
                        grantedSpec &= GRANTED_SAW;
                    } else {
                        grantedSpec |= GRANTED_SAW;
                    }
                    if (confirmedSpec == requestSpec) {
                        if (deniedPermissionsList.size() != 0) {
                            //Ask for the permissions
                            String[] deniedPermissions = new String[deniedPermissionsList.size()];
                            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                                deniedPermissions[i] = deniedPermissionsList.get(i);
                            }
                            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec ^ grantedSpec) == GRANTED_WS) {
                                deniedResults.add("Manifest.permission.WRITE_SETTINGS");
                            }

                            if ((requestSpec ^ grantedSpec) == GRANTED_SAW) {
                                deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");
                            }

                            if (deniedResults.size() != 0 || neverAskAgainList.size() != 0) {
                                listener.onDenied(deniedResults, neverAskAgainList);
                            } else {
                                listener.onGranted();
                            }

                            requestSpec = 0x0000;
                            grantedSpec = 0x0000;
                            confirmedSpec = 0x0000;
                            deniedPermissionsList = null;
                        }
                    }
                }
                break;
            case Constants.PERMISSION_WRITE_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    confirmedSpec |= GRANTED_WS;
                    if (!android.provider.Settings.System.canWrite(this)) {
                        //denied
                        grantedSpec &= GRANTED_WS;
                    } else {
                        grantedSpec |= GRANTED_WS;
                    }
                    if (confirmedSpec == requestSpec) {
                        if (deniedPermissionsList.size() != 0) {
                            //Ask for the permissions
                            String[] deniedPermissions = new String[deniedPermissionsList.size()];
                            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                                deniedPermissions[i] = deniedPermissionsList.get(i);
                            }
                            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec ^ grantedSpec) == GRANTED_WS) {
                                deniedResults.add("Manifest.permission.WRITE_SETTINGS");
                            }

                            if ((requestSpec ^ grantedSpec) == GRANTED_SAW) {
                                deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");
                            }

                            if (deniedResults.size() != 0 || neverAskAgainList.size() != 0) {
                                listener.onDenied(deniedResults, neverAskAgainList);
                            } else {
                                listener.onGranted();
                            }

                            requestSpec = 0x0000;
                            grantedSpec = 0x0000;
                            confirmedSpec = 0x0000;
                            deniedPermissionsList = null;
                        }
                    }
                }
                break;
            case Constants.CHANGEING_DEFAULT_SMS_APP:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
                    CLog.d(TAG, "defaultSmsApp " + defaultSmsApp);
                    if (listener != null && resultCode == RESULT_OK) {
                        if (defaultSmsApp.equals(defaultSysSmsApp))
                            listener.onDenied(null, null);
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
            if (titles.empty())
                titles.push("功能列表");

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
