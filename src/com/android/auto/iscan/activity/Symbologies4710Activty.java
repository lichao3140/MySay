package com.android.auto.iscan.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.MotoSE4500Device;
import com.android.MotoSE47XXScanner;
import com.android.auto.iscan.R;

public class Symbologies4710Activty extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.se4710_setting);
		ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.title);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.drawable.iscan);
		actionBar.show();

		TextView tvTitle = (TextView) findViewById(R.id.Titletext);
		tvTitle.setText(this.getString(R.string.iscan_enable_code));
		Button btnBack = (Button) findViewById(R.id.TitleBackBtn);
		btnBack.setText(getString(R.string.iscan_back));
		btnBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		// super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference != null) {
			//String prefere = preference.getKey();

			MotoSE4500Device.SetParameter();

		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public void onDestroy() {
		SendBraodcast();
		super.onDestroy();
	}

	public void SendBraodcast() {
		Intent localIntent = new Intent();
		localIntent.setAction("android.intent.action.updatesetting");
		sendBroadcast(localIntent);
	}

	@Override
	public boolean onPreferenceChange(Preference key, Object arg1) {
		// TODO Auto-generated method stub
		Log.d("012", "onPreferenceChange::" + key.getKey());
		

		return true;
	}

}