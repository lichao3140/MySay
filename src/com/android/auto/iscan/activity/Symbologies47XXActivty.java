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

import com.android.MotoSE47XXScanner;
import com.android.auto.iscan.R;

public class Symbologies47XXActivty extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.se47xx_setting);
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
			String prefere = preference.getKey();
			if (prefere.equals("key_special_setting")) {

				Intent intent = new Intent();
				intent.setClass(Symbologies47XXActivty.this,
						ScannerSettingsActivity.class);
				startActivity(intent);
			} else if (prefere.equals("key_preview_image")) {

				showPassswordDialog(Symbologies47XXActivty.this
						.getString(R.string.iscan_enter_password));

			} else {
				MotoSE47XXScanner.SetParameter();

			}

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
	//	Log.d("012", "onPreferenceChange::" + key.getKey());
		if (key.getKey().equals("key_special_setting")) {

			return false;

		}

		return true;
	}

	public void showPassswordDialog(final String key_title) {
		final EditText mCurrentFormat;
		LinearLayout inputLayout;
		inputLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.input2, null);
		mCurrentFormat = (EditText) inputLayout.findViewById(R.id.xET);
		new AlertDialog.Builder(this)
				.setTitle(key_title)
				.setView(inputLayout)

				.setPositiveButton(getString(R.string.iscan_password_ok),
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								if (mCurrentFormat.getText().toString()
										.equals("666666")) {

									Intent intent = new Intent();
									intent.setClass(
											Symbologies47XXActivty.this,
											PreviewActivty.class);
									startActivity(intent);

								} else {
									Toast toast = Toast
											.makeText(
													Symbologies47XXActivty.this,
													Symbologies47XXActivty.this
															.getString(R.string.iscan_password_error),
													Toast.LENGTH_SHORT);
									toast.setGravity(Gravity.CENTER, 0, 0);
									toast.show();
								}
							}
						})

				.setNegativeButton(getString(R.string.iscan_password_cancel),
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) { //

							}
						}).show();
	}

}