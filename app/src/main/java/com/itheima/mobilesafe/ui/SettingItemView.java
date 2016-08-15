package com.itheima.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.mobilesafe.R;

/**
 * 自定义组合控件的过程
 * <p/>
 * 1.自定义一个View 一般来说，继承相对布局，或者线性布局  ViewGroup；
 * 2.实现父类的构造方法。一般来说，需要在构造方法里初始化自定义的布局文件；
 * 3.根据一些需要或者需求，定义一些API方法；
 * ----------------------------------
 * 4.根据需要，自定义控件的属性，可以参照TextView属性；
 * <p/>
 * 5.自定义命名空间，例如：
 * xmlns:itheima="http://schemas.android.com/apk/res/《包名》"
 * xmlns:itheima="http://schemas.android.com/apk/res/com.itheima.mobilesafe"
 * <p/>
 * 6.自定义我们的属性，在Res/values/attrs.xml
 * <p/>
 * <?xml version="1.0" encoding="utf-8"?>
 * <resources>
 * <p/>
 * <declare-styleable name="TextView">
 * <attr name="title" format="string" />
 * <attr name="desc_on" format="string" />
 * <attr name="desc_off" format="string" />
 * </declare-styleable>
 * <p/>
 * </resources>
 * <p/>
 * 7.使用我们自定义的属性
 * 例如：
 * itheima:title="设置自动更新"
 * itheima:desc_on="设置自动更新已经开启"
 * itheima:desc_off="设置自动更新已经关闭"
 * <p/>
 * 8.在我们自定义控件的带有两个参数的构造方法里AttributeSet attrs 取出我们的属性值，关联自定义布局文件对应的控件；
 * <p/>
 * 我们自定义的组合控件，它里面有两个TextView ，还有一个CheckBox,还有一个View
 *
 * @author Administrator
 */
public class SettingItemView extends RelativeLayout {
    private CheckBox cb_status;
    private TextView tv_desc;
    private TextView tv_title;

    private String title_;
    private String desc_on;
    private String desc_off;

    /**
     * 初始化布局文件
     *
     * @param context
     */
    private void iniView(Context context) {

        //把一个布局文件---》View 并且加载在SettingItemView
        View.inflate(context, R.layout.setting_item_view, this);
        cb_status = (CheckBox) this.findViewById(R.id.cb_status);
        tv_desc = (TextView) this.findViewById(R.id.tv_desc);
        tv_title = (TextView) this.findViewById(R.id.tv_title);

    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        iniView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        iniView(context);
        title_ = attrs.getAttributeValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "title_");
        desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "desc_on");
        desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/com.itheima.mobilesafe", "desc_off");
        tv_title.setText(title_);
        tv_desc.setText(desc_off);
    }


    public SettingItemView(Context context) {
        super(context);
        iniView(context);
    }

    /**
     * 校验组合控件是否选中
     */

    public boolean isChecked() {
        return cb_status.isChecked();
    }

    /**
     * 设置组合控件的状态
     */

    public void setChecked(boolean checked) {
        cb_status.setChecked(checked);

        if (checked)
            tv_desc.setText(desc_on);
        else
            tv_desc.setText(desc_off);
    }

    /**
     * 设置 组合控件的描述信息
     */

    public void setDesc(String text) {
        tv_desc.setText(text);
    }


}
