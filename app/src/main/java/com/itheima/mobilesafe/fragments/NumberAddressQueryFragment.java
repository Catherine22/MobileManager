package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.db.dao.NumberAddressDao;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class NumberAddressQueryFragment extends Fragment {

    private static final String TAG = "NumberAddressQueryFragment";
    private EditText ed_phone;
    private TextView tv_result;
    private Button bt_numberAddressQuery;

    public static NumberAddressQueryFragment newInstance() {
        return new NumberAddressQueryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_address_query, container, false);
        ed_phone = (EditText) view.findViewById(R.id.ed_phone);
        tv_result = (TextView) view.findViewById(R.id.tv_result);
        bt_numberAddressQuery = (Button) view.findViewById(R.id.bt_numberAddressQuery);
        bt_numberAddressQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(ed_phone.getText().toString())) {
                    String number = ed_phone.getText().toString();
                    NumberAddressDao nad = new NumberAddressDao();
                    String address = nad.queryNumber(number);
                    if (!TextUtils.isEmpty(address))
                        tv_result.setText(address);
                    else
                        tv_result.setText("查无此号");
                } else
                    Toast.makeText(getActivity(), "您还没输入电话号码!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}