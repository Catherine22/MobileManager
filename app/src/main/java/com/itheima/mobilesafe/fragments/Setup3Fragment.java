package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itheima.mobilesafe.Constants;
import com.itheima.mobilesafe.MainInterface;
import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.Settings;
import com.itheima.mobilesafe.utils.CLog;

import tw.com.softworld.messagescenter.AsyncResponse;
import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Result;
import tw.com.softworld.messagescenter.Server;

/**
 * Created by Catherine on 2016/8/12.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class Setup3Fragment extends Fragment {

    private static final String TAG = "Setup3Fragment";
    private MainInterface mainInterface;
    private Server sv;
    private EditText et_phone_number;
    private Client client;

    public static Setup3Fragment newInstance() {
        return new Setup3Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup3, container, false);

        initData();

        et_phone_number = (EditText) view.findViewById(R.id.et_phone_number);
        if (!TextUtils.isEmpty(Settings.safePhone))
            et_phone_number.setText(Settings.safePhone);
        et_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(et_phone_number.getText()))
                    sv.pushInt("DISABLE_SWIPING", 3);
                else
                    sv.pushInt("DISABLE_SWIPING", -1);
                Settings.safePhone = et_phone_number.getText().toString();
            }
        });
        Button bt_choose_contacts = (Button) view.findViewById(R.id.bt_choose_contacts);
        bt_choose_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.callFragment(Constants.CONSTANTS);
            }
        });
        return view;
    }

    private void initData() {
        mainInterface = (MainInterface) getActivity();
        AsyncResponse ar = new AsyncResponse() {
            @Override
            public void onFailure(int errorCode) {
                CLog.e(TAG, "onFailure" + errorCode);
            }
        };
        sv = new Server(getActivity(), ar);


        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                CLog.d(TAG, "You got " + result.getString());
                et_phone_number.setText(Settings.safePhone);
                if (TextUtils.isEmpty(et_phone_number.getText()))
                    sv.pushInt("DISABLE_SWIPING", 3);
                else
                    sv.pushInt("DISABLE_SWIPING", -1);
            }
        };
        client = new Client(getActivity(), cr);
        client.gotMessages("SAFE_PHONE");
    }

    @Override
    public void onResume() {
        if (TextUtils.isEmpty(et_phone_number.getText()))
            sv.pushInt("DISABLE_SWIPING", 3);
        else
            sv.pushInt("DISABLE_SWIPING", -1);

        if (!TextUtils.isEmpty(Settings.safePhone))
            et_phone_number.setText(Settings.safePhone);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        client.release();
        super.onDestroy();
    }
}
