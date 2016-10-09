# MobileManager
## 自定义控件

#### 自定义控件属性      
  - [fragment_settings]
  - [SettingItemView]
  - [attrs]     

#### [自定义Toast]

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
  
#### 可滑动、上下交换的RecyclerView
  - [BlacklistFragment]
  - [BlacklistAdapter]

## 其他应用
#### MD5加密
  - [Encryption]

#### 多渠道打包与自动隐藏debug log
  - [build.gradle]之productFlavors{...}
  - [MyApplication]
  - [CLog]

#### 开机后自动启动、检查SIM卡       
  - 在[AndroidManifest]添加权限与注册receiver
```JAVA
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
<!--使用WakeLock使Android应用程序保持后台唤醒-->
<uses-permission android:name="android.permission.WAKE_LOCK" />

<application
    <receiver android:name=".utils.BootCompletedReceiver">
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

#### XmlPullParser
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
   - [XMLPullParserHandler]

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
#### Design Pattern
  - [Singleton]
  - [Factory]
  - [AbstractFactory]
  - [Builder]

####  [SQLite operation]

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



   [SettingItemView]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/SettingItemView.java>
   [fragment_settings]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/layout/fragment_settings.xml>
   [attrs]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/values/attrs.xml>
   [HomeActivity]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/HomeActivity.java>
   [Encryption]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/Encryption.java>
   [build.gradle]: <https://github.com/Catherine22/MobileManager/blob/master/app/build.gradle>
   [AndroidManifest]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/AndroidManifest.xml>
   [MyApplication]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/MyApplication.java>
   [CLog]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/CLog.java>
   [BootCompletedReceiver]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/BootCompletedReceiver.java>
   [SMSReceiver]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/SMSReceiver.java>
   [GPSService]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/services/GPSService.java>
   [火星坐标偏移算法]: <https://github.com/Catherine22/MobileManager/tree/master/app/src/main/java/com/itheima/mobilesafe/services/gcj02>
   [Android 6.0 运行时权限处理]: <https://www.aswifter.com/2015/11/04/android-6-permission/>
   [Setup2Fragment]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/setup/Setup2Fragment.java>
   [MyAdminManager]: <https://github.com/Catherine22/MobileManager/tree/master/app/src/main/java/com/itheima/mobilesafe/utils/MyAdminManager.java>
   [MyDeviceAdminReceiver]: <https://github.com/Catherine22/MobileManager/tree/master/app/src/main/java/com/itheima/mobilesafe/utils/MyDeviceAdminReceiver.java>
   [device_admin_sample]: <https://github.com/Catherine22/MobileManager/tree/master/app/src/main/res/xml/device_admin_sample.xml>
   [device-admin API 文档]: <https://developer.android.com/guide/topics/admin/device-admin.html>
   [正则式语句列表]: <https://msdn.microsoft.com/zh-cn/library/ae5bf541(v=vs.100).aspx>
   [XMLPullParserHandler]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/XMLPullParserHandler.java>
   [权限无法获取问题]: <http://www.jianshu.com/p/2746a627c6d2>
   [自定义Toast]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/MyToast.java>
   [MyPermissionsResultListener]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/interfaces/MyPermissionsResultListener.java>
   [BlacklistFragment]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/fragments/BlacklistFragment.java>
   [BlacklistAdapter]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/adapter/BlacklistAdapter.java>
   [Singleton]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/singleton>
   [Factory]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/factory>
   [AbstractFactory]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/abstract_factory>
   [Builder]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/designpattern/builder>
   [SQLite operation]:<https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/db/dao/BlacklistDao.java>
