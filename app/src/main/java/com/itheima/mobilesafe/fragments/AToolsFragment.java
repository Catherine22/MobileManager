package com.itheima.mobilesafe.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.interfaces.MainInterface;
import com.itheima.mobilesafe.utils.Constants;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AToolsFragment extends Fragment {

    private static final String TAG = "AToolsFragment";
    private TextView tv_number_query;
    private MainInterface mainInterface;

    public static AToolsFragment newInstance() {
        return new AToolsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a_tools, container, false);
        mainInterface = (MainInterface) getActivity();

        tv_number_query = (TextView) view.findViewById(R.id.tv_number_query);
        tv_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.callFragment(Constants.NUM_ADDRESS_QUERY_FRAG);
            }
        });
        return view;
    }
}
