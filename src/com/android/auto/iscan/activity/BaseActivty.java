package com.android.auto.iscan.activity;

import java.util.Date;

import com.android.auto.iscan.utility.ConstantUtil;
import com.android.auto.iscan.utility.Gather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BaseActivty extends Activity {

	public void SendBroadcast(boolean enable) {
		Intent _intent = new Intent(ConstantUtil.KEY_SETTINGACTIVITY_ACTION);
		_intent.putExtra(ConstantUtil.KEY_SETTINGACTIVITY_ACTION, enable);
		sendBroadcast(_intent);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SendBroadcast(true);
		// Log.d("013","onCreate------");
	}

	protected void onDestroy() {
		// Log.d("013","onDestroy------");
		SendBroadcast(false);
		super.onDestroy();
	}

}
