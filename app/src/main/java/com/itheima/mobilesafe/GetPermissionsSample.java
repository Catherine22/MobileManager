package com.itheima.mobilesafe;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Catherine on 2017/3/6.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class GetPermissionsSample extends FragmentActivity {
    private final static String TAG = "GetPermissionsSample";
    private OnRequestPermissionsListener listener;

    private interface OnRequestPermissionsListener {
        /**
         * 用户开启权限
         */
        void onGranted();

        /**
         * 用户拒绝打开权限
         */
        void onDenied(@Nullable List<String> deniedPermissions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                CLog.d(TAG, "onGranted");
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions) {
                CLog.d(TAG, "onDenied");
                StringBuilder context = new StringBuilder();
                if (deniedPermissions != null) {
                    for (String p : deniedPermissions) {
                        if (Manifest.permission.INTERNET.equals(p)) {
                            context.append("网络、");
                        } else if (Manifest.permission.READ_PHONE_STATE.equals(p)) {
                            context.append("电话、");
                        } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(p)) {
                            context.append("存储、");
                        }
                    }
                }
                context.deleteCharAt(context.length() - 1);


                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(GetPermissionsSample.this);
                myAlertDialog.setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("注意")
                        .setMessage(String.format("您目前未授权%1$s存取权限，未授权将造成程式无法执行，是否开启权限？", context.toString()))
                        .setNegativeButton("继续关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setPositiveButton("确定开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                        finish();
                    }
                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                myAlertDialog.show();
            }
        });
    }

    private final int GRANTED_SAW = 0x0001;     //同意特殊权限(SYSTEM_ALERT_WINDOW)
    private final int GRANTED_WS = 0x0010;      //同意特殊权限(WRITE_SETTINGS)
    private int requestSpec = 0x0000;           //需要的特殊权限
    private int grantedSpec = 0x0000;           //已取得的特殊权限
    private int confirmedSpec = 0x0000;         //已询问的特殊权限
    private List<String> deniedPermissionsList; //被拒绝的权限
    private final int ACCESS_PERMISSION = 1001;

    /**
     * 要求用户打开权限,仅限android 6.0 以上
     * <p/>
     * SYSTEM_ALERT_WINDOW 和 WRITE_SETTINGS, 这两个权限比较特殊，
     * 不能通过代码申请方式获取，必须得用户打开软件设置页手动打开，才能授权。
     *
     * @param permissions 手机权限 e.g. Manifest.permission.ACCESS_FINE_LOCATION
     * @param listener    此变量implements事件的接口,负责传递信息
     */
    public void getPermissions(String[] permissions, OnRequestPermissionsListener listener) {
        this.listener = listener;
        deniedPermissionsList = new LinkedList<>();

        for (String p : permissions) {
            if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                requestSpec |= GRANTED_SAW;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(GetPermissionsSample.this)) {
                    grantedSpec &= GRANTED_SAW;
                } else
                    grantedSpec |= GRANTED_SAW;
            } else if (p.equals(Manifest.permission.WRITE_SETTINGS)) {
                requestSpec |= GRANTED_WS;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(GetPermissionsSample.this)) {
                    grantedSpec &= GRANTED_WS;
                } else
                    grantedSpec |= GRANTED_WS;
            } else if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionsList.add(p);
            }
        }

        if (requestSpec != grantedSpec) {
            getASpecPermission(requestSpec | grantedSpec);
        } else {// Granted all of the special permissions
            if (deniedPermissionsList.size() != 0) {
                //Ask for the permissions
                String[] deniedPermissions = new String[deniedPermissionsList.size()];
                for (int i = 0; i < deniedPermissionsList.size(); i++) {
                    deniedPermissions[i] = deniedPermissionsList.get(i);
                }
                ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
            } else {
                listener.onGranted();

                requestSpec = 0x0000;
                grantedSpec = 0x0000;
                confirmedSpec = 0x0000;
                deniedPermissionsList = null;
            }
        }
    }

    private void getASpecPermission(int permissions) {
        CLog.d(TAG, "getSpec " + permissions);
        if ((permissions & GRANTED_SAW) == GRANTED_SAW) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + GetPermissionsSample.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_OVERLAY);
        }

        if ((permissions & GRANTED_WS) == GRANTED_WS) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + GetPermissionsSample.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CLog.d(TAG, "onRequestPermissionsResult");

        List<String> deniedResults = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedResults.add(permissions[i]);
            }
        }

        if ((requestSpec ^ grantedSpec) == GRANTED_WS) {
            deniedResults.add("Manifest.permission.WRITE_SETTINGS");
        }

        if ((requestSpec ^ grantedSpec) == GRANTED_SAW) {
            deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");
        }

        if (deniedResults.size() != 0) {
            listener.onDenied(deniedResults);
        } else {
            listener.onGranted();
        }

        requestSpec = 0x0000;
        grantedSpec = 0x0000;
        confirmedSpec = 0x0000;
        deniedPermissionsList = null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CLog.d(TAG, "request:" + requestCode + "/resultCode" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.PERMISSION_OVERLAY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    confirmedSpec |= GRANTED_SAW;

                    if (!android.provider.Settings.canDrawOverlays(this)) {
                        //denied
                        grantedSpec &= GRANTED_SAW;
                    } else {
                        grantedSpec |= GRANTED_SAW;
                    }
                    if (confirmedSpec == requestSpec) {
                        if (deniedPermissionsList.size() != 0) {
                            //Ask for the permissions
                            String[] deniedPermissions = new String[deniedPermissionsList.size()];
                            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                                deniedPermissions[i] = deniedPermissionsList.get(i);
                            }
                            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec ^ grantedSpec) == GRANTED_WS) {
                                deniedResults.add("Manifest.permission.WRITE_SETTINGS");
                            }

                            if ((requestSpec ^ grantedSpec) == GRANTED_SAW) {
                                deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");
                            }

                            if (deniedResults.size() != 0) {
                                listener.onDenied(deniedResults);
                            } else {
                                listener.onGranted();
                            }

                            requestSpec = 0x0000;
                            grantedSpec = 0x0000;
                            confirmedSpec = 0x0000;
                            deniedPermissionsList = null;
                        }
                    }
                }
                break;
            case Constants.PERMISSION_WRITE_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    confirmedSpec |= GRANTED_WS;
                    if (!android.provider.Settings.System.canWrite(this)) {
                        //denied
                        grantedSpec &= GRANTED_WS;
                    } else {
                        grantedSpec |= GRANTED_WS;
                    }
                    if (confirmedSpec == requestSpec) {
                        if (deniedPermissionsList.size() != 0) {
                            //Ask for the permissions
                            String[] deniedPermissions = new String[deniedPermissionsList.size()];
                            for (int i = 0; i < deniedPermissionsList.size(); i++) {
                                deniedPermissions[i] = deniedPermissionsList.get(i);
                            }
                            ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
                        } else {
                            List<String> deniedResults = new ArrayList<>();
                            if ((requestSpec ^ grantedSpec) == GRANTED_WS) {
                                deniedResults.add("Manifest.permission.WRITE_SETTINGS");
                            }

                            if ((requestSpec ^ grantedSpec) == GRANTED_SAW) {
                                deniedResults.add("Manifest.permission.SYSTEM_ALERT_WINDOW");
                            }

                            if (deniedResults.size() != 0) {
                                listener.onDenied(deniedResults);
                            } else {
                                listener.onGranted();
                            }

                            requestSpec = 0x0000;
                            grantedSpec = 0x0000;
                            confirmedSpec = 0x0000;
                            deniedPermissionsList = null;
                        }
                    }
                }
                break;
        }
    }
}
