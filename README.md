# MobileManager

## 功能导航
| 功能 | 代码 |
| ------------ | ------------- |
| 号码归属地查询 | [NumberAddressQueryFragment] |
| 设置黑名单 | [BlacklistFragment] |
| 进程管理、删除 | [TaskFragment] |
| 应用卸载、启动、分享 | [AppsManagerFragment] |
| 监听来电，显示号码归属地悬浮窗 | [AddressService] |
| 短信、通话拦截 | [BlockCallsSmsService] |
| 取得GPS位置 | [GPSService] |
| 拦截短信后，利用管理员权限卸载应用、设置锁屏、清除数据 | [SMSReceiver], [MyAdminManager] |
| 数据备份、还原（短信） | [SmsBackup] |
| Facebook Account kit 登入 |[SettingsFragment], [AccountKitUtils]|
| Deep linking（以branch.io实现） |[HomeActivity]|

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

#### Design Pattern
  - [Singleton]
  - [Factory]
  - [AbstractFactory]
  - [Builder]

#### [SQLite operation]

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
 - 特别注意条码扫描器app会呼叫链接两次，造成第二次开启app时不用扫QR code也能导入链接，所以在onInitFinished中加上+match_guaranteed必须为true的判断，详见[HomeActivity]
 - Activity在Manifest的scheme配置应避免http或https，会导致系统开启链接时出现浏览器的选项（应直接导向该app而非交由浏览器拦截）
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
  - 需要在用到权限的地方，自定义是否检查权限，处理SYSTEM_ALERT_WINDOW和WRITE_SETTINGS例外
  - 参考[Android 6.0 运行时权限处理]、[权限无法获取问题]，改成以注册listener的方式支援批次处理，在Activity接收用户事件，需要权限的fragment或activity则注册listener监听结果，主要代码如下：


[HomeActivity]
```JAVA
private boolean grantedSAW = true;//同意特殊权限(SYSTEM_ALERT_WINDOW)
private boolean grantedWS = true;//同意特殊权限(WRITE_SETTINGS)
private boolean grantedAll = true;//同意一般权限
private int specCount = 0;//等待同意特殊權限數(没有获取就不用添加)

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
public void getPermissions(String[] permissions, MyPermissionsResultListener listener) {
    this.listener = listener;
    List<String> deniedPermissionsList = new LinkedList<>();
    specCount = 0;
    for (String p : permissions) {
        if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED && !p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) && !p.equals(Manifest.permission.WRITE_SETTINGS))
            deniedPermissionsList.add(p);
        else if (p.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(HomeActivity.this)) {
                grantedSAW = false;
                specCount++;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + HomeActivity.this.getPackageName()));
                startActivityForResult(intent, Constants.OVERLAY_PERMISSION_REQ_CODE);
            } else
                grantedSAW = true;
        } else if (p.equals(Manifest.permission.WRITE_SETTINGS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(HomeActivity.this)) {
                grantedWS = false;
                specCount++;
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + HomeActivity.this.getPackageName()));
                startActivityForResult(intent, Constants.PERMISSION_WRITE_SETTINGS);
            } else
                grantedWS = true;
        } else {
            grantedSAW = true;
            grantedWS = true;
            // You've got SYSTEM_ALERT_WINDOW permission.
        }
    }

    if (deniedPermissionsList.size() != 0) {
        grantedAll = false;
        String[] deniedPermissions = new String[deniedPermissionsList.size()];
        for (int i = 0; i < deniedPermissionsList.size(); i++) {
            deniedPermissions[i] = deniedPermissionsList.get(i);
        }
        ActivityCompat.requestPermissions(this, deniedPermissions, ACCESS_PERMISSION);
    } else {
        // All of the permissions granted
        grantedAll = true;
        if (grantedSAW && grantedWS)
            listener.onGranted();
    }
    return;
}

@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    doNext(requestCode, grantResults);
}

private void doNext(int requestCode, int[] grantResults) {
    int count = 0;
    if (requestCode == ACCESS_PERMISSION) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                count++;
        }
        if (count == grantResults.length)
            grantedAll = true;
        else
            grantedAll = false;

        if (grantedAll && grantedSAW && grantedWS)//全部同意
            listener.onGranted();// Permission Granted
    }
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
        case Constants.REQUEST_CODE_ENABLE_ADMIN:
            if (resultCode == RESULT_OK) {
                sv.pushBoolean("ADMIN_PERMISSION", true);
            } else {
                sv.pushBoolean("ADMIN_PERMISSION", false);
            }
            break;
        case Constants.OVERLAY_PERMISSION_REQ_CODE:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                specCount--;
                if (!Settings.canDrawOverlays(this)) {
                    // Special permission not granted...
                    grantedSAW = false;
                    listener.onDenied();
                } else {
                    grantedSAW = true;
                    if (grantedAll && grantedWS)
                        listener.onGranted();
                    // You've got SYSTEM_ALERT_WINDOW permission.
                    if (!grantedSAW && specCount == 1)//表示用戶不同意另一個特殊權限
                        listener.onDenied();
                }
            }
            break;
        case Constants.PERMISSION_WRITE_SETTINGS:
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                specCount--;
                if (!Settings.System.canWrite(this)) {
                    // Special permission not granted...
                    grantedWS = false;
                    listener.onDenied();
                } else {
                    grantedWS = true;
                    if (grantedAll && grantedSAW)
                        listener.onGranted();
                    // You've got SYSTEM_ALERT_WINDOW permission.
                    if (!grantedSAW && specCount == 1)//表示用戶不同意另一個特殊權限
                        listener.onDenied();
                }
            }
            break;
    }
    CLog.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
}
```
[Setup2Fragment]
```JAVA
private MainInterface mainInterface;

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
...
    mainInterface = (MainInterface) getActivity();
}

private void doSomethingWithPermissions(){
    mainInterface.getPermissions(new String[]{
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS},
        new MyPermissionsResultListener() {
            @Override
            public void onGranted() {
            //You've got all permissions you need
            }
            @Override
            public void onDenied() {
                //Do something to handle this situaction
            }
        }
    );
}
```
[MyPermissionsResultListener]
```JAVA
public interface MyPermissionsResultListener {
    /**
     * 用户开启权限
     */
    void onGranted();

    /**
     * 用户拒绝打开权限
     */
    void onDenied();
}
```



   [SettingItemView]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/SettingItemView.java>
   [fragment_settings]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/layout/fragment_settings.xml>
   [attrs]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/values/attrs.xml>
   [HomeActivity]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/HomeActivity.java>
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
   [MyPermissionsResultListener]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/interfaces/MyPermissionsResultListener.java>
   [BlacklistFragment]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/BlacklistFragment.java>
   [BlacklistAdapter]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/adapter/BlacklistAdapter.java>
   [Singleton]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/singleton>
   [Factory]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/factory>
   [AbstractFactory]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/abstract_factory>
   [Builder]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/builder>
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
   