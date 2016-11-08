package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.mobilesafe.utils.BroadcastActions;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/11/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class TypePwdActivity extends Activity {
    private TextView tv_title;
    private ImageView iv_icon;
    private EditText et_pwd;
    private String packname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_pwd);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        et_pwd = (EditText) findViewById(R.id.et_pwd);

        if (getIntent() != null) {
            packname = getIntent().getStringExtra("packname");
            String title = String.format(getString(R.string.packname), packname);
            tv_title.setText(title);


            try {
                PackageManager pm = getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(packname,0);
                iv_icon.setImageDrawable(ai.loadIcon(pm));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("1234")) {
                    AsyncResponse ar = new AsyncResponse() {
                        @Override
                        public void onFailure(int errorCode) {

                        }
                    };
                    Server server = new Server(TypePwdActivity.this, ar);
                    server.pushString(BroadcastActions.WATCHDOG_STOP, packname);
                    finish();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * 按下back key返回桌面（其实就是打开桌面应用）
     */
    @Override
    public void onBackPressed() {
        //找到launcher的源码,根据intent-filter的部分定义intent

//        <intent-filter>
//        <action android:name="android.intent.action.MAIN" />
//        <category android:name="android.intent.category.HOME"/>
//        <category android:name="android.intent.category.DEFAULT" />
//        <category android:name="android.intent.category.MONKEY" />
//        </intent-filter>

        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
