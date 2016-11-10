package com.itheima.mobilesafe.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.adapter.AppInfoListAdapter;
import com.itheima.mobilesafe.db.dao.AppsLockDao;
import com.itheima.mobilesafe.ui.AdjustView;
import com.itheima.mobilesafe.ui.AutoResizeTextView;
import com.itheima.mobilesafe.ui.recycler_view.DividerItemDecoration;
import com.itheima.mobilesafe.utils.BroadcastActions;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.SystemInfoUtils;
import com.itheima.mobilesafe.utils.objects.AppInfo;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import tw.com.softworld.messagescenter.Client;
import tw.com.softworld.messagescenter.CustomReceiver;
import tw.com.softworld.messagescenter.Result;

/**
 * Created by Catherine on 2016/8/25.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */
public class AppsManagerFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "AppsManagerFragment";
    private final String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String romPath = Environment.getDataDirectory().getAbsolutePath();


    private AutoResizeTextView tv_sd_info, tv_rom_info;
    private RecyclerView rv_user_apps;
    private AppInfoListAdapter userAdapter;
    private LinearLayout ll_loading;
    private List<AppInfo> userInfo;
    private ItemTouchHelper userItemTouchHelper;
    private PopupWindow pw;
    private AppInfo onSelectedItem;
    private int onSelectedPosition;
    private Client client;
    private AppsLockDao dao;
    private ImageView iv_lock;
    private TextView tv_lock;

    public static AppsManagerFragment newInstance() {
        return new AppsManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_manager, container, false);

        tv_sd_info = (AutoResizeTextView) view.findViewById(R.id.tv_sd_info);
        tv_rom_info = (AutoResizeTextView) view.findViewById(R.id.tv_rom_info);
        rv_user_apps = (RecyclerView) view.findViewById(R.id.rv_user_apps);
        ll_loading = (LinearLayout) view.findViewById(R.id.ll_loading);

        //添加分割线
        rv_user_apps.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //设置布局管理器,可实现GridVIew
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        rv_user_apps.setLayoutManager(manager);

        CustomReceiver cr = new CustomReceiver() {
            @Override
            public void onBroadcastReceive(Result result) {
                PackageManager pm = getActivity().getPackageManager();
                try {
                    pm.getApplicationInfo(onSelectedItem.getPackageName(), PackageManager.GET_META_DATA);
                    fillInData();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        };
        client = new Client(getActivity(), cr);
        client.gotMessages(BroadcastActions.FINISHED_UNINSTALLING);

        dao = new AppsLockDao(getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fillInData();
    }

    @Override
    public void onDestroy() {
        if (pw != null) {
            pw.dismiss();
            pw = null;
        }
        client.release();
        super.onDestroy();
    }

    private void fillInData() {
        ll_loading.setVisibility(View.VISIBLE);

        DecimalFormat df = new DecimalFormat("#.##");
        //SD卡可用空间
        String sdSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(sdPath));
        String sdP = df.format((float) SystemInfoUtils.getAvailableSpace(sdPath) / (float) SystemInfoUtils.getTotalSpace(sdPath) * 100) + "%";
        //ROM可用空间
        String romSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(romPath));
        String romP = df.format((float) SystemInfoUtils.getAvailableSpace(romPath) / (float) SystemInfoUtils.getTotalSpace(romPath) * 100) + "%";

        tv_sd_info.setText(String.format(getActivity().getString(R.string.sd_size), sdSize, sdP));
        tv_rom_info.setText(String.format(getActivity().getString(R.string.rom_size), romSize, romP));

        CLog.d(TAG, SystemInfoUtils.getAppInfos(getActivity()).toString());

        new Thread() {
            public void run() {
                userInfo = new LinkedList<>();

                List<AppInfo> infos = SystemInfoUtils.getAppInfos(getActivity());
                for (AppInfo info : infos) {
                    info.setLocked(dao.find(info.getPackageName()));
                    userInfo.add(info);
                }

                userAdapter = new AppInfoListAdapter(getActivity(), userInfo);
                userAdapter.setOnItemClickLitener(new AppInfoListAdapter.OnItemClickLitener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (pw != null && pw.isShowing()) {
                            pw.dismiss();
                            pw = null;
                        }
                        onSelectedItem = userAdapter.getItem(position);
                        onSelectedPosition = position;

                        View v = View.inflate(getActivity(), R.layout.popup_app_manager, null);
                        LinearLayout ll_share = (LinearLayout) v.findViewById(R.id.ll_share);
                        ll_share.setOnClickListener(AppsManagerFragment.this);
                        LinearLayout ll_uninstall = (LinearLayout) v.findViewById(R.id.ll_uninstall);
                        ll_uninstall.setOnClickListener(AppsManagerFragment.this);
                        LinearLayout ll_open = (LinearLayout) v.findViewById(R.id.ll_open);
                        ll_open.setOnClickListener(AppsManagerFragment.this);
                        LinearLayout ll_lock = (LinearLayout) v.findViewById(R.id.ll_lock);
                        ll_lock.setOnClickListener(AppsManagerFragment.this);

                        iv_lock = (ImageView) v.findViewById(R.id.iv_lock);
                        tv_lock = (TextView) v.findViewById(R.id.tv_lock);
                        if (userAdapter.getItem(position).isLocked()) {
                            iv_lock.setImageDrawable(AdjustView.getDrawable(getActivity(), R.drawable.unlock));
                            tv_lock.setText("解锁");
                        } else {
                            iv_lock.setImageDrawable(AdjustView.getDrawable(getActivity(), R.drawable.lock));
                            tv_lock.setText("加密");
                        }


                        pw = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        int location[] = new int[2];//距离屏幕左边、上面的距离
                        view.getLocationInWindow(location);

                        //动画效果的播放必须窗体要有背景颜色(透明色也行)，否则不会生效
                        pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        pw.showAtLocation(view, Gravity.LEFT | Gravity.TOP, location[0], location[1]);

                        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                        scaleAnimation.setDuration(250);
                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
                        alphaAnimation.setDuration(250);

                        AnimationSet set = new AnimationSet(false);
                        set.addAnimation(scaleAnimation);
                        set.addAnimation(alphaAnimation);
                        v.startAnimation(set);
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        showDetailDialog(userAdapter.getItem(position));
                    }
                });
                rv_user_apps.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (pw != null && pw.isShowing()) {
                            pw.dismiss();
                            pw = null;
                        }
                    }
                });
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ll_loading.setVisibility(View.GONE);

                            if (userAdapter.getItemCount() == 0) {
                                rv_user_apps.setVisibility(View.GONE);
                            } else {
                                rv_user_apps.setVisibility(View.VISIBLE);
                            }

                            rv_user_apps.setAdapter(userAdapter);
//                            userItemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(userAdapter));
//                            userItemTouchHelper.attachToRecyclerView(rv_user_apps);

                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.tran_in);
                            rv_user_apps.startAnimation(animation);
                        }
                    });
                }
            }
        }.start();
    }

    private AutoResizeTextView tv_name, tv_package_name, tv_version, tv_first_installed_time, tv_last_update_time;

    private void showDetailDialog(AppInfo appInfo) {
        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_app_detail);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        tv_name = (AutoResizeTextView) alertDialog.findViewById(R.id.tv_name);
        tv_name.setText(appInfo.getName());
        tv_package_name = (AutoResizeTextView) alertDialog.findViewById(R.id.tv_package_name);
        tv_package_name.setText(appInfo.getPackageName());
        tv_version = (AutoResizeTextView) alertDialog.findViewById(R.id.tv_version);
        tv_version.setText(String.format(getString(R.string.app_version), appInfo.getVersionName()));
        tv_first_installed_time = (AutoResizeTextView) alertDialog.findViewById(R.id.tv_first_installed_time);
        tv_first_installed_time.setText(String.format(getString(R.string.app_first_installed_time), appInfo.getFirstInstallTime()));
        tv_last_update_time = (AutoResizeTextView) alertDialog.findViewById(R.id.tv_last_update_time);
        tv_last_update_time.setText(String.format(getString(R.string.app_last_update_time), appInfo.getLastUpdateTime()));
    }

    private void refresh(boolean refreshList) {
        DecimalFormat df = new DecimalFormat("#.##");
        //SD卡可用空间
        String sdSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(sdPath));
        String sdP = df.format((float) SystemInfoUtils.getAvailableSpace(sdPath) / (float) SystemInfoUtils.getTotalSpace(sdPath) * 100) + "%";
        //ROM可用空间
        String romSize = SystemInfoUtils.formatFileSize(SystemInfoUtils.getAvailableSpace(romPath));
        String romP = df.format((float) SystemInfoUtils.getAvailableSpace(romPath) / (float) SystemInfoUtils.getTotalSpace(romPath) * 100) + "%";

        tv_sd_info.setText(String.format(getActivity().getString(R.string.sd_size), sdSize, sdP));
        tv_rom_info.setText(String.format(getActivity().getString(R.string.rom_size), romSize, romP));

        if (refreshList)
            fillInData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_open:
                CLog.d(TAG, "open " + onSelectedItem.getPackageName());
                if (pw != null && pw.isShowing()) {
                    pw.dismiss();
                    pw = null;
                }
                openApp(onSelectedItem.getPackageName());
                break;
            case R.id.ll_share:
                CLog.d(TAG, "share " + onSelectedItem.getPackageName());
                if (pw != null && pw.isShowing()) {
                    pw.dismiss();
                    pw = null;
                }
                share(onSelectedItem);
                break;
            case R.id.ll_uninstall:
                CLog.d(TAG, "uninstall " + onSelectedItem.getPackageName());
                if (pw != null && pw.isShowing()) {
                    pw.dismiss();
                    pw = null;
                }
                uninstallApp(onSelectedItem);
                break;
            case R.id.ll_lock:
                CLog.d(TAG, "lock " + onSelectedItem.getPackageName());
                if (dao.find(onSelectedItem.getPackageName())) {
                    dao.remove(onSelectedItem.getPackageName());
                    userAdapter.setLock(onSelectedPosition, false);
                    iv_lock.setImageDrawable(AdjustView.getDrawable(getActivity(), R.drawable.lock));
                    tv_lock.setText("加密");
                } else {
                    dao.add(onSelectedItem.getPackageName());
                    userAdapter.setLock(onSelectedPosition, true);
                    iv_lock.setImageDrawable(AdjustView.getDrawable(getActivity(), R.drawable.unlock));
                    tv_lock.setText("解锁");
                }

                break;
        }
    }

    /**
     * 分享信息
     * 所有有在manifest中加入intent-filter（含action.SEND、CATEGORY_DEFAULT、text/plain）特性的应用都能收到次intent，
     * 也就是反之，在manifest中加入需要的intent-filter，可以拦截到对应的intent。
     *
     * @param appinfo 应用信息
     */
    private void share(AppInfo appinfo) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用「" + appinfo.getName() + "」软件");
        startActivity(intent);
    }

    /**
     * 卸载应用
     *
     * @param appinfo 应用信息
     */
    private void uninstallApp(AppInfo appinfo) {
//        <intent-filter>
//        <action android:name="android.intent.action.VIEW" />
//        <action android:name="android.intent.action.DELETE" />
//        <action android:name="android.intent.action.UNINSTALL_PACKAGE" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <data android:scheme="package" />
        if (appinfo.isUserApp()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setAction("android.intent.action.DELETE");
            intent.setAction("android.intent.action.UNINSTALL_PACKAGE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + appinfo.getPackageName()));
            getActivity().startActivityForResult(intent, Constants.UNINSTASLL_APP);
        } else
            Toast.makeText(getActivity(), "当前应用为系统应用，没法卸載", Toast.LENGTH_LONG).show();

    }

    /**
     * 开启应用
     *
     * @param packageName 应用包名
     */
    private void openApp(String packageName) {
        PackageManager pm = getActivity().getPackageManager();
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.MAIN");
//                intent.addCategory("android.intent.category.LAUNCHER");
//                //获取到全部具有action.MAIN与category.LAUNCHER（也就是具有启动能力）的应用
//                List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);

        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null)
            startActivity(intent);
        else
            Toast.makeText(getActivity(), "没法启动当前应用", Toast.LENGTH_LONG).show();

    }
}
