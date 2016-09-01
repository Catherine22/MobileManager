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
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.TelephoneUtils;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class NumberAddressQueryFragment extends Fragment {

    private static final String TAG = "NumberAddressQueryFragment";
    private EditText ed_phone;
    private TextView tv_result;

    public static NumberAddressQueryFragment newInstance() {
        return new NumberAddressQueryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_address_query, container, false);
        ed_phone = (EditText) view.findViewById(R.id.ed_phone);
        ed_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    queryAddress();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_result = (TextView) view.findViewById(R.id.tv_result);
        Button bt_numberAddressQuery = (Button) view.findViewById(R.id.bt_numberAddressQuery);
        bt_numberAddressQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAddress();
            }
        });
        return view;
    }

    /**
     * 输入手机号码查询归属地
     * 限中国地区号码
     * <p/>
     * 规则如下:
     * 1. 11码
     * 2. 13, 14, 15, 16开头
     */
    private void queryAddress() {
        if (!TextUtils.isEmpty(ed_phone.getText().toString())) {
            String number = ed_phone.getText().toString();
            String address = TelephoneUtils.getAddressFromNum(number);
            tv_result.setText(address);
        } else
            Toast.makeText(getActivity(), "您还没输入电话号码!", Toast.LENGTH_SHORT).show();
    }

}