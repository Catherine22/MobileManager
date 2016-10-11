package com.itheima.mobilesafe.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.BlacklistAdapter;
import com.itheima.mobilesafe.db.dao.BlacklistDao;
import com.itheima.mobilesafe.utils.DaoFactory;
import com.itheima.mobilesafe.db.dao.DaoConstants;
import com.itheima.mobilesafe.ui.recycler_view.DividerItemDecoration;
import com.itheima.mobilesafe.ui.recycler_view.ItemTouchCallback;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.objects.BlockedCaller;

import java.util.List;

/**
 * Created by Catherine on 2016/9/20.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class BlacklistFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BlacklistFragment";
    private RecyclerView rv_blacklist;
    private TextView tv_no_data, tv_add;
    private ItemTouchHelper itemTouchHelper;
    private BlacklistDao dao;
    private BlacklistAdapter adapter;
    private List<BlockedCaller> blockedCallers;

    public static TestFragment newInstance() {
        return new TestFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blacklist, container, false);

        DaoFactory daoF = new DaoFactory();
        dao = (BlacklistDao) daoF.createDao(getActivity(), DaoConstants.BLACKLIST);
        blockedCallers = dao.queryAll();

        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        tv_add = (TextView) view.findViewById(R.id.tv_add);
        tv_add.setOnClickListener(this);
        rv_blacklist = (RecyclerView) view.findViewById(R.id.rv_blacklist);
        if (blockedCallers != null) {
            tv_no_data.setVisibility(View.INVISIBLE);

            rv_blacklist.addItemDecoration(new DividerItemDecoration(
                    getActivity(), DividerItemDecoration.VERTICAL_LIST));
            rv_blacklist.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            adapter = new BlacklistAdapter(getActivity(), blockedCallers, new BlacklistAdapter.OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    CLog.d(TAG, "onItemClick " + adapter.getList().get(position).toString());
                    showModifyDialog(adapter.getList().get(position));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                    CLog.d(TAG, "onItemLongClick " + adapter.getList().get(position).toString());
                }

                @Override
                public void onItemSwap(int fromPosition, int toPosition) {
                    CLog.d(TAG, "onItemSwap " + "swap " + fromPosition + " for " + toPosition);
                    BlockedCaller item1 = adapter.getList().get(toPosition);
                    BlockedCaller item2 = adapter.getList().get(fromPosition);
                    dao.swap(item1, item2, new BlacklistDao.OnResponse() {
                        @Override
                        public void OnFinish() {
                            adapter.updateDataSet(dao.queryAll());
                        }

                        @Override
                        public void onFail(int what, String errorMessage) {
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onItemDismiss(int position, BlockedCaller item) {
                    CLog.d(TAG, "onItemDismiss " + position + " " + item.getNumber());
                    dao.remove(item.getNumber());
                }
            });
            rv_blacklist.setAdapter(adapter);
            itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(adapter));
            itemTouchHelper.attachToRecyclerView(rv_blacklist);

        } else
            tv_no_data.setVisibility(View.VISIBLE);

        return view;
    }

    //Dialog components
    private EditText et_name, et_phone;
    private Button bt_ok, bt_cancel;
    private Spinner s_mode;
    private Dialog alertDialog;
    private int mode;
    private int dialogType;
    private final static int SETUP = 0;
    private final static int MODIFY = 1;

    private void showSetupDialog() {
        dialogType = SETUP;
        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_setup_blacklist);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        et_name = (EditText) alertDialog.findViewById(R.id.et_name);
        et_phone = (EditText) alertDialog.findViewById(R.id.et_phone);
        s_mode = (Spinner) alertDialog.findViewById(R.id.s_mode);
        s_mode.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, BlacklistDao.MODES));
        s_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mode = BlacklistDao.MODE_BOTH_BLOCKED;
            }
        });
        bt_ok = (Button) alertDialog.findViewById(R.id.bt_setup_ok);
        bt_ok.setOnClickListener(this);
        bt_cancel = (Button) alertDialog.findViewById(R.id.bt_setup_cancel);
        bt_cancel.setOnClickListener(this);
    }


    private void showModifyDialog(final BlockedCaller caller) {
        dialogType = MODIFY;
        alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_setup_blacklist);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        et_name = (EditText) alertDialog.findViewById(R.id.et_name);
        et_name.setText(caller.getName());
        et_phone = (EditText) alertDialog.findViewById(R.id.et_phone);
        et_phone.setText(caller.getNumber());

        s_mode = (Spinner) alertDialog.findViewById(R.id.s_mode);
        s_mode.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, BlacklistDao.MODES));
        s_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mode = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mode = caller.getMODE();
            }
        });
        //set spinner default
        s_mode.setSelection(caller.getMODE());
        bt_ok = (Button) alertDialog.findViewById(R.id.bt_setup_ok);
        bt_ok.setOnClickListener(this);
        bt_cancel = (Button) alertDialog.findViewById(R.id.bt_setup_cancel);
        bt_cancel.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        CLog.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        CLog.d(TAG, "onResume");
        super.onResume();
    }

    private BlockedCaller item;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_add:
                showSetupDialog();
                break;
            case R.id.bt_setup_ok:
                item = new BlockedCaller();
                item.setName(et_name.getText().toString());
                item.setNumber(et_phone.getText().toString());
                item.setMODE(mode);
                if (TextUtils.isEmpty(item.getName()))
                    Toast.makeText(getActivity(), "姓名不得为空", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(item.getNumber()))
                    Toast.makeText(getActivity(), "号码不得为空", Toast.LENGTH_SHORT).show();
                else {

                    if (dialogType == SETUP) {
                        dao.add(item, new BlacklistDao.OnResponse() {
                            @Override
                            public void OnFinish() {
                                adapter.addItem(item);
                                alertDialog.dismiss();
                            }

                            @Override
                            public void onFail(int what, String errorMessage) {
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    } else {
                        dao.modify(item, new BlacklistDao.OnResponse() {
                            @Override
                            public void OnFinish() {
                                adapter.updateDataSet(dao.queryAll());
                                alertDialog.dismiss();
                            }

                            @Override
                            public void onFail(int what, String errorMessage) {
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        });
                    }
                }
                break;
            case R.id.bt_setup_cancel:
                alertDialog.dismiss();
                break;
        }
    }
}