package com.itheima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.itheima.mobilesafe.ui.SettingItemView;

public class SettingActivity extends Activity {
	private SettingItemView siv_update;
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		
		boolean update = sp.getBoolean("update", false);
		if(update){
			//自动升级已经开启
			siv_update.setChecked(true);
			siv_update.setDesc("自动升级已经开启");
		}else{
			//自动升级已经关闭
			siv_update.setChecked(false);
			siv_update.setDesc("自动升级已经关闭");
		}
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				//判断是否有选中
				//已经打开自动升级了
				if(siv_update.isChecked()){
					siv_update.setChecked(false);
					siv_update.setDesc("自动升级已经关闭");
					editor.putBoolean("update", false);
					
				}else{
					//没有打开自动升级
					siv_update.setChecked(true);
					siv_update.setDesc("自动升级已经开启");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
	}

}
