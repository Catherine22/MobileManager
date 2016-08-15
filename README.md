# MobileManager

### 自定义控件
自定义控件属性      
  - [fragment_settings]
  - [SettingItemView]
  - [attrs]     

自定义对话框
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
 

### 其他应用
MD5加密
  - [Encryption]


  




   [SettingItemView]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/ui/SettingItemView.java>
   [fragment_settings]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/layout/fragment_settings.xml>
   [attrs]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/res/values/attrs.xml>
   [HomeActivity]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/HomeActivity.java>
   [Encryption]: <https://github.com/Catherine22/MobileManager/blob/master/app/src/main/java/com/itheima/mobilesafe/utils/Encryption.java>