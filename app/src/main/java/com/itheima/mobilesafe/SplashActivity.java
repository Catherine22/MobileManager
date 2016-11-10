package com.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.designpattern.abstract_factory.CarFactory;
import com.itheima.mobilesafe.designpattern.builder.OldStyleRobotBuilder;
import com.itheima.mobilesafe.designpattern.builder.Robot;
import com.itheima.mobilesafe.designpattern.builder.RobotDirector;
import com.itheima.mobilesafe.designpattern.factory.ColorFactory;
import com.itheima.mobilesafe.designpattern.singleton.BillPughSingleton;
import com.itheima.mobilesafe.designpattern.singleton.EagerInitializingSingleton;
import com.itheima.mobilesafe.designpattern.singleton.EnumSingleton;
import com.itheima.mobilesafe.designpattern.singleton.LazyInitializingSingleton;
import com.itheima.mobilesafe.designpattern.singleton.SafeLazyInitializingSingleton;
import com.itheima.mobilesafe.utils.CLog;
import com.itheima.mobilesafe.utils.Constants;
import com.itheima.mobilesafe.utils.Settings;
import com.itheima.mobilesafe.utils.SpNames;
import com.itheima.mobilesafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {

    protected static final String TAG = "SplashActivity";
    protected static final int SHOW_UPDATE_DIALOG = 0;
    protected static final int ENTER_HOME = 1;
    protected static final int URL_ERROR = 2;
    protected static final int NETWORK_ERROR = 3;
    protected static final int JSON_ERROR = 4;
    private String description;
    private TextView tv_update_info;
    /**
     * 新版本的下载地址
     */
    private String apkurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
        initComponents();
        initSettings();

//        testSingleton();
//        testFactory();
//        testAbstractFactory();
        testBuilder();


        boolean update = sp.getBoolean(SpNames.update, false);
        if (update) {
            // 检查升级
            checkUpdate();
        } else {
            //自动升级已经关闭
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    //进入主页面
                    enterHome();

                }
            }, 2000);

        }

        createShortcut();
    }

    //————————————————————————Design Pattern start————————————————————————
    private BillPughSingleton bInstance1, bInstance2;
    private EagerInitializingSingleton eInstance1, eInstance2;
    private EnumSingleton eunm1, eunm2;
    private LazyInitializingSingleton lInstance1, lInstance2;
    private SafeLazyInitializingSingleton sInstance1, sInstance2;

    private void testSingleton() {
        bInstance1 = BillPughSingleton.getInstance();
        bInstance1.print();

        eInstance1 = EagerInitializingSingleton.getInstance();
        eInstance1.print();

        eunm1 = EnumSingleton.INSTANCE;
        eInstance1.print();

        lInstance1 = LazyInitializingSingleton.getInstance();
        lInstance1.print();

        sInstance1 = SafeLazyInitializingSingleton.getInstance();
        sInstance1.print();

        Thread t = new Thread(r);
        t.start();
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            bInstance2 = BillPughSingleton.getInstance();
            eInstance2 = EagerInitializingSingleton.getInstance();
            eunm2 = EnumSingleton.INSTANCE;
            lInstance2 = LazyInitializingSingleton.getInstance();
            sInstance2 = SafeLazyInitializingSingleton.getInstance();

            if (bInstance1 == bInstance2)
                CLog.d("Singleton", "BillPughSingleton 同一个实例");
            else
                CLog.e("Singleton", "BillPughSingleton 不同实例");

            if (eInstance1 == eInstance2)
                CLog.d("Singleton", "EagerInitializingSingleton 同一个实例");
            else
                CLog.e("Singleton", "EagerInitializingSingleton 不同实例");

            if (eunm1 == eunm2)
                CLog.d("Singleton", "EnumSingleton 同一个实例");
            else
                CLog.e("Singleton", "EnumSingleton 不同实例");

            if (lInstance1 == lInstance2)
                CLog.d("Singleton", "LazyInitializingSingleton 同一个实例");
            else
                CLog.e("Singleton", "LazyInitializingSingleton 不同实例");

            if (sInstance1 == sInstance2)
                CLog.d("Singleton", "SafeLazyInitializingSingleton 同一个实例");
            else
                CLog.e("Singleton", "SafeLazyInitializingSingleton 不同实例");
        }
    };

    private void testFactory() {
        ColorFactory cf = new ColorFactory();
        cf.getColor(ColorFactory.BLUE).onDraw();
        cf.getColor(ColorFactory.RED).onDraw();
    }

    private void testAbstractFactory() {
        CarFactory cf = new CarFactory();
        cf.getColor(CarFactory.RED).onDraw();
        cf.getBrand(CarFactory.BENTLEY).show();
    }

    private void testBuilder() {
        RobotDirector rd = new RobotDirector(new OldStyleRobotBuilder());
        rd.makeRobot();
        Robot robot = rd.getRobot();
        CLog.d("Builder", robot.getArms());
        CLog.d("Builder", robot.getHead());
        CLog.d("Builder", robot.getLegs());
        CLog.d("Builder", robot.getTorso());
    }

    //————————————————————————Design Pattern end————————————————————————
    private void initComponents() {
        TextView tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号" + getVersionName());
        tv_update_info = (TextView) findViewById(R.id.tv_update_info);
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(500);
        findViewById(R.id.rl_root_splash).startAnimation(aa);
    }

    /**
     * 建立快捷方式
     * 广播的意图须包含3项信息：1.名称 2.图标 3.intent的条件过滤
     */
    private void createShortcut() {
        SharedPreferences sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
        boolean firstOpen = sp.getBoolean(SpNames.first_open, true);
        if (firstOpen) {    //避免重复创建
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(SpNames.first_open, false);
            editor.apply();

            //建立一般快捷图标（环保，因为应用卸载后系统自动删除）
            Intent shortcutIntent = new Intent();
            shortcutIntent.setAction("android.intent.action.MAIN");//只要apps内的intent-filter内包含此action都符合
            shortcutIntent.addCategory("android.intent.category.LAUNCHER");//所有可以执行的apps都符合
            shortcutIntent.setClassName(getPackageName(), "com.itheima.mobilesafe.SplashActivity");//指定特定目标

            //如果想创建其他快捷图标，比如点击后直接导向HomeActivity而非启动页面，
            //需要在manifest为HomeActivity加入自订的intent-filter，並指定給EXTRA_SHORTCUT_INTENT
            //应用卸载后不会删除
            Intent homeShortcutIntent = new Intent();
            homeShortcutIntent.setAction("com.itheima.home");
            homeShortcutIntent.addCategory("android.intent.category.DEFAULT");
            homeShortcutIntent.putExtra("OPEN_PAGE", Constants.TASK_FRAG);//自定义信息，用来导向特定页面

            //发送广播的意图，让系统告诉桌面应用创建图标
            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士");
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            sendBroadcast(intent);

            //发送广播的意图，创建另一个快捷图标，直接导向特定页面
            Intent intent2 = new Intent();
            intent2.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            intent2.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机小卫士TASK");
            intent2.putExtra(Intent.EXTRA_SHORTCUT_INTENT, homeShortcutIntent);
            intent2.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            sendBroadcast(intent2);
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:// 显示升级的对话框
                    CLog.i(TAG, "显示升级的对话框");
                    showUpdateDialog();
                    break;
                case ENTER_HOME:// 进入主页面
                    enterHome();
                    break;

                case URL_ERROR:// URL错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();

                    break;

                case NETWORK_ERROR:// 网络异常
                    enterHome();
                    Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    break;

                case JSON_ERROR:// JSON解析出错
                    enterHome();
                    Toast.makeText(SplashActivity.this, "JSON解析出错", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }

    };

    /**
     * 检查是否有新版本，如果有就升级
     */
    private void checkUpdate() {

        new Thread() {
            public void run() {
                // URLhttp://192.168.1.254:8080/updateinfo.html

                Message mes = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {

                    URL url = new URL(getString(R.string.serverurl));
                    // 联网
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        // 联网成功
                        InputStream is = conn.getInputStream();
                        // 把流转成String
                        String result = StreamUtils.toString(is);
                        CLog.i(TAG, "联网成功了" + result);
                        // json解析
                        JSONObject obj = new JSONObject(result);
                        // 得到服务器的版本信息
                        String version = (String) obj.get("version");

                        description = (String) obj.get("description");
                        apkurl = (String) obj.get("apkurl");

                        // 校验是否有新版本
                        if (getVersionName().equals(version)) {
                            // 版本一致，没有新版本，进入主页面
                            mes.what = ENTER_HOME;
                        } else {
                            // 有新版本，弹出一升级对话框
                            mes.what = SHOW_UPDATE_DIALOG;

                        }

                    }

                } catch (MalformedURLException e) {

                    mes.what = URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {

                    mes.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {

                    e.printStackTrace();
                    mes.what = JSON_ERROR;
                } finally {

                    long endTime = System.currentTimeMillis();
                    // 我们花了多少时间
                    long dTime = endTime - startTime;
                    // 2000
                    if (dTime < 2000) {
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }

                    handler.sendMessage(mes);
                }

            }
        }.start();

    }

    /**
     * 弹出升级对话框
     */
    protected void showUpdateDialog() {
        //this = Activity.this
        AlertDialog.Builder builder = new Builder(SplashActivity.this);
        builder.setTitle("提示升级");
//		builder.setCancelable(false);//强制升级
        builder.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

                //进入主页面
                enterHome();
                dialog.dismiss();

            }
        });
        builder.setMessage(description);
        builder.setPositiveButton("立刻升级", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载APK，并且替换安装
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // sdcard存在
                    // afnal
//                    FinalHttp finalhttp = new FinalHttp();
//                    finalhttp.download(apkurl, Environment
//                                    .getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe2.0.apk",
//                            new AjaxCallBack<File>() {
//
//                                @Override
//                                public void onFailure(Throwable t, int errorNo,
//                                                      String strMsg) {
//                                    t.printStackTrace();
//                                    Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
//                                    super.onFailure(t, errorNo, strMsg);
//                                }
//
//                                @Override
//                                public void onLoading(long count, long current) {
//
//                                    super.onLoading(count, current);
//                                    tv_update_info.setVisibility(View.VISIBLE);
//                                    //当前下载百分比
//                                    int progress = (int) (current * 100 / count);
//                                    tv_update_info.setText("下载进度：" + progress + "%");
//                                }
//
//                                @Override
//                                public void onSuccess(File t) {
//
//                                    super.onSuccess(t);
//                                    installAPK(t);
//                                }
//
//                                /**
//                                 * 安装APK
//                                 * @param t
//                                 */
//                                private void installAPK(File t) {
//                                    Intent intent = new Intent();
//                                    intent.setAction("android.intent.action.VIEW");
//                                    intent.addCategory("android.intent.category.DEFAULT");
//                                    intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
//
//                                    startActivity(intent);
//
//                                }
//
//
//                            });
                } else {
                    Toast.makeText(getApplicationContext(), "没有sdcard，请安装上在试",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("下次再说", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                enterHome();// 进入主页面
            }
        });
        builder.show();

    }

    protected void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        // 关闭当前页面
        finish();
        //必须要在finish()或startActivity()后面执行
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    /**
     * 得到应用程序的版本名称
     */

    private String getVersionName() {
        // 用来管理手机的APK
        PackageManager pm = getPackageManager();

        try {
            // 得到知道APK的功能清单文件
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {

            e.printStackTrace();
            return "";
        }
    }

    /**
     * 取得设备信息
     * 初始化数据库
     */
    private void initSettings() {
        //取得屏幕信息
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Settings.DISPLAY_WIDTH_PX = metrics.widthPixels;
        Settings.DISPLAY_HEIGHT_PX = metrics.heightPixels;

        //取得安全碼信息
        SharedPreferences sp = getSharedPreferences(SpNames.FILE_CONFIG, MODE_PRIVATE);
        Settings.safePhone = sp.getString(SpNames.safe_phone, "");


        copyDb("address.db");
        copyDb("descpvirus.db");
    }

    /**
     * 拷贝数据库到/data/data/包名/files目录下
     * @param fileName
     */
    private void copyDb(String fileName) {
        try {
            File file = new File(getFilesDir(), fileName);
            if (file.exists() && file.length() > 0) {//文件已存在,且长度正常,就不需要再拷贝了
//                CLog.d(TAG, Constants.DB_NAME + " 文件已存在");
            } else {
                InputStream is = getAssets().open(fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
