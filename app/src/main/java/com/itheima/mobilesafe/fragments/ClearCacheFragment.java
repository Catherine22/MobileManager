package com.itheima.mobilesafe.fragments;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.SystemInfoUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Catherine on 2016/11/11.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class ClearCacheFragment extends Fragment {

    private static final String TAG = "ClearCacheFragment";

    public static ClearCacheFragment newInstance() {
        return new ClearCacheFragment();
    }

    private TextView tv_status;
    private ProgressBar pb;
    private LinearLayout ll_content;
    private PackageManager pm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clear_cache, container, false);

        tv_status = (TextView) view.findViewById(R.id.tv_status);
        pb = (ProgressBar) view.findViewById(R.id.pb);
        ll_content = (LinearLayout) view.findViewById(R.id.ll_content);
        scanCache();
        return view;
    }

    private void scanCache() {
        pm = getActivity().getPackageManager();
        new Thread() {
            public void run() {
                List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
                //Reflection
                Method[] methods = PackageManager.class.getDeclaredMethods();
                Method getPackageSizeInfo = null;
                for (Method m : methods) {
//                    CLog.d(TAG, m.getName());
                    if ("getPackageSizeInfo".equals(m.getName())) {
                        getPackageSizeInfo = m;
                    }
                }
                pb.setMax(installedPackages.size());
                for (int i = 0; i < installedPackages.size(); i++) {
                    try {
                        getPackageSizeInfo.invoke(pm, installedPackages.get(i).packageName, new SratsObserver());
                        pb.setProgress(i + 1);
                        Thread.sleep(200);

                        if (i == installedPackages.size() - 1) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_status.setText(String.format("扫描完毕！"));
                                    }
                                });
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }

    private class SratsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(final PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cacheSize = pStats.cacheSize;
            long codeSize = pStats.codeSize;
            long dataSize = pStats.dataSize;
            ApplicationInfo info = null;
            try {
                info = pm.getApplicationInfo(pStats.packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            final String name = (String) info.loadLabel(pm);

            CLog.d(TAG, pStats.packageName);
            CLog.d(TAG, "cacheSize " + SystemInfoUtils.formatFileSize(cacheSize));
            CLog.d(TAG, "codeSize " + SystemInfoUtils.formatFileSize(codeSize));
            CLog.d(TAG, "dataSize " + SystemInfoUtils.formatFileSize(dataSize));
            CLog.d(TAG, "--------------------------------");

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_status.setText(String.format("正在扫描%s", name));
                        if (cacheSize > 0) {
                            View v = View.inflate(getActivity(), R.layout.list_item_cache, null);
                            TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
                            TextView tv_cacheSize = (TextView) v.findViewById(R.id.tv_cache_size);
                            ImageView iv_clear = (ImageView) v.findViewById(R.id.iv_clear);
                            iv_clear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Not work, due to lacking android.permission.DELETE_CACHE_FILES
                                    //DELETE_CACHE_FILES which allows an application to delete cache files is Not for use by third-party applications.

                                    try {
                                        //Reflection
                                        Method deleteApplicationCacheFiles = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
                                        deleteApplicationCacheFiles.invoke(pm, pStats.packageName, new DataObserver());
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            tv_name.setText(name);
                            tv_cacheSize.setText(String.format("缓存大小：%s", SystemInfoUtils.formatFileSize(cacheSize)));
                            ll_content.addView(v, 0);
                        }
                    }
                });
            }
        }
    }

    private class DataObserver extends IPackageDataObserver.Stub {
        public void onRemoveCompleted(final String packageName, final boolean succeeded) {
            CLog.d(TAG, "DataObserver " + succeeded);
        }
    }
}
