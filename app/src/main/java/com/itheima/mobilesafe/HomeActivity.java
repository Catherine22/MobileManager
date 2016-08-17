package com.itheima.mobilesafe;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.adapter.MyGridViewAdapter;
import com.itheima.mobilesafe.fragments.AntiTheftFragment;
import com.itheima.mobilesafe.fragments.SettingsFragment;
import com.itheima.mobilesafe.fragments.Setup1Fragment;
import com.itheima.mobilesafe.fragments.Setup2Fragment;
import com.itheima.mobilesafe.fragments.Setup3Fragment;
import com.itheima.mobilesafe.fragments.Setup4Fragment;
import com.itheima.mobilesafe.fragments.SetupFragment;
import com.itheima.mobilesafe.utils.Encryption;

import java.util.Stack;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, MainInterface {
    private final static String TAG = "HomeActivity";
    private SharedPreferences sp;
    private TextView tv_title;
    private FragmentManager fm = getSupportFragmentManager();
    private Stack<String> titles = new Stack<>();
    private static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"

    };

    private static int[] ids = {
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        initComponent();
        initSettings();
    }

    /**
     * 取得设备信息
     */
    private void initSettings() {
        //取得屏幕信息
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Settings.DISPLAY_WIDTH_PX = metrics.widthPixels;
        Settings.DISPLAY_HEIGHT_PX = metrics.heightPixels;

        //取得sim卡信息
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        Settings.simSerialNumber = tm.getSimSerialNumber();
        Settings.simSerialNumber = "65123576";
    }

    /**
     * 跳页至某Fragment
     *
     * @param ID Tag of the Fragment
     */
    @Override
    public void callFragment(int ID) {
        Fragment fragment = null;
        String tag = null;
        String title = "";
        tv_title.setVisibility(View.VISIBLE);
        switch (ID) {
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
                tv_title.setVisibility(View.GONE);
                break;
        }

        titles.push(title);
        tv_title.setText(title);

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fl_container, fragment, tag);
        transaction.addToBackStack(title);
        transaction.commitAllowingStateLoss();
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
                    case 8://进入设置中心
                        callFragment(Constants.SETTINGS_FRAG);
                        break;

                    default:
                        break;
                }

            }
        });
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
        String pwd = sp.getString("password", null);
        return !TextUtils.isEmpty(pwd);
    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            titles.pop();
            tv_title.setText(titles.peek());
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
                    editor.putString("password", Encryption.doMd5(password));
                    editor.apply();
                    alertDialog.dismiss();
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
                String savedPassword = sp.getString("password", "");
                if (TextUtils.isEmpty(input)) {
                    Toast.makeText(this, "密码不得为空", Toast.LENGTH_LONG).show();
                    return;
                }

                if (Encryption.doMd5(input).equals(savedPassword)) {
                    alertDialog.dismiss();
                    sp = getSharedPreferences("config", MODE_PRIVATE);
                    if (!sp.getBoolean("configed", false))
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
