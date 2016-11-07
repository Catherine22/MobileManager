package com.itheima.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.BroadcastActions;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/11/7.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class TypePwd extends Activity {
    private EditText et_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_pwd);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
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
                    Server server = new Server(TypePwd.this, ar);
                    server.pushBoolean(BroadcastActions.WATCHDOG_FLAG, false);
                    finish();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
