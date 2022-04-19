/**
 *author:Ray
 *date:2015-1-23
 *TODO
 **/

package com.qgnix.main.ixxo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.qgnix.main.ixxo.maze.MusicService;
import com.qgnix.main.ixxo.util.Constant;
import com.qgnix.main.ixxo.util.SharedpreferencesUtils;
import com.qgnix.main.ixxo.util.UtilTools;
import com.qgnix.main.ixxo.view.Switch;
import com.qgnix.main.ixxo.view.Switch.OnCheckedChangeListener;


public class SettingActivity extends Activity implements OnClickListener {
	private Switch turn_muisc;
	private Switch turn_song;
	private View ll_update;
	protected boolean serviceFlag;
	protected Intent AudioIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(0);
		turn_muisc = (Switch)findViewById(0);
		turn_song = (Switch)findViewById(0);
		ll_update = (View)findViewById(0);
		String isOn = SharedpreferencesUtils.getPreValue(Constant.IS_TURN_ON_BACKGROUND_MUSIC, this);
		String isOn2 = SharedpreferencesUtils.getPreValue(Constant.IS_TURN_ON_CLICK_SONG, this);
		turn_muisc.setChecked(isOn == null || Constant.YES.equals(isOn)?true:false);
		turn_song.setChecked(isOn2 == null || Constant.YES.equals(isOn2)?true:false);
		ll_update.setOnClickListener(this);
		turn_muisc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(Switch switchView, boolean isChecked) {
				SharedpreferencesUtils.putSharedpre(Constant.IS_TURN_ON_BACKGROUND_MUSIC, isChecked?Constant.YES:Constant.NO, SettingActivity.this);
				AudioIntent = new Intent("com.ray.Android.MUSIC");
				AudioIntent.setClass(SettingActivity.this, MusicService.class);
				if(isChecked){ 
					if (serviceFlag == false) {
						startService(AudioIntent);
						serviceFlag = true;
					}
				}else{
					if (serviceFlag == true) {
						stopService(AudioIntent);  
						serviceFlag = false;
					}
				}
			}
		});
		turn_song.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(Switch switchView, boolean isChecked) {
				SharedpreferencesUtils.putSharedpre(Constant.IS_TURN_ON_CLICK_SONG, isChecked?Constant.YES:Constant.NO, SettingActivity.this);
			}
		});
	}

	@Override
	public void onClick(View arg0) {
        if(arg0 == ll_update){
        	UtilTools.checkUpdate(this, true);
        }
	}
	public void onResume() {
		super.onResume();

		}
		public void onPause() {
		super.onPause();

		}

}
