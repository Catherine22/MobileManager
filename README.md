# MobileManager

## 功能导航
| 功能 | 代码 |
| ------------ | ------------- |
| 号码归属地查询 | [NumberAddressQueryFragment] |
| 设置黑名单 | [BlacklistFragment] |
| 进程管理、删除 | [TaskFragment] |
| 应用卸载、启动、分享、应用锁 | [AppsManagerFragment] |
| 监听来电，显示号码归属地悬浮窗 | [AddressService] |
| 短信、通话拦截 | [BlockCallsSmsService] |
| 取得GPS位置 | [GPSService] |
| 拦截短信后，利用管理员权限卸载应用、设置锁屏、清除数据 | [SMSReceiver], [MyAdminManager] |
| 数据备份、还原（短信） | [SmsBackup] |
| 看门狗 | [WatchDogService] |
| 病毒查杀 | [AntiVirusFragment] |
| 清除缓存 | [ClearCacheFragment] |
| 应用上传、下载流量统计 | [TrafficManagerFragment] |
| Facebook Account kit 登入 |[SettingsFragment], [AccountKitUtils]|
| Deep linking（以branch.io实现） |[HomeActivity]|
| 利用NDK存取authentication key |[SplashActivity]|

## 自定义控件
#### 自定义控件属性      
  - [fragment_settings]
  - [SettingItemView]
  - [attrs]     

#### [自定义Toast]

#### Widgets
  - 定义一个类[MyAppWidgetProvider]继承AppWidgetProvider
  - 定义此widget显示xml文档[my_appwidget_info]于res/xml/目录下
  - 注册service [AutoCleanService] 监听widget点击事件（service注册的时间点应为widget启用时，详见[MyAppWidgetProvider]
  - 视情况可自定义广播，注册接收者响应widget点击事件
  - Manifest配置
```xml
<application>
<receiver
            android:name=".ui.MyAppWidgetProvider"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
                <action android:name="android.appwidget.action.APPWIDGET_ENABLED" />
                <action android:name="android.appwidget.action.APPWIDGET_DELETED" />
                <action android:name="android.appwidget.action.APPWIDGET_DISABLED" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/my_appwidget_info" />
        </receiver>

<service android:name=".services.AutoCleanService" />

<receiver android:name=".receivers.WidgetReceiver">
            <intent-filter>
                <action android:name="com.itheima.mobliesafe.KILLALL" />
            </intent-filter>
        </receiver>
</application>
```

#### 自定义对话框
```JAVA
    /**
     * 输入密码对话框
     */
    private void showPwdDialog() {
        alertDialog = new Dialog(this);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.dialog_type_password);
        //设置dialog背景透明
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        et_type_pwd = (EditText) alertDialog.findViewById(R.id.et_type_pwd);
        bt_ok = (Button) alertDialog.findViewById(R.id.bt_type_ok);
        bt_ok.setOnClickListener(this);
        bt_cancel = (Button) alertDialog.findViewById(R.id.bt_type_cancel);
        bt_cancel.setOnClickListener(this);
    }
```
  - [HomeActivity]

#### 悬浮窗体
```JAVA
View v = View.inflate(getActivity(), R.layout.popup_app_manager, null);
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
```
  -  [AppsManagerFragment]

#### 自定义对话框

#### 可滑动、上下交换的RecyclerView
  - [BlacklistFragment]
  - [BlacklistAdapter]      

  - [TaskFragment]
  - [TaskInfoListAdapter]

#### 两种在UI线程更新介面的方法
```JAVA
getActivity().runOnUiThread(new Runnable() {
    @Override
    public void run() {
        tv_title.setText("刷新textview");
    }
});
```

```JAVA
private void sendMessage() {
  Message msg = Message.obtain();
  msg.obj = “你好啊”;
  msg.what = 0;
  handler.sendMessage(msg);
}


private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                String str = (String) msg.obj;
                tv_title.setText(str);
            break;
        }
    }
};
```

## 其他应用

#### MD5加密
  - [Encryption]

#### 多渠道打包与自动隐藏debug log
  - [build.gradle]之productFlavors{...}
  - [MyApplication]
  - [CLog]

#### Http网络请求（含JSON解析）
  - [NetUtils]
  - [NetAsyncTask]

#### 进程、服务管理
  - [SystemInfoUtils]
  - [ServiceUtils]

#### 开机后自动启动、检查SIM卡       
  - 在[AndroidManifest]添加权限与注册receiver
```JAVA
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<!--使用WakeLock使Android应用程序保持后台唤醒-->
<uses-permission android:name="android.permission.WAKE_LOCK" />

<application
    <receiver android:name=".receivers.BootCompletedReceiver">
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
        </intent-filter>
    </receiver>
</application>
```
  - [BootCompletedReceiver]

#### 获取经纬度，中国境内已修正火星坐标偏移
  - [GPSService]（只有在[SMSReceiver]中收到来自安全码的SMS信息才会触发）
  - [火星坐标偏移算法]

#### DevicePolicyManager, 设备管理器
  - [MyAdminManager]（锁屏、解锁屏幕、修改屏幕密码、恢复出厂设置、卸载应用）
  - 需注册Receiver [MyDeviceAdminReceiver]，并添加资源文件 [device_admin_sample]，详见[device-admin API 文档]

#### XmlPullParser & XmlGenerator
```XML
<root>
<ENV_CgiName>/cgi-bin/mobile/MobileQueryAttribution.cgi</ENV_CgiName>
<ENV_ClientAgent>
Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36
</ENV_ClientAgent>
<ENV_ClientIp>203.75.244.133</ENV_ClientIp>
<ENV_QueryString>chgmobile=13412345678</ENV_QueryString>
<ENV_RequestMethod>GET</ENV_RequestMethod>
<ENV_referer/>
<chgmobile>13412345678</chgmobile>
<city>东莞</city>
<province>广东</province>
<retcode>0</retcode>
<retmsg>OK</retmsg>
<supplier>移动</supplier>
<tid/>
</root>
```
   - Parser [XMLPullParserHandler]
   - Generator [SmsBackup]

#### 正则式
  - 对照[正则式语句列表]
```JAVA
/**
 * 输入手机号码查询归属地
 * 限中国地区号码
 *
 * 规则如下:
 * 1. 11码
 * 2. 13, 14, 15, 16开头
 *
 */
private String phone="1351234567";
if(phone.matches("^1[3456]\\d{9}$")){
 //符合规则

 /**
 * ^ 开头
 * 1 第一位限定1
 * [3456] 第二位是3、4、5、6任一都行
 * [0-9] 效果等同于 \d，适用于之后的九位数字，所以是 \d\d\d\d\d\d\d\d\d 等同于 \d{9}
 * $ 结尾
 *
 * 正则式为 ^1[3456]\d{9}$
 */
 }

```

#### 任务栈
  - [TypePwdActivity]

#### 网络状态监听
  - 建立常驻service并通过自定义的BroadcastReceiver内部类获取网络状态

注册与释放service
```JAVA
private Intent nhs;

@Override
protected void onCreate(Bundle savedInstanceState) {
    nhs = new Intent(HomeActivity.this, NetworkHealthService.class);
    startService(nhs);
}

@Override
protected void onDestroy() {
    stopService(nhs);
}
```

service源码

```JAVA
public class NetworkHealthService extends Service {
    private final static String TAG = "NetworkHealthService";
    private InternetConnectivityReceiver internetReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        internetReceiver = new InternetConnectivityReceiver();
        IntentFilter internetIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        internetReceiver = new InternetConnectivityReceiver();
        registerReceiver(internetReceiver, internetIntentFilter);
    }

    public class InternetConnectivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getExtras() != null) {
                    final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                    if (ni != null && ni.isConnectedOrConnecting()) {
                        Toast.makeText(NetworkHealthService.this, "Network " + ni.getTypeName() + " connected", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(NetworkHealthService.this, "Network disabled", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                unregisterReceiver(internetReceiver);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(internetReceiver);
        } catch (Exception e) {
            unregisterReceiver(internetReceiver);
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
```
到Manifest注册service。

#### 建立桌面快捷图标
```JAVA
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
```
```XML
<activity ...>
<!--建立桌面快捷图标时使用此intent-filter可以在开启时直接导向特定特定activity-->
    <intent-filter>
        <action android:name="com.itheima.home" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>
</activity>
```

#### 使用Reflection实现挂断电话
  - 添加权限android.permission.CALL_PHONE（Android 6.0预设没有，须额外获取）
  - 添加远程调用（aidl）[ITelephony]与[NeighboringCellInfo]
```JAVA
/**
 * 使用反射机制加载被隐藏的方法
 */
private void endCall() {
    //api仍然存在，只是被隐藏而已，所以须使用反射找到方法
    //ServiceManager被隐藏（/** @hide */）所以会报错：Cannot resolve symbol ServiceManager
    //IBinder b =  ServiceManager.getService(Context.TELEPHONY_SERVICE);

    //改用：
    try {
        //加载ServiceManager的字节码
        Class clazz = BlockCallsSmsService.class.getClassLoader().loadClass("android.os.ServiceManager");
        //呼叫的方法与带入的参数型别
        Method method = clazz.getDeclaredMethod("getService", String.class);
        //the object on which to call this method (or null for static methods)
        IBinder b = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
        ITelephony.Stub.asInterface(b).endCall();
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
    } catch (NoSuchMethodException e) {
        e.printStackTrace();
    } catch (InvocationTargetException e) {
        e.printStackTrace();
    } catch (IllegalAccessException e) {
        e.printStackTrace();
    } catch (RemoteException e) {
        e.printStackTrace();
    }
}
```

#### Reflection范例
```JAVA
try {
    Class<?> clazz = Class.forName("android.app.ActivityManager");
    Method methods[] = clazz.getDeclaredMethods();
    final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    //打印所有找到的方法
    for (int i = 0; i < methods.length; i++) {
        Log.d(TAG, "找到的方法：" + methods[i].toString());
    }

    /**
     * 取代tasks = am.getRunningTasks(100);
     */
    final Method method = clazz.getMethod("getRunningTasks", int.class);
    private List<ActivityManager.RunningTaskInfo> tasks = (List<ActivityManager.RunningTaskInfo>) method.invoke(am, 100);

    //拿到栈顶的activity也就是当前运行的activity
    String packname = tasks.get(0).topActivity.getPackageName();
    CLog.d(TAG, "当前用户操作：" + packname);
} catch (ClassNotFoundException e) {
    e.printStackTrace();
} catch (NoSuchMethodException e) {
    e.printStackTrace();
}
```
  - 在method.invoke(am, 100)里，如果是static方法，am改成null，如果不是，需填入实例
  - 获取实例，比如ActivityManager(Context context, Handler handler){...}
```JAVA
Class<?> clazz = Class.forName("android.app.ActivityManager");
Constructor<?> cons = clazz.getConstructor(String.class, Handler.class);
ActivityManager am = (ActivityManager)cons.newInstance(this, new Handler());
//实际应用ActivityManager获取实例用getSystemService方法而非new一个ActivityManager
/**
 * LOLLIPOP以上用getRunningAppProcesses().get(0).processName取代am.getRunningTasks(100).get(0).topActivity.getPackageName()，
 * 添加权限<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />
 */
```
  - [WatchDogService]
  - [ClearCacheFragment]  search Reflection

#### [SQLite operation]

## 利用NDK存取authentication key
 - 两种方法配置JNI——CmakeLists.txt和Android.mk，这边用Android.mk示范
 - 在main目录底下建立JNI目录 —— 建立[Android.mk]和[Application.mk]，进行相关配置 —— 建立C/C++文件（[Config.cpp]）

![JNI 1][NDK1]

 - 在模块的build.gradle内添加：

 ```gradle
 externalNativeBuild {
     ndkBuild {
         path 'src/main/jni/Android.mk'
     }
 }
 ```
 - 详见[SecurityUtils]和[SplashActivity]

### 验证应用是否为正版（由相同keystore打包）
 1. 首先原理是每个keystore都能获取一个唯一的、固定的signature，把这个signature作为识别符判断该应用是否为指定keystore打包。

```Java
//要先用正确的keystore打包执行过一次，得到的strResult值就是FINGERPRINT_SHA1的值，之后执行如果换了apk就会返回false。
private final static String FINGERPRINT_SHA1 = "xxxxxxx";
public boolean verifyApk(Context ctx) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
    PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
    String strResult = "";
    for (Signature signature : pkgInfo.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(signature.toByteArray());
        for (byte b : md.digest()) {
            String strAppend = Integer.toString(b & 0xff, 16);
            if (strAppend.length() == 1)
                strResult += "0";
            strResult += strAppend;
        }
        strResult = strResult.toUpperCase();
    }
    return FINGERPRINT_SHA1.equals(strResult);
}
```

 2. 但如果用户反编译apk，并且置换FINGERPRINT_SHA1的值对应自己keystore的值就能破解，所以这边增加验证的复杂度。
    (1) 定义一组secret key，做base64编码(encode)，把这种key存在AndroidManifest的<meta-data>标签内。
    (2) 把先前取得的SHA-1签名和secret key拿来做MD5再做比较。

```xml
<application
    android:name=".utils.MyApplication"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/AppTheme">

    <meta-data
        android:name="Catherine.secret.key"
        android:value="MjAxNzA2MTdDYXRoZXJpbmUx" />

</application>
```

```Java
//要先用正确的keystore打包执行过一次，得到的apkKey值就是FINGERPRINT_SHA1的值，之后执行如果换了apk就会返回false。
private final static String FINGERPRINT_SHA1 = "xxxxxxx";
public boolean verifyApk(Context ctx) throws PackageManager.NameNotFoundException, NoSuchAlgorithmException {
    PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA | PackageManager.GET_SIGNATURES);
    Bundle bundle = pkgInfo.applicationInfo.metaData;
    if (!bundle.containsKey("Catherine.secret.key")) {
        CLog.e(TAG, "Error meta-data");
        return false;
    } else {
        String SDKKey = bundle.getString("Catherine.secret.key");
        //SHA1 fingerprint
        String strResult = "";
        for (Signature signature : pkgInfo.signatures) {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(signature.toByteArray());
            for (byte b : md.digest()) {
                String strAppend = Integer.toString(b & 0xff, 16);
                if (strAppend.length() == 1)
                    strResult += "0";
                strResult += strAppend;
            }
            strResult = strResult.toUpperCase();
        }
        String decodeKey;
        try {
            byte[] data1 = Base64.decode(SDKKey, Base64.DEFAULT);

            decodeKey = new String(data1, "UTF-8");
        } catch (Exception e) {
            decodeKey = "";
        }
        String apkKey = md5(decodeKey + strResult).toUpperCase();
        return FINGERPRINT_SHA1.equals(apkKey);
    }
}
```

 3. 再更进一步，让Manifest里secret key的值每次添加时都会变动，参考[SecurityUtils]里的verifyApk(Context)方法。


## App links几个要点
 - android M 及其新版支援以http/https为scheme的Url开启app（之前的版本导向浏览器）
 - 如果希望google搜寻结果出现打开app的链接，须注册[App Indexing on Google Search]
 - 如果预设导向该app而非浏览器等其他app（弹出选项），有一个auto-verify机制，intent-filter中须定义
 - 在domain中定义app信息于assetlinks.json，当链接以浏览器开启时，导向该domain时可根据assetlinks.json信息开启app
```xml
<intent-filter android:autoVerify="true">
    <!-- Accepts URIs that begin with "http://itheima.com/mobilesafe" -->
    <data android:scheme="http" android:host="itheima.com" android:pathPrefix="/mobilesafe" /><!--pathPrefix必须有／前缀-->
    <data android:scheme="https" android:host="itheima.com" android:pathPrefix="/mobilesafe" />

    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />    </intent-filter>
```
#### branch.io 实作

几个坑：
 - 特别注意条码扫描器（com.google.zxing.client.android）app会呼叫链接两次，由于branch.io后台有做栈的记录（它认为用户是扫两次条码而不是条码扫描器app的bug），造成关闭重启app时不用扫QR code也能导入链接，所以在onInitFinished中加上+match_guaranteed必须为true的判断（guaranteed为true代表branch.io告诉我们这笔数据的来源是百分之百正确的），详见[HomeActivity]
 - 部分浏览器无法直接开启App（比如部分LG系统自带、web browser），会导向用户自定链接（比如google play），一旦通过google play开启app，+match_guaranteed就一定是false，也就是这类用户会因为上述的过滤机制被认为是无效的链接，这边的做法是在唤起app时get Intent，先判断branch.io是否收到值，在+match_guaranteed为false的情况下检查intent是否带有包名（google play通过intent开启activity，一旦通过intent开启其它应用就必须带包名，用户直接开启app则不会有包名），有带包名表示用户是因为浏览器不给力，找不到应用因而导向google play而非直接开启，这时候我们就当成合法的branch.io动作，详见[HomeActivity]。
 - 部分浏览器无法直接开启App（比如LG系统自带），onResume时有可能点击链接后不处理。


 - Activity在Manifest的scheme配置应避免http或https，会导致系统开启链接时出现浏览器的选项（应直接导向该app而非交由浏览器拦截）
 - 在设置scheme时，若装置上同时安装两个相同scheme的应用，在branch io导向时，会出现两边都能开启的情况，但只有在branch io后台设置的包名可以正确的收到branch io带入的值。

```xml
<activity android:name="com.itheima.mobilesafe.HomeActivity"
android:windowSoftInputMode="adjustPan">
	<intent-filter android:autoVerify="true">
		<data android:scheme="itheima.mobilesafe" />

		<action android:name="android.intent.action.VIEW" />
		<category android:name="android.intent.category.DEFAULT" />
		<category android:name="android.intent.category.BROWSABLE" />
	</intent-filter>
</activity>
```

## Android6.0或以上权限设置

> 一般流程为：<br>
> 要求用户授权 - 用户同意 - 进入程序 - 下次打开直接進入<br>
> 要求用户授权 - 用户拒绝 - 关闭程序 - 下次打开再重新询问<br>
<br>
> 特殊情况：<br>
> 要求用户授权 - 用户勾选“不再提示”并强制选择拒绝 - 关闭程序 - ？<br>

特殊情况时，那些被勾选不再提示的权限以后都无法获取权限（再也不会弹出授权对话框询问），用户若想获取，必须自己手动到系统设置页开启。

此处做了些调整：
> 要求用户授权 - 用户同意 - 进入程序 - 下次打开直接進入<br>
> 要求用户授权 - 用户拒绝 - 弹出自定义提示对话窗，询问用户选择关闭或重新授权 - 同意 - 进入程序<br>
> 要求用户授权 - 用户拒绝 - 弹出自定义提示对话窗，询问用户选择关闭或重新授权 - 拒绝 - 关闭程序 - 下次打开再重新询问<br>
> <br>
> 要求用户授权 - 用户勾选“不再提示”并强制选择拒绝 - 关闭程序 - <br>
> 下次开启时会检查被用户勾选“不再提示的列表”，如果列表为空就回到一般流程检测权限。<br>
> 但是如果列表不为空 - 弹出自定义提示对话窗，询问用户是否授权 - 用户同意 - 导向设置页面让用户手动开启。<br>


  - 如果gradle中设定compileSdkVersion与targetSdkVersion低于23，在Android M后的手机就会预设没有获取权限。
  - 所以如果不想每次打开App都要逐一检查权限，就直接设compileSdkVersion与targetSdkVersion为22（或以下），预设一开始就取得Manifest所有权限。
  - 通过此方法获取的权限仅限于manifest有注册的权限，如果没注册这边也无法获取。
  - 需要在用到权限的地方，自定义是否检查权限，处理SYSTEM_ALERT_WINDOW和WRITE_SETTINGS例外
  - **Manifest设置allowBackup = false**，避免SharedPreference未清楚导致 (ActivityCompat.shouldShowRequestPermissionRationale(this, p) || isFirstTimeRun)逻辑错误。
  - 参考[Android 6.0 运行时权限处理]、[权限无法获取问题]，改成以注册listener的方式支援批次处理，在Activity接收用户事件，需要权限的fragment或activity则注册listener监听结果，主要代码如下：


[AndroidManifest]
 ```XML
 <application
   android:allowBackup="false">

 </application>
 ```  

[HomeActivity]
```JAVA
    private final int GRANTED_SAW = 0x0001;     //同意特殊权限(SYSTEM_ALERT_WINDOW)
    private final int GRANTED_WS = 0x0010;      //同意特殊权限(WRITE_SETTINGS)
    private int requestSpec = 0x0000;           //需要的特殊权限
    private int grantedSpec = 0x0000;           //已取得的特殊权限
    private int confirmedSpec = 0x0000;         //已询问的特殊权限
    private List<String> deniedPermissionsList; //被拒绝的权限
    private List<String> neverAskAgainList;     //被标记"不再询问"的权限
    private SharedPreferences isFirstTime;      //记录是否为初次启动app的SharePreferences
    private boolean isFirstTimeRun;             //是否为初次启动app
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
@Override
    public void getPermissions(String[] permissions, OnRequestPermissionsListener listener) {
        this.listener = listener;
        deniedPermissionsList = new LinkedList<>();
        neverAskAgainList = new LinkedList<>();

        for (String p : permissions) {
            if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                requestSpec |= GRANTED_SAW;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(HomeActivity.this)) {
                    grantedSpec &= GRANTED_SAW;
                } else
                    grantedSpec |= GRANTED_SAW;
            } else if (p.equals(Manifest.permission.WRITE_SETTINGS)) {
                requestSpec |= GRANTED_WS;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(HomeActivity.this)) {
                    grantedSpec &= GRANTED_WS;
                } else
                    grantedSpec |= GRANTED_WS;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, p) || isFirstTimeRun) {
                deniedPermissionsList.add(p);
            } else if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                neverAskAgainList.add(p);
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
                if (neverAskAgainList.size() != 0)
                    listener.onDenied(null, neverAskAgainList);
                else
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
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + HomeActivity.this.getPackageName()));
            startActivityForResult(intent, Constants.PERMISSION_OVERLAY);
        }

        if ((permissions & GRANTED_WS) == GRANTED_WS) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + HomeActivity.this.getPackageName()));
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

        if (deniedResults.size() != 0 || neverAskAgainList.size() != 0) {
            listener.onDenied(deniedResults, neverAskAgainList);
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

                            if (deniedResults.size() != 0 || neverAskAgainList.size() != 0) {
                                listener.onDenied(deniedResults, neverAskAgainList);
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

                            if (deniedResults.size() != 0 || neverAskAgainList.size() != 0) {
                                listener.onDenied(deniedResults, neverAskAgainList);
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
```
```JAVA
private MainInterface mainInterface;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
...
    mainInterface = (MainInterface) getActivity();
	init();
}

private void init(){
        mainInterface.getPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new OnRequestPermissionsListener() {
            @Override
            public void onGranted() {
                SharedPreferences.Editor ed = isFirstTime.edit();
                ed.putBoolean("isFirstTimeRun", false);
                ed.apply();
                isFirstTimeRun = false;

               //do something
            }

            @Override
            public void onDenied(@Nullable List<String> deniedPermissions, @Nullable final List<String> neverAskAgainPermissions) {
			//以上是给App首次开启用的，建议放在Activity进入
                SharedPreferences.Editor editor = isFirstTime.edit();
                editor.putBoolean("isFirstTimeRun", false);
                editor.apply();
                isFirstTimeRun = false;

				//提示对话框内容，此处可自定
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

                if (neverAskAgainPermissions != null) {
                    for (String p : neverAskAgainPermissions) {
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

                AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(HomeActivity.this);
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
                        if (neverAskAgainPermissions != null && neverAskAgainPermissions.size() != 0) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                            finish();
                        } else
                            init();
                    }
                });
                myAlertDialog.show();
            }
        });
}
```
[OnRequestPermissionsListener]
```JAVA
public interface OnRequestPermissionsListener {
    /**
     * 用户开启权限
     */
    void onGranted();

    /**
     * 用户拒绝打开权限
     */
    void onDenied(@Nullable List<String> deniedPermissions, @Nullable List<String> neverAskAgainPermissions);
}
```

**特别注意在Android 6.0 以上有自动备份功能，SharedPreference的值不会被清除！
也就是通過SharedPreference來判別是否為应用安装后第一次启动会失效。**

  - 解决方案1
```XML
<application android:allowBackup="false">

</application>
}
```  

  - 解决方案2
 获取权限时不考虑用户是否勾选“不再提示”，只判断是否同意。
 新的流程为：
 > 要求用户授权 - 用户同意 - 进入程序 - 下次打开直接進入<br>
 > 要求用户授权 - 用户拒绝 - 弹出自定义提示对话窗，询问用户选择关闭或重新授权 - 同意 - 导向设置页面让用户手动开启<br>
 > 要求用户授权 - 用户拒绝 - 弹出自定义提示对话窗，询问用户选择关闭或重新授权 - 拒绝 - 关闭程序 - 下次打开再重新询问

[GetPermissionsSample]
```JAVA
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
```


# License

```
Copyright 2017 Catherine Chen (https://github.com/Catherine22)

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```

[SettingItemView]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/SettingItemView.java>
   [fragment_settings]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/layout/fragment_settings.xml>
   [attrs]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/values/attrs.xml>
   [HomeActivity]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/HomeActivity.java>  
   [SplashActivity]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/SplashActivity.java>
   [GetPermissionsSample]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/GetPermissionsSample.java>
   [TypePwdActivity]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/TypePwdActivity.java>
   [Encryption]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/Encryption.java>
   [build.gradle]:<https://github.com/Catherine22/MobileManager/blob/master/app/build.gradle>
   [AndroidManifest]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/AndroidManifest.xml>
   [MyApplication]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/MyApplication.java>
   [CLog]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/CLog.java>
   [BootCompletedReceiver]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/receivers/BootCompletedReceiver.java>
   [SMSReceiver]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/receivers/SMSReceiver.java>
   [GPSService]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/services/GPSService.java>
   [火星坐标偏移算法]:<https://github.com/Catherine22/MobileManager/tree/master/app/src/main/java/com/itheima/mobilesafe/services/gcj02>
   [Android 6.0 运行时权限处理]:<https://www.aswifter.com/2015/11/04/android-6-permission/>
   [Setup2Fragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/setup/Setup2Fragment.java>
   [MyAdminManager]:<https://github.com/Catherine22/MobileManager/tree/master/app/src/main/java/com/itheima/mobilesafe/utils/MyAdminManager.java>
   [MyDeviceAdminReceiver]:<https://github.com/Catherine22/MobileManager/tree/master/app/src/main/java/com/itheima/mobilesafe/receivers/MyDeviceAdminReceiver.java>
   [device_admin_sample]:<https://github.com/Catherine22/MobileManager/tree/master/app/src/main/res/xml/device_admin_sample.xml>
   [device-admin API 文档]:<https://developer.android.com/guide/topics/admin/device-admin.html>
   [正则式语句列表]:<https://msdn.microsoft.com/zh-cn/library/ae5bf541(v=vs.100).aspx>
   [XMLPullParserHandler]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/XMLPullParserHandler.java>
   [权限无法获取问题]:<http://www.jianshu.com/p/2746a627c6d2>
   [自定义Toast]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/MyToast.java>
   [OnRequestPermissionsListener]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/interfaces/OnRequestPermissionsListener.java>
   [BlacklistFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/BlacklistFragment.java>
   [BlacklistAdapter]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/adapter/BlacklistAdapter.java>
   [SQLite operation]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/db/dao/BlacklistDao.java>
   [ITelephony]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/aidl/com/android/internal/telephony/ITelephony.aidl>
   [NeighboringCellInfo]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/aidl/android/telephony/NeighboringCellInfo.aidl>
   [BlockCallsSmsService]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/services/BlockCallsSmsService.java>
   [NumberAddressQueryFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/NumberAddressQueryFragment.java>
   [TaskFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/TaskFragment.java>
   [AddressService]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/services/AddressService.java>
   [NetUtils]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/NetUtils.java>
   [NetAsyncTask]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/NetAsyncTask.java>
   [SystemInfoUtils]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/SystemInfoUtils.java>
   [ServiceUtils]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/ServiceUtils.java>
   [SmsBackup]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/backup/SmsBackup.java>
   [App Indexing on Google Search]:<https://support.google.com/googleplay/android-developer/answer/6041489>
   [TaskFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/TaskFragment.java>
   [AppsManagerFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/AppsManagerFragment.java>
   [TaskInfoListAdapter]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/adapter/TaskInfoListAdapter.java>
   [MyAppWidgetProvider]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/MyAppWidgetProvider.java>
   [my_appwidget_info]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/xml/my_appwidget_info.xml>
   [AutoCleanService]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/services/AutoCleanService.java>
   [AccountKitUtils]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/login/AccountKitUtils.java>
   [SettingsFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/SettingsFragment.java>
   [TrafficManagerFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/TrafficManagerFragment.java>
   [AntiVirusFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/AntiVirusFragment.java>
   [ClearCacheFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/ClearCacheFragment.java>
   [WatchDogService]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/services/WatchDogService.java>
   [Config.cpp]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/jni/Config.cpp>
   [SecurityUtils]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/SecurityUtils.java>
   [Android.mk]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/jni/Android.mk>     
   [Application.mk]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/jni/Application.mk>
   [NDK1]: https://github.com/Catherine22/MobileManager/blob/master/jni1.png  
